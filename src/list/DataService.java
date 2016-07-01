package list;

import list.entry.Entry;
import list.entry.ListEntry;
import list.entry.SearchedEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * TODO write some javadoc here
 *
 * @author maciej
 */
public class DataService
{
	private String encodedLogin;
	public String malAddress = "http://myanimelist.net/";

	public static final int PARSER_THREADS = 4;

	private ArrayList<ListEntry> entries;
	private ExecutorService parserThreadPool;

	public DataService(String encodedLogin, String username) throws IOException
	{
		this.encodedLogin = encodedLogin;
		parserThreadPool = Executors.newFixedThreadPool(PARSER_THREADS);

		String userListAddress = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime";
		Document userListDocument = Jsoup.connect(userListAddress).get();

		Elements animeEntries = userListDocument.getElementsByTag("anime");
		entries = new ArrayList<>(animeEntries.size());
		animeEntries.forEach(entry -> entries.add(new ListEntry(entry)));
	}

	public ArrayList<ListEntry> getEntries()
	{
		return entries;
	}

	/**
	 * Searches for entries that match to given title
	 *
	 * @param seriesTitle title of the sought anime
	 * @return array of ListEntry that have been found
	 */
	public synchronized SearchedEntry[] searchForEntries(String seriesTitle)
	{
		try
		{
			String address = malAddress + "api/anime/search.xml?q=" + seriesTitle.replaceAll(" ", "+").toLowerCase();

			Document searchingResultsDocument = Jsoup.connect(address).header("Authorization", encodedLogin).get();
			Elements entries = searchingResultsDocument.getElementsByTag("entry");
			int amountOfEntries = entries.size();

			long milis = System.currentTimeMillis();
			System.out.println("Amount of entries: " + amountOfEntries);

			SearchedEntry[] entriesArray = new SearchedEntry[amountOfEntries];
			List<Future<Entry>> futureTasks = new ArrayList<>(amountOfEntries);

			//Multithreading here
			entries.forEach(entry -> futureTasks.add(parserThreadPool.submit(new EntryParser(EntryParser.EntryType.SEARCHED_ENTRY, entry))));

			for(int i = 0; i < amountOfEntries; i++)
			{
				entriesArray[i] = (SearchedEntry) futureTasks.get(i).get();
			}

			System.out.println(System.currentTimeMillis() - milis);
			return entriesArray;
		}
		catch (IOException e)
		{
			return new SearchedEntry[0];
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return new SearchedEntry[0];
	}


	//Adds new entry to user's list
	public void addEntryToMAL(SearchedEntry entry, int initEpisode)
	{
		/*
		 * There should be a place, where user search for anime, and then, selected anime should be written (maybe)
          * as list.entry.SearchedEntry, and then, program should create a XML String with data from list.entry.SearchedEntry, and
          * then, add it to MAL
         */
		try
		{
			String address = "http://myanimelist.net/api/animelist/add/" + entry.getId() + ".xml";
			String entryToAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
					"<entry>" +
					"<episode>" + initEpisode + "</episode>" +
					"<status>1</status>" +
					"<score>0</score>" +
					"</entry>";

			Document addAnimeDocument = Jsoup.connect(address).data("data", entryToAdd).header("Authorization", encodedLogin).post();

			if (addAnimeDocument.title().contains("Created"))
			{
				System.out.println("Added successfully");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void deleteEntryFromMAL(long id)
	{
		try
		{
			String address = malAddress + "api/animelist/delete/" + id + ".xml";
			Jsoup.connect(address).header("Authorization", encodedLogin).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Tries to update given entry in user anime list
	 *
	 * @param entry to update
	 * @return true if server returns "Updated"
	 */
	public boolean updateEntryToMAL(ListEntry entry)
	{
		String address = malAddress + "api/animelist/update/" + entry.getSeriesDataBaseID() + ".xml";

		StringBuilder entryBuilder = new StringBuilder();
		entryBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		entryBuilder.append("<entry>");
		entryBuilder.append("<episode>" + entry.getMyWatchedEpisodes() + "</episode>");
		entryBuilder.append("<status>" + entry.getMyStatus().getStatusNumber() + "</status>");
		entryBuilder.append("<score>" + entry.getMyScore().getScore() + "</score>");
		entryBuilder.append("</entry>");

		try
		{
			Document response = Jsoup.connect(address).data("data", entryBuilder.toString()).header("Authorization", encodedLogin).post();

			if (response.body().ownText().equals("Updated"))
				return true;
			else
				return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
