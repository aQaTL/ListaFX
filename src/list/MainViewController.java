package list;

import javafx.concurrent.Task;
import javafx.event.Event;
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
import list.entry.EntryAddListener;
import list.entry.ListEntry;
import list.entry.MyScoreEnum;
import list.entry.MyStatusEnum;
import list.search.SearchController;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class MainViewController
{
	private DataService service;
	private String currentTabId;

	public SplitPane splitPane;
	//Left side components
	@FXML
	private ListView<ListEntry> entriesList;
	@FXML
	private Tab allTab;
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

			Stage searchWindow = new Stage();

			Parent parent = loader.load();
			loader.<SearchController>getController().init(service, new EntryAddListenerImpl(searchWindow));

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
		if(entriesList.getSelectionModel().getSelectedItem() == null)
			return;

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

	@FXML
	private void tabChanged(Event event)
	{
		String tabId = ((Tab) event.getSource()).getId();

		if(!tabId.equals(currentTabId) && service != null)
		{
			currentTabId = tabId;
			loadEntriesWithFilter();
		}
	}

	private void loadEntriesWithFilter()
	{
		ArrayList<ListEntry> filteredEntries = new ArrayList<>(service.getEntries().size());
		entriesList.getItems().clear();

		switch (currentTabId)
		{
			case "allTab":
			{
				entriesList.getItems().addAll(service.getEntries());
				break;
			}
			case "watchingTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.WATCHING)
						filteredEntries.add(entry);
				});
				entriesList.getItems().addAll(filteredEntries);

				break;
			}
			case "completedTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.COMPLETED)
						filteredEntries.add(entry);
				});
				entriesList.getItems().addAll(filteredEntries);

				break;
			}
			case "onHoldTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.ONHOLD)
						filteredEntries.add(entry);
				});
				entriesList.getItems().addAll(filteredEntries);

				break;
			}
			case "droppedTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.DROPPED)
						filteredEntries.add(entry);
				});
				entriesList.getItems().addAll(filteredEntries);

				break;
			}
			case "planToWatchTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.PLANTOWATCH)
						filteredEntries.add(entry);
				});
				entriesList.getItems().addAll(filteredEntries);

				break;
			}
		}

		entriesList.getSelectionModel().selectFirst();
		updateEntryDetails();
	}

	private class EntryAddListenerImpl implements EntryAddListener
	{
		Stage searchWindow;

		private EntryAddListenerImpl(Stage stage)
		{
			searchWindow = stage;
		}

		@Override
		public void entryAdded(ListEntry entry)
		{
			entriesList.getItems().add(entry);
			service.getEntries().add(entry);
			searchWindow.close();
		}
	}
}
