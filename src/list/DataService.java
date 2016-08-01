package list;

import list.entry.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * DataService object represents logged user account. This class provides basic operations on user MAL:
 * getting user list,
 * adding new entry,
 * deleting entry.
 * updating entry.
 *
 * @author maciej
 */
public class DataService
{
	private String encodedLogin;

	public static String malAddress = "http://myanimelist.net/";

	public static final int PARSER_THREADS = 4;

	private ArrayList<ListEntry> entries;
	private Map<Integer, URL> customURLs;

	private ExecutorService parserThreadPool;
	private boolean locked = false;

	/**
	 * Represents user account with correct credentials. If credentials aren't correct, throws IOException.
	 * Then, downloads xml with user list and parses it to ArrayList of ListEntry
	 *
	 * @param encodedLogin used for authenticating operations on list
	 * @param username user login
	 * @throws IOException
	 */
	public DataService(String encodedLogin, String username) throws IOException
	{
		this.encodedLogin = encodedLogin;
		parserThreadPool = Executors.newFixedThreadPool(PARSER_THREADS);

		String userListAddress = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime";
		Document userListDocument = Jsoup.connect(userListAddress).get();

		customURLs = loadCustomWebsites();

		Elements animeEntries = userListDocument.getElementsByTag("anime");
		entries = new ArrayList<>(animeEntries.size());

		animeEntries.forEach(entry ->
		{
			ListEntry listEntry = new ListEntry(entry);

			if (customURLs.containsKey(listEntry.getSeriesDataBaseID()))
				listEntry.setWebsite(customURLs.get(listEntry.getSeriesDataBaseID()));

			entries.add(listEntry);
		});
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
		if (!locked)
		{
			try
			{
				locked = true;

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

				for (int i = 0; i < amountOfEntries; i++)
				{
					entriesArray[i] = (SearchedEntry) futureTasks.get(i).get();
				}

				System.out.println(System.currentTimeMillis() - milis);
				return entriesArray;
			}
			catch (IOException | InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
			finally
			{
				locked = false;
			}
		}
		return new SearchedEntry[0];
	}


	/**
	 * Tires to add given entry to MAL
	 *
	 * @param entry  that is about to be added
	 * @param initEpisode initial episode
	 * @return added ListEntry
	 */
	public ListEntry addEntryToMAL(SearchedEntry entry, int initEpisode)
	{
		try
		{
			String address = "http://myanimelist.net/api/animelist/add/" + entry.getId() + ".xml";

			StringBuilder xmlData = new EntryXMLDataBuilder()
					.addEpisode(initEpisode)
					.addStatus(MyStatusEnum.WATCHING)
					.addScore(MyScoreEnum.NOT_RATED_YET)
					.build();

			Document addAnimeDocument = Jsoup.connect(address).data("data", xmlData.toString()).header("Authorization", encodedLogin).post();

			if (addAnimeDocument.title().contains("Created"))
			{
				return Entry.convertToListEntry(entry);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tries to delete entry form MAL
	 *
	 * @param id of entry that you want delete
	 * @return true if entry has been deleted successfully; otherwise false
	 */
	public boolean deleteEntryFromMAL(long id)
	{
		try
		{
			String address = malAddress + "api/animelist/delete/" + id + ".xml";
			Document response = Jsoup.connect(address).header("Authorization", encodedLogin).get();

			if (response.body().ownText().equals("Deleted"))
				return true;

			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
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

		StringBuilder xmlData = new EntryXMLDataBuilder()
				.addEpisode(entry.getMyWatchedEpisodes())
				.addStatus(entry.getMyStatus())
				.addScore(entry.getMyScore())
				.build();

		try
		{
			Document response = Jsoup.connect(address).data("data", xmlData.toString()).header("Authorization", encodedLogin).post();

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

	/**
	 * Looks for given ListEntry and changes its website field
	 *
	 * @param entry the ListEntry, which website will be changed
	 * @param website to store in given entry
	 */
	public void addCustomWebiste(ListEntry entry, URL website)
	{
		entries.get(entries.indexOf(entry)).setWebsite(website);
		customURLs.put(entry.getSeriesDataBaseID(), website);
	}

	/**
	 * Looks for customURLs.dat file in app dir.
	 *
	 * @return Either deserialized or new HashMap used to store customURls for MAL entries
	 */
	private HashMap<Integer, URL> loadCustomWebsites()
	{
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("customURLs.dat")))
		{
			return (HashMap<Integer, URL>) in.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			return new HashMap<>();
		}
	}

	/**
	 * Stores serialized list of user custom URLs (in app directory), since they aren't part of MAL.
	 */
	private void storeCustomWebsites()
	{
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("customURLs.dat")))
		{
			out.writeObject(customURLs);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void storeUserPrefs()
	{
		storeCustomWebsites();
	}

	public void storePassword() //TODO
	{
		try
		{
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			SecretKey aesKey = keyGenerator.generateKey();

			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

			byte[] loginToStore = encodedLogin.getBytes();

			byte[] encodedLogin = aesCipher.doFinal(loginToStore);

			aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
			byte[] decryptedLogin = aesCipher.doFinal(encodedLogin);

			System.out.println("Comparing logins...");
			System.out.println(loginToStore);
			System.out.println(decryptedLogin);

		}
		catch (GeneralSecurityException e)
		{
			System.err.println("Couldn't save your password!");
			e.printStackTrace();
		}
	}

	private String loadCredentials()
	{
		return null;
	}
}
