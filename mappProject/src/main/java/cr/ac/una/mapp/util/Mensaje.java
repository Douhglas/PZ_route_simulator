package cr.ac.una.mapp.util;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import javafx.util.Duration;

public class Mensaje {

    public static void show(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public void showModal(AlertType tipo, String titulo, Window padre, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.initOwner(padre);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void showConfirmation(String titulo, Window padre, String mensaje, BooleanProperty girar) {
        Platform.runLater(()->{
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.initOwner(padre);
        alert.setContentText(mensaje);
        Optional<ButtonType> result = alert.showAndWait();
        girar.set(result.get() == ButtonType.OK);
        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(e-> alert.close());
        });
    }
  public boolean showConfirmation2(String titulo, Window padre, String mensaje) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    Platform.runLater(() -> {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.initOwner(padre);
        alert.setContentText(mensaje);
        Optional<ButtonType> result = alert.showAndWait();
        future.complete(result.isPresent() && result.get() == ButtonType.OK);
    });

    try {
        return future.get();  // Wait for the future to complete and get the result
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        return false;
    }
}
}
