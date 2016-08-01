package list.search;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import list.DataService;
import list.entry.SearchedEntry;

import java.io.IOException;

/**
 * Represents single searchResult as VBox with some info about it
 *
 * Created by Maciej on 2016-06-17.
 */
public class Result extends VBox
{
	private SearchedEntry entry;

	@FXML
	private Label seriesTitleLabel;
	@FXML
	private ImageView seriesImageView;
	@FXML
	private TextField seriesEpisodesField;
	@FXML
	private TextField seriesTypeField;
	@FXML
	public Button addButton;

	public Result(SearchedEntry entry)
	{
		this.entry = entry;

		loadFXML();

		seriesTitleLabel.setText(entry.getTitle());
		seriesImageView.setImage(entry.getImage());
		seriesEpisodesField.setText(Integer.toString(entry.getEpisodes()));
		seriesTypeField.setText(entry.getType().toString());
	}

	/**
	 * Encapsulates FXMLLoader logic
	 */
	private void loadFXML()
	{
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("ResultView.fxml"));
		loader.setController(this);

		try
		{
			getChildren().add(loader.load());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public SearchedEntry getEntry()
	{
		return entry;
	}
}
