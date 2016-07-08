package list.search;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import list.DataService;
import list.entry.SearchedEntry;

/**
 * Created by Maciej on 2016-06-14.
 */
public class SearchController
{
	private DataService dataService;
	private SearchService searchService;

	@FXML private TextField searchField;
	@FXML private GridPane resultsGrid;
	@FXML private ProgressBar progressBar;

	/**
	 * Initializes this controller
	 *
	 * @param service
	 */
	public void init(DataService service)
	{
		this.dataService = service;
		searchService = new SearchService();

		searchService.setOnRunning(event -> progressBar.setVisible(true));
		searchService.setOnSucceeded((serviceState) ->
		{
			showSearchResults(searchService.getValue());
			progressBar.setVisible(false);
		});
	}

	@FXML
	private void search(KeyEvent event)
	{
		if (event.getCode() == KeyCode.ENTER)
		{
			searchService.restart();
		}
	}

	public void showSearchResults(SearchedEntry[] searchedEntries)
	{
		resultsGrid.getChildren().clear();

		int row = 0;
		int column = 0;

		for (SearchedEntry e : searchedEntries)
		{
			if (column >= 5)
			{
				column = 0;
				row++;
			}

			Result result = new Result(dataService, e);
			GridPane.setConstraints(result, column, row);
			resultsGrid.getChildren().add(result);

			column++;
		}
	}

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
}
