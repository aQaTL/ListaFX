package list.entry;

import javafx.scene.image.Image;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;

/**
 * Created by Maciej on 2016-05-25.
 */
public class SearchedEntry extends Entry
{
	private int id;
	private String title, englishTitle;
	private String[] synonyms;
	private int episodes;
	private double score;
	private String type; //TODO Make this enum
	private String status; //Here too
	private String startDate; //TODO Maybe some kind of date class would be nice to use here
	private String endDate; //Here too
	private String synopsis;
	private Image image;

	public SearchedEntry(Element entry)
	{
		this.entry = entry;

		id = Integer.parseInt(getStringFromElement("id"));
		title = getStringFromElement("title");
		englishTitle = getStringFromElement("english");
		synonyms = parseSynonyms(getStringFromElement("synonyms"));
		episodes = Integer.parseInt(getStringFromElement("episodes"));
		score = Double.parseDouble(getStringFromElement("score"));
		type = getStringFromElement("type");
		status = getStringFromElement("status");
		startDate = getStringFromElement("start_date");
		endDate = getStringFromElement("end_date");
		synopsis = getStringFromElement("synopsis");
		image = new Image(getStringFromElement("image"));
	}

	private String[] parseSynonyms(String synonyms)
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

	public int getId()
	{
		return id;
	}

	public String getTitle()
	{
		return title;
	}

	public String getEnglishTitle()
	{
		return englishTitle;
	}

	public String[] getSynonyms()
	{
		return synonyms;
	}

	public int getEpisodes()
	{
		return episodes;
	}

	public double getScore()
	{
		return score;
	}

	public String getType()
	{
		return type;
	}

	public String getStatus()
	{
		return status;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public String getSynopsis()
	{
		return synopsis;
	}

	public Image getImage()
	{
		return image;
	}
}
