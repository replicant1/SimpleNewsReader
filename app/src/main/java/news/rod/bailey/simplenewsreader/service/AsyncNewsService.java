package news.rod.bailey.simplenewsreader.service;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;
import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * The implementation of INewsService that is actually used in production. The data is retrieved from a remote source
 * asynchronously and returned in a thread different from the thread that called getNews().
 */
public class AsyncNewsService implements INewsService {

    /**
     * Identifies log statements from this class
     */
    private static final String LOG_TAG = AsyncNewsService.class.getSimpleName();

    /**
     * Identifies async jobs issued by this class (so they can be canelled later)
     */
    private static final String VOLLEY_REQUEST_TAG = "async-news-service-job";

    /**
     * Volley request queue that all HTTP GET requests for news data are placed upon. There should only be one at a
     * time.
     */
    private final RequestQueue requestQueue;

    public AsyncNewsService() {
        Cache cache = new DiskBasedCache(
                SimpleNewsReaderApplication.context.getCacheDir(),
                ConfigSingleton.getInstance().NewsServiceCacheKB() * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    @Override
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler) {
        String newsServiceURL = ConfigSingleton.getInstance().NewsServiceUrl();

        // First check if it's in the cache
        Cache cache = requestQueue.getCache();
        Cache.Entry cacheEntry = cache.get(newsServiceURL);

        if (cacheEntry != null) {
            Log.i(LOG_TAG, "Retrieving from Volley cache: " + newsServiceURL);

            try {
                String data = new String(cacheEntry.data, "UTF-8");
                successHandler.onResponse(data);
            } catch (UnsupportedEncodingException x) {
                Log.w(LOG_TAG, "Failed to decode string retrieved from cache", x);
                failureHandler.onErrorResponse(new VolleyError("Failed to decode cached version of JSON feed"));
            }
        } else {
            Log.i(LOG_TAG, "Retrieving from network: " + newsServiceURL);

            // Cancel any incomplete requests for the news feed
            requestQueue.cancelAll(VOLLEY_REQUEST_TAG);

            StringRequest request = new StringRequest(
                    Request.Method.GET, //
                    ConfigSingleton.getInstance().NewsServiceUrl(), //
                    successHandler, //
                    failureHandler);
            request.setTag(VOLLEY_REQUEST_TAG);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    ConfigSingleton.getInstance().NewsServiceTimeoutMillis(), // Initial timeout
                    ConfigSingleton.getInstance().NewsServiceRetriesMax(), // Max retries
                    0)); // Backoff multiplier
            request.setShouldCache(true);

            Log.i(LOG_TAG, "Launching Volley request for JSON data at " + ConfigSingleton.getInstance().NewsServiceUrl());

            requestQueue.add(request);
        }
    }

}
