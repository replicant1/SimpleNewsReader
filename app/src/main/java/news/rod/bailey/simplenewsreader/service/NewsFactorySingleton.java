package news.rod.bailey.simplenewsreader.service;

import android.graphics.Bitmap;

import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * Created by rodbailey on 6/10/2016.
 */
public class NewsFactorySingleton {

    private static final NewsFactorySingleton singleton = new NewsFactorySingleton();

    public static NewsFactorySingleton getSingleton() {
        return singleton;
    }

    public INewsService getNewsService() {
        if (ConfigSingleton.getInstance().UseFakeNewsService()) {
            return new FakeNewsServce();
        }
        return new NewsService();
    }
}
