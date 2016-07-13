package list;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import list.entry.ListEntry;
import list.entry.MyScoreEnum;
import list.entry.MyStatusEnum;
import list.search.SearchController;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

		Task<Boolean> task = new Task<Boolean>()
		{
			@Override
			protected Boolean call() throws Exception
			{
				return service.updateEntryToMAL(entryToUpdate);
			}
		};
		task.setOnSucceeded(workerState ->
		{
			try
			{
				if (task.get())
					System.out.println("Updated successfully!");
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		});
		new Thread(task).start();
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
		try
		{
			FXMLLoader loader = new FXMLLoader(SearchController.class.getResource("SearchView.fxml"));

			Parent parent = loader.load();
			loader.<SearchController>getController().init(service);

			Stage searchWindow = new Stage();
			searchWindow.setTitle("Search for anime");
			searchWindow.setScene(new Scene(parent));
			searchWindow.showAndWait();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
			Task<Boolean> deleteEntryTask = new Task<Boolean>()
			{
				@Override
				protected Boolean call() throws Exception
				{
					return service.deleteEntryFromMAL(entriesList.getSelectionModel().getSelectedItem().getSeriesDataBaseID());
				}
			};

			deleteEntryTask.setOnSucceeded(workerState ->
			{
				try
				{
					if (deleteEntryTask.get())
					{
						service.getEntries().remove(entriesList.getSelectionModel().getSelectedItem());
						entriesList.getItems().remove(entriesList.getSelectionModel().getSelectedItem());
					}
				}
				catch (InterruptedException | ExecutionException e)
				{
					e.printStackTrace();
				}
			});

			new Thread(deleteEntryTask).start();
		}
	}

	@FXML
	private void showCustomWebsiteDialog()
	{
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Set custom website");
		dialog.setHeaderText("Set custom website for " + entriesList.getSelectionModel().getSelectedItem().getSeriesTitle());

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(url ->
		{
			try
			{
				URL customWebsite = new URL(url);
				ListEntry entry = entriesList.getSelectionModel().getSelectedItem();
				entry.setWebsite(customWebsite);
				service.addCustomWebiste(entry, customWebsite);
			}
			catch (MalformedURLException e)
			{
				showCustomWebsiteDialog();
			}
		});
	}
}
