/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.mapp.model;

import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Ahab
 */
public class Carro {

    Image carroImage = new Image(getClass().getResourceAsStream("/cr/ac/una/mapp/resources/Car.png"));
    ImageView carroImageView = new ImageView(carroImage);
    int tiempo;
    private AnchorPane anchorPane;
    private PathTransition pathTransition;
    private Grafo grafo;
    
    private Integer origen;
    private Integer siguienteNodo;
    private Integer destino;

    public Carro(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        carroImageView.setFitWidth(40);
        carroImageView.setFitHeight(35);
    }

    public void crearSimulacion(Arista arista, int tiempoAnimacion) {
        anchorPane.getChildren().remove(carroImageView);

        // Configurar la línea de la arista
        Line ruta = new Line();
        ruta.setStartX(arista.getOrigen().getX());
        ruta.setStartY(arista.getOrigen().getY());
        ruta.setEndX(arista.getDestino().getX());
        ruta.setEndY(arista.getDestino().getY());
        siguienteNodo = arista.getDestino().getId();

        // Calcular el ángulo de rotación para que el carro apunte hacia el siguiente nodo
        double deltaX = arista.getDestino().getX() - arista.getOrigen().getX();
        double deltaY = arista.getDestino().getY() - arista.getOrigen().getY();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Aplicar rotación a la imagen del carro
        carroImageView.setRotate(angle);

        // Configurar la transición para mover el carro a lo largo de la línea
        pathTransition = new PathTransition();
        pathTransition.setNode(carroImageView);
        pathTransition.setPath(ruta);
        pathTransition.setDuration(Duration.seconds(tiempoAnimacion));
        pathTransition.setCycleCount(1);

        // Agregar el carro al AnchorPane y reproducir la animación
        anchorPane.getChildren().add(carroImageView);
        pathTransition.play();
        
        // Cuando finalice la transición, decidir el siguiente movimiento
        pathTransition.setOnFinished(event -> {
           if (origen != destino) {
               origen = siguienteNodo;
               List<Arista> camino = grafo.floydWarshall(origen, destino);
               if (camino == null) {
                   return;
               }
               crearSimulacion(camino.get(0), tiempoAnimacion);
           }
        });
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }
    
    public PathTransition getPathTransition(){
        return pathTransition;
    }

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
    }

    public void setOrigen(Integer origen) {
        this.origen = origen;
    }

    public void setDestino(Integer destino) {
        this.destino = destino;
    }
}