package recetario.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    public void ready(){
        try {
            pasos = this.app.pasoDao.queryBuilder().where().eq(Paso.NAME_FIELD_RECETA, r.getId()).query();
            ingredientes = this.app.ingredienteDao.queryBuilder().where().eq(Ingrediente.NAME_FIELD_RECETA, r.getId()).query();
        } catch (SQLException ex) {
            System.out.println("Error en la búsqueda de la receta");
        }
        showInformation();
        showPasos();
        showIngredientes();
    }
    public void showInformation(){
        name.setText(r.getName());
        people.setText(r.getPeople()+" comensales");
        category.setText(r.getCategory().getName());
        time.setText(r.getTime());
        level.setFill(Color.web(r.getLevel()));
        image.setImage(r.getImage());
    }
    public void showPasos(){
         ObservableList<Paso> data = FXCollections.observableArrayList(pasos);
         pasosList.setItems(null);
         pasosList.setItems(data);       
    }
         
    public void showIngredientes(){
        ObservableList<Ingrediente> ingr = FXCollections.observableArrayList(ingredientes);
        ingredientesList.setItems(null);
        ingredientesList.setItems(ingr);
    }
    public void abrirVisor(Paso p){
        System.out.println("Hello");
        Controlador v = this.app.abrirVentana("Visor", "Visor");
        v.ready();
    }
    @FXML
    private void initialize() {
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
                            description.setText(item.getDescription());
                            ImageView media = (ImageView)root.lookup("#media");
                            String med = item.getMedia();
                            if (med.contains("youtube")){
                                ImageView play = (ImageView)root.lookup("#play");
                                play.setVisible(true);
                                media.setImage(item.loadYoutubeImage());
                                play.setOnMouseClicked((event) -> { abrirVisor(item);  });
                            }else{
                                media.setImage(item.loadMediaImage());
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
                protected void updateItem(Ingrediente item, boolean empty) {
                    if(!empty){
                        try {
                            super.updateItem(item, empty);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Ingrediente.fxml"));
                            Parent root = (Parent) loader.load();
                            Label weight = (Label)root.lookup("#weight");
                            weight.setText(item.getCantidad());
                            Label description = (Label)root.lookup("#description");
                            description.setText(item.getName());
                            ImageView media = (ImageView)root.lookup("#media");
                            media.setImage(item.loadImage());                            
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
