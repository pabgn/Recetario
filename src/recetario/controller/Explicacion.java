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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import recetario.model.Paso;
import recetario.model.Receta;

public class Explicacion extends Controlador{
    public Receta r;
    public List<Paso> pasos;    
    @FXML
    private ListView pasosList;
    public void ready(){
        try {
            pasos = this.app.pasoDao.queryBuilder().where().eq(Paso.NAME_FIELD_RECETA, r.getId()).query();
        } catch (SQLException ex) {
            System.out.println("Error en la búsqueda de pasos");
        }
        showPasos();
    }
    public void showPasos(){
         ObservableList<Paso> data = FXCollections.observableArrayList(pasos);
         pasosList.setItems(null);
         pasosList.setItems(data);
         
            
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
    }
}
