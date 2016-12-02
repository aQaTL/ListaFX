package list.logIn;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.xml.stream.XMLStreamException;
import list.DataService;
import list.MainViewController;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Maciej on 2016-05-07.
 */
public class LogInController
{
	private Stage stage;
	private FXMLLoader loader;
	private Parent mainView;
	private LogInService logInService;
	private DataService service;

	private final URI SIGN_UP_ADDR = URI.create(DataService.MAL_ADDRESS + "register.php");

	@FXML
	TextField usernameField;
	@FXML
	PasswordField userPasswordField;
	@FXML
	Hyperlink signUpLink;
	@FXML
	Label errorLabel;
	@FXML
	ProgressBar progressBar;

	/**
	 * Initializes this controller
	 */
	public void init(Stage stage) throws IOException
	{
		this.stage = stage;
		loader = new FXMLLoader(MainViewController.class.getResource("MainView.fxml"));
		mainView = loader.load();

		initLogInService();
	}

	/**
	 * Initializes logInService
	 */
	private void initLogInService()
	{
		logInService = new LogInService();
		logInService.setOnFailed(workerStateEvent ->
		{
			Throwable e = (Throwable) workerStateEvent.getSource().getException();

			if (e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() == 401)
			{
				progressBar.setVisible(false);
				errorLabel.setVisible(true);
			}
			else if(e instanceof SocketTimeoutException)
			{
				logInService.restart();
			}
			else
				e.printStackTrace();
		});
		logInService.setOnSucceeded(workerStateEvent ->
		{
			service = logInService.getValue();
			loader.<MainViewController>getController().init(service);

			stage.setOnCloseRequest(windowEvent -> showExitWarning(windowEvent));
			stage.setScene(new Scene(mainView, loader.<MainViewController>getController().masterDetailPane.getPrefWidth(), loader.<MainViewController>getController().masterDetailPane.getPrefHeight()));
		});
	}

	/**
	 * Invoked every time, when user press a key in usernameField or userPasswordField
	 */
	@FXML
	private void logIn(KeyEvent event)
	{
		if (event.getCode() == KeyCode.ENTER)
		{
			progressBar.setVisible(true);

			logInService.restart();
		}
		else
		{
			errorLabel.setVisible(false);
		}
	}

	/**
	 * Opens MAL sign up page in default browser
	 */
	@FXML
	private void openSignUpPage(MouseEvent event)
	{
		new Thread(() ->
		{
			try
			{
				Desktop.getDesktop().browse(SIGN_UP_ADDR);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Shows exit confirmation dialog;
	 * if user answers yes, then program
	 * saves his encryptedLogin form DataService
	 * and exits with 0
	 */
	private void showExitWarning(WindowEvent event)
	{
		event.consume();

		Alert warning = new Alert(Alert.AlertType.CONFIRMATION, "Confirm exit", ButtonType.YES, ButtonType.NO);
		warning.setHeaderText("Quit program?");
		warning.setContentText("Are you sure you want to exit?");

		DialogPane alertPane = warning.getDialogPane();
		alertPane.getStylesheets().add(mainView.getStylesheets().get(0));
		alertPane.getStyleClass().add("myAlert");

		warning.showAndWait();

		if (warning.getResult() == ButtonType.YES)
		{
			service.storeUserPrefs();
			System.exit(0);
		}
	}

	/**
	 * Service for validating user credentials and loading DataService
	 */
	private class LogInService extends Service<DataService>
	{
		@Override
		protected Task<DataService> createTask()
		{
			return new Task<DataService>()
			{
				@Override
				protected DataService call() throws IOException, XMLStreamException
				{
					String userCredentials = usernameField.getText() + ":" + userPasswordField.getText();
					String encodedLogin = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userCredentials.getBytes());

					Jsoup.connect("https://myanimelist.net/api/account/verify_credentials.xml").header("Authorization", encodedLogin).get();

					return new DataService(encodedLogin, usernameField.getText());
				}
			};
		}
	}
}
