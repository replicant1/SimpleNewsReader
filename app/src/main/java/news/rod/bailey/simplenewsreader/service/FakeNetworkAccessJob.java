package news.rod.bailey.simplenewsreader.service;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by rodbailey on 6/10/2016.
 */

public class FakeNetworkAccessJob extends AsyncTask<Void, Void, Void> {
    private static final String TAG = FakeNetworkAccessJob.class.getSimpleName();

    private final Response.Listener successHandler;

    private final Response.ErrorListener failureHandler;

    private final long millisToRunFor;

    private final boolean jobDoesSucceed;

    /**
     *
     * @param successHandler
     * @param failureHandler
     * @param millisToRunFor
     * @param jobDoesSucceed
     */
    public FakeNetworkAccessJob(Response.Listener successHandler, Response.ErrorListener failureHandler, long
            millisToRunFor, boolean jobDoesSucceed) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.millisToRunFor = millisToRunFor;
        this.jobDoesSucceed = jobDoesSucceed;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(millisToRunFor, 0);
        } catch (InterruptedException iex) {
            Log.w(TAG, iex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(TAG, "FakeNetworkAccessJob has ended");

        if (jobDoesSucceed) {
            successHandler.onResponse(null);
        } else {
            failureHandler.onErrorResponse(new VolleyError());
        }
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "ConsumeTimeJob is beginning");
    }
}
