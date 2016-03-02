package cu;

import cu.models.CardListener;
import cu.models.Student;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class Main extends Application
{
    private Thread cardListenerThread;

    //maybe change to context..?
    public static Student currentStudent;
    //private final String scannerPrefix = "././";
    //private final String scannerSuffix = ".\\.\\";
    //private String keyInput = "";
    GlobalKeyboardHook keyboardHook;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        primaryStage.setTitle("CU CEM Laboratory Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> keyboardHook.shutdownHook());
        keyboardHook = new GlobalKeyboardHook();
        keyboardHook.addKeyListener(new GlobalKeyAdapter()
        {
            @Override
            public void keyPressed(GlobalKeyEvent event)
            {
                System.out.println(event.getKeyChar());
            }
        });


        cardListenerThread = new Thread(new CardListener());
        cardListenerThread.setDaemon(true);
        cardListenerThread.start();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}

/*
 primaryStage.getScene().setOnKeyPressed(event ->
        {
            if(event.getCode().equals(KeyCode.ENTER))
            {
                if(keyInput.startsWith(scannerPrefix) && keyInput.endsWith(scannerSuffix))
                {
                    System.out.println(keyInput + "-----------------SUCCESSSSS");
                    keyInput = "";
                }
                else
                {
                    System.out.println("Buffer cleared");
                    keyInput = "";
                }
            }
            else
            {
                keyInput+=event.getText();
                System.out.println(event.getText() + "    ----------------------    ||||||       "+keyInput);
            }
        });
 */