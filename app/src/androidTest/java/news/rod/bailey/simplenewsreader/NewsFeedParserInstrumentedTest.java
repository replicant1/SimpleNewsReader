package news.rod.bailey.simplenewsreader;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import news.rod.bailey.simplenewsreader.json.NewsFeed;
import news.rod.bailey.simplenewsreader.json.NewsFeedItem;
import news.rod.bailey.simplenewsreader.json.NewsFeedParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class NewsFeedParserInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    public NewsFeedParser parser;

    @Before
    public void createParser() {
        parser = new NewsFeedParser();
    }

    @Test
    public void parseEmptyString() {
        NewsFeed feed = parser.parseFeedFromString("");
        assertNull(feed);
    }

    @Test
    public void parseEmptyJsonObject() {
        NewsFeed feed = parser.parseFeedFromString("{ }");
        assertNotNull(feed);
        assertNull(feed.title);
        assertNull(feed.rows);
    }

    @Test
    public void parseEmptyRows() {
        NewsFeed feed = parser.parseFeedFromString("{ \"title\": \"Scooby Doo\", \"rows\": [ ] }");
        assertNotNull(feed);
        assertEquals("Scooby Doo", feed.title);
        assertNotNull(feed.rows);
        assertEquals(0, feed.rows.size());
    }

    @Test
    public void parseNullFeedProperties() {
        NewsFeed feed = parser.parseFeedFromString(
                "{ \"title\" : null," +
                        "\"rows\" : null }");
        assertNotNull(feed);
        assertNull(feed.title);
        assertNull(feed.rows);
    }

    @Test
    public void parseNullItemProperties() {
        NewsFeed feed = parser.parseFeedFromString(
                "{ \"title\" : \"Literature\"," +
                        "\"rows\": [ " +
                        "{  \"title\"      : null," +
                        "   \"description\": null," +
                        "   \"imageHref\"  : null  } ] }");
        assertNotNull(feed);
        assertEquals("Literature", feed.title);
        assertNotNull(feed.rows);
        assertEquals(1, feed.rows.size());

        NewsFeedItem item = feed.rows.get(0);
        assertNotNull(item);
        assertNull(item.title);
        assertNull(item.description);
        assertNull(item.imageHref);
    }

    @Test
    public void parseOneRow() {
        NewsFeed feed = parser.parseFeedFromString(
                "{ \"title\" : \"Literature\"," +
                        "\"rows\": [ " +
                        "{  \"title\"      : \"Jabberwocky\"," +
                        "   \"description\": \"Twas brillig and the slithy tove did gyre and gimble in the wabe\"," +
                        "   \"imageHref\"  : \"http://www.jabberwocky.com/pics/jabberwocky.jpg\"  } ] }");

        assertNotNull(feed);
        assertEquals("Literature", feed.title);
        assertNotNull(feed.rows);
        assertEquals(1, feed.rows.size());

        NewsFeedItem item = feed.rows.get(0);
        assertNotNull(item);
        assertNotNull(item.title);
        assertNotNull(item.description);
        assertNotNull(item.imageHref);
        assertEquals("Jabberwocky", item.title);
        assertEquals("Twas brillig and the slithy tove did gyre and gimble in the wabe", item.description);
        assertEquals("http://www.jabberwocky.com/pics/jabberwocky.jpg", item.imageHref);
    }


}
