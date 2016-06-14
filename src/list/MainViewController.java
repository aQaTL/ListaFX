package list;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import list.entry.ListEntry;
import list.entry.MyScoreEnum;
import list.entry.MyStatusEnum;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

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
	private ChoiceBox<MyScoreEnum> myScoreBox;
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

	/**
	 * Initializes this controller
	 */
	public void init(DataService service)
	{
		this.service = service;

		episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
		myScoreBox.getItems().addAll(MyScoreEnum.values());
		mySeriesStatusBox.getItems().addAll(MyStatusEnum.values());

		mySeriesStatusBox.setOnAction(event ->
		{
			if (mySeriesStatusBox.getSelectionModel().getSelectedItem() == MyStatusEnum.COMPLETED)
			{
				episodeSpinner.setDisable(true);
				episodeSpinner.getEditor().setText(Integer.toString(entriesList.getSelectionModel().getSelectedItem().getSeriesEpisodes()));
			}
			else
			{
				episodeSpinner.setDisable(false);
			}
		});

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

			if (selectedEntry.getMyStatus() == MyStatusEnum.COMPLETED)
				episodeSpinner.setDisable(true);
			else
				episodeSpinner.setDisable(false);
		}
	}

	@FXML
	private void updateEntryRemotely()
	{
		ListEntry entryToUpdate = entriesList.getSelectionModel().getSelectedItem();
		entryToUpdate.setMyWatchedEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
		entryToUpdate.setMyStatus(mySeriesStatusBox.getSelectionModel().getSelectedItem());
		entryToUpdate.setMyScore(myScoreBox.getSelectionModel().getSelectedItem());

		if (service.updateEntryToMAL(entryToUpdate))
		{
			System.out.println("Updated successfully.");
		}
		else
		{
			System.err.println("Couldn't update");
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
			entriesList.getSelectionModel().getSelectedItem().setMyWatchedEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
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
