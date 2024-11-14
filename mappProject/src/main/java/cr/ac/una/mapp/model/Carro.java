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

    Image carroImage = new Image(getClass().getResourceAsStream("/cr/ac/una/mapp/resources/tank.png"));
    ImageView carroImageView = new ImageView(carroImage);
    int tiempo;
    private Timeline timeline;
    private AnchorPane anchorPane;
    private PathTransition pathTransition;
    private Grafo grafo;
    
    private Integer origen;
    private Integer siguienteNodo;
    private Integer destino;

    public Carro(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        //
        carroImageView.setFitWidth(20);
        carroImageView.setFitHeight(20);
    }

    public void mostrarCarro() {
        // Crear un cuadrado para representar el vehículo
        Rectangle cuadrado = new Rectangle(20, 20); // Tamaño del cuadrado
        cuadrado.setFill(Color.BLACK);
    }

    public void crearSimulacion(Arista arista, int tiempoAnimacion) {
        anchorPane.getChildren().remove(carroImageView);

        // Crear línea de la arista
        Line ruta = new Line();
        ruta.setStartX(arista.getOrigen().getX());
        ruta.setStartY(arista.getOrigen().getY());
        ruta.setEndX(arista.getDestino().getX());
        ruta.setEndY(arista.getDestino().getY());
        siguienteNodo = arista.getDestino().getId();

        // Configurar la transición para mover el carro a lo largo de la línea
        pathTransition = new PathTransition();
        pathTransition.setNode(carroImageView);
        pathTransition.setPath(ruta);
        pathTransition.setDuration(Duration.seconds(tiempoAnimacion));
        pathTransition.setCycleCount(1);

        // Agregar el carro al AnchorPane
        anchorPane.getChildren().add(carroImageView);
        pathTransition.play();
        pathTransition.setOnFinished(event -> {
           if(origen != destino){
               origen = siguienteNodo;
               
               List<Arista> camino = grafo.floydWarshall(origen, destino);
               if(camino == null){
                   return;
               }
               crearSimulacion(camino.get(0), 4);
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
