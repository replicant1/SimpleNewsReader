package news.rod.bailey.simplenewsreader;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;
import java.util.List;

import news.rod.bailey.simplenewsreader.adapter.NewsFeedItemArrayAdapter;
import news.rod.bailey.simplenewsreader.app.NewsFeedCacheSingleton;
import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;
import news.rod.bailey.simplenewsreader.json.NewsFeed;
import news.rod.bailey.simplenewsreader.json.NewsFeedItem;
import news.rod.bailey.simplenewsreader.json.NewsFeedParser;
import news.rod.bailey.simplenewsreader.service.INewsService;
import news.rod.bailey.simplenewsreader.service.NewsServiceFactorySingleton;
import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * Sole activity of the SimpleNewsReader application. This app reads a JSON news feed from a URL specified in the
 * config.properties file and displays each new item in a list, one row per news item. Note that if a new item does
 * not have BOTH a title and a description defined, it is not included in the list. The image is considered optional.
 * The app occupies only a single screen.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * For Log statements
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Main component - a list of news items, one item per row
     */
    private ListView listView;

    /**
     * Service from which news is retrieved. May be local/remote or a/sync depending on the implementation
     */
    private INewsService newsService;

    /**
     * Provides pull-to-hardRefresh functionaity for the listview of news items
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * @param feed The NewsFeed, presumed already cached, that is to be installed in the UI. It's title will be
     *                 put in the title bar, it's "rows" data will be put in the list view.
     */
    private void installNewsFeed(NewsFeed feed) {
        // Put feed.title in the action bar
        getSupportActionBar().setTitle(feed.title);

        // Title and description are mandatory, image is not.
        List<NewsFeedItem> items = feed.rows;
        List<NewsFeedItem> strippedItems = new LinkedList<NewsFeedItem>();

        for (NewsFeedItem item : items) {
            if ((item.title != null) && (item.description != null)) {
                strippedItems.add(item);
            }
        }

        // Put parsed data in the list view
        NewsFeedItemArrayAdapter adapter = new NewsFeedItemArrayAdapter(
                strippedItems, ImageLoader.getInstance());
        ListView listView = (ListView) findViewById(R.id.news_item_list);
        listView.setAdapter(adapter);
    }

    private void maybeInitImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            // Clear the singleton image cache (bit not what' on disk).
            int memoryCacheBytes = ConfigSingleton.getInstance().ImageLoaderMemoryCacheKB() * 1024;
            int diskCacheBytes = ConfigSingleton.getInstance().ImageLoaderDiskCacheKB() * 1024;

            ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheSize(memoryCacheBytes)
                    .diskCacheSize(diskCacheBytes)
                    .writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(imageConfig);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "Creating MainActivity");

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_item_list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_item_list_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeToRefreshListener());
        swipeRefreshLayout.setOnChildScrollUpCallback(new ChildScrollUpCallback());
        swipeRefreshLayout.setDistanceToTriggerSync(ConfigSingleton.getInstance().SwipeRefreshLayoutPullDistanceDP());

        maybeInitImageLoader();

        if (NewsFeedCacheSingleton.getInstance().containsFeedForUrl(ConfigSingleton.getInstance().NewsServiceUrl())) {
            // We've already got the feed data cached, so no need to reload it. This happens when the MainActivity is
            // re-created after an orientation change.
            NewsFeed newsFeed = NewsFeedCacheSingleton.getInstance().getFeedForUrl(
                    ConfigSingleton.getInstance().NewsServiceUrl());
            installNewsFeed(newsFeed);
        } else {
            // News feed data not yet loaded - go fetch it.
            hardRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            hardRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void raiseFailureToast() {
        Toast.makeText(SimpleNewsReaderApplication.context, R.string.news_feed_load_failure, Toast
                .LENGTH_LONG).show();
    }

    /**
     * Initiates a complete reloading of data. The imageLoader is destroyed and recreated, so all cached images are
     * lost. The JSON feed, which is only  "cached" by Volley and in the ListView itself, is also destroyed and must
     * be refetched.
     */
    private void hardRefresh() {
        // Set the "hardRefresh" animation going. Will be turned off in the GetNewsSuccessHandler and GetsNewsFailureHandler
        swipeRefreshLayout.setRefreshing(true);

        NewsFeedCacheSingleton.getInstance().clear();

        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();

        // Create the INewsService as per config.properties.
        newsService = NewsServiceFactorySingleton.getSingleton().getNewsService();
        newsService.getNews(new GetNewsSuccessHandler(), new GetsNewsFailureHandler());
    }

    /**
     * Handles failure of the HTTP GET that is used to retrieve the feed's JSON. Just cancels the "hardRefresh" animation
     * and raises a Toast with an error message in it.
     */
    private class GetsNewsFailureHandler implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(LOG_TAG, error.getMessage(), error.getCause());
            NewsFeedCacheSingleton.getInstance().clear();
            swipeRefreshLayout.setRefreshing(false);
            raiseFailureToast();
        }
    }

    /**
     * Handles successfull retrieval of the feed's JSON file. Parses out the data into the local domain model then
     * feeds that model into the list view via the NewsFeedItemArrayAdapter. Cancels the "hardRefresh" animation.
     */
    private class GetNewsSuccessHandler implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            if (response != null) {
                Log.i(LOG_TAG, "Received news feed string of length " + response.length());

                // Parse retrieved JSON string into domain objects
                NewsFeedParser parser = new NewsFeedParser();
                NewsFeed feed = parser.parseFeedFromString(response);

                // Cache the feed
                NewsFeedCacheSingleton.getInstance().putFeedForUrl(feed, ConfigSingleton.getInstance().NewsServiceUrl());

                installNewsFeed(feed);
            } else {
                Log.w(LOG_TAG, "Received news feed string of null");
                raiseFailureToast();
            }

            swipeRefreshLayout.setRefreshing(false);

        } // onResponse()
    }

    /**
     * Handles when user swipes on list view in such a way as to indicate they want to "pull to hardRefresh".
     */
    private class SwipeToRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            hardRefresh();
        }
    }

    /**
     * Bug fix supplied in support library v24.1.0 - enables us to turn off triggerring of the hardRefresh with the
     * "pull" gesture unless the first row of the list is showing.
     */
    private class ChildScrollUpCallback implements SwipeRefreshLayout.OnChildScrollUpCallback {
        @Override
        public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
            return listView.getFirstVisiblePosition() != 0;
        }
    }
}
