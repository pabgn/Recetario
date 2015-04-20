
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
    
    public static final String NAME_FIELD_RECETA = "receta_id";
    @DatabaseField(canBeNull = true, foreign = true)
    private Receta receta;
    
    public static final String NAME_FIELD_CANTIDAD = "cantidad";
    @DatabaseField(columnName= NAME_FIELD_CANTIDAD, canBeNull = true)
    private String cantidad;
    
    public static final String NAME_FIELD_IMAGEN = "image";
    @DatabaseField(columnName = NAME_FIELD_IMAGEN, canBeNull = true)
    private String image;
    public Ingrediente(){
        
    }
    public String getName(){
        return this.name;
    }
    
    public String getCantidad(){
        return this.cantidad;
    }
    
    public String getImage(){
        return this.image;
    }
    public Image loadImage(){
        Image img;
        try{
            img = new Image("/resources/"+this.image);
        } catch(Exception e){
            img = new Image("/resources/comida.jpg");
        } 
        return img;      
    }
    
    public Receta getReceta(){
        return this.receta;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setCantidad(String cantidad){
        this.cantidad = cantidad;
    }
    
    public void setImage(String img){
        this.image = img;
    }
    

}
