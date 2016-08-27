package list;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import list.entry.Entry;
import list.entry.EntryEventHandler;
import list.entry.ListEntry;
import list.entry.data.MyScoreEnum;
import list.entry.data.MyStatusEnum;
import list.search.SearchController;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Represents application main window
 */
public class MainViewController
{
	private DataService service;
	private Notifications notify;
	private String currentTabId;

	private ListEntry selectedEntry;

	private String notifyAddMsg = "Entry has been added";
	private String notifyDeleteMsg = "Entry has been deleted";
	private String notifyUpdateMsg = "Entry has been updated";

	public MasterDetailPane masterDetailPane;
	//Left side components
	@FXML
	private GridView<ListEntry> entriesView;
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

		notify = Notifications.create()
				.title("ListaFX")
				.position(Pos.TOP_RIGHT)
				.hideAfter(new Duration(1500));

		episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
		myScoreBox.getItems().addAll(MyScoreEnum.values());
		mySeriesStatusBox.getItems().addAll(MyStatusEnum.values());

		mySeriesStatusBox.setOnAction(event ->
		{
			if (mySeriesStatusBox.getSelectionModel().getSelectedItem() == MyStatusEnum.COMPLETED)
			{
				episodeSpinner.setDisable(true);
				episodeSpinner.getEditor().setText(Integer.toString(selectedEntry.getSeriesEpisodes()));
			}
			else
			{
				episodeSpinner.setDisable(false);
			}
		});

		entriesView.setCellFactory(param -> new ListEntryCell());

		EntryEventHandler showDetailsHandler = entry -> showDetails(entry);

		service.getEntries().forEach(entry ->
		{
			entry.setOnMouseClicked(showDetailsHandler);
			entriesView.getItems().add(entry);
		});

		selectedEntry = entriesView.getItems().get(0);
		updateEntryDetails();
	}

	private void showDetails(Entry entry)
	{
		if (masterDetailPane.isShowDetailNode() && entry == selectedEntry)
		{
			masterDetailPane.setShowDetailNode(false);
			return;
		}
		masterDetailPane.setShowDetailNode(true);

		selectedEntry = (ListEntry) entry;

		updateEntryDetails();
	}

	/**
	 * Refreshes entry details side to match currently selected entry
	 */
	@FXML
	public void updateEntryDetails()
	{
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

	/**
	 * Creates task that updates currently selected entry in user MAL
	 */
	@FXML
	private void updateEntryRemotely()
	{
		ListEntry entryToUpdate = selectedEntry;
		entryToUpdate.setMyWatchedEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
		entryToUpdate.setMyStatus(mySeriesStatusBox.getSelectionModel().getSelectedItem());
		entryToUpdate.setMyScore(myScoreBox.getSelectionModel().getSelectedItem());

		Task<Boolean> task = service.updateEntryToMAL(entryToUpdate);
		task.setOnSucceeded(workerState ->
		{
			try
			{
				if (task.get())
				{
					notify.text(notifyUpdateMsg).showInformation();
				}
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		});
		new Thread(task).start();
	}

	/**
	 * Opens selectedEntry website in default browser
	 */
	@FXML
	private void openWebsite()
	{
		try
		{
			Desktop.getDesktop().browse(selectedEntry.getWebsite().toURI());
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
			selectedEntry.setMyWatchedEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
		}
		catch (NumberFormatException e)
		{
			episodeSpinner.getEditor().setText(episodeSpinner.getEditor().getText().replace(event.getText(), ""));
		}
	}

	/**
	 * Shows SearchView window that allows browse and add entries
	 */
	@FXML
	private void addEntry()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(SearchController.class.getResource("SearchView.fxml"));

			Parent parent = loader.load();
			loader.<SearchController>getController().init(service, new EntryEventHandlerImpl());

			Stage searchWindow = new Stage();
			searchWindow.setTitle("Search for anime");
			searchWindow.setMinWidth(428);
			searchWindow.setMinHeight(540);
			searchWindow.setScene(new Scene(parent));
			searchWindow.showAndWait();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Shows deleting alert, if user confirms, creates task that deletes selected entry from MAL
	 */
	@FXML
	private void deleteEntry()
	{
		if (selectedEntry == null)
			return;

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("Deleting " + selectedEntry.getSeriesTitle() + " from list");
		alert.setContentText("Are you sure?");
		alert.setTitle("Confirm");

		DialogPane alertPane = alert.getDialogPane();
		alertPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
		alertPane.getStyleClass().add("myAlert");

		alert.showAndWait();

		if (alert.getResult().getButtonData().isDefaultButton())
		{
			Task<Boolean> deleteEntryTask = service.deleteEntryFromMAL(selectedEntry.getSeriesDataBaseID());

			deleteEntryTask.setOnSucceeded(workerState ->
			{
				try
				{
					if (deleteEntryTask.get())
					{
						service.getEntries().remove(selectedEntry);
						entriesView.getItems().remove(selectedEntry);

						notify.text(notifyDeleteMsg).showInformation();
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

	/**
	 * Shows dialog that allows to set custom website to selected entry.
	 * Custom websites are stored in app directory in customURLs.dat file.
	 */
	@FXML
	private void showCustomWebsiteDialog()
	{
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Set custom website");
		dialog.setHeaderText("Set custom website for " + selectedEntry.getSeriesTitle());

		DialogPane alertPane = dialog.getDialogPane();
		alertPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
		alertPane.getStyleClass().add("myAlert");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(url ->
		{
			try
			{
				URL customWebsite = new URL(url);
				selectedEntry.setWebsite(customWebsite);
				service.addCustomWebiste(selectedEntry, customWebsite);
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

		if (!tabId.equals(currentTabId) && service != null)
		{
			currentTabId = tabId;
			loadEntriesWithFilter();
		}
	}

	/**
	 * Adjust currently showing entries in entriesView to match currently selected tab
	 * <p>
	 * TODO use ObservableList
	 */
	private void loadEntriesWithFilter()
	{
		ArrayList<ListEntry> filteredEntries = new ArrayList<>(service.getEntries().size());
		entriesView.getItems().clear();

		switch (currentTabId)
		{
			case "allTab":
			{
				entriesView.getItems().addAll(service.getEntries());
				break;
			}
			case "watchingTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.WATCHING)
						filteredEntries.add(entry);
				});
				entriesView.getItems().addAll(filteredEntries);

				break;
			}
			case "completedTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.COMPLETED)
						filteredEntries.add(entry);
				});
				entriesView.getItems().addAll(filteredEntries);

				break;
			}
			case "onHoldTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.ONHOLD)
						filteredEntries.add(entry);
				});
				entriesView.getItems().addAll(filteredEntries);

				break;
			}
			case "droppedTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.DROPPED)
						filteredEntries.add(entry);
				});
				entriesView.getItems().addAll(filteredEntries);

				break;
			}
			case "planToWatchTab":
			{
				service.getEntries().forEach(entry ->
				{
					if (entry.getMyStatus() == MyStatusEnum.PLANTOWATCH)
						filteredEntries.add(entry);
				});
				entriesView.getItems().addAll(filteredEntries);

				break;
			}
		}
		if (entriesView.getItems().size() > 0)
		{
			selectedEntry = entriesView.getItems().get(0);
			updateEntryDetails();
		}
	}

	/**
	 * Class for displaying ListEntry in gridView
	 */
	private class ListEntryCell extends GridCell<ListEntry>
	{
		private ListEntry entry;

		@Override
		protected void updateItem(ListEntry item, boolean empty)
		{
			super.updateItem(item, empty);

			entry = item;
			if (!empty)
			{
				setGraphic(item.getView());
			}
			else
			{
				setGraphic(null);
			}
		}

		public ListEntry getEntry()
		{
			return entry;
		}
	}

	/**
	 * Defines action that is fired after successfully adding new entry
	 */
	private class EntryEventHandlerImpl implements EntryEventHandler
	{
		@Override
		public void handleEvent(Entry entry)
		{
			entriesView.getItems().add((ListEntry) entry);
			service.getEntries().add((ListEntry) entry);

			notify.text(notifyAddMsg).showInformation();
		}
	}
}
