package list.entry;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

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
}
