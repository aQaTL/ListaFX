package list.search;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import list.DataService;
import list.entry.EntryAddListener;
import list.entry.ListEntry;
import list.entry.SearchedEntry;
import list.entry.SeriesTypeEnum;
import org.controlsfx.control.*;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Maciej on 2016-06-14.
 */
public class SearchController
{
	private DataService dataService;
	private EntryAddListener entryAddListener;
	private SearchService searchService;

	private Result[] allResults;
	final ObservableList<Result> displayedResults = FXCollections.observableArrayList();
	private ObservableList<SeriesTypeEnum> selectedFilters = FXCollections.observableArrayList();
	private SearchedEntry selectedEntry;

	private EventHandler<MouseEvent> addButtonEventHandler;

	@FXML
	private BorderPane borderPane;
	@FXML
	private TextField searchField;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private GridView<Result> gridView;
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
	private Label seriesStart, seriesEnd, statusLabel;
	@FXML
	private WebView synopsis;
	@FXML
	private CheckComboBox<SeriesTypeEnum> filterBox;

	/**
	 * Initializes this controller
	 *
	 * @param service
	 */
	public void init(DataService service, EntryAddListener entryAddListener)
	{
		this.dataService = service;
		this.entryAddListener = entryAddListener;
		searchService = new SearchService();

		addButtonEventHandler = mouseEvent -> showDetails((Result) ((Button) mouseEvent.getSource()).getParent().getParent()); //Spaghetti ftw!

		searchService.setOnRunning(event -> progressBar.setVisible(true));
		searchService.setOnSucceeded((serviceState) ->
		{
			showSearchResults(searchService.getValue());
			progressBar.setVisible(false);
		});
		searchService.setOnFailed(serviceState -> serviceState.getSource().getException().printStackTrace());

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
		allResults = new Result[searchedEntries.length];
		displayedResults.clear();

		for (int i = 0; i < searchedEntries.length; i++)
		{
			Result result = new Result(searchedEntries[i]);
			result.addButton.setOnMouseClicked(addButtonEventHandler);

			allResults[i] = result;
		}
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
	private void showDetails(Result result)
	{
		if (masterDetailPane.isShowDetailNode() && result.getEntry() == selectedEntry)
		{
			masterDetailPane.setShowDetailNode(false);
			return;
		}
		masterDetailPane.setShowDetailNode(true);

		this.selectedEntry = result.getEntry();

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
		seriesStart.setText(selectedEntry.getStartDate());
		seriesEnd.setText(selectedEntry.getEndDate());
		statusLabel.setText(selectedEntry.getStatus());
		synopsis.getEngine().loadContent(selectedEntry.getSynopsis());
	}

	/**
	 * Filters displayed results based on selected
	 * checkboxes in filterBox
	 */
	private void filterResults()
	{
		displayedResults.setAll(allResults);

		if (selectedFilters.size() == 0)
			return;

		for (Result result : allResults)
		{
			if (!selectedFilters.contains(result.getEntry().getType()))
				displayedResults.remove(result);
		}
	}

	/**
	 * Creates task that adds selectedEntry to MAL
	 * and executes it in new thread
	 */
	@FXML
	private void add()
	{
		Task<ListEntry> addEntryTask = new Task<ListEntry>()
		{
			@Override
			protected ListEntry call() throws Exception
			{
				return dataService.addEntryToMAL(selectedEntry, 0);
			}
		};

		addEntryTask.setOnSucceeded(workerState ->
		{
			try
			{
				ListEntry newEntry = addEntryTask.get();
				if (newEntry != null && entryAddListener != null)
					entryAddListener.entryAdded(newEntry);
				else
					System.err.println("Couldn't add");
				//TODO alert user when adding fails
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		});

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
			return new Task<SearchedEntry[]>()
			{
				@Override
				protected SearchedEntry[] call() throws Exception
				{
					return dataService.searchForEntries(searchField.getText());
				}
			};
		}
	}

	/**
	 * Class for displaying Result in gridView
	 */
	private class ResultCell extends GridCell<Result>
	{
		@Override
		protected void updateItem(Result item, boolean empty)
		{
			super.updateItem(item, empty);

			if (!empty)
			{
				setGraphic(item);
			}
		}
	}
}
