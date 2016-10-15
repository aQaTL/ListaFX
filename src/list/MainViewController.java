package list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents application main window
 */
public class MainViewController
{
	private DataService service;
	private Notifications notify;
	private String currentTabId;
	private EntryEventHandler showDetailsHandler;

	private ObservableList<ListEntry> displayedEntries;
	private ListEntry selectedEntry;

	private String notifyAddMsg = "Entry has been added";
	private String notifyDeleteMsg = "Entry has been deleted";
	private String notifyUpdateMsg = "Entry has been updated";

	@FXML
	private BorderPane rootPane;
	@FXML
	public MasterDetailPane masterDetailPane;
	@FXML
	private HBox topHBox;
	private TextField searchTextField; //Couldn't inject ClearableTextField in fxml
	//Left side components
	@FXML
	private GridView<ListEntry> entriesView;
	@FXML
	private Tab allTab, watchingTab, completedTab, onHoldTab, droppedTab, planToWatchTab;
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

		//Initializes notifications
		notify = Notifications.create()
				.title("ListaFX")
				.position(Pos.TOP_RIGHT)
				.hideAfter(new Duration(1500));

		//Initializes entry-related components
		episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
		myScoreBox.getItems().addAll(MyScoreEnum.values());
		mySeriesStatusBox.getItems().addAll(MyStatusEnum.values());

		mySeriesStatusBox.setOnAction(event ->
		{
			if (mySeriesStatusBox.getSelectionModel().getSelectedItem() == MyStatusEnum.COMPLETED)
			{
				episodeSpinner.setDisable(true);
				episodeSpinner.getEditor().setText(Integer.toString(selectedEntry.getEpisodes()));
			}
			else
			{
				episodeSpinner.setDisable(false);
			}
		});

		entriesView.setCellFactory(param -> new ListEntryCell());

		showDetailsHandler = this::showDetails;

		//Initializes searchTextField
		searchTextField = TextFields.createClearableTextField();
		searchTextField.setVisible(false);
//		topHBox.getChildren().add(searchTextField);
		EventHandler<KeyEvent> searchBarKeyEvent = (keyEvent ->
		{
			if (keyEvent.getCode() == KeyCode.F3)
				toggleSearchBar();
		});
		rootPane.setOnKeyPressed(searchBarKeyEvent);
		searchTextField.setOnKeyPressed(searchBarKeyEvent);
		searchTextField.setOnKeyReleased(event -> loadEntriesWithFilter(searchTextField.getText()));

		//Determines which tab is open by default
		currentTabId = allTab.getId();

		//Loads images in background
		new Thread(new ImageLoader(service.getEntries())).start();

		//Filters and displays entries
		service.getEntries().forEach(entry -> entry.setOnMouseClicked(showDetailsHandler));
		displayedEntries = FXCollections.observableArrayList(service.getEntries());
		loadEntriesWithFilter(null);
		entriesView.setItems(displayedEntries);

		selectedEntry = displayedEntries.get(0);
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
	private void updateEntryDetails()
	{
		if (selectedEntry != null)
		{
			seriesTitleLabel.setText(selectedEntry.getTitle());
			myScoreBox.getSelectionModel().select(selectedEntry.getMyScore());
			episodeSpinner.getEditor().setText(Integer.toString(selectedEntry.getMyWatchedEpisodes()));
			mySeriesStatusBox.getSelectionModel().select(selectedEntry.getMyStatus());
			seriesImageView.setImage(selectedEntry.getImage());

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
					selectedEntry.initView(); //Just updates it
					loadEntriesWithFilter(searchTextField.getText());
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
		alert.setHeaderText("Deleting " + selectedEntry.getTitle() + " from list");
		alert.setContentText("Are you sure?");
		alert.setTitle("Confirm");

		DialogPane alertPane = alert.getDialogPane();
		alertPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
		alertPane.getStyleClass().add("myAlert");

		alert.showAndWait();

		if (alert.getResult().getButtonData().isDefaultButton())
		{
			Task<Boolean> deleteEntryTask = service.deleteEntryFromMAL(selectedEntry.getDatabaseId());

			deleteEntryTask.setOnSucceeded(workerState ->
			{
				try
				{
					if (deleteEntryTask.get())
					{
						service.getEntries().remove(selectedEntry);
						displayedEntries.remove(selectedEntry);

						if(displayedEntries.size() > 0)
						{
							selectedEntry = displayedEntries.get(0);
							updateEntryDetails();
						}
						else
						{
							masterDetailPane.setShowDetailNode(false);
						}

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
		dialog.setHeaderText("Set custom website for " + selectedEntry.getTitle());

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
			loadEntriesWithFilter(searchTextField.getText());
		}
	}

	/**
	 * Adjust currently showing entries in entriesView to match currently selected tab and to match filter
	 * in searchTextField (if any)
	 *
	 * @param titleFilter additional filter to series title (can be null to display all entries)
	 */
	private void loadEntriesWithFilter(String titleFilter)
	{
		displayedEntries.clear();

		if(!currentTabId.equals(allTab.getId()))
		{
			MyStatusEnum selectedTab = MyStatusEnum.valueOf(currentTabId.substring(0, currentTabId.length() - 3).toUpperCase());
			service.getEntries().forEach(entry ->
			{
				if(entry.getMyStatus() == selectedTab)
					displayedEntries.add(entry);
			});
		}
		else
			displayedEntries.setAll(service.getEntries());

		if(titleFilter != null)
			displayedEntries.removeIf(listEntry -> !listEntry.getTitle().toLowerCase().contains(titleFilter.toLowerCase()));

		if (displayedEntries.size() > 0 && !displayedEntries.contains(selectedEntry))
		{
			selectedEntry = displayedEntries.get(0);
			updateEntryDetails();
		}
		else if (displayedEntries.size() == 0)
		{
			masterDetailPane.setShowDetailNode(false);
		}
	}

	private void toggleSearchBar()
	{
		if(searchTextField.isVisible())
		{
			topHBox.getChildren().remove(searchTextField);
			searchTextField.setVisible(false);
		}
		else
		{
			topHBox.getChildren().add(searchTextField);
			searchTextField.setVisible(true);
			searchTextField.requestFocus();
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
			entry.setOnMouseClicked(showDetailsHandler);
			entriesView.getItems().add((ListEntry) entry);
			service.getEntries().add((ListEntry) entry);

			notify.text(notifyAddMsg).showInformation();
		}
	}
}
