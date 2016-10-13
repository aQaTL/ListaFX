package list;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import list.entry.ListEntry;
import list.entry.SearchedEntry;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maciej on 06.10.2016.
 */
public class MALParser
{
	public static List<ListEntry> parseToListEntryList(InputStream input) throws XMLStreamException
	{
		XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(input);

		List<ListEntry> entries = new ArrayList<>();
		while (parser.hasNext())
		{
			int event = parser.next();
			if (event == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("anime"))
			{
				event = parser.next();
				String[] entryValues = new String[19];
				for (int i = 0; i < entryValues.length; event = parser.next())
				{
					if (event == XMLStreamConstants.START_ELEMENT)
					{
						entryValues[i] = parser.getElementText();
						i++;
					}
				}

				entries.add(new ListEntry(entryValues));
			}
		}
		return entries;
	}

	public static List<SearchedEntry> parseToSearchedEntryList(InputStream input) throws XMLStreamException
	{
		XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(input);

		List<SearchedEntry> entries = new ArrayList<>();
		while (parser.hasNext())
		{
			int event = parser.next();
			if (event == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("entry"))
			{
				event = parser.next();
				String[] entryValues = new String[12];
				for (int i = 0; i < entryValues.length; event = parser.next())
				{
					if (event == XMLStreamConstants.START_ELEMENT)
					{
						entryValues[i] = parser.getElementText();
						i++;
					}
				}

				entries.add(new SearchedEntry(entryValues));
			}
		}
		return entries;
	}
}
