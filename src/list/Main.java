package list;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import list.logIn.LogInController;

public class Main extends Application
{
    public static final double PREFERRED_WIDTH = 700;
    public static final double PREFERRED_HEIGHT = 430;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(LogInController.class.getResource("logIn.fxml"));
        Parent logInView = loader.load();
        loader.<LogInController>getController().init(primaryStage);

        primaryStage.setTitle("ListaFX");
        primaryStage.setScene(new Scene(logInView, PREFERRED_WIDTH, PREFERRED_HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
