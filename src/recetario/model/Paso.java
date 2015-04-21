package recetario.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.image.Image;

@DatabaseTable(tableName="pasos") 
public class Paso {
    @DatabaseField(generatedId= true) 
    private int id;
    
    
    public static final String NAME_FIELD_DESCRIPTION = "description";
    @DatabaseField(columnName = NAME_FIELD_DESCRIPTION, canBeNull = false)
    private String description;
    
    public static final String NAME_FIELD_ORDER = "order";
    @DatabaseField(columnName = NAME_FIELD_ORDER, canBeNull = false)
    private int order;
    
    public static final String NAME_FIELD_MEDIA = "media";
    @DatabaseField(columnName = NAME_FIELD_MEDIA, canBeNull = true)
    private String media;
    
    public static final String NAME_FIELD_RECETA = "receta_id";
    @DatabaseField(canBeNull = true, foreign = true)
    private Receta receta;
    
    public Paso(String description, int order, Receta r){
        this.description=description;
        this.order=order;
        this.receta=r;
        this.media="";
    }
    Paso(){
        
    }
    public int getId(){
        return this.id;
    }
    public int getOrder(){
        return this.order;
    }
    public String getDescription(){
        return this.description;
    }
    public String getMedia(){
        return this.media;
    }
    
    public void setOrder(int order){
        this.order = order;
    }
    
    public Image loadMediaImage(){
        Image img;
        try{
            img = new Image("file:./data/images/"+this.media);
        } catch(Exception e){
            img = new Image("/resources/comida.jpg");
        } 
        return img;      
    }
    public String getYoutubeId(String url){
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        String id=null;
        if(matcher.find()){
            id= matcher.group();
        }
        return id;
    }
    public Image loadYoutubeImage(){
        Image img;
        String id = this.getYoutubeId(this.getMedia());
        try{
            img = new Image("http://img.youtube.com/vi/"+id+"/0.jpg");
        } catch(Exception e){
            img = new Image("/resources/comida.jpg");
        } 
        return img;   
    }
    public void setDescription(String d){
        this.description=d;
    }
    public void setMedia(String m){
        this.media=m;
    }
    
}
