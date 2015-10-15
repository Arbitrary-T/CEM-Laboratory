package cu.gui;

import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import javafx.


public class Main extends Application implements CardInterface
{
    Thread cardListenerThread;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        /*primaryStage.setOnCloseRequest((WindowEvent event) ->
        {
            Platform.exit();
            System.exit(0);
        });*/
        primaryStage.show();
        CardListener.activateAgent(this);
        cardListenerThread = new Thread(new CardListener());
        cardListenerThread.setDaemon(true);
        cardListenerThread.start();
    }


    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void cardDetected(String cardUID)
    {
        System.out.println("HERE FROM MAIN: " + cardUID);
    }
}
