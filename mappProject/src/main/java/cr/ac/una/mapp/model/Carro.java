/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.mapp.model;

import java.util.List;

import cr.ac.una.mapp.controller.MainMapController;
import cr.ac.una.mapp.util.AppContext;
import java.util.ArrayList;
import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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

    private double costoTotalTiempo = 0;
    private double costoTotalPeso = 0;
    private static final double COSTO_POR_SEGUNDO = 0.5;
    private static final double COSTO_POR_PESO = 1.0;


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

    public void crearSimulacion(Arista arista, int tiempoBaseAnimacion) {
        // Obtener nivel de tráfico directamente de la arista
        int nivelDeTrafico = arista.getNivelTrafico();
        int tiempoAnimacion = tiempoBaseAnimacion * nivelDeTrafico;

        System.out.println("Tiempo de animación ajustado por tráfico: " + tiempoAnimacion + " segundos (Nivel de tráfico: " + nivelDeTrafico + ")");

        // Calcular costos
        double costoPeso = arista.getPeso() * COSTO_POR_PESO;
        costoTotalPeso += costoPeso;

        double costoTiempo = tiempoAnimacion * COSTO_POR_SEGUNDO;
        costoTotalTiempo += costoTiempo;

        System.out.println("Costo actual por peso: " + costoPeso + ". Costo total acumulado por peso: " + costoTotalPeso);
        System.out.println("Costo actual por tiempo: " + costoTiempo + ". Costo total acumulado por tiempo: " + costoTotalTiempo);

        caminoRecorrido.add(arista);
        anchorPane.getChildren().remove(carroImageView);

        // Crear línea de ruta
        Line ruta = new Line();
        ruta.setStartX(arista.getOrigen().getX());
        ruta.setStartY(arista.getOrigen().getY());
        ruta.setEndX(arista.getDestino().getX());
        ruta.setEndY(arista.getDestino().getY());
        siguienteNodo = arista.getDestino().getId();

        rotarCarro(arista);

        // Dibujar rastro
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

        anchorPane.getChildren().add(carroImageView);
        pathTransition.play();

        pathTransition.setOnFinished(event -> {
            System.out.println("Evento de transición finalizado. Verificando estado...");
            System.out.println("Origen actual: " + origen + ", Destino final: " + destino);

            if (origen.equals(destino)) {
                System.out.println("El vehículo ha llegado al destino. Mostrando costo final...");
                mostrarCostoFinal();
                return;
            }

            List<Arista> camino;
            origen = siguienteNodo;

            System.out.println("Intentando encontrar un camino desde el nodo: " + origen + " al nodo destino: " + destino);

            if (grafo.isUsingDijkstra) {
                camino = grafo.dijkstra(origen, destino);
            } else {
                camino = grafo.floydWarshall(origen, destino);
            }

            if (camino == null || camino.isEmpty() || camino.stream().allMatch(Arista::getIsClosed)) {
                if (origen.equals(destino)) {
                    System.out.println("El vehículo ya está en el destino. Mostrando costo final...");
                    mostrarCostoFinal();
                    return;
                }

                System.out.println("No se encontró un camino válido. Deteniendo para esperar...");
                esperarYReintentar(15, tiempoBaseAnimacion);
                return;
            }

            Arista siguienteArista = camino.stream().filter(ar -> !ar.getIsClosed()).findFirst().orElse(null);

            if (siguienteArista == null) {
                if (origen.equals(destino)) {
                    System.out.println("El vehículo ya está en el destino. Mostrando costo final...");
                    mostrarCostoFinal();
                    return;
                }

                System.out.println("No se encontró una arista abierta desde el nodo actual. Deteniendo para esperar...");
                esperarYReintentar(15, tiempoBaseAnimacion);
                return;
            }

            System.out.println("Ruta encontrada: De " + siguienteArista.getOrigen().getId() + " a " + siguienteArista.getDestino().getId());
            crearSimulacion(siguienteArista, tiempoBaseAnimacion);
        });
    }


    private void mostrarAlertaNoHayCamino() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ruta no disponible");
            alert.setHeaderText(null);
            alert.setContentText("No hay un camino disponible debido a calles cerradas. Intentando regresar...");
            alert.showAndWait();
        });
    }

    private void intentarDesdeNodosAnteriores(int tiempoAnimacion) {

        if (origen == destino) {
            System.out.println("El vehículo ya está en el destino. Mostrando costo final...");
            mostrarCostoFinal();
            return;
        }

        while (!caminoRecorrido.isEmpty()) {
            Arista ultimaArista = caminoRecorrido.remove(caminoRecorrido.size() - 1);
            origen = ultimaArista.getOrigen().getId();

            System.out.println("Retrocediendo al nodo: " + origen);

            List<Arista> nuevoCamino;
            if (grafo.isUsingDijkstra) {
                nuevoCamino = grafo.dijkstra(origen, destino);
            } else {
                nuevoCamino = grafo.floydWarshall(origen, destino);
            }

            if (nuevoCamino != null && !nuevoCamino.isEmpty() && nuevoCamino.stream().anyMatch(ar -> !ar.getIsClosed())) {
                System.out.println("Nuevo camino encontrado desde el nodo: " + origen);
                Arista siguienteArista = nuevoCamino.stream().filter(ar -> !ar.getIsClosed()).findFirst().orElse(null);

                if (siguienteArista != null) {
                    crearSimulacion(siguienteArista, tiempoAnimacion);
                    return;
                }
            } else {
                System.out.println("No se encontró un camino desde el nodo: " + origen);
            }
        }

        mostrarAlertaNoHayCamino();
        mostrarCostoFinal();
    }

    private void esperarYReintentar(int tiempoDeEspera, int tiempoAnimacion) {
        System.out.println("El vehículo está detenido. Esperando que se habilite alguna carretera...");

        new Thread(() -> {
            int tiempoTranscurrido = 0;
            boolean caminoHabilitado = false;

            while (tiempoTranscurrido < tiempoDeEspera && !caminoHabilitado) {
                try {
                    Thread.sleep(1000);
                    tiempoTranscurrido++;
                    System.out.println("Tiempo transcurrido en espera: " + tiempoTranscurrido + " segundos.");

                    List<Arista> camino;
                    if (grafo.isUsingDijkstra) {
                        camino = grafo.dijkstra(origen, destino);
                    } else {
                        camino = grafo.floydWarshall(origen, destino);
                    }

                    if (camino != null && camino.stream().anyMatch(ar -> !ar.getIsClosed())) {
                        caminoHabilitado = true;
                        System.out.println("Se habilitó una carretera. Reanudando el viaje...");
                        Arista siguienteArista = camino.stream().filter(ar -> !ar.getIsClosed()).findFirst().orElse(null);
                        if (siguienteArista != null) {
                            Platform.runLater(() -> crearSimulacion(siguienteArista, tiempoAnimacion));
                        }
                        return;
                    }
                } catch (InterruptedException e) {
                    System.err.println("Error en la espera: " + e.getMessage());
                }
            }

            if (!caminoHabilitado) {
                System.out.println("No se habilitó ninguna carretera después de " + tiempoDeEspera + " segundos. Iniciando retroceso...");
                Platform.runLater(() -> intentarDesdeNodosAnteriores(tiempoAnimacion));
            }
        }).start();
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

    private void mostrarCostoFinal() {
        double costoTotal = costoTotalTiempo + costoTotalPeso;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Costo del recorrido");
            alert.setHeaderText("Recorrido finalizado");
            alert.setContentText(
                            "Costo total del recorrido: " + costoTotal
            );
            alert.showAndWait();
        });

        costoTotalTiempo = 0;
        costoTotalPeso = 0;
    }


}
