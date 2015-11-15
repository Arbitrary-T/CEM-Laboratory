package cu;

import cu.controllers.tabs.MainTabViewController;
import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import cu.models.StudentDatabase;
import javafx.application.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application
{
    Thread cardListenerThread;

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
        StudentDatabase studentDatabase = new StudentDatabase("students");

    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
