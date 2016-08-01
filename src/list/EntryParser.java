package list;

import list.entry.Entry;
import list.entry.ListEntry;
import list.entry.SearchedEntry;
import org.jsoup.nodes.Element;

import java.util.concurrent.Callable;


/**
 * Helper class for parsing user MAL
 *
 * Created by maciej on 6/24/16.
 */
public class EntryParser implements Callable<Entry>
{
	public enum EntryType {LIST_ENTRY, SEARCHED_ENTRY};

	private Element element;
	private EntryType entryType;

	public EntryParser(EntryType entryType, Element element)
	{
		this.element = element;
		this.entryType = entryType;
	}

	@Override
	public Entry call() throws Exception
	{
		if (entryType == EntryType.LIST_ENTRY)
		{
			return new ListEntry(element);
		}
		else
		{
			return new SearchedEntry(element);
		}
	}
}
