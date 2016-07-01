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
 * Created by Maciej on 2016-06-17.
 */
public class Result extends VBox
{
	private DataService service;
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
	private Button addButton;

	public Result(DataService service, SearchedEntry entry)
	{
		this.service = service;
		this.entry = entry;

		loadFXML();

		seriesTitleLabel.setText(entry.getTitle());
		seriesImageView.setImage(entry.getImage());
		seriesEpisodesField.setText(Integer.toString(entry.getEpisodes()));
		seriesTypeField.setText(entry.getType());
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

	/**
	 * Action for addButton Button
	 */
	@FXML
	private void add()
	{
		service.addEntryToMAL(entry, 0);
		//TODO close serachWindow stage (this)
	}


}
