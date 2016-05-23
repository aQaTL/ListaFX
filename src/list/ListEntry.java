package list;

import javafx.scene.image.Image;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class ListEntry implements Serializable
{
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
    private short myScore;
    private short myStatus; //1/watching, 2/completed, 3/onhold, 4/dropped, 6/plantowatch
    private int myRewatching;
    private int myRewatchingEpisode;
    private String myLastUpdated;

    private String description, myTags;

    private URL website;

    public ListEntry()
    {

    }

    @Override
    public String toString()
    {
        return seriesTitle;
    }

    public void setSeriesImage(String imageURL)
    {
        seriesImage = new Image(imageURL);
    }

    public Image getSeriesImage()
    {
        return seriesImage;
    }

    public String[] getSeriesSynonyms()
    {
        return seriesSynonyms;
    }

    /**
     * Parses String with synonyms that have been divided by semicolon
     *
     * @param seriesSynonyms String to parse
     */
    public void setSeriesSynonyms(String seriesSynonyms)
    {
        ArrayList<String> synonymsArray = new ArrayList<>(5); //Usually entries doesn't have more than 5 synonyms

        StringBuilder seriesSynonymBuilder = new StringBuilder();
        for(char c : seriesSynonyms.toCharArray())
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

        this.seriesSynonyms = (String[]) synonymsArray.toArray();
    }

    //===========================
    // PLAIN GETTERS AND SETTERS
    //===========================
    public int getSeriesDataBaseID()
    {
        return seriesDataBaseID;
    }

    public void setSeriesDataBaseID(int seriesDataBaseID)
    {
        this.seriesDataBaseID = seriesDataBaseID;
    }

    public String getSeriesTitle()
    {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle)
    {
        this.seriesTitle = seriesTitle;
    }

    public short getSeriesType()
    {
        return seriesType;
    }

    public void setSeriesType(short seriesType)
    {
        this.seriesType = seriesType;
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

    public void setSeriesStatus(short seriesStatus)
    {
        this.seriesStatus = seriesStatus;
    }

    public String getSeriesStart()
    {
        return seriesStart;
    }

    public void setSeriesStart(String seriesStart)
    {
        this.seriesStart = seriesStart;
    }

    public String getSeriesEnd()
    {
        return seriesEnd;
    }

    public void setSeriesEnd(String seriesEnd)
    {
        this.seriesEnd = seriesEnd;
    }

    public int getMyID()
    {
        return myID;
    }

    public void setMyID(int myID)
    {
        this.myID = myID;
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

    public void setMyStartDate(String myStartDate)
    {
        this.myStartDate = myStartDate;
    }

    public String getMyFinishDate()
    {
        return myFinishDate;
    }

    public void setMyFinishDate(String myFinishDate)
    {
        this.myFinishDate = myFinishDate;
    }

    public short getMyScore()
    {
        return myScore;
    }

    public void setMyScore(short myScore)
    {
        this.myScore = myScore;
    }

    public short getMyStatus()
    {
        return myStatus;
    }

    public void setMyStatus(short myStatus)
    {
        this.myStatus = myStatus;
    }

    public int getMyRewatching()
    {
        return myRewatching;
    }

    public void setMyRewatching(int myRewatching)
    {
        this.myRewatching = myRewatching;
    }

    public int getMyRewatchingEpisode()
    {
        return myRewatchingEpisode;
    }

    public void setMyRewatchingEpisode(int myRewatchingEpisode)
    {
        this.myRewatchingEpisode = myRewatchingEpisode;
    }

    public String getMyLastUpdated()
    {
        return myLastUpdated;
    }

    public void setMyLastUpdated(String myLastUpdated)
    {
        this.myLastUpdated = myLastUpdated;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMyTags()
    {
        return myTags;
    }

    public void setMyTags(String myTags)
    {
        this.myTags = myTags;
    }

    public URL getWebsite()
    {
        return website;
    }

    public void setWebsite(URL website)
    {
        this.website = website;
    }
}
