package news.rod.bailey.simplenewsreader;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import news.rod.bailey.simplenewsreader.util.AssetUtils;

/**
 * Created by rodbailey on 7/10/2016.
 */
@RunWith(AndroidJUnit4.class)
public class AssetUtilsInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void loadSampleJsonAssetFile() throws IOException {
        String str = AssetUtils.loadAssetFileAsString("sample.json");
        Assert.assertNotNull(str);
        Assert.assertTrue(str.trim().startsWith("{"));
        Assert.assertTrue(str.trim().endsWith("}"));
        Assert.assertTrue(str.contains("\"title\""));
        Assert.assertTrue(str.contains("\"rows\""));
    }
}
