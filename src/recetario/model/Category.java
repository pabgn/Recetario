package recetario.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "categories")
public class Category {
@DatabaseField(generatedId = true)
private int id;
    
    public static final String NAME_FIELD_NAME = "name";
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
    private String name;
    
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }


}
