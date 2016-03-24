package cu;

import cu.models.CardListener;
import cu.models.CodeScannerCOM;
import cu.models.Student;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application
{
    //maybe change to context..?
    public static Student currentStudent;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        primaryStage.setTitle("CU CEM Laboratory Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        Main.primaryStage = primaryStage;
    }

    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static void main(String[] args)
    {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(new CodeScannerCOM());
        exec.submit(new CardListener());
        launch(args);
        exec.shutdownNow();
    }

}