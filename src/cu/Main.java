package cu;

import cu.models.listeners.CardListener;
import cu.models.listeners.CodeScannedListener;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application
{

    /**
     * Method from parent class: loads the JavaFX Application
     * @param primaryStage  The GUI window
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        primaryStage.setTitle("CU CEM Laboratory Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Main program entry point, launches two threads for managing the NFC card reader and the QR Code scanner
     * followed by starting the JavaFX application.
     * @param args ...
     */
    public static void main(String[] args)
    {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(new CodeScannedListener());
        exec.submit(new CardListener());
        launch(args);
        exec.shutdownNow();
    }

}