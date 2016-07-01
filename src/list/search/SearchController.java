package list.search;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private DataService service;
	private FXMLLoader loader;

	@FXML
	private TextField searchField;

	@FXML
	private GridPane resultsGrid;

	/**
	 * Initializes this controller
	 *
	 * @param service
	 */
	public void init(DataService service)
	{
		this.service = service;
	}


	public synchronized void search(KeyEvent event)
	{
		if (event.getCode() == KeyCode.ENTER)
		{
			SearchedEntry[] searchedEntries = service.searchForEntries(searchField.getText());

			//TODO clear resultsGrid

			int row = 0;
			int column = 0;

			for (SearchedEntry e : searchedEntries)
			{
				if (column >= 5)
				{
					column = 0;
					row++;
				}

				Result result = new Result(service, e);
				GridPane.setConstraints(result, column, row);
				resultsGrid.getChildren().add(result);

				column++;
			}

		}
	}
}
