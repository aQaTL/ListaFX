package list.entry;

/**
 * Helper class for generating xml data of ListEntry or SearchedEntry
 *
 * @author Maciej
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

	public EntryXMLDataBuilder addEpisode(int episode)
	{
		builder.append("<episode>");
		builder.append(episode);
		builder.append("</episode>");

		return this;
	}

	public EntryXMLDataBuilder addStatus(MyStatusEnum status)
	{
		if (status != null)
		{
			builder.append("<status>");
			builder.append(status.getStatusNumber());
			builder.append("</status>");
		}
		return this;
	}

	public EntryXMLDataBuilder addScore(MyScoreEnum score)
	{
		if (score != null)
		{
			builder.append("<score>");
			builder.append(score.getScore());
			builder.append("</score>");
		}
		return this;
	}

	public StringBuilder build()
	{
		builder.append("</entry>");
		return builder;
	}
}
