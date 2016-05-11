package list;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import list.logIn.LogInController;

import java.io.IOException;

public class Main extends Application
{
    private static final double PREFERRED_WIDTH = 700;
    private static final double PREFERRED_HEIGHT = 430;

    private Stage primaryStage;
    private DataService service;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        service = new DataService();

        primaryStage.setOnCloseRequest(event -> service.save());

        FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("MainView.fxml"));
        Parent mainView = loader.load();
        loader.<MainViewController>getController().init(service);

        primaryStage.setTitle("ListaFX");
        primaryStage.setScene(showPasswordScene(new Scene(mainView, PREFERRED_WIDTH, PREFERRED_HEIGHT)));
        primaryStage.show();
    }

    private Scene showPasswordScene(Scene sceneToSetAfterLogIn) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(LogInController.class.getResource("logIn.fxml"));
        Parent logInView = loader.load();
        loader.<LogInController>getController().init(service, primaryStage, sceneToSetAfterLogIn);

        return new Scene(logInView, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }



    public static void main(String[] args)
    {
        launch(args);
    }
}
