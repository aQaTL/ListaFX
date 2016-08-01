package list.entry;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Maciej on 2016-05-23.
 */
public abstract class Entry
{
	protected Element entry;

	/**
	 * Gets String value of first given elementTag children node
	 *
	 * @param elementTag name of tag
	 */
	protected String getStringFromElement(String elementTag)
	{
		if(entry != null)
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

	/**
	 * Parses synonyms that are separated by semicolon
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

	/**
	 * Coverts given searched entry to ListEntry
	 * Values, that don't exist in SearchedEntry are set to default values
	 */
	public static ListEntry convertToListEntry(SearchedEntry searchedEntry)
	{
		ListEntry listEntry = new ListEntry(null);
		listEntry.setSeriesDataBaseID(searchedEntry.getId());
		listEntry.setSeriesTitle(searchedEntry.getTitle());
		listEntry.setSeriesSynonyms(searchedEntry.getSynonyms());
		listEntry.setSeriesType(searchedEntry.getType());
		listEntry.setSeriesEpisodes(searchedEntry.getEpisodes());
//		listEntry.setSeriesStatus(searchedEntry.getStatus()); TODO implement SeriesStatus
		listEntry.setSeriesStart(searchedEntry.getStartDate());
		listEntry.setSeriesEnd(searchedEntry.getEndDate());
		listEntry.setSeriesImage(searchedEntry.getImage());
		listEntry.setMyID(0);
		listEntry.setMyWatchedEpisodes(0);
		listEntry.setMyStartDate("0000-00-00");
		listEntry.setMyFinishDate("0000-00-00");
		listEntry.setMyScore(MyScoreEnum.NOT_RATED_YET);
		listEntry.setMyStatus(MyStatusEnum.WATCHING);
		listEntry.setMyRewatching(0);
		listEntry.setMyRewatchingEpisode(0);
		listEntry.setMyLastUpdated("0"); //Not sure about this

		return listEntry;
	}
}
