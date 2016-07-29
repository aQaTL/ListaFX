package list.entry;

import javafx.scene.image.Image;
import org.jsoup.nodes.Element;

import java.net.URL;

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
	private SeriesTypeEnum type;
	private String status; //Here too
	private String startDate; //TODO Maybe some kind of date class would be nice to use here
	private String endDate; //Here too
	private String synopsis;
	private Image image;
	private URL website;

	public SearchedEntry(Element entry)
	{
		this.entry = entry;

		id = Integer.parseInt(getStringFromElement("id"));
		title = getStringFromElement("title");
//		englishTitle = getStringFromElement("english");
//		synonyms = parseSynonyms(getStringFromElement("synonyms"));
		episodes = Integer.parseInt(getStringFromElement("episodes"));
//		score = Double.parseDouble(getStringFromElement("score"));
		type = SeriesTypeEnum.valueOf(getStringFromElement("type").toUpperCase());
//		status = getStringFromElement("status");
//		startDate = getStringFromElement("start_date");
//		endDate = getStringFromElement("end_date");
		synopsis = getStringFromElement("synopsis");
		image = new Image(getStringFromElement("image"));
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
		if (englishTitle == null)
			englishTitle = getStringFromElement("english");

		return englishTitle;
	}

	public String[] getSynonyms()
	{
		if (synonyms == null)
			parseSynonyms(getStringFromElement("synonyms"));

		return synonyms;
	}

	public int getEpisodes()
	{
		return episodes;
	}

	public double getScore()
	{
		if (score == 0)
			score = Double.parseDouble(getStringFromElement("score"));

		return score;
	}

	public SeriesTypeEnum getType()
	{
		return type;
	}

	public String getStatus()
	{
		if (status == null)
			status = getStringFromElement("status");

		return status;
	}

	public String getStartDate()
	{
		if (startDate == null)
			startDate = getStringFromElement("start_date");

		return startDate;
	}

	public String getEndDate()
	{
		if (endDate == null)
			endDate = getStringFromElement("end_date");

		return endDate;
	}

	public String getSynopsis()
	{
		if (synopsis == null)
			synopsis = getStringFromElement("synopsis");

		return synopsis;
	}

	public Image getImage()
	{
		return image;
	}

	public URL getWebsite()
	{
		if(website == null)
			website = super.getWebsite(id);

		return website;
	}
}
