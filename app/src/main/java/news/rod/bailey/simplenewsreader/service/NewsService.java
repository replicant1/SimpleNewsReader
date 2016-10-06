package news.rod.bailey.simplenewsreader.service;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;

/**
 * Created by rodbailey on 6/10/2016.
 */
public class NewsService implements INewsService {

    private static final String TAG = NewsService.class.getSimpleName();

    private static final String VOLLEY_TAG = "fake-news-service-job";

    private final RequestQueue requestQueue;

    public NewsService() {
        requestQueue = Volley.newRequestQueue(SimpleNewsReaderApplication.context);
    }

    @Override
    public void getNewsAsync(Response.Listener successHandler, Response.ErrorListener failureHandler) {

    }

    public void cancelAll() {
        requestQueue.cancelAll(VOLLEY_TAG);
    }
}
