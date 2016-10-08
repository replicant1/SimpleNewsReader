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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;
import java.util.List;

import news.rod.bailey.simplenewsreader.adapter.NewsFeedItemArrayAdapter;
import news.rod.bailey.simplenewsreader.json.NewsFeed;
import news.rod.bailey.simplenewsreader.json.NewsFeedItem;
import news.rod.bailey.simplenewsreader.json.NewsFeedParser;
import news.rod.bailey.simplenewsreader.service.FakeSyncNewsService;
import news.rod.bailey.simplenewsreader.service.INewsService;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageLoader imageLoader;

    private ListView listView;

    private INewsService newsService;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_item_list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_item_list_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeToRefreshListener());
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.Red),
                getResources().getColor(R.color.Green),
                getResources().getColor(R.color.Yellow),
                getResources().getColor(R.color.Blue));
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return listView.getFirstVisiblePosition() != 0;
            }
        });

        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "canChildScrollUp=" + swipeRefreshLayout.canChildScrollUp());
        if (item.getItemId() == R.id.action_refresh) {
            Log.i(LOG_TAG, getString(R.string.action_refresh));

            // Restart the present activity - this results in image cache being destroyed and
            // recreated. Also, feed JSON will be reloaded.
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        Log.i(LOG_TAG, "** Into MainActivity.refresh() ****");
        swipeRefreshLayout.setRefreshing(true);



        // @see https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration
        if (imageLoader != null) {
            imageLoader.destroy();
        }

        ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(this).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(imageConfig);

        newsService = new FakeSyncNewsService();
        newsService.getNews(new GetNewsSuccessHandler(), new GetsNewsFailureHandler());
    }

    private class GetsNewsFailureHandler implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(LOG_TAG, error.getMessage(), error.getCause());
            swipeRefreshLayout.setRefreshing(false);
            // TODO: Raise a toast saying "Failed, try refreshing again"
        }
    }

    /**
     *
     */
    private class GetNewsSuccessHandler implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            if (response != null) {
                Log.i(LOG_TAG, "Received news feed string of length " + response.length());

                NewsFeedParser parser = new NewsFeedParser();
                NewsFeed feed = parser.parseFeedFromString(response);

                // Put feed.title in the action bar
                getSupportActionBar().setTitle(feed.title);

                // Title and description are mandatory, image is not.
                List<NewsFeedItem> items = feed.rows;
                List<NewsFeedItem> strippedItems = new LinkedList<NewsFeedItem>();
                for(NewsFeedItem item : items) {
                    if ((item.title != null) && (item.description != null)) {
                        strippedItems.add(item);
                    }
                }

                // Put feed.rows in the list view
                NewsFeedItemArrayAdapter adapter = new NewsFeedItemArrayAdapter(strippedItems, imageLoader);
                ListView listView = (ListView) findViewById(R.id.news_item_list);
                listView.setAdapter(adapter);


            }
            else {
                Log.w(LOG_TAG, "Received news feed string of null");
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class SwipeToRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            Log.i(LOG_TAG, "*** onRefresh() is called ***");

            refresh();
        }
    }
}
