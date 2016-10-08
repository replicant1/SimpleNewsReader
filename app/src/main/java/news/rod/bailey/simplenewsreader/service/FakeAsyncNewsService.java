package news.rod.bailey.simplenewsreader.service;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.IOException;

import news.rod.bailey.simplenewsreader.util.AssetUtils;
import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * An implementation of INewsService that is suitable for local testing. Returns dummy data
 * read from /assets/sample.json with a delay of a few seconds artificially created to simulate
 * network latency. The dummy data is returned on a different thread from that which calls the getNews()
 * method.
 */
public class FakeAsyncNewsService implements INewsService {

    @Override
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler) {
        FakeAsyncGetNewsJob job = new FakeAsyncGetNewsJob(
                successHandler,
                failureHandler);
        job.execute();
    }

    /**
     * Generic types represent: Params, Progress, Result.
     * Called asynchronously to mimic the retrieval of JSON data from a remote source. Actually just reads the file
     * /assets/sample.json and then sleeps awhile to simulate network latency.
     */
    private class FakeAsyncGetNewsJob extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FakeAsyncGetNewsJob.class.getSimpleName();

        private final Response.Listener successHandler;

        private final Response.ErrorListener failureHandler;

        /**
         * @param successHandler Invoked when data is loaded OK
         * @param failureHandler Invoked when data cannot be loaded. This is usef for testing load failure, and
         *                       requires that ConfigSingleton.getInstance().FakeNewsServiceSucceeds() be set to false.
         */
        public FakeAsyncGetNewsJob(Response.Listener successHandler, Response.ErrorListener failureHandler) {
            this.successHandler = successHandler;
            this.failureHandler = failureHandler;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            String assetFileName = ConfigSingleton.getInstance().FakeNewsServiceAsset();

            try {
                Thread.sleep(ConfigSingleton.getInstance().FakeNewsServiceDelayMillis());
            } catch (InterruptedException iex) {
                Log.e(LOG_TAG, "Failed to sleep to imitate network latency", iex);
            }

            try {
                result = AssetUtils.loadAssetFileAsString(assetFileName);
            } catch (IOException x) {
                Log.e(LOG_TAG, String.format("Failed to load %s from assets folder", assetFileName), x);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            boolean doesSucceed = ConfigSingleton.getInstance().FakeNewsServiceSucceeds();
            if (doesSucceed && (result != null)) {
                successHandler.onResponse(result);
            } else {
                failureHandler.onErrorResponse(new VolleyError());
            }
        }
    }
}
