package news.rod.bailey.simplenewsreader.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;

/**
 * Provides read-only access to the runtime configuration properties contained in the file assets/PROPERTIES_FILE_NAME.
 * Clients should use the singleton instance available through ConfigSingleton.getInstance().
 */
public final class ConfigSingleton {

    /**
     * Name of properties file in the assets directory
     */
    private static final String PROPERTIES_FILE_NAME = "config.properties";

    /**
     * Class name for logging purposes
     */
    private static final String TAG = ConfigSingleton.class.getSimpleName();

    private static ConfigSingleton singleton = new ConfigSingleton();

    /**
     * Internal store of raw property values as read from PROPERTIES_FILE_NAME. All values are strings.
     */
    private final Properties configProperties = new Properties();

    private ConfigSingleton() {
        InputStream istream;
        try {
            Context context = SimpleNewsReaderApplication.context;
            istream = context.getAssets().open(PROPERTIES_FILE_NAME);
            configProperties.load(istream);
        } catch (IOException e) {
            Log.w(TAG, String.format("Failed to load configuration properties file %s file from assets directory",
                                     PROPERTIES_FILE_NAME), e);
        }
    }

    /**
     * @return The singleton instance of this class used application-wide
     */
    public static ConfigSingleton getInstance() {
        return singleton;
    }

    public String FakeNewsServiceAsset() {
        return getStringProperty("NewsService.fake.asset");
    }

    public int FakeNewsServiceDelayMillis() {
        return getIntProperty("NewsService.fake.delay.millis");
    }

    public boolean FakeNewsServiceSucceeds() {
        return getBoolProperty("NewsService.fake.succeeds");
    }

    public int NewsServiceTimeoutMillis() {
        return getIntProperty("NewsService.timeout.millis");
    }

    public String NewsServiceUrl() {
        return getStringProperty("NewsService.url");
    }

    public boolean UseFakeNewsService() {
        return getBoolProperty("NewsService.fake");
    }

    public boolean CallNewsServiceAsync() { return getBoolProperty("NewsService.async"); }

    private boolean getBoolProperty(String propertyName) {
        return Boolean.parseBoolean(configProperties.getProperty(propertyName));
    }

    private int getIntProperty(String propertyName) {
        return Integer.parseInt(configProperties.getProperty(propertyName));
    }

    private String getStringProperty(String propertyName) {
        return configProperties.getProperty(propertyName).trim();
    }

    /**
     * Made this public to facilitate testing.
     *
     * @param propertyName
     * @return
     */
    public boolean hasProperty(String propertyName) {
        return configProperties.containsKey(propertyName);
    }

}
