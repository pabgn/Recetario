package recetario.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


@DatabaseTable(tableName = "recetas")

public class Receta {
    
    @DatabaseField(generatedId = true)
    private int id;
    
    public static final String NAME_FIELD_NAME = "name";
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
    private String name;

    public static final String NAME_FIELD_IMAGEN = "imagen";
    @DatabaseField(columnName = NAME_FIELD_IMAGEN, canBeNull = true)
    private String imagen;

    public static final String NAME_FIELD_TIME = "time";
    @DatabaseField(columnName = NAME_FIELD_TIME, canBeNull = true)
    private int time;
    
    public static final String NAME_FIELD_PEOPLE = "people";
    @DatabaseField(columnName = NAME_FIELD_PEOPLE, canBeNull = true)
    private int people;
    
    public static final String NAME_FIELD_LEVEL = "level";
    @DatabaseField(columnName = NAME_FIELD_LEVEL, canBeNull = true)
    private int level;
    
    public static final String NAME_FIELD_CATEGORY = "category_id";
    @DatabaseField(canBeNull = true, foreign = true)
    private Category category;
    
    public static final String NAME_FIELD_RATING = "rating";
    @DatabaseField(columnName = NAME_FIELD_RATING, canBeNull = true)
    private int rating;
   
    public boolean nueva;
    Receta(){
       
    }
    public Receta(boolean n){
        if(n){
            this.name="";
            this.nueva=true;
        }
    }
    public String getName() {
        return this.name;
    }
     public Image getImage(){
        Image img;
        try{
            img = new Image("file:./data/images/"+this.imagen);
        }catch(Exception e){
            img = new Image("resources/chef.jpg");            
        }
        return img;
    }
    public Category getCategory(){
        return this.category;
    }
    public int getTime(){
        return this.time;
        
    }
    public int getPeople(){
        return this.people;
    }
    public int getPlainLevel(){
        return this.level;
    }
    
    public String getLevel(){
        if(this.level==1){ return "green"; }
        if(this.level==2){ return "yellow"; }
        return "red";
    }

    public int getId() {
        return this.id;
    }
    
    public void setName(String n){
        this.name = n;
    }
    
    public void setTime(int t){
        this.time = t;
    }
    
    public void setPeople(int p){
        this.people = p;
    }
    
    public void setImage(String i){
        this.imagen = i;
    }
    public void setCategory(Category c){
        this.category=c;
    }
    public void setLevel(int l){
        this.level=l;
    }
    
    public Image loadImage(){
        Image img;
        try{
            img = new Image("file:./data/images/"+this.imagen);
        } catch(Exception e){
            img = new Image("/resources/comida.jpg");
        } 
        return img;      
    }
}
