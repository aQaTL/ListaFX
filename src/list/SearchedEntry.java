package list;

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
		TextNode node = (TextNode) entry.getElementsByTag("id").first().childNode(0);
		id = Integer.parseInt(node.getWholeText());

		node = (TextNode) entry.getElementsByTag("title").first().childNode(0);
		title = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("english").first().childNode(0);
		englishTitle = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("synonyms").first().childNode(0);
		synonyms = parseSynonyms(node.getWholeText());

		node = (TextNode) entry.getElementsByTag("episodes").first().childNode(0);
		episodes = Integer.parseInt(node.getWholeText());

		node = (TextNode) entry.getElementsByTag("score").first().childNode(0);
		score = Double.parseDouble(node.getWholeText());

		node = (TextNode) entry.getElementsByTag("type").first().childNode(0);
		type = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("status").first().childNode(0);
		status = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("start_date").first().childNode(0);
		startDate = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("end_date").first().childNode(0);
		endDate = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("synopsis").first().childNode(0);
		synopsis = node.getWholeText();

		node = (TextNode) entry.getElementsByTag("image").first().childNode(0);
		image = new Image(node.getWholeText());
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

		return (String[]) synonymsArray.toArray();
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
