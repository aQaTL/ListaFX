package list.logIn;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import list.DataService;
import list.Main;
import list.MainViewController;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import java.io.IOException;

/**
 * Created by Maciej on 2016-05-07.
 */
public class LogInController
{
    Stage stage;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField, userPasswordField;
    @FXML
    Label errorLabel;

    public void init(Stage stage)
    {
        this.stage = stage;
    }

    /**
     * Invoked every time, when user press a key in usernameField or userPasswordField
     */
    @FXML
    private void tryToLogIn(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            try
            {
                String userCredentials = usernameField.getText() + ":" + userPasswordField.getText();
                String encodedLogin = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userCredentials.getBytes());

                Jsoup.connect("http://myanimelist.net/api/account/verify_credentials.xml").header("Authorization", encodedLogin).get();

                DataService service = new DataService(encodedLogin);
                stage.setOnCloseRequest(e -> service.save());

                FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("MainView.fxml"));
                Parent mainView = loader.load();
                loader.<MainViewController>getController().init(service);
                stage.setScene(new Scene(mainView, Main.PREFERRED_WIDTH, Main.PREFERRED_HEIGHT));
            }
            catch (IOException e)
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
