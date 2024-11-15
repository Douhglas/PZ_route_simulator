package cr.ac.una.mapp.controller;

import cr.ac.una.mapp.model.Arista;
import cr.ac.una.mapp.model.Grafo;
 
import cr.ac.una.mapp.model.Vertice;
import cr.ac.una.mapp.util.AppManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author stward segura
 */
public class GraphEditorController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView mapaImg;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;
    private List<Circle> circulos = new ArrayList<>();
    private Integer click = 0;
    private Circle origen;
    private Circle destino;
    private List<Line> lineas = new ArrayList<>();
    private List<Vertice> vertices = new ArrayList<>();
    private List<Arista> aristas = new ArrayList<>();
    private int index = 0;
    Grafo grafo =  new Grafo();
   boolean isLoadingData = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaImg.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            colocarCirculo(event.getX(), event.getSceneY());
        });
        inicializarOnActions();
    }

    @Override
    public void initialize() {

    }

    private void inicializarOnActions() {
        minimizeButton.setOnAction(event -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        // Acción para el botón de maximizar/restaurar
        maximizeButton.setOnAction(event -> {
            Stage stage = (Stage) maximizeButton.getScene().getWindow();
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });

        // Acción para el botón de cerrar
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }

    private void colocarCirculo(double x, double y) {
        
        Circle circle = new Circle(x, y, 3);
        circle.setFill(Color.RED);
        circle.setStroke(Color.BLACK);
        Vertice vertice = new Vertice();
        vertice.setId(index);
        vertice.setX(x);
        vertice.setY(y);
        vertices.add(vertice);
        circle.setUserData(vertice);
        circulos.add(circle);
        System.out.println("se creo un circulo");
        if(!isLoadingData){
        grafo.agregarVertice(vertice);
        }
        root.getChildren().add(circle);

        circle.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                crearVinculo(circle, e);

            } else if (e.getButton() == MouseButton.SECONDARY) {
                //change color and other things maybe
                root.getChildren().remove(circle);

                // Remover el círculo de la lista
                vertices.remove(circle.getUserData());
                circulos.remove(circle);
            }
        });
        circle.setOnMouseEntered(e -> {
            circle.setRadius(7);
        });

        circle.setOnMouseExited(e -> {
            circle.setRadius(3);
        });
        index++;
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
        line.setStroke(Color.TRANSPARENT);

        line.setStrokeWidth(2);
        line.setUserData(arista);
        Circle origenCircle = obtenerCirculoDesdeVertice(arista.getOrigen());
        Circle destinoCircle = obtenerCirculoDesdeVertice(arista.getDestino());

        line.setOnMouseClicked(event -> {

            efectoDeLinea(line, origenCircle, destinoCircle, event);

            if (event.getButton() == MouseButton.PRIMARY) {

            } else if (event.getButton() == MouseButton.SECONDARY) {
                root.getChildren().remove(line);

                aristas.remove(line.getUserData());
                lineas.remove(line);
            }

        });
        line.setOnMouseEntered(e -> {
            if (origenCircle != null) {
                origenCircle.setRadius(7);
            }
            if (destinoCircle != null) {
                destinoCircle.setRadius(7);
            }
        });
        line.setOnMouseExited(e -> {
            if (origenCircle != null) {
                origenCircle.setRadius(3);
            }
            if (destinoCircle != null) {
                destinoCircle.setRadius(3);
            }
        });
        lineas.add(line);
        drawArrow(line);
        root.getChildren().add(line);
    }

    private void circleInfo(Circle cir) {
        Vertice ver = (Vertice) cir.getUserData();
        int i = 0;
        for (Arista arista : ver.getAristas()) {
            System.out.println("arista" + Integer.toString(++i));
        }
    }

    private void crearVinculo(Circle circle, MouseEvent e) {
        circleInfo(circle);
        if (click == 0) {
            click++;
            origen = circle;
            System.out.println("origen" + origen.getId());
        } else if (click == 1) {

            destino = circle;
            if (origen != destino) {
                click++;
            }

            System.out.println("destino"  + destino.getId());

        }

        if (click == 2) {

            //agregar la arista y la linea en pantalla
            Arista arista = new Arista();
            Vertice origenV = (Vertice) origen.getUserData();
            Vertice destinoV = (Vertice) destino.getUserData();
            origenV.getAristas().add(arista);//agregando a la lista de aristas del vertice
            destinoV.getRecibidas().add(arista);
            arista.setDestino(destinoV);
            arista.setOrigen(origenV);
            setDistanciaVentana(arista);
            aristas.add(arista);
            drawLine(arista);
            origen = null;
            destino = null;
            click = 0;

        }

        e.consume();
    }

    private Circle obtenerCirculoDesdeVertice(Vertice vertice) {
        for (Circle circle : circulos) {
            Vertice v = (Vertice) circle.getUserData();
            if (v.equals(vertice)) {
                return circle;
            }
        }
        return null;
    }

    private void efectoDeLinea(Line line, Circle origenCircle, Circle destinoCircle, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {

            if (origenCircle != null) {
                origenCircle.setRadius(7);
            }
            if (destinoCircle != null) {
                destinoCircle.setRadius(7);
            }

            line.setStroke(Color.RED);
        } else if (event.getButton() == MouseButton.SECONDARY) {

            if (origenCircle != null) {
                origenCircle.setRadius(5);
            }
            if (destinoCircle != null) {
                destinoCircle.setRadius(5);
            }

            line.setStroke(Color.BLACK);
        }
    }

    public void setDistanciaVentana(Arista arista) {

        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Ingresar Longitud");

        Label label = new Label("Ingrese la longitud de la ruta (en metros):");

        TextField textField = new TextField();
        textField.setPromptText("Longitud en metros");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        Button confirmarButton = new Button("Confirmar");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        confirmarButton.setOnAction(event -> {
            String textoIngresado = textField.getText();
            try {

                Integer longitud = Integer.valueOf(textoIngresado);

                if (longitud <= 0) {
                    throw new NumberFormatException("La longitud debe ser mayor que cero.");
                }
                arista.setLongitud(longitud);
                arista.setPeso(longitud/2);
                grafo.agregarArista(arista);
               
                ventana.close();
            } catch (NumberFormatException e) {

                errorLabel.setText("Por favor ingrese un número válido y positivo.");
            }
        });

        VBox vbox = new VBox(10, label, textField, confirmarButton, errorLabel);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        ventana.setScene(scene);
        ventana.setOnCloseRequest(event -> {
            event.consume();
            errorLabel.setText("Debe ingresar un valor válido para continuar.");
        });

        ventana.showAndWait();
    }

    @FXML
    private void guardarAction(ActionEvent event) {

        AppManager.guardar(grafo);
        System.out.println("Guardado exitoso");
    }

    @FXML
    private void cargarAction(ActionEvent event) {
        vertices.clear();
        circulos.clear();
        lineas.clear();
        aristas.clear();
        grafo = AppManager.getInstance().cargar();
        isLoadingData = true;
        for (Vertice vertice : grafo.getVertices()) {
            colocarCirculo(vertice.getX(), vertice.getY());
        }
        for (Arista arista : grafo.getAristas()) {
            drawLine(arista);
        }
        isLoadingData = false;
    }

    private void drawArrow(Line line) {
        // Coordenadas de la línea
        double startX = line.getStartX();
        double startY = line.getStartY();
        double endX = line.getEndX();
        double endY = line.getEndY();

        // Vector de la línea
        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double angle = Math.atan2(deltaY, deltaX);

        // Tamaño de la flecha
        double arrowLength = 10;
        double arrowWidth = 2;

        // Calcula las posiciones de las puntas de la flecha
        double x1 = endX - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = endY - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = endX - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = endY - arrowLength * Math.sin(angle + Math.PI / 6);

        // Dibuja las dos líneas de la flecha
        Line arrow1 = new Line(endX, endY, x1, y1);
        Line arrow2 = new Line(endX, endY, x2, y2);
        arrow1.setStrokeWidth(arrowWidth);
        arrow2.setStrokeWidth(arrowWidth);
        arrow1.setStroke(Color.BLACK);
        arrow2.setStroke(Color.BLACK);

        arrow1.setOnMouseClicked(event -> {
            //ventana para agregar accidentes,inhabilitar la arista, cantidad de transito, y mas
            //configurar la linea puede ser
             if (event.getButton() == MouseButton.PRIMARY) {
                 Arista linea = (Arista)line.getUserData();
                 System.out.println("linea origen : " + linea.getLongitud() + " Destino : " + linea.getLongitud() );
            } else if (event.getButton() == MouseButton.SECONDARY) {
              Arista linea = (Arista)line.getUserData();
                 System.out.println("linea origen : " + linea.getLongitud() + " Destino : " + linea.getLongitud());
            }
        });
        arrow2.setOnMouseClicked(event -> {
            //ventana para agregar accidentes,inhabilitar la arista, cantidad de transito, y mas
            //configurar la linea puede ser
             if (event.getButton() == MouseButton.PRIMARY) {

            } else if (event.getButton() == MouseButton.SECONDARY) {
                         Arista linea = (Arista)line.getUserData();
                 System.out.println("linea origen : " +linea.getLongitud() + " Destino : " + linea.getLongitud() );
   
            }
        });
        arrow1.setOnMouseEntered(e -> {
            line.setStroke(Color.RED);
            line.setStrokeWidth(3);
            arrow1.setStroke(Color.RED);
            arrow2.setStroke(Color.RED);
            arrow1.setStrokeWidth(4);
            arrow2.setStrokeWidth(4);
        });
        arrow2.setOnMouseEntered(e -> {
            line.setStrokeWidth(3);
            line.setStroke(Color.RED);
            arrow1.setStroke(Color.RED);
            arrow2.setStroke(Color.RED);
            arrow1.setStrokeWidth(4);
            arrow2.setStrokeWidth(4);
        });
        arrow1.setOnMouseExited(e -> {
            line.setStroke(Color.TRANSPARENT);
            line.setStrokeWidth(2);
            arrow1.setStroke(Color.BLACK);
            arrow2.setStroke(Color.BLACK);
            arrow1.setStrokeWidth(3);
            arrow2.setStrokeWidth(3);
        });
        arrow2.setOnMouseExited(e -> {
            line.setStroke(Color.TRANSPARENT);
            line.setStrokeWidth(2);
            arrow1.setStroke(Color.BLACK);
            arrow2.setStroke(Color.BLACK);
            arrow1.setStrokeWidth(3);
            arrow2.setStrokeWidth(3);
        });
        root.getChildren().addAll(arrow1, arrow2);
    }

    public boolean isUniqueVertice(Vertice vertice) {
        if (vertices != null && !vertices.isEmpty()) {
            for (Vertice aux : vertices) {
                if (aux.getX() == vertice.getX() && aux.getY() == vertice.getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    @FXML
    private void mostrarMatriz(ActionEvent event) {
        grafo.mostrarMatrizAdyacenciaActual();
    }
}
