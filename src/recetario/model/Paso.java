package recetario.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
    
    public Paso(){
        
    }
    
}
