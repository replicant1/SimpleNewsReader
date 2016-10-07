package news.rod.bailey.simplenewsreader.service;

import com.android.volley.Response;

/**
 * All access to remote data occurs via this interface. There are two implementations of it
 * provided - one for testing and one for production. The "test" implementation reads JSON from
 * assets/sample.json. The "production" implementation reads the JSON from a remote URL.
 */
public interface INewsService {

    /**
     * Kicks off asynchronous loading of travel times data. Callbacks occur in either the calling thread or a
     * different thread, depending on the implementation. Data may be sourced remotely or locally, depending on the
     * implementation.
     *
     * @param successHandler Once loaded and parsed OK, the data is passed back to this party
     * @param failureHandler If data can't be loaded or parsed, for any reason, this party is
     *                       called with a displayable string indicating the reason for failure.
     */
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler);
}
