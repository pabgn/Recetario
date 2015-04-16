
package recetario.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.scene.image.Image;

@DatabaseTable(tableName="ingredientes") 
public class Ingrediente {
    @DatabaseField(generatedId= true) 
    private int id;
    
    public static final String NAME_FIELD_NAME = "name";
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
    private String name;
    
    public static final String NAME_FIELD_ID_RECETA = "id_receta";
    @DatabaseField(canBeNull = true, foreign = true)
    private Receta id_receta;
    
    public static final String NAME_FIELD_CANTIDAD = "cantidad";
    @DatabaseField(columnName= NAME_FIELD_CANTIDAD, canBeNull = true)
    private String cantidad;
    
    public static final String NAME_FIELD_IMAGEN = "imagen";
    @DatabaseField(columnName = NAME_FIELD_IMAGEN, canBeNull = true)
    private String imagen;
    
    public String getName(){
        return this.name;
    }
    
    public String getCantidad(){
        return this.cantidad;
    }
    
    public Image getImagen(){
        Image img;
        try{
            img = new Image("/resources/"+this.imagen);
        } catch(Exception e){
            img = new Image("/resources/comida.jpg");
        } 
        return img;      
    }
    
    public Receta getReceta(){
        return this.id_receta;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setCantidad(String cantidad){
        this.cantidad = cantidad;
    }
    
    public void setImage(String img){
        this.imagen = img;
    }
    
    public void setIdReceta(Receta receta){
        this.id_receta = receta;
    }
}
