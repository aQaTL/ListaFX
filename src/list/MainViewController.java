package list;

import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainViewController
{
	private DataService service;

	public SplitPane splitPane;
	//Left side components
	@FXML
	private ListView<ListEntry> entriesList;
	//Right side components
	@FXML
	private Label seriesTitleLabel;
	@FXML
	private ChoiceBox<String> myScoreBox;
	@FXML
	private Label progressLabel;
	@FXML
	private Spinner episodeSpinner;
	@FXML
	private ChoiceBox<MyStatusEnum> mySeriesStatusBox;
	@FXML
	private ImageView seriesImageView;
	@FXML
	private Button websiteButton;

	public void init(DataService service)
	{
		this.service = service;

		episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
		mySeriesStatusBox.getItems().addAll(MyStatusEnum.values());

		entriesList.getItems().addAll(service.getEntries());
		entriesList.getSelectionModel().selectFirst();
		updateEntryDetails();
	}

	@FXML
	public void updateEntryDetails()
	{
		ListEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();

		if (selectedEntry != null)
		{
			seriesTitleLabel.setText(selectedEntry.getSeriesTitle());
			myScoreBox.getSelectionModel().select(selectedEntry.getMyScore());
			episodeSpinner.getEditor().setText(Integer.toString(selectedEntry.getMyWatchedEpisodes()));
			mySeriesStatusBox.getSelectionModel().select(selectedEntry.getMyStatus());
			seriesImageView.setImage(selectedEntry.getSeriesImage());
		}
	}

	@FXML
	private void openWebsite()
	{
		try
		{
			Desktop.getDesktop().browse(entriesList.getSelectionModel().getSelectedItem().getWebsite().toURI());
		}
		catch (IOException | URISyntaxException e)
		{
			System.err.println("Website couldn't be opened!");

			Alert error = new Alert(Alert.AlertType.ERROR);
			error.setHeaderText(null);
			error.setContentText("Website couldn't be opened! Check address.");
			error.showAndWait();
		}
	}

	@FXML
	private void showAboutDialog()
	{
		System.err.println("Not implemented yet");
		//TODO About dialog
	}

	/**
	 * Removes everything that isn't a number from episodeSpinner editor
	 */
	@FXML
	private void checkSpinner(KeyEvent event)
	{
		try
		{
			entriesList.getSelectionModel().getSelectedItem().setSeriesEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
		}
		catch (NumberFormatException e)
		{
			episodeSpinner.getEditor().setText(episodeSpinner.getEditor().getText().replace(event.getText(), ""));
		}
	}

	@FXML
	private void addEntry()
	{
		//Searching for new entry
	}


	@FXML
	private void deleteEntry()
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("Deleting " + entriesList.getSelectionModel().getSelectedItem().getSeriesTitle() + " from list");
		alert.setContentText("Are you sure?");
		alert.setTitle("Confirm");
		alert.showAndWait();

		if (alert.getResult().getButtonData().isDefaultButton())
		{
			service.deleteEntryFromMAL(entriesList.getSelectionModel().getSelectedItem().getSeriesDataBaseID());
			service.getEntries().remove(entriesList.getSelectionModel().getSelectedItem());
			entriesList.getItems().remove(entriesList.getSelectionModel().getSelectedItem());
		}
	}
}
