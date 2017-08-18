package list.search;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javax.xml.stream.XMLStreamException;
import list.DataService;
import list.ImageLoader;
import list.entry.Entry;
import list.entry.EntryEventHandler;
import list.entry.ListEntry;
import list.entry.SearchedEntry;
import list.entry.data.SeriesTypeEnum;
import org.controlsfx.control.*;

import java.awt.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Maciej on 2016-06-14.
 */
public class SearchController
{
	private DataService dataService;
	private EntryEventHandler entryEventHandler;
	private SearchService searchService;

	private SearchedEntry[] results;
	final ObservableList<SearchedEntry> displayedResults = FXCollections.observableArrayList();
	private ObservableList<SeriesTypeEnum> selectedFilters = FXCollections.observableArrayList();
	private SearchedEntry selectedEntry;

	private EntryEventHandler addButtonEvent;

	@FXML
	private BorderPane borderPane;
	@FXML
	private TextField searchField;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private GridView<SearchedEntry> gridView;
	@FXML
	private MasterDetailPane masterDetailPane;

	//Details pane
	@FXML
	private GridPane detailsPane;
	@FXML
	private Button addButton;
	@FXML
	private Label seriesTitleLabel, episodesLabel;
	@FXML
	private ImageView seriesImage;
	@FXML
	HyperlinkLabel websiteLabel;
	@FXML
	Rating score;
	@FXML
	private Label seriesStartDate, seriesEndDate, statusLabel;
	@FXML
	private WebView synopsis;
	@FXML
	private CheckComboBox<SeriesTypeEnum> filterBox;

	/**
	 * Initializes this controller
	 *
	 * @param service
	 */
	public void init(DataService service, EntryEventHandler entryEventHandler)
	{
		this.dataService = service;
		this.entryEventHandler = entryEventHandler;
		searchService = new SearchService();

		addButtonEvent = this::showDetails;

		searchService.setOnRunning(event -> progressBar.setVisible(true));
		searchService.setOnSucceeded((serviceState) ->
		{
			showSearchResults(searchService.getValue());
			progressBar.setVisible(false);
		});
		searchService.setOnFailed(serviceState ->
		{
			Throwable e = serviceState.getSource().getException();
			if (e instanceof SocketTimeoutException)
				searchService.restart();
			else if (e instanceof XMLStreamException) //Invoked new search or nothing was found
			{
				progressBar.setVisible(false);
				displayedResults.clear();
			}
			else
				e.printStackTrace();
		});

		filterBox.getItems().setAll(SeriesTypeEnum.values());
		filterBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<SeriesTypeEnum>()
		{
			@Override
			public void onChanged(Change<? extends SeriesTypeEnum> c)
			{
				selectedFilters.setAll(filterBox.getCheckModel().getCheckedItems());
				filterResults();
			}
		});

		gridView.setCellFactory(param -> new ResultCell());

		progressBar.prefWidthProperty().bind(borderPane.widthProperty());

		gridView.setItems(displayedResults);

		synopsis.getEngine().setUserStyleSheetLocation(getClass().getResource("WebViewStyle.css").toString());
	}

	/**
	 * Resets searchService when user presses enter in searchField
	 */
	@FXML
	private void search(KeyEvent event)
	{
		if (event.getCode() == KeyCode.ENTER)
			searchService.restart();
	}

	/**
	 * Shows search results in gridView
	 */
	public void showSearchResults(SearchedEntry[] searchedEntries)
	{
		for (SearchedEntry searchedEntry : searchedEntries)
		{
			searchedEntry.setOnMouseClicked(addButtonEvent);
		}
		results = searchedEntries;

		new Thread(new ImageLoader(Arrays.asList(results))).start();

		displayedResults.clear();
		filterResults();
	}

	/**
	 * Opens selectedEntry website in default browser
	 */
	@FXML
	private void openWebsite(Event event) throws IOException, URISyntaxException
	{
		if (selectedEntry != null)
			Desktop.getDesktop().browse(selectedEntry.getWebsite().toURI());
	}

	/**
	 * Either shows or hides detailNode based on it actual state
	 * and refreshes it components values
	 */
	private void showDetails(Entry entry)
	{
		if (masterDetailPane.isShowDetailNode() && entry == selectedEntry)
		{
			masterDetailPane.setShowDetailNode(false);
			return;
		}
		masterDetailPane.setShowDetailNode(true);

		this.selectedEntry = (SearchedEntry) entry;

		updateDetails();
	}

	/**
	 * Changes detailNode components values to match selectedEntry properties
	 */
	private void updateDetails()
	{
		seriesTitleLabel.setText(selectedEntry.getTitle());
		episodesLabel.setText(Integer.toString(selectedEntry.getEpisodes()));
		seriesImage.setImage(selectedEntry.getImage());
		score.setRating(selectedEntry.getScore());
		seriesStartDate.setText(selectedEntry.getStartDate());
		seriesEndDate.setText(selectedEntry.getEndDate());
		statusLabel.setText(selectedEntry.getStatus());
		synopsis.getEngine().loadContent(selectedEntry.getSynopsis());
	}

	/**
	 * Filters displayed results based on selected
	 * checkboxes in filterBox
	 */
	private void filterResults()
	{
		displayedResults.clear();

		if (selectedFilters.size() == 0)
			displayedResults.addAll(results);
		else
			Arrays.stream(results).filter(result -> selectedFilters.contains(result.getSeriesType())).forEach(displayedResults::add);
	}

	/**
	 * Creates task that adds selectedEntry to MAL
	 * and executes it in new thread
	 */
	@FXML
	private void add()
	{
		Task<ListEntry> addEntryTask = dataService.addEntryToMAL(selectedEntry, 0);

		addEntryTask.setOnSucceeded(workerState ->
		{
			try
			{
				ListEntry newEntry = addEntryTask.get();
				if (newEntry != null && entryEventHandler != null)
					entryEventHandler.handleEvent(newEntry);
				else
					System.err.println("Couldn't add");
				//TODO alert user when adding fails
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		});
		addEntryTask.setOnFailed(workerState -> addEntryTask.getException().printStackTrace());

		new Thread(addEntryTask).start();
	}

	/**
	 * Service for executing searching for entries task in separate thread
	 */
	private class SearchService extends Service<SearchedEntry[]>
	{
		@Override
		protected Task<SearchedEntry[]> createTask()
		{
			return dataService.searchForEntries(searchField.getText());
		}
	}

	/**
	 * Class for displaying SearchedEntry in gridView
	 */
	private class ResultCell extends GridCell<SearchedEntry>
	{
		@Override
		protected void updateItem(SearchedEntry entry, boolean empty)
		{
			super.updateItem(entry, empty);

			if (!empty)
			{
				setGraphic(entry.getView());
			}
		}
	}
}
