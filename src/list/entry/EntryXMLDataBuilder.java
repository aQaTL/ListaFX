package list.entry;

/**
 * @author Maciej
 *
 * Helper class for generating xml data of ListEntry or SearchedEntry
 */
public class EntryXMLDataBuilder
{
	private StringBuilder builder;

	public EntryXMLDataBuilder()
	{
		builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<entry>");
	}

	public void addEpisode(int episode)
	{
		builder.append("<episode>");
		builder.append(episode);
		builder.append("</episode>");
	}

	public void addStatus(MyStatusEnum status)
	{
		if(status != null)
		{
			builder.append("<status>");
			builder.append(status.getStatusNumber());
			builder.append("</status>");
		}
	}

	public void addScore(MyScoreEnum score)
	{
		if(score != null)
		{
			builder.append("<score>");
			builder.append(score.getScore());
			builder.append("</score>");
		}
	}

	public String getXML()
	{
		builder.append("</entry>");
		return builder.toString();
	}
}
