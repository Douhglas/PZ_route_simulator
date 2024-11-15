package cr.ac.una.mapp.controller;

import cr.ac.una.mapp.model.Arista; 
import cr.ac.una.mapp.model.Carro;
import cr.ac.una.mapp.model.Grafo;
import cr.ac.una.mapp.model.Vertice;
import cr.ac.una.mapp.util.AppContext;
import cr.ac.una.mapp.util.AppManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private Button btnInfo;
    @FXML
    private ImageView mapaImg;
    @FXML
    private Spinner<?> spinnerTrafico;
    @FXML
    private CheckBox checkBoxCerrado;

    private List<Line> lineasRuta = new ArrayList<>();

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
    private Carro carro = new Carro(root);
    private List<Arista> caminoActual;
    private List<List<Arista>> caminosRecorridos; 
    @FXML
    private Button btnFloyd;

    @FXML
    private Button btnDijkstra;

    int origenDjikstra = 0;

    int destinoDjikstra = 0;

    int origenFloyd = 0;

    int destinoFloyd = 0;

    Color color;

    Arista aristaSeleccionada;

    private Line lineaSeleccionada = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void initialize() {
        //elimianr lo que haya limpiar todo y despues cargar los nodos
        origen = new Vertice();
        carro.setAnchorPane(root);

        grafo = AppManager.getInstance().cargar();
        AppContext.getInstance().set("grafo", grafo);
        carro.setGrafo(grafo);
        
        if (grafo != null && !grafo.getVertices().isEmpty()) {
           for(Vertice vertice : grafo.getVertices()){
               colocarCirculo(vertice);
           }
           for(Arista arista : grafo.getAristas()){
               drawLine(arista, Color.RED);

           }
        } else {
            System.out.println("Grafo nulo");
        }

        mapaImg.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Line lineaCercana = obtenerLineaCercana(event.getX(), event.getY());

            if (lineaCercana != null) {
                seleccionarLinea(lineaCercana);
            }
        });


    }

    private void seleccionarLinea(Line linea) {
        if (lineaSeleccionada != null) {
            // Desselecciona la línea previamente seleccionada
            lineaSeleccionada.setStroke(Color.TRANSPARENT);
        }

        lineaSeleccionada = linea;

        if (lineaSeleccionada != null) {
            lineaSeleccionada.setStroke(Color.YELLOW);
            lineaSeleccionada.toFront();
        }
    }


    private void colocarCirculo(Vertice vertice) {

        Circle circle = new Circle(vertice.getX(), vertice.getY(), 3);
        circle.setFill(Color.ALICEBLUE);
        circle.setStroke(Color.AQUA);

        circle.setUserData(vertice);
        circulos.add(circle);
        System.out.println("se creo un circulo ");

        root.getChildren().add(circle);

        circle.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (click == 0) {
                    
                    origen = (Vertice) circle.getUserData();
                    origenDjikstra = origen.getId();
                    origenFloyd = origen.getId();
                    click++;
                    System.out.println("click en nodo : " + origen.getId());

                } else if (click == 1 && origen != (Vertice) circle.getUserData()) {
                    destino = (Vertice) circle.getUserData();
                    destinoDjikstra = destino.getId();
                    destinoFloyd = destino.getId();
                    click = 0;
//                    List<Arista> camino = grafo.floydWarshall(origen.getId(), destino.getId());
//                    if (camino == null) {
//                        System.out.println("No existe camino");
//                    } else {
//                        drawPath(camino);
////                        carro.setOrigen(origen.getId());
////                        carro.setDestino(destino.getId());
////                        carro.crearSimulacion(camino.get(0), 3);
//                    }

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

    private void drawLine(Arista arista, Paint color) {

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

        line.setStroke(color);
        line.setStrokeWidth(2);
        line.setUserData(arista);

        lineas.add(line);
        drawArrow(line);
        root.getChildren().add(line);
    }

    private void drawArrow(Line line) {
        if (lineaAnterior != null) {
            lineaAnterior.setStroke(Color.TRANSPARENT);

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
            if (v.equals(verticeBuscado)) {
                return v;
            }
        }
        return null;
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
                drawLine(nuevaRuta, Color.CADETBLUE);
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

    public void drawPath(List<Arista> aristas) {
        clearPath(); // Limpia solo la lista lineasRuta
        for (Arista arista : aristas) {
            Line line = new Line();
            line.setStrokeWidth(4);
            line.setStartX(arista.getOrigen().getX());
            line.setStartY(arista.getOrigen().getY());
            line.setEndX(arista.getDestino().getX());
            line.setEndY(arista.getDestino().getY());
            line.setStroke(Color.BLUE);

            root.getChildren().add(line);
            lineasRuta.add(line);
        }
    }


    public void clearPath() {
        for (Line line : lineasRuta) {
            root.getChildren().remove(line);
        }
        lineasRuta.clear();
    }



    @FXML
    void onActionCalcularDjikstra(ActionEvent event) {
        grafo.isUsingDijkstra = true;
        List<Arista> caminoAristas = grafo.dijkstra(origenDjikstra, destinoDjikstra);
        System.out.println("Dijkstra");
        if (caminoAristas == null) {
            System.out.println("No existe camino ");
        } else {
            drawPath(caminoAristas);
            carro.setOrigen(origen.getId());
            carro.setDestino(destino.getId());
            carro.IniciarRecorrido(caminoAristas);
        }

    }

    @FXML
    void onActionCalcularFloyd(ActionEvent event) {
        grafo.isUsingDijkstra = false;
        List<Arista> camino = grafo.floydWarshall(origen.getId(), destino.getId());
        System.out.println("Floyd");
        if (camino == null) {
            System.out.println("No existe camino");
        } else {
            drawPath(camino);
            carro.setOrigen(origen.getId());
            carro.setDestino(destino.getId());
            carro.IniciarRecorrido(camino);

            AppContext.getInstance().set("caminoInicial", camino);
        }
    }

   /* private void verificarYActualizarRuta(Vertice actual, Vertice destino) {
        if (condicionesCambiadas()) {
            List<Integer> nuevaRuta = grafo.dijkstra(actual.getId(), destino.getId());
            List<Arista> nuevaRutaAristas = grafo.crearCamino(nuevaRuta);

            clearPath();
            drawPath(nuevaRutaAristas);

            setRutaParaMovimiento(nuevaRutaAristas);

        }
    }*/

    @FXML
    void onActionAbrirInfo(ActionEvent event) {
        System.out.println("Cambiando...");

        for (Arista arista : grafo.getAristas()) {
            System.out.println("Aristas antes: " + arista.getPeso());
        }
        grafo.mostrarMatrizAdyacenciaActual();
        grafo.matrizAdyacencia.get(1).get(2).setPeso(2000);
        for (Arista arista : grafo.getAristas()) {
            System.out.println("Aristas después: " + arista.getPeso());
        }
        grafo.mostrarMatrizAdyacenciaActual();
        AppContext.getInstance().set("grafo", grafo);
    }

    @FXML
    void onActionGuardarCambios(ActionEvent event) {
        if (lineaSeleccionada == null) {
            System.out.println("No hay una arista seleccionada para modificar.");
            return;
        }



        Arista aristaSeleccionada = (Arista) lineaSeleccionada.getUserData();

        System.out.println("Arista antes: " + aristaSeleccionada);
        grafo.mostrarMatrizAdyacenciaActual();

        int nuevoNivelTrafico = (int) spinnerTrafico.getValue();
        boolean isClosed = checkBoxCerrado.isSelected();

        aristaSeleccionada.setLongitud(1000);
        aristaSeleccionada.setNivelTrafico(nuevoNivelTrafico);
        aristaSeleccionada.setIsClosed(isClosed);
        aristaSeleccionada.setPeso(aristaSeleccionada.getLongitud() * nuevoNivelTrafico);

        int idOrigen = aristaSeleccionada.getOrigen().getId();
        int idDestino = aristaSeleccionada.getDestino().getId();
        grafo.matrizAdyacencia.get(idOrigen).set(idDestino, aristaSeleccionada);

        System.out.println("Arista modificada: " + aristaSeleccionada);
        grafo.mostrarMatrizAdyacenciaActual();

        lineaSeleccionada.setStroke(Color.TRANSPARENT);
        lineaSeleccionada = null;

        AppContext.getInstance().set("grafo", grafo);
    }


    private Line obtenerLineaCercana(double clickX, double clickY) {
        Line lineaCercana = null;
        double distanciaMinima = 15;

        for (Line line : lineas) {
            double distancia = distanciaPuntoSegmento(clickX, clickY, line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                lineaCercana = line;
            }
        }
        return lineaCercana;
    }

    private double distanciaPuntoSegmento(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            dx = px - x1;
            dy = py - y1;
            return Math.sqrt(dx * dx + dy * dy);
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double proyX = x1 + t * dx;
        double proyY = y1 + t * dy;

        dx = px - proyX;
        dy = py - proyY;
        return Math.sqrt(dx * dx + dy * dy);
    }



}
