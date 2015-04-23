# Recetario

Recetario programado en Java y con soporte de base de datos H2 con capacidad para gestión mediante categorías, filtrado por nombre y dificultad, subida de imágenes para pasos e ingredientes de media local o vídeos externos (YouTube) con un diseño basado en la simplicidad, la iconografía y la facilidad en la edición de todas las opciones.

![Screenshot](http://i.imgur.com/CbQu3cg.png "Optional title")


![Screenshot](http://i.imgur.com/0oGY4AH.png "Optional title")



La aplicación está estructurada con el esquema Modelo-Vista-Controlador (MVC), por lo tanto, en el proyecto encontraremos las rutas definidas como:

| Ruta                 | Clases                                                  | Uso                                                                                                                                | Extiende a... |
|----------------------|---------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|---------------|
| recetario            | Recetario.java                                          | Hilo principal que ejecuta el GUI y gestiona las ventanas.                                                                         | Application   |
| recetario.view       | Explicacion.fxml, Ingrediente.fxml, Inicio.fxml...      | FXML con la disposición gráfica de las vistas.                                                                                     | FXML          |
| recetario.controller | Explicacion.java, Inicio.java, Visor.java               | Controladores que se aplican a las vistas para la gestión de sus elementos, eventos....                                            | Controlador   |
| recetario.model      | Category.java, Ingrediente.java, Paso.java, Receta.java | Modelos básicos conectados a la base de datos, que son rellenados con los datos de ésta y usados en los controladores pertinentes. | -             |

# 1. Flujo de ejecución en hilo principal

Cuando el main() contenido en Recetario.java se ejecuta, se procesan diversas tareas cruciales para el comportamiento normal de la aplicación:

**1. Creación del Stage principal y apertura de ventana**
```
        this.stage = stage;
        Controlador c = abrirVentana("Inicio", "Recetario");
```

**2. Conexión a la base de datos H2, creación (si no existen) de las tablas, y conexión con el lector y su modelo**
```
    connectionSource = new JdbcConnectionSource(DATABASE_NAME);
```
```
	categoryDao = DaoManager.createDao(connectionSource, Category.class);
```
```
	TableUtils.createTableIfNotExists(connectionSource, Category.class);
```

**3. abrirVentana() conecta ésta clase principal con el nuevo controlador de la pantalla a mostrar**
```

    public Controlador abrirVentana(String viewName, String title){
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/"+viewName+".fxml"));
        Parent root = null;
        Stage stage = new Stage();
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {}
        Controlador controller = loader.getController();
        controller.app=this;
```

**4. Una vez conectado, avisamos al controlador de que está listo para funcionar**  
```
    c.ready();
```

# 2. Flujo de ejecución en los controladores de las vistas cargadas

**1. Extender a Controlador para tener el atributo "app" antes conectado así como el método ready() para recibir el aviso por parte del hilo principal**
```
    public class Inicio extends Controlador 
```

**2. Realizar los accesos y consultas a la base de datos en el método start, ya que será solo en ese momento cuando podamos acceder a los conectores (categoryDao, recetaDao...) definidos en el hilo principal.**
```
    List<Category> categories = this.app.categoryDao.queryForAll();
```
**3. Para incluir el ArrayList de la respuesta de la DB en la mayoría de objetos de JavaFX es necesario convertirlos en ObvervableList**
``` 
    ObservableList<Category> categories_l = 
    FXCollections.observableArrayList(categories);

```

# 3. Funcionamiento del renderizado de ObservableList en objetos JavaFX
Cuando aplicamos los ObservableList con métodos como .setList() y demás, estamos insertando los valores con tipos genéricos (Receta, Categoria...). Por lo tanto, a la hora de mostrarlos en el objeto (ListView, ComboBox...) JavaFX necesita una especificación visual. 
Para conseguirlo, podemos definir Vistas de bloques concretos como las celdas o los "items" que estos objetos mostrarán, por tal de **no generarlos mediantes código**, ya que es una práctica visualmente complicada.
Por ello, definimos los trozos o bloques que hemos dicho con nombres tales como : Celda.fxml, Paso.fxml, Instruccion.fxml... que **no van vinculados a un modelo**, pero nos son igualmente útiles a la hora de **inyectarlos** como si de una celda normal se tratase.
El método general que "dibuja" o "renderiza" estos bloques está contenido en los objetos de JavaFX y suele recibir el nombre de updateItem(), por lo que es llamado cada vez y para cada item que necesite renderizar. 
``` 
 //Estilo de la lista de pasos
        pasosList.setCellFactory((list) -> {
            return new ListCell<Paso>() {
                @Override
                protected void updateItem(Paso item, boolean empty) {
                    if(!empty){
                        try {
                            super.updateItem(item, empty);
 ``` 
 Cargamos mediante el gestor de FXML la celda o bloque que queremos inyectar
 ``` 
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/recetario/view/Paso.fxml"));
         Parent root = (Parent) loader.load();
```
Y modificamos los atributos de los elementos que contenga buscándolos con la orden lookup sobre el layout principal.
``` 
         Label number = (Label)root.lookup("#number");
         number.setText(item.getOrder()+".");
``` 
Por último, no olvidemos que pueden mandarnos renderizar una posicion de celda que no contenga item alguno, también debemos decirle cómo comportarse. En este caso, monstrando un simple VBox vacío.
``` 
         }else{
             VBox v = new VBox();
             setGraphic(v);
         }
``` 












