package news.rod.bailey.simplenewsreader.json;

import com.google.gson.Gson;

/**
 * Responsible for parsing the feed's JSON into a domain model. Delegates this to Google's GSON library.
 */
public class NewsFeedParser {

    public NewsFeed parseFeedFromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, NewsFeed.class);
    }
}
