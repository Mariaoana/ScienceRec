
package sciencerec;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScienceRecMain extends Application {
    private static ScienceRecMain instance = null;
    private Stage primaryStage = null;
    
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ScienceRec");
        this.primaryStage.setResizable(false);
        setScene("LogIn.fxml");
    }
    
    public static ScienceRecMain getInstance() {
        return instance;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void setScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            primaryStage.show();
        } catch (IOException ioex) {
            System.out.println("Error setting the scene.");
            ioex.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

