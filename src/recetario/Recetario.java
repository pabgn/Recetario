package recetario;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import recetario.controller.Controlador;
import recetario.model.Category;
import recetario.model.Ingrediente;
import recetario.model.Receta;

public class Recetario extends Application {
    private final static String DATABASE_NAME = "jdbc:h2:file:./data/recetario";
    public Dao<Category, Integer> categoryDao;
    public Dao<Receta, Integer> recetaDao;
    public Dao<Ingrediente, Integer> ingredienteDao;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Inicio.fxml"));
        Parent root = (Parent) loader.load();
        Controlador controller = loader.getController();
        controller.app=this;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        ConnectionSource connectionSource = null;
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_NAME);
          
        } finally {
		if (connectionSource != null) {
		connectionSource.close();
		}
	}
        setupDatabase(connectionSource);
        controller.ready();
    }


    public static void main(String[] args) throws Exception {
	
        launch(args);
    }
    public void setupDatabase(ConnectionSource connectionSource) throws Exception {
	categoryDao = DaoManager.createDao(connectionSource, Category.class);
	recetaDao = DaoManager.createDao(connectionSource, Receta.class);
	ingredienteDao = DaoManager.createDao(connectionSource, Ingrediente.class);
        
        TableUtils.createTableIfNotExists(connectionSource, Receta.class);
        TableUtils.createTableIfNotExists(connectionSource, Category.class);
        TableUtils.createTableIfNotExists(connectionSource, Ingrediente.class);
        
    }
    
}
