package list.entry;

import javafx.scene.image.Image;
import list.entry.data.MyScoreEnum;
import list.entry.data.MyStatusEnum;
import list.entry.data.SeriesTypeEnum;
import org.jsoup.nodes.Element;

import java.net.URL;

/**
 * This class represents a single entry of user MAL
 */
public final class ListEntry extends Entry
{
	private int seriesDataBaseID;
	private String seriesTitle;
	private String[] seriesSynonyms;
	private SeriesTypeEnum seriesType;
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
		super(entry, "EntryView.fxml");
	}

	@Override
	public void initView()
	{
		titleLabel.setText(getSeriesTitle());
		imageView.setImage(getSeriesImage());
		episodesField.setText(getMyWatchedEpisodes() + "/" + Integer.toString(getSeriesEpisodes()));
		typeOrScoreField.setText(Integer.toString(getMyScore().getScore()) + "/" + "10");
	}

	/**
	 * Initializes the most frequently used ListEntry fields
	 */
	@Override
	protected void initFields()
	{
		seriesDataBaseID = Integer.parseInt(getStringFromElement("series_animedb_id"));
		seriesTitle = getStringFromElement("series_title");
//		seriesSynonyms = parseSynonyms(getStringFromElement("series_synonyms"));
//		seriesType = Short.parseShort(getStringFromElement("series_type"));
		seriesEpisodes = Integer.parseInt(getStringFromElement("series_episodes"));
//		seriesStatus = Short.parseShort(getStringFromElement("series_status"));
//		seriesStart = getStringFromElement("series_start");
//		seriesEnd = getStringFromElement("series_end");
		seriesImage = new Image(getStringFromElement("series_image"));
//		myID = Integer.parseInt(getStringFromElement("my_id"));
		myWatchedEpisodes = Integer.parseInt(getStringFromElement("my_watched_episodes"));
//		myStartDate = getStringFromElement("my_start_date");
//		myFinishDate = getStringFromElement("my_finish_date");
		myScore = MyScoreEnum.getMyScoreEnum(getStringFromElement("my_score"));
		myStatus = MyStatusEnum.getMyStatusEnum(getStringFromElement("my_status"));
//		myRewatching = Integer.parseInt(getStringFromElement("my_rewatching"));
//		myRewatchingEpisode = Integer.parseInt(getStringFromElement("my_rewatching_ep"));
//		myLastUpdated = getStringFromElement("my_last_updated");

		try
		{
			myTags = getStringFromElement("my_tags");
		}
		catch (IndexOutOfBoundsException e)
		{
			myTags = "";
		}

		website = super.getWebsite(seriesDataBaseID);
	}

	@Override
	public String toString()
	{
		return seriesTitle;
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
		if (seriesSynonyms == null)
			seriesSynonyms = parseSynonyms(getStringFromElement("series_synonyms"));

		return seriesSynonyms;
	}

	public SeriesTypeEnum getSeriesType()
	{
		if (seriesType == null)
			seriesType = SeriesTypeEnum.valueOf(Integer.parseInt(getStringFromElement("series_type")));

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
		if (seriesStatus == 0)
			seriesStatus = Short.parseShort(getStringFromElement("series_status"));

		return seriesStatus;
	}

	public String getSeriesStart()
	{
		if (seriesStart == null)
			seriesStart = getStringFromElement("series_start");

		return seriesStart;
	}

	public String getSeriesEnd()
	{
		if (seriesEnd == null)
			seriesEnd = getStringFromElement("series_end");

		return seriesEnd;
	}

	public Image getSeriesImage()
	{
		return seriesImage;
	}

	public int getMyID()
	{
		if (myID == 0)
			myID = Integer.parseInt(getStringFromElement("my_id"));

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
		if (myStartDate == null)
			myStartDate = getStringFromElement("my_start_date");

		return myStartDate;
	}

	public String getMyFinishDate()
	{
		if (myFinishDate == null)
			myFinishDate = getStringFromElement("my_finish_date");

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
		if (myRewatching == 0)
			myRewatching = Integer.parseInt(getStringFromElement("my_rewatching"));

		return myRewatching;
	}

	public int getMyRewatchingEpisode()
	{
		if (myRewatchingEpisode == 0)
			myRewatchingEpisode = Integer.parseInt(getStringFromElement("my_rewatching_ep"));

		return myRewatchingEpisode;
	}

	public String getMyLastUpdated()
	{
		if (myLastUpdated == null)
			myLastUpdated = getStringFromElement("my_last_updated");

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

	public void setWebsite(URL website)
	{
		this.website = website;
	}

	public void setSeriesDataBaseID(int seriesDataBaseID)
	{
		this.seriesDataBaseID = seriesDataBaseID;
	}

	public void setSeriesTitle(String seriesTitle)
	{
		this.seriesTitle = seriesTitle;
	}

	public void setSeriesSynonyms(String[] seriesSynonyms)
	{
		this.seriesSynonyms = seriesSynonyms;
	}

	public void setSeriesType(SeriesTypeEnum seriesType)
	{
		this.seriesType = seriesType;
	}

	public void setSeriesStatus(short seriesStatus)
	{
		this.seriesStatus = seriesStatus;
	}

	public void setSeriesStart(String seriesStart)
	{
		this.seriesStart = seriesStart;
	}

	public void setSeriesEnd(String seriesEnd)
	{
		this.seriesEnd = seriesEnd;
	}

	public void setSeriesImage(Image seriesImage)
	{
		this.seriesImage = seriesImage;
	}

	public void setMyID(int myID)
	{
		this.myID = myID;
	}

	public void setMyStartDate(String myStartDate)
	{
		this.myStartDate = myStartDate;
	}

	public void setMyFinishDate(String myFinishDate)
	{
		this.myFinishDate = myFinishDate;
	}

	public void setMyRewatching(int myRewatching)
	{
		this.myRewatching = myRewatching;
	}

	public void setMyRewatchingEpisode(int myRewatchingEpisode)
	{
		this.myRewatchingEpisode = myRewatchingEpisode;
	}

	public void setMyLastUpdated(String myLastUpdated)
	{
		this.myLastUpdated = myLastUpdated;
	}

	public void setMyTags(String myTags)
	{
		this.myTags = myTags;
	}
}