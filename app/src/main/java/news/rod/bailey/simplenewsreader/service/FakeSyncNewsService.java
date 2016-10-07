package news.rod.bailey.simplenewsreader.service;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.IOException;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;
import news.rod.bailey.simplenewsreader.util.AssetUtils;
import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * An implementation of INewsService that is suitable for JUnit testing. Returns dummy data
 * read from /assets/sample.json.
 */
public class FakeSyncNewsService implements INewsService {

    private static final String LOG_TAG = FakeSyncNewsService.class.getSimpleName();

    /**
     * Dummy data is returned on the same thread as the caller.
     *
     * @param successHandler Once loaded OK, the data is passed back to this party
     * @param failureHandler If data can't be loaded for any reason, this party is notified
     */
    @Override
    public void getNews(Response.Listener successHandler, Response.ErrorListener failureHandler) {
        String assetFileName = ConfigSingleton.getInstance().FakeNewsServiceAsset();
        String result = null;

        try {
            result = AssetUtils.loadAssetFileAsString(assetFileName);
        } catch (IOException x) {
            Log.e(LOG_TAG, String.format("Failed to load %s from assets folder", assetFileName), x);
        }

        if (result == null) {
            failureHandler.onErrorResponse(new VolleyError());
        } else {
            successHandler.onResponse(result);
        }
    }


}

