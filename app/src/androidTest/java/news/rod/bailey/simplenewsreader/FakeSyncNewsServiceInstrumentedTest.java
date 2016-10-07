package news.rod.bailey.simplenewsreader;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import news.rod.bailey.simplenewsreader.service.FakeSyncNewsService;
import news.rod.bailey.simplenewsreader.service.INewsService;

/**
 * Created by rodbailey on 7/10/2016.
 */
@RunWith(AndroidJUnit4.class)
public class FakeSyncNewsServiceInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void fakeSyncNewsServiceReadsSampleJson() {
        INewsService service = new FakeSyncNewsService();
        service.getNews(new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Assert.assertNotNull(response);
                                Assert.assertTrue(response.trim().startsWith("{"));
                                Assert.assertTrue(response.trim().endsWith("}"));
                                Assert.assertTrue(response.contains("\"title\""));
                                Assert.assertTrue(response.contains("\"rows\""));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Assert.assertTrue("Failed to read sample.json", false);
                            }
                        });
    }

}
