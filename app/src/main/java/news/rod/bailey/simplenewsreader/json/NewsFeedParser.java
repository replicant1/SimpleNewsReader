package news.rod.bailey.simplenewsreader.json;

import com.google.gson.Gson;

/**
 * Created by rodbailey on 6/10/2016.
 */
public class NewsFeedParser {

    public NewsFeed parseFeedFromString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, NewsFeed.class);
    }
}
