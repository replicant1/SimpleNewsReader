package news.rod.bailey.simplenewsreader.service;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * An implementation of INewsService that is suitable for local testing. Returns dummy data
 * read from /assets/sample.json with a delay of a few seconds artificially created to simulate
 * network latency.
 */
public class FakeNewsServce implements INewsService {
    public static final boolean OPERATION_SUCCEEDS =
            ConfigSingleton.getInstance().FakeNewsServiceSucceeds();

    private static final String TAG = FakeNewsServce.class.getSimpleName();

    private static final String SAMPLE_JSON_ASSET =
            ConfigSingleton.getInstance().FakeNewsServiceAsset();

    private static final long MILLIS_FAKE_DELAY =
            ConfigSingleton.getInstance().FakeNewsServiceDelayMillis();

    @Override
    public void getNewsAsync(Response.Listener successHandler, Response.ErrorListener failureHandler) {
        FakeNetworkAccessJob job = new FakeNetworkAccessJob(
                successHandler,
                failureHandler,
                MILLIS_FAKE_DELAY,
                OPERATION_SUCCEEDS);
        job.execute();
    }
}
