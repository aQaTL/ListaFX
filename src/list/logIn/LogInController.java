package list.logIn;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import list.DataService;

/**
 * Created by Maciej on 2016-05-07.
 */
public class LogInController
{
    DataService service;
    Stage stage;
    Scene sceneToSet;

    @FXML
    PasswordField passwordField;
    @FXML
    Label errorLabel;

    public void init(DataService service, Stage stage, Scene sceneToSet)
    {
        this.service = service;
        this.stage = stage;
        this.sceneToSet = sceneToSet;
    }

    @FXML
    private void tryToLogIn(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ENTER)
        {
            if (service.checkPassword(passwordField.getText()))
            {
                stage.setScene(sceneToSet);
            }
            else
            {
                errorLabel.setVisible(true);
            }
        }
        else
        {
            errorLabel.setVisible(false);
        }
    }
}
