package list;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class ListEntry implements Serializable
{

    public enum Tag
    {
        ACTION, MAGIC, SCHOOL
    }

    private String name, description;
    private int episodeCounter;
    private URL website;
    private ArrayList<Tag> tags;

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getEpisodeNumber()
    {
        return episodeCounter;
    }

    public URL getWebsite()
    {
        return website;
    }

    public ArrayList<Tag> getTags()
    {
        return tags;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEpisodeCounter(int episodeCounter)
    {
        this.episodeCounter = episodeCounter;
    }

    public void setWebsite(URL website)
    {
        this.website = website;
    }

    public void setTags(ArrayList<Tag> tags)
    {
        this.tags = tags;
    }

    @Override
    public String toString()
    {
        return name;
    }


}
