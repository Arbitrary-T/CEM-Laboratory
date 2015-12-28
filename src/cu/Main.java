package cu;

import cu.models.CardListener;
import cu.models.Student;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application
{
    private Thread cardListenerThread;

    //maybe change to context..?
    public static Student currentStudent;

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
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
