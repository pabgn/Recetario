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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
import javafx.util.StringConverter;
import org.h2.store.fs.FileUtils;
import recetario.model.Category;
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
    private ImageView editarButton;
    @FXML
    private ImageView addIngrediente;
    @FXML
    private ImageView removeIngrediente;
    @FXML
    private ImageView saveButton;
    @FXML
    private ImageView addPaso;
    @FXML
    private ImageView removePaso;
    @FXML
    private ImageView image;
    @FXML
    private Label name;
    @FXML
    private Label category;
    @FXML
    private TextField nameEdit;
    @FXML
    private TextField time;
    @FXML
    private TextField people;
    @FXML
    private ComboBox categoriaEdit;
    @FXML
    private Circle level;
    @FXML
    private Button elegirImagen;
    
    private boolean needsInsert=false;
    private boolean isEditing = false;
    public void ready(){
        if(this.r.nueva){
            this.r.nueva=false;
            needsInsert=true;
            modoEditar(true);
        }else{
            try {
                showPasos();
                showIngredientes();
                fillCategories();
            } catch (SQLException ex) {
                System.out.println("Error en la búsqueda de la receta");
            }
            showInformation();
        }
    }
    public void fillCategories() throws SQLException{
        List<Category> categories = this.app.categoryDao.queryForAll();

        ObservableList<Category> categories_l = FXCollections.observableArrayList(categories);
        categoriaEdit.setItems(categories_l);
         if(!needsInsert){
            try{
            Category cat = this.app.categoryDao.queryBuilder().where().eq("id", r.getCategory().getId()).queryForFirst();
            category.setText(cat.getName());
             }catch(Exception e){}
         }
    }
    public void showInformation(){
        name.setText(r.getName());
        people.setText(Integer.toString(r.getPeople()));
        time.setText(Integer.toString(r.getTime()));
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
    public void createIfNeeded(){
        if(needsInsert){
            try {
                this.app.recetaDao.create(r);
            } catch (SQLException ex) {
                System.out.println("Error al crear la receta");
            }
            needsInsert=false;
        }
        
    }
    public void addPaso(){
        createIfNeeded();
        Paso p = new Paso("", pasos.size()+1, r);
        try {
            this.app.pasoDao.create(p);
        } catch (SQLException ex) {
            System.out.println("Error creando nuevo paso");
        }
        this.ready();
    }
    
    public void addIngrediente(){
        createIfNeeded();
        Ingrediente i = new Ingrediente("", "", r);
        try{
            this.app.ingredienteDao.create(i);
        } catch (SQLException ex){
            System.out.println("Error creando nuevo ingrediente");
        }
        modoEditar(true);
    }
    
    public void save() throws SQLException{
        createIfNeeded();
        for (Object a : pasosList.getItems()) {
            Paso p = (Paso)a;
            TextArea description = (TextArea)pasosList.lookup("#description"+p.getId());
            p.setDescription(description.getText());     
            this.app.pasoDao.update(p);
        }
        
        for (Object b : ingredientesList.getItems()) {
            Ingrediente i = (Ingrediente) b;
            TextField description = (TextField) ingredientesList.lookup("#description"+i.getId());
            i.setName(description.getText());  
            TextField weight = (TextField) ingredientesList.lookup("#weight"+i.getId());
            i.setCantidad(weight.getText());
            this.app.ingredienteDao.update(i);
        }
        
        try{
            r.setName(nameEdit.getText());
            r.setTime(Integer.parseInt(time.getText()));
            r.setPeople(Integer.parseInt(people.getText()));
            r.setCategory((Category) categoriaEdit.getSelectionModel().getSelectedItem());
            this.app.recetaDao.update(r);
        }catch(Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en los datos");
            alert.setHeaderText("Datos incorrectos");
            alert.setContentText("Asegurate de introducir los datos correctamente");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){}
        }
        this.app.inicio.ready();
    }
    public void modoEditar(boolean t){
        isEditing=t;
        editarButton.setVisible(!t);
        saveButton.setVisible(t);
        name.setVisible(!t);
        time.setDisable(!t);
        people.setDisable(!t);
        category.setVisible(!t);
        nameEdit.setVisible(t);
        nameEdit.setText(name.getText());
        categoriaEdit.setVisible(t);
        addPaso.setVisible(t);
        removePaso.setVisible(t);
        addIngrediente.setVisible(t);
        removeIngrediente.setVisible(t);
        elegirImagen.setVisible(t);
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
    
    public void elegirImagenReceta(){
        try{
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                File dest = new File("./data/images/"+file.getName());
                try {
                    copyFile(file, dest);
                    r.setImage(file.getName());
                    image.setImage(r.loadImage());
                } catch (IOException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error en la BBDD");
                    alert.setContentText("No ha sido posible guardar la imagen");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){}
                }
                   
            }
        }catch(Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error en la imagen");
            alert.setHeaderText("Imagen erronea");
            alert.setContentText("Asegurate de introducir la imagen correctamente");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){}
        }
    }
    @FXML
    public void levelClick(Event e){
       if(isEditing){
       int nextLevel = (this.r.getPlainLevel()+1)%3;
       this.r.setLevel(nextLevel);
       level.setFill(Color.web(r.getLevel()));
       }
       
    }
    @FXML
    private void initialize() {
        //Botones
        elegirImagen.setOnMouseClicked((event) -> {elegirImagenReceta();}); 
        addPaso.setOnMouseClicked((event) -> { addPaso();  });
        addIngrediente.setOnMouseClicked((event) -> { addIngrediente();  });
        editarButton.setOnMouseClicked((event) -> { modoEditar(true); });
        saveButton.setOnMouseClicked((event) -> { 
          try{ 
            save(); 
            modoEditar(false);
            
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
                                  media.setVisible(true);
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
        
         categoriaEdit.setCellFactory((comboBox) -> {
            return new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        });
         
         categoriaEdit.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category person) {
                if (person == null) {
                    return null;
                } else {
                    return person.getName();
                }
            }
            @Override
            public Category fromString(String personString) {
                return null; // No conversion fromString needed.
            }
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
                            TextField weightEdit = (TextField)root.lookup("#weightEdit");
                            TextField descriptionEdit = (TextField)root.lookup("#descriptionEdit");
                            Button choose = (Button)root.lookup("#choose");
                            removeIngrediente.setOnMouseClicked((event) -> {eliminarIngrediente(i);});
                            
                            if(isEditing){
                                weightEdit.setVisible(true);
                                weightEdit.setText(weight.getText());
                                weightEdit.setId("weight"+i.getId());
                                descriptionEdit.setVisible(true);
                                descriptionEdit.setText(description.getText());
                                descriptionEdit.setId("description"+i.getId());
                                media.setVisible(true);
                                choose.setVisible(true);
                                choose.setOnMouseClicked((event) -> { mediaIngrediente(i, media);  });
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
    }
}
