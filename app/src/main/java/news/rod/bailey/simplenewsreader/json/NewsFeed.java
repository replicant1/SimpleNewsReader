package news.rod.bailey.simplenewsreader.json;

import java.util.Collections;
import java.util.List;

/**
 * Created by rodbailey on 6/10/2016.
 */
public class NewsFeed {
    public String title;

    public List<NewsFeedItem> rows;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append(":title=" + title);
        buf.append(",rows=");

        if (rows == null) {
            buf.append("null");
        }
        else {
            buf.append("[");
            for (NewsFeedItem item : rows) {
                buf.append(item.toString() + ",");
            }
            buf.append("]");
        }

        return buf.toString();
    }
}
