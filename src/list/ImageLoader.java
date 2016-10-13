package list;

import javafx.scene.image.Image;
import list.entry.Entry;
import list.entry.ListEntry;
import list.entry.SearchedEntry;

import java.util.List;

/**
 * Created by Maciej on 08.10.2016.
 */
public class ImageLoader implements Runnable
{
	private List<? extends Entry> entries;

	public ImageLoader(List<? extends Entry> entries)
	{
		this.entries = entries;
	}

	@Override
	public void run()
	{
		entries.forEach(entry -> entry.setImage(new Image(entry.getImageUrl())));
	}
}
