package recetario.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.h2.store.fs.FileUtils;
import recetario.model.Ingrediente;
import recetario.model.Paso;
import recetario.model.Receta;

public class Explicacion extends Controlador{
    public Receta r;
    public List<Paso> pasos;
    public List<Ingrediente> ingredientes;
    @FXML
    private ListView pasosList;
    @FXML 
    private ListView ingredientesList;
    @FXML
    private Button editarButton;
    @FXML
    private Button addIngrediente;
    @FXML
    private Button removeIngrediente;
    @FXML
    private Button saveButton;
    @FXML
    private Button addPaso;
    @FXML
    private Button removePaso;
    @FXML
    private ImageView image;
    @FXML
    private Label name;  
    @FXML
    private Label time;  
    @FXML
    private Label people;
    @FXML
    private Label category;
    @FXML
    private Circle level;
    
    private boolean isEditing = false;
    public void ready(){
        try {
            showPasos();
            showIngredientes();
        } catch (SQLException ex) {
            System.out.println("Error en la búsqueda de la receta");
        }
        showInformation();
    }
    public void showInformation(){
        name.setText(r.getName());
        people.setText(r.getPeople());
        category.setText(r.getCategory().getName());
        time.setText(r.getTime());
        level.setFill(Color.web(r.getLevel()));
        image.setImage(r.getImage());
    }
    public void showPasos() throws SQLException{
         pasos = this.app.pasoDao.queryBuilder().where().eq(Paso.NAME_FIELD_RECETA, r.getId()).query();
         ObservableList<Paso> data = FXCollections.observableArrayList(pasos);
         pasosList.setItems(null);
         pasosList.setItems(data);       
    }
      
    private static void copyFile(File sourceFile, File destFile)
		throws IOException {
	if (!sourceFile.exists()) {
		return;
	}
	if (!destFile.exists()) {
		destFile.createNewFile();
	}
	FileChannel source = null;
	FileChannel destination = null;
	source = new FileInputStream(sourceFile).getChannel();
	destination = new FileOutputStream(destFile).getChannel();
	if (destination != null && source != null) {
		destination.transferFrom(source, 0, source.size());
	}
	if (source != null) {
		source.close();
	}
	if (destination != null) {
		destination.close();
	}

}
    public void mediaDialog(Paso p, ImageView preview){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Elegir recurso");
        alert.setHeaderText("Elige un tipo de multimedia para continuar");
        alert.setContentText("");

        ButtonType youtube = new ButtonType("YouTube");
        ButtonType image = new ButtonType("Imagen local");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(youtube, image, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == youtube){
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Dirección YouTube");
            dialog.setHeaderText("Dirección YouTube");
            dialog.setContentText("Introduce la dirección del vídeo en YouTube:");

            Optional<String> resultYouTube = dialog.showAndWait();
            if (resultYouTube.isPresent()){
                p.setMedia(resultYouTube.get());
                preview.setImage(p.loadYoutubeImage());
            }


        } else if (result.get() == image) {
              final FileChooser fileChooser = new FileChooser();
              File file = fileChooser.showOpenDialog(stage);
               if (file != null) {
                   File dest = new File("./data/images/"+file.getName());
                  try {
                      copyFile(file, dest);
                      p.setMedia(file.getName());
                      preview.setImage(p.loadMediaImage());
                  } catch (IOException ex) {
                      System.out.println(ex.getMessage());
                  }
                   
               }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    
    public void mediaIngrediente(Ingrediente i, ImageView preview){
        final FileChooser fileChooser = new FileChooser();
              File file = fileChooser.showOpenDialog(stage);
               if (file != null) {
                   File dest = new File("./data/images/"+file.getName());
                  try {
                      copyFile(file, dest);
                      i.setImage(file.getName());
                      preview.setImage(i.loadImage());
                  } catch (IOException ex) {
                      System.out.println(ex.getMessage());
                  }
                   
               }
    }
    
    public void showIngredientes() throws SQLException{
        ingredientes = this.app.ingredienteDao.queryBuilder().where().eq(Ingrediente.NAME_FIELD_RECETA, r.getId()).query();
        ObservableList<Ingrediente> ingr = FXCollections.observableArrayList(ingredientes);
        ingredientesList.setItems(null);
        ingredientesList.setItems(ingr);
    }
    public void abrirVisor(Paso p){
        Visor v = (Visor)this.app.abrirVentana("Visor", "Visor");
        if(p.getMedia().contains("youtube")){
            v.id=p.getYoutubeId(p.getMedia());
        }else{
            v.image=p.loadMediaImage();
        }
        v.ready();
    }
    
    public void abrirImagen(Ingrediente i){
        Visor v = (Visor) this.app.abrirVentana("Visor", "Visor");
        v.image = i.loadImage();
        v.ready();
    }
    
    public void addPaso(){
        Paso p = new Paso("", pasos.size()+1, r);
        try {
            this.app.pasoDao.create(p);
        } catch (SQLException ex) {
            System.out.println("Error creando nuevo paso");
        }
        this.ready();
    }
    
    public void addIngrediente(){
        Ingrediente i = new Ingrediente("", "", r);
        try{
            this.app.ingredienteDao.create(i);
        } catch (SQLException ex){
            System.out.println("Error creando nuevo ingrediente");
        }
        modoEditar(true);
    }
    
    public void save() throws SQLException{
        for (Object a : pasosList.getItems()) {
            Paso p = (Paso)a;
            TextArea description = (TextArea)pasosList.lookup("#description"+p.getId());
            p.setDescription(description.getText());     
            this.app.pasoDao.update(p);
        }

    }
    public void modoEditar(boolean t){
        isEditing=t;
        editarButton.setVisible(!t);
        saveButton.setVisible(t);
        this.ready();
    }
    
    public void eliminarIngrediente(Ingrediente i){
        try{
            this.app.ingredienteDao.delete(i);
        }catch(SQLException ex){
            System.out.println("Error al borrar ingrediente");
        }
        this.ready();
    }
    
    public void eliminarPaso(Paso p){
        try{
            this.app.pasoDao.delete(p);         
        }catch(SQLException ex){
            System.out.println("Error al eliminar paso");
        }
        this.ready();
    }
    @FXML
    private void initialize() {
        //Botones
        addPaso.setDisable(true);
        removePaso.setDisable(true);
        addPaso.setOnMouseClicked((event) -> { addPaso();  });
        addIngrediente.setOnMouseClicked((event) -> { addIngrediente();  });
        editarButton.setOnMouseClicked((event) -> { 
            modoEditar(true); 
            removePaso.setDisable(false);
            addPaso.setDisable(false); });
        saveButton.setOnMouseClicked((event) -> { 
          try{ 
            save(); 
            modoEditar(false);
            addPaso.setDisable(true);
          } catch (SQLException ex) { System.out.println("Error al guardar información");} });
        //Estilo de la lista de pasos
        pasosList.setCellFactory((list) -> {
            return new ListCell<Paso>() {
                @Override
                protected void updateItem(Paso item, boolean empty) {
                    if(!empty){
                        try {
                            super.updateItem(item, empty);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Paso.fxml"));
                            Parent root = (Parent) loader.load();
                            Label number = (Label)root.lookup("#number");
                            number.setText(item.getOrder()+".");
                            Label description = (Label)root.lookup("#description");
                            TextArea descriptionEdit = (TextArea)root.lookup("#descriptionEdit");
                            description.setText(item.getDescription());
                            ImageView media = (ImageView)root.lookup("#media");
                            Button choose = (Button)root.lookup("#choose");
                            removePaso.setDisable(true);
                            removePaso.setOnMouseClicked((event) -> {eliminarPaso(item);});
                            String med = item.getMedia();
                            if (med.contains("youtube")){
                                ImageView play = (ImageView)root.lookup("#play");
                                play.setVisible(true);
                                media.setImage(item.loadYoutubeImage());
                                play.setOnMouseClicked((event) -> { abrirVisor(item);  });
                            }else{
                                media.setImage(item.loadMediaImage());
                                media.setOnMouseClicked((event) -> { abrirVisor(item);  });
                            }
                            if(isEditing){
                                  removePaso.setDisable(false);
                                  descriptionEdit.setText(item.getDescription());
                                  descriptionEdit.setVisible(true);
                                  descriptionEdit.setId("description"+item.getId());
                                  choose.setVisible(true);
                                  choose.setOnMouseClicked((event) -> { mediaDialog(item, media);  });
                            }
                            
                            setGraphic(root);           
                        } catch (IOException ex) {
                            System.out.println("Error abriendo celda");
                        }

                    }else{
                        VBox v = new VBox();
                        setGraphic(v);
                    }
                }
            };
        });
        
        
        ingredientesList.setCellFactory((list) -> {
            return new ListCell<Ingrediente>() {
                @Override
                protected void updateItem(Ingrediente i, boolean empty) {
                    if(!empty){
                        try {
                            super.updateItem(i, empty);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Ingrediente.fxml"));
                            Parent root = (Parent) loader.load();
                            Label weight = (Label)root.lookup("#weight");
                            weight.setText(i.getCantidad());
                            Label description = (Label)root.lookup("#description");
                            description.setText(i.getName());
                            ImageView media = (ImageView)root.lookup("#media");
                            media.setImage(i.loadImage());
                            media.setOnMouseClicked((event) -> { abrirImagen(i);  });
                            TextField weightEdit = (TextField)root.lookup("#weigthEdit");
                            TextField descriptionEdit = (TextField)root.lookup("#descriptionEdit");
                            Button choose = (Button)root.lookup("#choose");
                            removeIngrediente.setOnMouseClicked((event) -> {eliminarIngrediente(i);});
                            setGraphic(root);  
                            /**
                            if(isEditing){
                                descriptionEdit.setText(i.getName());
                                descriptionEdit.setVisible(true);
                                descriptionEdit.setId("description");
                                weightEdit.setText(i.getCantidad());
                                weightEdit.setVisible(true);
                                weightEdit.setEditable(true);
                                weightEdit.setId("weight");
                                choose.setVisible(true);
                                media.setVisible(false);
                                choose.setOnMouseClicked((event) -> { mediaIngrediente(i, media);  });
                            }
                            */
                        } catch (IOException ex) {
                            System.out.println("Error abriendo celda");
                        }
                    }else{
                        VBox v = new VBox();
                        setGraphic(v);
                    }
                }
            };
        });
    }
}
