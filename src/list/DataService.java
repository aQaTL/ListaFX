package list;

import javafx.concurrent.Task;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.stream.XMLStreamException;
import list.entry.Entry;
import list.entry.EntryXMLDataBuilder;
import list.entry.ListEntry;
import list.entry.SearchedEntry;
import list.entry.data.MyScoreEnum;
import list.entry.data.MyStatusEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;
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

	private List<ListEntry> entries;
	private Map<Integer, URL> customURLs;

	private ExecutorService parserThreadPool;

	/**
	 * Represents user account with correct credentials. If credentials aren't correct, throws IOException.
	 * Then, downloads xml with user list and parses it to ArrayList of ListEntry
	 *
	 * @param encodedLogin used for authenticating operations on list
	 * @param username     user login
	 * @throws IOException
	 */
	public DataService(String encodedLogin, String username) throws IOException, XMLStreamException
	{
		this.encodedLogin = encodedLogin;
		parserThreadPool = Executors.newFixedThreadPool(PARSER_THREADS);

		String userListAddress = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime";

		customURLs = loadCustomWebsites();

		System.out.println("Parsing...");
		long startTime = System.currentTimeMillis();

		try (InputStream url = new URL(userListAddress).openStream())
		{
			entries = MALParser.parseToListEntryList(url);
		}

		//Loads local custom websites
		entries.forEach(entry ->
		{
			if(customURLs.containsKey(entry.getDatabaseId()))
				entry.setWebsite(customURLs.get(entry.getDatabaseId()));
		});

		System.out.println("Parsing ended. Time " + (System.currentTimeMillis() - startTime));
	}

	public List<ListEntry> getEntries()
	{
		return entries;
	}

	/**
	 * Searches for entries that match to given title
	 *
	 * @param seriesTitle title of the sought anime
	 * @return array of ListEntry that have been found
	 */
	public Task<SearchedEntry[]> searchForEntries(String seriesTitle)
	{
		return new Task<SearchedEntry[]>()
		{
			@Override
			protected SearchedEntry[] call() throws Exception
			{
				String address = malAddress + "api/anime/search.xml?q=" + seriesTitle.replaceAll(" ", "+").toLowerCase();

				HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Authorization", encodedLogin);
				connection.setRequestMethod("GET");

				InputStream response = connection.getInputStream();
				return MALParser.parseToSearchedEntryList(response).toArray(new SearchedEntry[0]);
			}
		};
	}


	/**
	 * Tires to add given entry to MAL
	 *
	 * @param entry       that is about to be added
	 * @param initEpisode initial episode
	 * @return added ListEntry
	 */
	public Task<ListEntry> addEntryToMAL(SearchedEntry entry, int initEpisode)
	{
		return new Task<ListEntry>()
		{
			@Override
			protected ListEntry call() throws Exception
			{
				String address = "http://myanimelist.net/api/animelist/add/" + entry.getDatabaseId() + ".xml";

				StringBuilder xmlData = new EntryXMLDataBuilder()
						.addEpisode(initEpisode)
						.addStatus(MyStatusEnum.WATCHING)
						.addScore(MyScoreEnum.NOT_RATED_YET)
						.build();

				Document addAnimeDocument = Jsoup.connect(address).data("data", xmlData.toString()).header("Authorization", encodedLogin).post();

				if (addAnimeDocument.title().contains("Created"))
					return Entry.convertToListEntry(entry);

				return null;
			}
		};
	}

	/**
	 * Tries to delete entry form MAL
	 *
	 * @param id of entry that you want delete
	 * @return true if entry has been deleted successfully; otherwise false
	 */
	public Task<Boolean> deleteEntryFromMAL(long id)
	{
		return new Task<Boolean>()
		{
			@Override
			protected Boolean call() throws Exception
			{
				String address = malAddress + "api/animelist/delete/" + id + ".xml";
				Document response = Jsoup.connect(address).header("Authorization", encodedLogin).get();

				if (response.body().ownText().equals("Deleted"))
					return true;

				return false;
			}
		};
	}

	/**
	 * Tries to update given entry in user anime list
	 *
	 * @param entry to update
	 * @return true if server returns "Updated"
	 */
	public Task<Boolean> updateEntryToMAL(ListEntry entry)
	{
		return new Task<Boolean>()
		{
			@Override
			public Boolean call() throws IOException
			{
				String address = malAddress + "api/animelist/update/" + entry.getDatabaseId() + ".xml";

				StringBuilder xmlData = new EntryXMLDataBuilder()
						.addEpisode(entry.getMyWatchedEpisodes())
						.addStatus(entry.getMyStatus())
						.addScore(entry.getMyScore())
						.build();

				Document response = Jsoup.connect(address).data("data", xmlData.toString()).header("Authorization", encodedLogin).post();

				if (response.body().ownText().equals("Updated"))
					return true;

				return false;
			}
		};
	}

	/**
	 * Looks for given ListEntry and changes its website field
	 *
	 * @param entry   the ListEntry, which website will be changed
	 * @param website to store in given entry
	 */
	public void addCustomWebiste(ListEntry entry, URL website)
	{
		entries.get(entries.indexOf(entry)).setWebsite(website);
		customURLs.put(entry.getDatabaseId(), website);
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
