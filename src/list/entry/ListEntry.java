package list.entry;

import javafx.scene.image.Image;
import list.entry.data.MyScoreEnum;
import list.entry.data.MyStatusEnum;
import list.entry.data.SeriesTypeEnum;
import org.jsoup.nodes.Element;

/**
 * This class represents a single entry of user MAL
 */
public final class ListEntry extends Entry
{
	private short seriesStatus;
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

	public ListEntry(Element entry)
	{
		super(entry);
	}

	public ListEntry()
	{
		super();
	}

	public ListEntry(String[] values)
	{
		super();

		initFields(values);
		loadFXML();
	}

	@Override
	public void initView()
	{
		titleLabel.setText(getTitle());
		imageView.setImage(getImage());
		episodesField.setText(getMyWatchedEpisodes() + "/" + Integer.toString(getEpisodes()));
		typeOrScoreField.setText(Integer.toString(getMyScore().getScore()) + "/" + "10");
	}

	private void initFields(String[] values)
	{
		databaseId = Integer.parseInt(values[0]);
		title = values[1];
		synonyms = parseSynonyms(values[2]);
		seriesType = SeriesTypeEnum.valueOf(Integer.parseInt(values[3]));
		episodes = Integer.parseInt(values[4]);
		seriesStatus = Short.parseShort(values[5]);
		startDate = values[6];
		endDate = values[7];
		//seriesImage = new Image(values[8]); //SPOTTED!!!!
		imageUrl = values[8];
		myID = Integer.parseInt(values[9]);
		myWatchedEpisodes = Integer.parseInt(values[10]);
		myStartDate = values[11];
		myFinishDate = values[12];
		myScore = MyScoreEnum.getMyScoreEnum(values[13]);
		myStatus = MyStatusEnum.getMyStatusEnum(values[14]);
		myRewatching = Integer.parseInt(values[15]);
		myRewatchingEpisode = Integer.parseInt(values[16]);
		myLastUpdated = values[17];
		myTags = values[18];

		website = super.getWebsite(databaseId);
	}

	/**
	 * Initializes the most frequently used ListEntry fields
	 */
	@Override
	protected void initFields()
	{
		databaseId = Integer.parseInt(getStringFromElement("series_animedb_id"));
		title = getStringFromElement("series_title");
//		synonyms = parseSynonyms(getStringFromElement("series_synonyms"));
//		seriesType = Short.parseShort(getStringFromElement("series_type"));
		episodes = Integer.parseInt(getStringFromElement("series_episodes"));
//		seriesStatus = Short.parseShort(getStringFromElement("series_status"));
//		startDate = getStringFromElement("series_start");
//		endDate = getStringFromElement("series_end");
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

		website = super.getWebsite(databaseId);
	}

	@Override
	public String toString()
	{
		return title;
	}


	//====================
	//GETTERS AND SETTERS
	//====================

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

	public void setSeriesStatus(short seriesStatus)
	{
		this.seriesStatus = seriesStatus;
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