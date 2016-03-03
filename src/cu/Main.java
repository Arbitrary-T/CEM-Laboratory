package cu;

import cu.models.CardListener;
import cu.models.CodeScanner;
import cu.models.Student;
import javafx.application.Application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import org.apache.commons.lang3.StringUtils;

public class Main extends Application
{
    private Thread cardListenerThread;
    private Thread codeScannerThread;

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
        this.primaryStage = primaryStage;

        codeScannerThread = new Thread(new CodeScanner());
        codeScannerThread.setDaemon(true);
        codeScannerThread.start();
        cardListenerThread = new Thread(new CardListener());
        cardListenerThread.setDaemon(true);
        cardListenerThread.start();
    }

    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}