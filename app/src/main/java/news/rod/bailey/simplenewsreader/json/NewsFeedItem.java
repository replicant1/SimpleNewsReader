package news.rod.bailey.simplenewsreader.json;

/**
 * Second level object in domain model. Each instance corresponds to an element of the "rows" array in the feed's JSON.
 * Field values are supplied by GSON.
 */
public class NewsFeedItem {

    public String description;

    public String imageHref;

    public String title;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append("title=" + title);
        buf.append(",description=" + description);
        buf.append(",imageHref=" + imageHref);
        return buf.toString();
    }
}
