package recetario.controller;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import recetario.model.Category;
import recetario.model.Paso;
import recetario.model.Receta;

public class Inicio extends Controlador {
    @FXML
    private ListView listView1;
    @FXML
    private ListView listView2; 
    @FXML
    private ComboBox levelFilter;
    @FXML
    private TextField search;
    @FXML
    private ImageView newReceta;
    @FXML
    private AnchorPane screen;
    @FXML
    private HBox all;    
    public int category=0;
    public int level=0;
    public int rating=0;
    
    public Inicio(){
        super();
    }
    public void abrirReceta(Receta r){
        Explicacion c = (Explicacion)this.app.abrirVentana("Explicacion", r.getName());
        c.r=r;
        c.ready();
    }
    public void eliminarReceta(Receta r){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Eliminar receta");
        alert.setHeaderText("Eliminar receta");
        alert.setContentText("¿Estás seguro que deseas eliminar esta receta y todo su contenido?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                this.app.recetaDao.delete(r);
                mostrarRecetas();
            } catch (SQLException ex) {
                System.out.println("Error borrando receta");
                
            }
        } else {
            
        }
    }
    public void newReceta(){
        Receta r = new Receta(true);
        abrirReceta(r);
    }
    public void mostrarRecetas() throws SQLException{
        List<Receta> recetas1;
        
        QueryBuilder<Receta, Integer> qb = this.app.recetaDao.queryBuilder();        
        Where where = qb.where();
        boolean and=false;
        if(category!=0){
             where.eq(Receta.NAME_FIELD_CATEGORY, category);
             and=true;
        }
        if(level!=0){
            if(and){ where.and(); }
             where.eq(Receta.NAME_FIELD_LEVEL, level);
             and=true;
        }
        if(rating!=0){
            if(and){ where.and(); }
             where.eq(Receta.NAME_FIELD_RATING, rating);
             and=true;
        }
        if(search.getText().length()!=0){
            if(and){ where.and(); }
            where.like(Receta.NAME_FIELD_NAME, "%"+search.getText()+"%");
            and=true;
        }
        
        if(and){
            PreparedQuery<Receta> p = qb.prepare();
            recetas1 = this.app.recetaDao.query(p);
        }else{
            recetas1= this.app.recetaDao.queryForAll();
        }
        List<Receta> recetas2 = new ArrayList<Receta>();
            //Separamos en dos columnas por posiciones pares e impares
            int index = 1;
            for (Iterator<Receta> it = recetas1.iterator(); it.hasNext(); ) {
                 Receta r = it.next();
                  if(index%2==0){
                    recetas2.add(r);
                    it.remove();
                }
                index++;
            }
            ObservableList<Receta> data1 = FXCollections.observableArrayList(recetas1);
            ObservableList<Receta> data2 = FXCollections.observableArrayList(recetas2);
            listView1.setItems(null);
            listView1.setItems(data1);
            listView2.setItems(null);
            listView2.setItems(data2);

    }
    public void ready(){
        try {
            //Cargamos las categorías de la DB:
            List<Category> categories = this.app.categoryDao.queryForAll();
            ObservableList<Category> categories_l = FXCollections.observableArrayList(categories);
            List<String> levels = Arrays.asList("Todos", "Fácil", "Medio", "Difícil");
            ObservableList<String> levels_l = FXCollections.observableArrayList(levels);
            levelFilter.setItems(levels_l);
            levelFilter.getSelectionModel().selectFirst();
            this.getNumbers();
            //Cargamos las recetas de la DB:
        } catch (SQLException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void initialize() {
        //Listas a aplicar las celdas especiales
        List<ListView> listas = Arrays.asList(listView1, listView2);
        for(ListView l: listas){
            l.setCellFactory((list) -> {
                return new ListCell<Receta>() {
                    @Override
                    protected void updateItem(Receta item, boolean empty) {
                        if(!empty){
                            try {
                                super.updateItem(item, empty);
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Celda.fxml"));
                                Parent root = (Parent) loader.load();
                                Label name = (Label)root.lookup("#name");
                                ImageView image = (ImageView)root.lookup("#image");
                                Label time = (Label)root.lookup("#time");
                                Circle level = (Circle)root.lookup("#level");  
                                Label people = (Label)root.lookup("#people");
                                Button open = (Button)root.lookup("#open");                
                                Button delete = (Button)root.lookup("#delete");                
                                name.setText(item.getName());
                                image.setImage(item.getImage());
                                time.setText(Integer.toString(item.getTime())+" minutos");
                                people.setText(Integer.toString(item.getPeople())+" comensales");
                                level.setFill(Color.web(item.getLevel()));
                                open.setOnAction((event) -> { abrirReceta(item);  });
                                delete.setOnAction((event) -> { eliminarReceta(item);  });

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
        
       
        
        levelFilter.setOnAction((event) -> {
            try {
                int level = levelFilter.getSelectionModel().selectedIndexProperty().intValue();
                this.level = level;
                mostrarRecetas();
            } catch (SQLException ex) {
                System.out.println("Error en búsqueda por nivel");
            }
            
        });
        search.setOnKeyPressed((event)->{ 
            if(event.getCode()==KeyCode.ENTER){
                try {
                   mostrarRecetas();
               } catch (SQLException ex) {
                   System.out.println("Error en búsqueda por valor");
               }
            }});
        
        newReceta.setOnMouseClicked((event)->{ newReceta(); });
    }
    private void getNumbers(){
       for(int i=1;i<=5;i++){
           try {
               QueryBuilder<Receta, Integer> qb = this.app.recetaDao.queryBuilder();
               qb.setCountOf(true);
               Where where = qb.where();
               where.eq(Receta.NAME_FIELD_CATEGORY, i);
               PreparedQuery p = qb.prepare();
               int c = (int)this.app.recetaDao.countOf(p);
               Label l = (Label)screen.lookup("#counter"+i);
               l.setText(c+"");
           } catch (SQLException ex) {
              System.out.println("Error contando");
           }

           
       }
    }
    @FXML
     private void categoryClick(Event event) {
         HBox h = (HBox)event.getSource();
         int id=0;
         if(h!=all){
            id = Integer.parseInt(h.getId());
         }
         category=id;
         all.getStyleClass().remove("selected");          
         for(int i=1;i<=5;i++){
           HBox hb = (HBox)screen.lookup("#"+i);
           hb.getStyleClass().remove("selected");
           
         }
         h.getStyleClass().add("selected");
        try {
            mostrarRecetas();
        } catch (SQLException ex) {
            System.out.println("Error");
        }
         
     }

     
        

}
