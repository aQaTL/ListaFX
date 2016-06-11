package list;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO write some javadoc here
 *
 * @author maciej
 */
public class DataService
{
	private String encodedLogin;
	public String malAddress = "http://myanimelist.net/";
	private long userID;

	private ArrayList<ListEntry> entries;

	public DataService(String encodedLogin, String username) throws IOException
	{
		this.encodedLogin = encodedLogin;

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
	public SearchedEntry[] searchForEntries(String seriesTitle)
	{
		try
		{
			String address = malAddress + "api/anime/search.xml?q=" + seriesTitle.replaceAll(" ", "+").toLowerCase();

			Document searchingResultsDocument = Jsoup.connect(malAddress).header("Authorization", encodedLogin).get();
			Elements entries = searchingResultsDocument.getElementsByTag("entry");

			SearchedEntry[] entriesArray = new SearchedEntry[entries.size()];
			entries.forEach(element -> entriesArray[entries.indexOf(element)] = new SearchedEntry(element));

			return entriesArray;
		}
		catch (IOException e)
		{
			return new SearchedEntry[0];
		}
	}


	//Adds new entry to user's list
	public void addEntryToMAL(long id, int initEpisode)
	{
		/*
		 * There should be a place, where user search for anime, and then, selected anime should be written (maybe)
          * as list.SearchedEntry, and then, program should create a XML String with data from list.SearchedEntry, and
          * then, add it to MAL
         */
		try
		{
			String address = "http://myanimelist.net/api/animelist/add/" + id + ".xml";
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
		catch (HttpStatusException e)
		{
			System.err.println("Couldn't add. STATUS=" + e.getStatusCode());
			e.printStackTrace();
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
		System.out.println(entryBuilder.toString());
		try
		{
			Document response = Jsoup.connect(address).data("data", entryBuilder.toString()).header("Authorization", encodedLogin).post();

			if(response.body().ownText().equals("Updated"))
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
