package list.entry;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import list.entry.data.SeriesTypeEnum;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;

/**
 * This class represents single entry of search results
 *
 * Created by Maciej on 2016-05-25.
 */
public final class SearchedEntry extends Entry
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
		super(entry, "EntryView.fxml");
	}

	@Override
	protected void initView()
	{
		titleLabel.setText(getTitle());
		imageView.setImage(getImage());
		episodesField.setText(Integer.toString(getEpisodes()));
		typeOrScoreField.setText(getType().toString());
	}

	@Override
	protected void initFields()
	{
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

	@FXML
	private void handleDetailsButton(MouseEvent event)
	{
		if(eventHandler != null)
			eventHandler.handleEvent(this);
	}

	//====================
	//GETTERS AND SETTERS
	//====================

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
