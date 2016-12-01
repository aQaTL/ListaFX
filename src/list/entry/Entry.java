package list.entry;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import list.DataService;
import list.entry.data.MyScoreEnum;
import list.entry.data.MyStatusEnum;
import list.entry.data.SeriesTypeEnum;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Maciej on 2016-05-23.
 */
public abstract class Entry
{
	private static final String VIEW_NAME = "EntryView.fxml";
	private static Image loadingImage = new Image(DataService.class.getResource("resources/BeautifulLoadingImage.gif").toExternalForm());

	protected VBox view;
	protected EntryEventHandler eventHandler;
	protected Element entry;

	//Entry values
	protected int databaseId;
	protected String title;
	protected String[] synonyms;
	protected SeriesTypeEnum seriesType;
	protected int episodes;
	protected String startDate; //TODO Maybe some kind of date class would be nice to use here
	protected String endDate; //Here too
	protected String imageUrl;
	protected Image image;

	protected URL website; //Custom or MAL website (depends on user settings)

	//View components
	@FXML
	protected Label titleLabel;
	@FXML
	protected ImageView imageView;
	@FXML
	protected TextField episodesField;
	@FXML
	protected Label typeOrScoreLabel;
	@FXML
	protected TextField typeOrScoreField;
	@FXML
	protected Button detailsButton;

	public Entry(Element entry)
	{
		this.entry = entry;

		if (entry != null)
		{
			initFields();
			view = loadFXML();
		}
	}

	public Entry()
	{
		image = loadingImage;
	}

	protected abstract void initView();

	protected abstract void initFields();

	/**
	 * Gets String value of first given elementTag children node
	 *
	 * @param elementTag name of tag
	 */
	protected String getStringFromElement(String elementTag)
	{
		if (entry != null)
		{
			try
			{
				entry.children().get(0).childNode(0);
				TextNode node = (TextNode) entry.getElementsByTag(elementTag).first().childNode(0);
				return node.getWholeText();
			}
			catch (IndexOutOfBoundsException e)
			{
				return "";
			}
		}
		else
		{
			return "";
		}
	}

	protected VBox loadFXML()
	{
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(VIEW_NAME));
		loader.setController(this);

		try
		{
			VBox vbox = loader.load();
			loader.<Entry>getController().initView();
			typeOrScoreField.setText("Type");
			return vbox;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void setOnMouseClicked(EntryEventHandler handler)
	{
		eventHandler = handler;
	}

	@FXML
	private void handleDetailsButton(MouseEvent event)
	{
		if (eventHandler != null)
			eventHandler.handleEvent(this);
	}

	/**
	 * Parses synonyms that are separated by semicolon
	 *
	 * @return array of synonyms
	 */
	protected String[] parseSynonyms(String synonyms)
	{
		ArrayList<String> synonymsArray = new ArrayList<>(5); //Usually entries doesn't have more than 5 synonyms

		StringBuilder seriesSynonymBuilder = new StringBuilder();
		for (char c : synonyms.toCharArray())
		{
			if (c != ';')
			{
				seriesSynonymBuilder.append(c);
			}
			else
			{
				synonymsArray.add(seriesSynonymBuilder.toString());
				seriesSynonymBuilder = new StringBuilder();
			}
		}

		return synonymsArray.toArray(new String[synonymsArray.size()]);
	}

	public VBox getView()
	{
		if (view == null)
			view = loadFXML();
		return view;
	}

	/**
	 * @return URL to MAL anime website based on its unique id
	 */
	protected URL getWebsite(int seriesDataBaseID)
	{
		try
		{
			return new URL("http://myanimelist.net/anime/" + seriesDataBaseID + "/");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//====================
	//GETTERS AND SETTERS
	//====================

	public int getDatabaseId()
	{
		return databaseId;
	}

	public void setDatabaseId(int databaseId)
	{
		this.databaseId = databaseId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String[] getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(String[] synonyms)
	{
		this.synonyms = synonyms;
	}

	public SeriesTypeEnum getSeriesType()
	{
		return seriesType;
	}

	public void setSeriesType(SeriesTypeEnum seriesType)
	{
		this.seriesType = seriesType;
	}

	public int getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(int episodes)
	{
		this.episodes = episodes;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		imageView.setImage(image);
	}

	public URL getWebsite()
	{
		return website;
	}

	public void setWebsite(URL website)
	{
		this.website = website;
	}

	/**
	 * Coverts given searched entry to ListEntry
	 * Values, that don't exist in SearchedEntry are set to default values
	 */
	public static ListEntry convertToListEntry(SearchedEntry searchedEntry)
	{
		ListEntry listEntry = new ListEntry();
		listEntry.setDatabaseId(searchedEntry.getDatabaseId());
		listEntry.setTitle(searchedEntry.getTitle());
		listEntry.setSynonyms(searchedEntry.getSynonyms());
		listEntry.setSeriesType(searchedEntry.getSeriesType());
		listEntry.setEpisodes(searchedEntry.getEpisodes());
		listEntry.setSeriesStatus((short) 0); //TODO implement SeriesStatus
		listEntry.setStartDate(searchedEntry.getStartDate());
		listEntry.setEndDate(searchedEntry.getEndDate());
		listEntry.setMyID(0);
		listEntry.setMyWatchedEpisodes(0);
		listEntry.setMyStartDate("0000-00-00");
		listEntry.setMyFinishDate("0000-00-00");
		listEntry.setMyScore(MyScoreEnum.NOT_RATED_YET);
		listEntry.setMyStatus(MyStatusEnum.WATCHING);
		listEntry.setMyRewatching(0);
		listEntry.setMyRewatchingEpisode(0);
		listEntry.setMyLastUpdated("0"); //Not sure about this
		listEntry.setWebsite(listEntry.getWebsite(listEntry.getDatabaseId()));

		listEntry.loadFXML();
		listEntry.setImage(searchedEntry.getImage());

		return listEntry;
	}
}
