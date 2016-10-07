package news.rod.bailey.simplenewsreader;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test of ConfigSingleton. Needs to be an instrumented test because ConfigSingleton needs the app
 * context to access the assets/config.properties file.
 */
@RunWith(AndroidJUnit4.class)
public class ConfigSingletonInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void hasAllExpectedProperties() {
        ConfigSingleton config = ConfigSingleton.getInstance();
        assertNotNull(config);
        assertTrue(config.hasProperty("NewsService.async"));
        assertTrue(config.hasProperty("NewsService.fake"));
        assertTrue(config.hasProperty("NewsService.fake.asset"));
        assertTrue(config.hasProperty("NewsService.fake.delay.millis"));
        assertTrue(config.hasProperty("NewsService.fake.succeeds"));
        assertTrue(config.hasProperty("NewsService.url"));
        assertTrue(config.hasProperty("NewsService.timeout.millis"));
    }

    @Test
    public void checkPropertyValuesUnlikelyToChange() {
        ConfigSingleton config = ConfigSingleton.getInstance();
        assertNotNull(config);
        Assert.assertEquals("https://dl.dropboxusercontent.com/u/746330/facts.json", config.NewsServiceUrl());
        Assert.assertEquals(2500, config.NewsServiceTimeoutMillis());
        Assert.assertEquals(5000, config.FakeNewsServiceDelayMillis());
        Assert.assertEquals("sample.json", config.FakeNewsServiceAsset());
    }
}
