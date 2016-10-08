package news.rod.bailey.simplenewsreader.service;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
        requestQueue = Volley.newRequestQueue(SimpleNewsReaderApplication.context);
    }

    /**
     * Immediately cancels all outstanding HTTP requests that we have previously issued. Ability to do this is one of
     * the nice things about Volley.
     */
    public void cancelAll() {
        requestQueue.cancelAll(VOLLEY_REQUEST_TAG);
    }

    @Override
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler) {
        StringRequest request = new StringRequest(
                Request.Method.GET, //
                ConfigSingleton.getInstance().NewsServiceUrl(), //
                successHandler, //
                failureHandler);
        request.setTag(VOLLEY_REQUEST_TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(ConfigSingleton.getInstance().NewsServiceTimeoutMillis(), 0, 0));

        Log.i(LOG_TAG, "Launching Volley request for JSON data at " + ConfigSingleton.getInstance().NewsServiceUrl());

        requestQueue.add(request);
    }

}
