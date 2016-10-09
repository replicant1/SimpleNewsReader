package news.rod.bailey.simplenewsreader.app;

import java.util.HashMap;
import java.util.Map;

import news.rod.bailey.simplenewsreader.json.NewsFeed;

/**
 * Created by rodbailey on 9/10/2016.
 */

public class NewsFeedCacheSingleton {

    private static final NewsFeedCacheSingleton singleton = new NewsFeedCacheSingleton();

    private final Map<String, NewsFeed> urlToFeed = new HashMap<String, NewsFeed>();

    public static NewsFeedCacheSingleton getInstance() {
        return singleton;
    }

    public boolean containsFeedForUrl(String url) {
        return urlToFeed.containsKey(url);
    }

    public NewsFeed getFeedForUrl(String url) {
        return urlToFeed.get(url);
    }

    public void putFeedForUrl(NewsFeed feed, String url) {
        urlToFeed.put(url, feed);
    }

    public void removeFeed(String feedUrl) {
        urlToFeed.remove(feedUrl);
    }

    public void clear() {
        urlToFeed.clear();
    }


}
