/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.mapp.model;

import java.util.List;

import cr.ac.una.mapp.controller.MainMapController;
import cr.ac.una.mapp.util.AppContext;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

    private AnchorPane anchorPane;
    private PathTransition pathTransition;
    private Grafo grafo;

    private Integer origen;
    private Integer siguienteNodo;
    private Integer destino;

    private List<Arista> caminoOriginal;
    private List<Arista> caminoRecorrido;

    public Carro(AnchorPane anchorPane) {
        caminoOriginal =  new ArrayList<>();
        caminoRecorrido =  new ArrayList<>();
        this.anchorPane = anchorPane;
        carroImageView.setFitWidth(40);
        carroImageView.setFitHeight(35);
    }

    public void IniciarRecorrido(List<Arista> camino) {
        caminoOriginal.clear();
        caminoRecorrido.clear();
        caminoOriginal = camino;
        crearSimulacion(camino.get(0), 3);
    }

    public void crearSimulacion(Arista arista, int tiempoAnimacion) {
        caminoRecorrido.add(arista);
        anchorPane.getChildren().remove(carroImageView);

        Line ruta = new Line();
        ruta.setStartX(arista.getOrigen().getX());
        ruta.setStartY(arista.getOrigen().getY());
        ruta.setEndX(arista.getDestino().getX());
        ruta.setEndY(arista.getDestino().getY());
        siguienteNodo = arista.getDestino().getId();

        rotarCarro(arista);

        Line rastro = new Line();
        rastro.setStroke(Color.TRANSPARENT);
        rastro.setStrokeWidth(2);
        rastro.setStartX(ruta.getStartX());
        rastro.setStartY(ruta.getStartY());
        anchorPane.getChildren().add(rastro);

        Transition rastroAnimation = new Transition() {
            {
                setCycleDuration(Duration.seconds(tiempoAnimacion));
            }

            @Override
            protected void interpolate(double frac) {
               
                double x = ruta.getStartX() + frac * (ruta.getEndX() - ruta.getStartX());
                double y = ruta.getStartY() + frac * (ruta.getEndY() - ruta.getStartY());
                rastro.setEndX(x);
                rastro.setEndY(y);
                rastro.setStroke(Color.ORANGERED);
            }
        };
        rastroAnimation.play();
        

        pathTransition = new PathTransition();
        pathTransition.setNode(carroImageView);
        pathTransition.setPath(ruta);
        pathTransition.setDuration(Duration.seconds(tiempoAnimacion));
        pathTransition.setCycleCount(1);

        // Agregar el carro al AnchorPane
        anchorPane.getChildren().add(carroImageView);
        pathTransition.play();

        // Cuando finalice la transición
        pathTransition.setOnFinished(event -> {
            if (origen != destino) {
                List<Arista> camino;
                origen = siguienteNodo;
                if (grafo.isUsingDijkstra) {
                    camino = grafo.dijkstra(origen, destino);
                } else {
                    camino = grafo.floydWarshall(origen, destino);
                }
                if (camino == null) {
                    return;
                }
                crearSimulacion(camino.get(0), tiempoAnimacion);
            }
        });
    }

    public void rotarCarro(Arista arista) {
        double deltaX = arista.getDestino().getX() - arista.getOrigen().getX();
        double deltaY = arista.getDestino().getY() - arista.getOrigen().getY();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        carroImageView.setRotate(angle);
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public PathTransition getPathTransition() {
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
