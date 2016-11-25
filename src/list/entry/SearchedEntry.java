package list.entry;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import list.entry.data.SeriesTypeEnum;
import org.jsoup.nodes.Element;

/**
 * This class represents single entry of search results
 *
 * Created by Maciej on 2016-05-25.
 */
public final class SearchedEntry extends Entry
{
	private String englishTitle;
	private double score;
	private String status; //Here too
	private String synopsis;

	public SearchedEntry(Element entry)
	{
		super(entry);
	}

	public SearchedEntry(String[] values)
	{
		super();

		initFields(values);
		loadFXML();
	}

	private void initFields(String[] values)
	{
		databaseId = Integer.parseInt(values[0]);
		title = values[1];
		englishTitle = values[2];
		synonyms = parseSynonyms(values[3]);
		episodes = Integer.parseInt(values[4]);
		score = Double.parseDouble(values[5]);
		seriesType = SeriesTypeEnum.valueOf(SeriesTypeEnum.class, values[6].toUpperCase());
		status = values[7];
		startDate = values[8];
		endDate = values[9];
		synopsis = values[10];
		imageUrl = values[11];

		website = super.getWebsite(databaseId);
	}

	@Override
	protected void initView()
	{
		typeOrScoreLabel.setText("Type");
		titleLabel.setText(getTitle());
		imageView.setImage(getImage());
		episodesField.setText(Integer.toString(getEpisodes()));
		typeOrScoreField.setText(getSeriesType().toString());
	}

	@Override
	protected void initFields()
	{
		databaseId = Integer.parseInt(getStringFromElement("id"));
		title = getStringFromElement("title");
//		englishTitle = getStringFromElement("english");
//		synonyms = parseSynonyms(getStringFromElement("synonyms"));
		episodes = Integer.parseInt(getStringFromElement("episodes"));
//		score = Double.parseDouble(getStringFromElement("score"));
		seriesType = SeriesTypeEnum.valueOf(getStringFromElement("type").toUpperCase());
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

	public String getEnglishTitle()
	{
		return englishTitle;
	}

	public double getScore()
	{
		return score;
	}

	public String getStatus()
	{
		return status;
	}

	public String getSynopsis()
	{
		return synopsis;
	}
}
