package news.rod.bailey.simplenewsreader.json;

import java.util.Collections;
import java.util.List;

/**
 * Top level object in the domain model. Represents an entire JSON feed whose content is to be displayed.
 * Field values are assigned by GSON library.
 */
public class NewsFeed {
    public List<NewsFeedItem> rows;

    public String title;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append(":title=" + title);
        buf.append(",rows=");

        if (rows == null) {
            buf.append("null");
        } else {
            buf.append("[");
            for (NewsFeedItem item : rows) {
                buf.append(item.toString() + ",");
            }
            buf.append("]");
        }

        return buf.toString();
    }
}
