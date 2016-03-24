package cu.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainViewController
{
    @FXML
    TabPane tabPane;
    public static int index = 0;

    @FXML
    void initialize()
    {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
        {
            System.out.println(newValue);
            index = newValue.intValue();
        });
    }
}
