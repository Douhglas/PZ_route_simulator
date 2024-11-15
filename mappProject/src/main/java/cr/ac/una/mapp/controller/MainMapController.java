package cr.ac.una.mapp.controller;

import cr.ac.una.mapp.model.Arista; 
import cr.ac.una.mapp.model.Carro;
import cr.ac.una.mapp.model.Grafo;
import cr.ac.una.mapp.model.Vertice;
import cr.ac.una.mapp.util.AppContext;
import cr.ac.una.mapp.util.AppManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Button btnInfo;
    @FXML
    private ImageView mapaImg;
    @FXML
    private Spinner<?> spinnerTrafico;
    @FXML
    private CheckBox checkBoxCerrado;

    @FXML
    private CheckBox cbAccidente;

    @FXML
    private ComboBox<String> cbCarril;
     private double xOffset = 0;
    private double yOffset = 0;

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

    @FXML
    private ToggleButton toggleOcultarPuntos;

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
          //  agregarBarraDeTitulo();
 
    }

    @Override
    public void initialize() {
        inicializarOnActions();


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

    private void inicializarOnActions() {
        minimizeButton.setOnAction(event -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        maximizeButton.setOnAction(event -> {
            Stage stage = (Stage) maximizeButton.getScene().getWindow();
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });

        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
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
        root.getChildren().add(circle);

        circle.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (click == 0) {
                    origen = (Vertice) circle.getUserData();
                    origenDjikstra = origen.getId();
                    click++;
                    System.out.println("Nodo origen seleccionado: " + origen.getId());
                    resaltarAristasAdyacentes(origen);
                } else if (click == 1 && origen != (Vertice) circle.getUserData()) {
                    destino = (Vertice) circle.getUserData();
                    destinoDjikstra = destino.getId();
                    click = 0;
                    System.out.println("Nodo destino seleccionado: " + destino.getId());

                    Arista aristaSeleccionada = seleccionarAristaEntreOrigenYDestino(origen, destino);

                    if (aristaSeleccionada != null) {
                        ocultarAristasAdyacentes(origen, aristaSeleccionada);
                    }
                }
            }
        });

        circle.setOnMouseEntered(e -> circle.setRadius(7));
        circle.setOnMouseExited(e -> circle.setRadius(3));
    }


    private void resaltarAristasAdyacentes(Vertice origen) {
        int origenId = origen.getId();

        List<Arista> aristasOrigen = grafo.matrizAdyacencia.get(origenId);

        for (Arista arista : aristasOrigen) {
            if (arista != null && arista.getOrigen().getId().equals(origen.getId())) {
                Line line = encontrarLineaPorArista(arista);

                if (line != null) {
                    line.setStroke(Color.YELLOW);
                    line.setStrokeWidth(3);
                    line.setVisible(true);
                }
            }
        }
    }


    private void ocultarAristasAdyacentes(Vertice origen, Arista aristaSeleccionada) {
        int origenId = origen.getId();
        List<Arista> aristasOrigen = grafo.matrizAdyacencia.get(origenId);

        for (Arista arista : aristasOrigen) {
            if (arista != null && arista.getOrigen().equals(origen) && !arista.equals(aristaSeleccionada)) {
                Line line = encontrarLineaPorArista(arista);

                if (line != null) {
                    line.setStroke(Color.TRANSPARENT);
                    line.setVisible(false);
                }
            }
        }
    }

    private Arista seleccionarAristaEntreOrigenYDestino(Vertice origen, Vertice destino) {
        int origenId = origen.getId();
        int destinoId = destino.getId();

        Arista arista = grafo.matrizAdyacencia.get(origenId).get(destinoId);

        if (arista != null) {
            Line line = encontrarLineaPorArista(arista);
            if (line != null) {
                line.setStroke(Color.YELLOW);
                line.setStrokeWidth(4);
                line.setVisible(true);
                System.out.println("Arista seleccionada entre " + origenId + " y " + destinoId);

                lineaSeleccionada = line;

                for (Line otherLine : lineas) {
                    if (otherLine != line) {
                        otherLine.setStroke(Color.TRANSPARENT);
                        otherLine.setVisible(false);
                    }
                }
            }
        } else {
            System.out.println("No se encontró una arista entre " + origenId + " y " + destinoId);
            lineaSeleccionada = null;

            for (Line line : lineas) {
                line.setStroke(Color.TRANSPARENT);
                line.setVisible(false);
            }
        }

        return arista;
    }

    private Line encontrarLineaPorArista(Arista arista) {
        for (Line line : lineas) {
            //System.out.println(line.getUserData());
            if (arista.equals(line.getUserData())) {
                return line;
            }
        }
        return null;
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

    public void mostrarRutasDeVertice(Vertice verticeSeleccionado) {
        Stage stage = new Stage();
        stage.setTitle("Rutas desde el vértice " + verticeSeleccionado.getId());

        // Contenedor principal
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        ListView<Arista> listaRutas = new ListView<>();
        listaRutas.getItems().addAll(verticeSeleccionado.getAristas());
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

        Label labelSeleccionada = new Label("Selecciona una ruta para editar:");
        CheckBox checkBoxCerrado = new CheckBox("Calle cerrada");
        Spinner<Integer> spinnerTrafico = new Spinner<>(1, 3, 1);
        spinnerTrafico.setEditable(false);

        listaRutas.getSelectionModel().selectedItemProperty().addListener((obs, oldRuta, nuevaRuta) -> {
            if (nuevaRuta != null) {
                checkBoxCerrado.setSelected(nuevaRuta.getIsClosed());
                spinnerTrafico.getValueFactory().setValue(nuevaRuta.getNivelTrafico());
                nuevaRuta.setPeso(nuevaRuta.getLongitud() * nuevaRuta.getNivelTrafico());
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
        clearPath();

        for (Arista arista : aristas) {
            Line line = new Line();
            line.setStrokeWidth(4);
            line.setStartX(arista.getOrigen().getX());
            line.setStartY(arista.getOrigen().getY());
            line.setEndX(arista.getDestino().getX());
            line.setEndY(arista.getDestino().getY());


            switch (arista.getNivelTrafico()) {
                case 1:
                    line.setStroke(Color.BLUE);
                    break;
                case 2:
                    line.setStroke(Color.YELLOW);
                    break;
                case 3:
                    line.setStroke(Color.RED);
                    break;
                default:
                    line.setStroke(Color.GRAY);
                    break;
            }
            line.setUserData(arista);
            root.getChildren().add(2, line);
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
        if (caminoAristas == null || caminoAristas.isEmpty()) {
            System.out.println("No se encontró un camino entre " + origenDjikstra + " y " + destinoDjikstra);
            return;
        }

        if (caminoAristas == null) {
            System.out.println("No existe camino debido a calles cerradas.");
            mostrarAlertaNoHayCamino();
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
            System.out.println("No existe camino debido a calles cerradas.");
           // mostrarAlertaNoHayCamino();
        } else {
            drawPath(camino);
            carro.setOrigen(origen.getId());
            carro.setDestino(destino.getId());
            carro.IniciarRecorrido(camino);
        }
    }

    private void mostrarAlertaNoHayCamino() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ruta no disponible");
        alert.setHeaderText(null);
        alert.setContentText("No hay una ruta disponible entre los puntos seleccionados debido a calles cerradas.");
        alert.showAndWait();
    }


    @FXML
    void onActionNuevaRuta(ActionEvent event) {
        System.out.println("Iniciando limpieza para nueva ruta...");

        clearPath();

        carro.limpiarRecorrido();
        origen = null;
        destino = null;

        System.out.println("Limpieza completada. Listo para definir una nueva ruta.");
    }


    @FXML
    void onActionGuardarCambios(ActionEvent event) {
        if (lineaSeleccionada == null) {
            System.out.println("No hay una arista seleccionada para modificar.");
            return;
        }

        Arista aristaSeleccionada = (Arista) lineaSeleccionada.getUserData();

        System.out.println("Arista antes: " + aristaSeleccionada);

        int nuevoNivelTrafico = (int) spinnerTrafico.getValue();
        boolean isClosed = checkBoxCerrado.isSelected();
        boolean isAccidente = cbAccidente.isSelected();

        aristaSeleccionada.setLongitud(1000);
        aristaSeleccionada.setNivelTrafico(nuevoNivelTrafico);
        aristaSeleccionada.setIsClosed(isClosed || isAccidente);
        aristaSeleccionada.setPeso(aristaSeleccionada.getLongitud() * nuevoNivelTrafico);

        int idOrigen = aristaSeleccionada.getOrigen().getId();
        int idDestino = aristaSeleccionada.getDestino().getId();
        grafo.matrizAdyacencia.get(idOrigen).set(idDestino, aristaSeleccionada);

        for (Line line : lineasRuta) {
            Arista aristaLinea = (Arista) line.getUserData();
            if (aristaLinea != null
                    && aristaLinea.getOrigen().getId() == aristaSeleccionada.getOrigen().getId()
                    && aristaLinea.getDestino().getId() == aristaSeleccionada.getDestino().getId()) {

                if (aristaSeleccionada.getIsClosed()) {
                    line.setStroke(Color.BLACK);
                } else {
                    switch (aristaSeleccionada.getNivelTrafico()) {
                        case 1:
                            line.setStroke(Color.BLUE);
                            break;
                        case 2:
                            line.setStroke(Color.YELLOW);
                            break;
                        case 3:
                            line.setStroke(Color.RED);
                            break;
                        default:
                            line.setStroke(Color.GRAY);
                            break;
                    }
                }
                break;
            }
        }

        lineaSeleccionada.setStroke(Color.TRANSPARENT);
        lineaSeleccionada = null;

        AppContext.getInstance().set("grafo", grafo);

        System.out.println("Arista modificada: " + aristaSeleccionada);
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


    @FXML
    void onActionOcultarPuntos(ActionEvent event) {
        boolean ocultar = toggleOcultarPuntos.isSelected();
        System.out.println(ocultar ? "Ocultando puntos del mapa..." : "Mostrando puntos del mapa...");

        for (Circle punto : circulos) {
            punto.setVisible(!ocultar);
        }
    }

    @FXML
    void onMouseClickedMapa(MouseEvent event) {

    }




}
