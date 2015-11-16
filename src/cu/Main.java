package cu;

import cu.listeners.CardListener;
import cu.models.Student;
import cu.models.StudentDatabase;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application
{
    Thread cardListenerThread;
    public static Student currentStudent;
    public static StudentDatabase studentDatabase;
    public static boolean isRegistrationWindowOpen = false;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        primaryStage.setTitle("CU CEM Laboratory Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        cardListenerThread = new Thread(new CardListener());
        cardListenerThread.setDaemon(true);
        cardListenerThread.start();
        studentDatabase = new StudentDatabase("students");
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
