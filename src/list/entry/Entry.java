package list.entry;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;

/**
 * Created by Maciej on 2016-05-23.
 */
public abstract class Entry
{
	protected Element entry;

	protected String getStringFromElement(String elementTag)
	{
		if(entry != null)
		{
			try
			{
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

	public static ListEntry convertToListEntry(SearchedEntry searchedEntry)
	{
		ListEntry listEntry = new ListEntry(null);
		listEntry.setSeriesDataBaseID(searchedEntry.getId());
		listEntry.setSeriesTitle(searchedEntry.getTitle());
		listEntry.setSeriesSynonyms(searchedEntry.getSynonyms());
//		listEntry.setSeriesType(searchedEntry.getType()); TODO implement SeriesTypeEnum
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
