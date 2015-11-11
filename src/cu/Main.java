package cu;

import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application implements CardInterface
{
    Thread cardListenerThread;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        primaryStage.setTitle("CU CEM Laboratory Management");
        primaryStage.setScene(new Scene(root));

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
