package recetario.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import recetario.model.Category;
import recetario.model.Receta;

public class Inicio extends Controlador {
    @FXML
    private ListView listView1;
    @FXML
    private ListView listView2; 
    @FXML
    private ComboBox categoryFilter;
    
    public Inicio(){
        super();
    }
    public void mostrarRecetas(int category) throws SQLException{
        List<Receta> recetas1;
        if(category==0){
            //No se ha especificado categoría para filtrar
            recetas1 = this.app.recetaDao.queryForAll();
        }else{
            recetas1 = this.app.recetaDao.queryBuilder().where().eq(Receta.NAME_FIELD_CATEGORY, category).query();
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
            
            listView1.setItems(data1);
            listView2.setItems(data2);
    }
    public void ready(){
        try {
            //Cargamos las categorías de la DB:
            List<Category> categories = this.app.categoryDao.queryForAll();
            ObservableList<Category> categories_l = FXCollections.observableArrayList(categories);
            categoryFilter.setItems(categories_l);
            //Cargamos las recetas de la DB:
            mostrarRecetas(0);
        } catch (SQLException ex) {
            Logger.getLogger(Inicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void initialize() {
        //Listas a aplicar las celdas especiales
        List<ListView> listas = Arrays.asList(listView1, listView2);
        for(ListView l: listas){
            l.setCellFactory(new Callback<ListView<Receta>, 
                ListCell<Receta>>() {
                    @Override 
                    public ListCell<Receta> call(ListView<Receta> list) {
                        return new CeldaReceta();
                    }
                }
            );
        }
        
        categoryFilter.setCellFactory((comboBox) -> {
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
        categoryFilter.setConverter(new StringConverter<Category>() {
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

        categoryFilter.setOnAction((event) -> {
            try {
                Category category = (Category) categoryFilter.getSelectionModel().getSelectedItem();
                mostrarRecetas(category.getId());
            } catch (SQLException ex) {
                System.out.println("Error en búsqueda por categoría");
            }
            
        });
    }

static class CeldaReceta extends ListCell<Receta> {
    @Override
    public void updateItem(Receta item, boolean empty) {
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
                name.setText(item.getName());
                image.setImage(item.getImage());
                time.setText(item.getTime());
                people.setText(item.getPeople());
                level.setFill(Color.web(item.getLevel()));
                setGraphic(root);
                
            } catch (IOException ex) {
                System.out.println("Error abriendo celda");
            }

            }
        }
    }
        

}
