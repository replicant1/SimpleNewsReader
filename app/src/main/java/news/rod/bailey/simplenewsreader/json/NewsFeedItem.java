package news.rod.bailey.simplenewsreader.json;

/**
 * Created by rodbailey on 6/10/2016.
 */

public class NewsFeedItem {

    public  String title;
    public  String description;
    public String imageHref;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append("title=" + title);
        buf.append(",description=" + description);
        buf.append(",imageHref=" + imageHref);
        return buf.toString();
    }
}
