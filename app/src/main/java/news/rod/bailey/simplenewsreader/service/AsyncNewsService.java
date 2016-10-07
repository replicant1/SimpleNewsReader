package news.rod.bailey.simplenewsreader.service;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;

/**
 * The implementation of INewsService that is actually used in production. The data is retrieved from a remote source
 * asynchronously and returned in a thread different from the thread that called getNews().
 */
public class AsyncNewsService implements INewsService {

    private static final String TAG = AsyncNewsService.class.getSimpleName();

    private static final String VOLLEY_TAG = "async-news-service-job";

    private final RequestQueue requestQueue;

    public AsyncNewsService() {
        requestQueue = Volley.newRequestQueue(SimpleNewsReaderApplication.context);
    }

    @Override
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler) {

    }

    public void cancelAll() {
        requestQueue.cancelAll(VOLLEY_TAG);
    }

}
