package cr.ac.una.mapp; 
import cr.ac.una.mapp.util.FlowController;
import javafx.application.Application;
import javafx.stage.Stage; 
/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
      FlowController.getInstance().InitializeFlow(stage, null);
      FlowController.getInstance().goViewInWindow("secundaryView");
 
    }

    public static void main(String[] args) {
        launch();
    }

}