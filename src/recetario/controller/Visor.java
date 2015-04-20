package recetario.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;

public class Visor extends Controlador{
    public String id;
    public Image image;
    @FXML
    private WebView web;
    @FXML
    private ImageView imageView;
    @FXML
    private HBox imageContainer;
    
    public void ready(){
        if(this.id!=null){
            WebEngine webEngine = web.getEngine();
            web.setVisible(true);
            webEngine.load("https://www.youtube.com/embed/"+id+"?autoplay=1");
        }else{
            imageContainer.setVisible(true);
            imageView.setImage(image);
        }
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent event) {
                web.getEngine().load(null);

            }
        });

    }
}
