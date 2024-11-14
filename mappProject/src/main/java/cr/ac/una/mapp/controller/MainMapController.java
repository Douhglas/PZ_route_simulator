package cr.ac.una.mapp.controller;

import cr.ac.una.mapp.model.Arista; 
import cr.ac.una.mapp.model.Grafo;
import cr.ac.una.mapp.model.Vertice;
import cr.ac.una.mapp.util.AppManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author stward segura
 */
public class MainMapController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView mapaImg;
    private List<Vertice> vertices = new ArrayList<>();
    private List<Arista> aristas = new ArrayList<>();
    private List<Circle> circulos = new ArrayList<>();
    private List<Line> lineas = new ArrayList<>();
    private Grafo grafo;
    private Vertice origen;
    private Line lineaAnterior;
    private Line arrowA;
    private Line arrowB;
    private Vertice destino;
    private Integer click = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        //elimianr lo que haya limpiar todo y despues cargar los nodos
        origen = new Vertice();
        aristas = AppManager.getInstance().cargar();
        if (aristas != null && !aristas.isEmpty()) {
            for (Arista arista : aristas) {
                // Asignar la arista al destino en la lista de "recibidas"
                Vertice destino1 = verticeExistente(arista.getDestino());
                arista.setPeso(arista.getLongitud() * arista.getNivelTrafico());
                arista.setIsClosed(Boolean.FALSE);
                if (destino1 == null) {
                    vertices.add(arista.getDestino());
                    arista.getDestino().getRecibidas().add(arista);
                } else {
                    destino1.getRecibidas().add(arista);
                }
                Vertice origen1 = verticeExistente(arista.getOrigen());

                if (origen1 == null) {
                    vertices.add(arista.getOrigen());
                    arista.getOrigen().getAristas().add(arista);
                } else {
                    origen1.getAristas().add(arista);
                }

                // Dibujar la línea que representa la arista
                drawLine(arista);
            }
        } else {
            System.out.println("No hay aristas");
        }
        if (vertices != null && !vertices.isEmpty()) {
            for (Vertice vertice : vertices) {
                colocarCirculo(vertice);
            }
            this.grafo = new Grafo(aristas);
            this.grafo.mostrarMatrizAdyacencia();
        }
 

    }

    private void colocarCirculo(Vertice vertice) {

        Circle circle = new Circle(vertice.getX(), vertice.getY(), 3);
        circle.setFill(Color.ALICEBLUE);
        circle.setStroke(Color.AQUA);

        circle.setUserData(vertice);
        circulos.add(circle);
        System.out.println("se creo un circulo");

        root.getChildren().add(circle);

        circle.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                //seleccionar nodo A y B para la ruta una vez seleccionados empezar ruta segun ruta elegida
                //al seleccionar dos se debe pintar la ruta 
                //e iniciar la animacion
                //cuando se inicia la animacion y llega a un nodo entonces se recalcula la ruta y si da otra entonces marcarla con otro color
                //en el grafo y mostrarla
                System.out.println("click en circulo");
                if (click == 0) {
                    origen = (Vertice) circle.getUserData();
                    click++;

                } else if (click == 1 && origen != (Vertice) circle.getUserData()) {
                    destino = (Vertice) circle.getUserData();
                    click = 0;
                   List<Vertice> camino = grafo.dijkstra(origen.getId(), destino.getId());
                    drawPath(camino);
                    for(Vertice c : camino){
                     System.out.println(c.getId() + " ");
                    }
                  
                    //calcular ruta y dibujarla
                    //ventana de empezar recorrido elegir algoritmo y mas 
                    //animacion
                }

            } else if (e.getButton() == MouseButton.SECONDARY) {
                //change color and other things maybe
                mostrarRutasDeVertice((Vertice) circle.getUserData());
            }
        });
        circle.setOnMouseEntered(e -> {
            circle.setRadius(7);
        });

        circle.setOnMouseExited(e -> {
            circle.setRadius(3);
        });
    }

    private void drawLine(Arista arista) {

        double offset = 10;

        double startX = arista.getOrigen().getX();
        double startY = arista.getOrigen().getY();
        double endX = arista.getDestino().getX();
        double endY = arista.getDestino().getY();

        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double scaleX = (deltaX / length) * offset;
        double scaleY = (deltaY / length) * offset;

        Line line = new Line(startX + scaleX, startY + scaleY, endX - scaleX, endY - scaleY);

        line.setStroke(Color.CADETBLUE);
        line.setStrokeWidth(2);
        line.setUserData(arista);

        lineas.add(line);
        drawArrow(line);
        root.getChildren().add(line);
    }

//    private Circle obtenerCirculoDesdeVertice(Vertice vertice) {
//        for (Circle circle : circulos) {
//            Vertice v = (Vertice) circle.getUserData();
//            if (v.equals(vertice)) {
//                return circle;
//            }
//        }
//        return null;
//    }
//
//    private void efectoDeLinea(Line line, Circle origenCircle, Circle destinoCircle, MouseEvent event) {
//        if (event.getButton() == MouseButton.PRIMARY) {
//
//            if (origenCircle != null) {
//                origenCircle.setRadius(7);
//            }
//            if (destinoCircle != null) {
//                destinoCircle.setRadius(7);
//            }
//            //seleccionada hacer algo para agreagar accidentes o demas
//            line.setStroke(Color.RED);
//        } else if (event.getButton() == MouseButton.SECONDARY) {
//
//            if (origenCircle != null) {
//                origenCircle.setRadius(3);
//            }
//            if (destinoCircle != null) {
//                destinoCircle.setRadius(3);
//            }
//
//            line.setStroke(Color.BLACK);
//        }
//    }
    private void drawArrow(Line line) {
        if (lineaAnterior != null) {
            lineaAnterior.setStroke(Color.TRANSPARENT); // Volver a color original

            arrowA.setStroke(Color.TRANSPARENT);
            arrowB.setStroke(Color.TRANSPARENT);

        }
        double startX = line.getStartX();
        double startY = line.getStartY();
        double endX = line.getEndX();
        double endY = line.getEndY();

        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double angle = Math.atan2(deltaY, deltaX);

        double arrowLength = 10;
        double arrowWidth = 3;

        double x1 = endX - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = endY - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = endX - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = endY - arrowLength * Math.sin(angle + Math.PI / 6);

        Line arrow1 = new Line(endX, endY, x1, y1);
        Line arrow2 = new Line(endX, endY, x2, y2);
        arrow1.setStrokeWidth(arrowWidth);
        arrow2.setStrokeWidth(arrowWidth);
        arrow1.setStroke(Color.CADETBLUE);
        arrow2.setStroke(Color.CADETBLUE);

        lineaAnterior = line;
        arrowA = arrow1;
        arrowB = arrow2;
        root.getChildren().addAll(arrow1, arrow2);
    }

    private Vertice verticeExistente(Vertice verticeBuscado) {
        for (Vertice v : vertices) {
            if (v.equals(verticeBuscado)) { // Suponiendo que equals está correctamente sobrecargado
                return v;
            }
        }
        return null;
    }

    public void animarMovimiento(Vertice destino) {
        // Crear un cuadrado para representar el vehículo
        Rectangle cuadrado = new Rectangle(20, 20); // Tamaño del cuadrado
        cuadrado.setFill(Color.BLACK);

        Line ruta = new Line();
        ruta.setStartX(origen.getX());
        ruta.setStartY(origen.getY());
        ruta.setEndX(destino.getX());
        ruta.setEndY(destino.getY());

        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(cuadrado);
        pathTransition.setPath(ruta);
        pathTransition.setDuration(Duration.seconds(3));
        pathTransition.setCycleCount(1);

        root.getChildren().add(cuadrado);

        pathTransition.play();
    }

    public void mostrarRutasDeVertice(Vertice verticeSeleccionado) {
        // Crear ventana nueva para mostrar las rutas
        Stage stage = new Stage();
        stage.setTitle("Rutas desde el vértice " + verticeSeleccionado.getId());

        // Contenedor principal
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        // Crear lista de rutas en un ListView
        ListView<Arista> listaRutas = new ListView<>();
        listaRutas.getItems().addAll(verticeSeleccionado.getAristas()); // Obtener rutas del vértice
        listaRutas.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Arista arista, boolean empty) {
                super.updateItem(arista, empty);
                if (empty || arista == null) {
                    setText(null);
                } else {
                    setText("Ruta a vértice " + arista.getDestino().getId() + " - Peso: " + arista.getPeso());
                }
            }
        });

        // Controles para editar la ruta seleccionada
        Label labelSeleccionada = new Label("Selecciona una ruta para editar:");
        CheckBox checkBoxCerrado = new CheckBox("Calle cerrada");
        Spinner<Integer> spinnerTrafico = new Spinner<>(1, 3, 1);
        spinnerTrafico.setEditable(false);

        // Acción al seleccionar una ruta en la lista
        listaRutas.getSelectionModel().selectedItemProperty().addListener((obs, oldRuta, nuevaRuta) -> {
            if (nuevaRuta != null) {
                // Actualizar valores de los controles con los valores de la arista seleccionada
                checkBoxCerrado.setSelected(nuevaRuta.getIsClosed());
                spinnerTrafico.getValueFactory().setValue(nuevaRuta.getNivelTrafico());
                nuevaRuta.setPeso(nuevaRuta.getLongitud() * nuevaRuta.getNivelTrafico());
                // Resaltar ruta seleccionada en la ventana principal
                drawLine(nuevaRuta);
            }
        });

        // Listener para actualizar si la calle está cerrada
        checkBoxCerrado.selectedProperty().addListener((obs, wasClosed, isClosed) -> {
            Arista seleccionada = listaRutas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                seleccionada.setIsClosed(isClosed);
            }
        });

        // Listener para actualizar nivel de tráfico de la ruta seleccionada
        spinnerTrafico.valueProperty().addListener((obs, oldValue, newValue) -> {
            Arista seleccionada = listaRutas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                seleccionada.setNivelTrafico(newValue);
                seleccionada.setPeso(seleccionada.getLongitud() * seleccionada.getNivelTrafico());
            }
        });

        // Botón para cerrar la ventana
        Button botonCerrar = new Button("Cerrar");
        botonCerrar.setOnAction(e -> stage.close());

        // Agregar todos los controles al VBox
        vbox.getChildren().addAll(listaRutas, labelSeleccionada, checkBoxCerrado, spinnerTrafico, botonCerrar);

        // Configurar y mostrar la escena
        Scene scene = new Scene(vbox, 300, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void drawPath(List<Vertice> vertices) {

        clearPath();
        for (int i = 0; i < vertices.size() - 1; i++) {
            Vertice origen1 = vertices.get(i);
            Vertice destino2 = vertices.get(i + 1);

            Line line = new Line();
            line.setStartX(origen1.getX());
            line.setStartY(origen1.getY());
            line.setEndX(destino2.getX());
            line.setEndY(destino2.getY());
            line.setStroke(Color.BLUE);

            root.getChildren().add(line);
            lineas.add(line);
        }
    }

    public void clearPath() {
        for (Line line : lineas) {
            root.getChildren().remove(line);
        }
        lineas.clear();
    }

}
