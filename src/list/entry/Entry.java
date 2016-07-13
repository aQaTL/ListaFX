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
}
