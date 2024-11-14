/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.mapp.model;

import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 *
 * @author Ahab
 */
public class Carro {

    Image carroImage = new Image(getClass().getResourceAsStream("/cr/ac/una/mapp/resources/tank.png"));
    ImageView carroImageView = new ImageView(carroImage);
    int tiempo;
    private Timeline timeline;
    private AnchorPane anchiorPane;

    public Carro(AnchorPane anchorPane) {
        anchiorPane = anchorPane;
        anchorPane.getChildren().add(carroImageView);
        carroImageView.setFitWidth(20);
        carroImageView.setFitHeight(20);
    }

    // El método recibe una Arista y el tiempo de duración de la animación
    public void crearSimulacion(Arista arista, int tiempoAnimacion) {
        // Obtenemos los nodos origen y destino de la arista
        Vertice origen = arista.getOrigen();
        Vertice destino = arista.getDestino();

        // Coordenadas de los nodos origen y destino
        double x1 = origen.getX();
        double y1 = origen.getY();
        double x2 = destino.getX();
        double y2 = destino.getY();
        

        // Establecer el tiempo de animación de la arista
        this.tiempo = arista.getTime(); // Tiempo en el que se realizará la animación
        if (tiempoAnimacion > 0) {
            tiempo = tiempoAnimacion; // Si se pasa un tiempo, se utiliza ese valor
        }

        // Crear una animación que mueva el carro de la posición inicial a la final
        timeline = new Timeline();
        timeline.setCycleCount(1); // Solo una vez

        // Configurar la animación del movimiento
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(tiempo), e -> {
            // Movimiento del carro de (x1, y1) a (x2, y2)
            carroImageView.setX(x1);
            carroImageView.setY(y1);

            Timeline moveCarro = new Timeline();
            moveCarro.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(tiempo),
                            new javafx.animation.KeyValue(carroImageView.xProperty(), x2),
                            new javafx.animation.KeyValue(carroImageView.yProperty(), y2)
                    ));
            moveCarro.play();
        });


        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        
    }
}
