package news.rod.bailey.simplenewsreader;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        INewsService service = new FakeSyncNewsService();
        service.getNews(new GetNewsSuccessHandler(), new GetsNewsFailureHandler());
    }

    private class GetsNewsFailureHandler implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(LOG_TAG, error.getMessage(), error.getCause());
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
            Log.i(LOG_TAG, getString(R.string.action_refresh));

            // Restart the present activity with an empty cache
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                NewsFeedItemArrayAdapter adapter = new NewsFeedItemArrayAdapter(strippedItems);
                ListView listView = (ListView) findViewById(R.id.news_item_list);
                listView.setAdapter(adapter);
            }
            else {
                Log.w(LOG_TAG, "Received news feed string of null");
            }
        }
    }
}
