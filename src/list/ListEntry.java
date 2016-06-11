package list;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListEntry extends Entry
{
	Element entry;

	private int seriesDataBaseID;
	private String seriesTitle;
	private String[] seriesSynonyms;
	private short seriesType;
	private int seriesEpisodes;
	private short seriesStatus;
	private String seriesStart;
	private String seriesEnd;
	private Image seriesImage;
	private int myID;
	private int myWatchedEpisodes;
	private String myStartDate;
	private String myFinishDate;
	private MyScoreEnum myScore;
	private MyStatusEnum myStatus; //1/watching, 2/completed, 3/onhold, 4/dropped, 6/plantowatch
	private int myRewatching;
	private int myRewatchingEpisode;
	private String myLastUpdated;
	private String myTags;

	private URL website; //Custom or MAL website (depends on user settings)

	public ListEntry(Element entry)
	{
		this.entry = entry;

		initFields(null);
	}

	public ListEntry(Element entry, String customWebsite)
	{
		this.entry = entry;

		initFields(customWebsite);
	}

	private String getStringFromElement(String elementTag)
	{
		TextNode node = (TextNode) entry.getElementsByTag(elementTag).first().childNode(0);
		return node.getWholeText();
	}

	private void initFields(String websiteURL)
	{
		seriesDataBaseID = Integer.parseInt(getStringFromElement("series_animedb_id"));
		seriesTitle = getStringFromElement("series_title");
		seriesSynonyms = parseSynonyms(getStringFromElement("series_synonyms"));
		seriesType = Short.parseShort(getStringFromElement("series_type"));
		seriesEpisodes = Integer.parseInt(getStringFromElement("series_episodes"));
		seriesStatus = Short.parseShort(getStringFromElement("series_status"));
		seriesStart = getStringFromElement("series_start");
		seriesEnd = getStringFromElement("series_end");
		seriesImage = new Image(getStringFromElement("series_image"));
		myID = Integer.parseInt(getStringFromElement("my_id"));
		myWatchedEpisodes = Integer.parseInt(getStringFromElement("my_watched_episodes"));
		myStartDate = getStringFromElement("my_start_date");
		myFinishDate = getStringFromElement("my_finish_date");
		myScore = MyScoreEnum.getMyScoreEnum(getStringFromElement("my_score"));
		myStatus = MyStatusEnum.getMyStatusEnum(getStringFromElement("my_status"));
		myRewatching = Integer.parseInt(getStringFromElement("my_rewatching"));
		myRewatchingEpisode = Integer.parseInt(getStringFromElement("my_rewatching_ep"));
		myLastUpdated = getStringFromElement("my_last_updated");

		try
		{
			myTags = getStringFromElement("my_tags");
		}
		catch (IndexOutOfBoundsException e)
		{
			myTags = "";
		}

		try
		{
			if (websiteURL != null)
			{
				website = new URL(websiteURL);
			}
			else
			{
				website = new URL("http://myanimelist.net/anime/" + seriesDataBaseID + "/");
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();

			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Bad website address");
			alert.showAndWait();
		}
	}


	@Override
	public String toString()
	{
		return seriesTitle;
	}

	private String[] parseSynonyms(String synonyms)
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

	//====================
	//GETTERS AND SETTERS
	//====================


	public int getSeriesDataBaseID()
	{
		return seriesDataBaseID;
	}

	public String getSeriesTitle()
	{
		return seriesTitle;
	}

	public String[] getSeriesSynonyms()
	{
		return seriesSynonyms;
	}

	public short getSeriesType()
	{
		return seriesType;
	}

	public int getSeriesEpisodes()
	{
		return seriesEpisodes;
	}

	public void setSeriesEpisodes(int seriesEpisodes)
	{
		this.seriesEpisodes = seriesEpisodes;
	}

	public short getSeriesStatus()
	{
		return seriesStatus;
	}

	public String getSeriesStart()
	{
		return seriesStart;
	}

	public String getSeriesEnd()
	{
		return seriesEnd;
	}

	public Image getSeriesImage()
	{
		return seriesImage;
	}

	public int getMyID()
	{
		return myID;
	}

	public int getMyWatchedEpisodes()
	{
		return myWatchedEpisodes;
	}

	public void setMyWatchedEpisodes(int myWatchedEpisodes)
	{
		this.myWatchedEpisodes = myWatchedEpisodes;
	}

	public String getMyStartDate()
	{
		return myStartDate;
	}

	public String getMyFinishDate()
	{
		return myFinishDate;
	}

	public MyScoreEnum getMyScore()
	{
		return myScore;
	}

	public void setMyScore(MyScoreEnum myScore)
	{
		this.myScore = myScore;
	}

	public MyStatusEnum getMyStatus()
	{
		return myStatus;
	}

	public void setMyStatus(MyStatusEnum status)
	{
		this.myStatus = status;
	}

	public int getMyRewatching()
	{
		return myRewatching;
	}

	public int getMyRewatchingEpisode()
	{
		return myRewatchingEpisode;
	}

	public String getMyLastUpdated()
	{
		return myLastUpdated;
	}

	public String getMyTags()
	{
		return myTags;
	}

	public URL getWebsite()
	{
		return website;
	}
}
