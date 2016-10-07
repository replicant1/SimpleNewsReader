package news.rod.bailey.simplenewsreader.service;

import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * Vends newly created implementations of {@link INewsService}. The implementation to vend is chosen with respect to
 * the current configuration parameters in assets/config.properties. This abstraction exists to support using
 * different implementations of INewsService for testing purposes and production.
 */
public class NewsServiceFactorySingleton {

    private static final NewsServiceFactorySingleton singleton = new NewsServiceFactorySingleton();

    /**
     * @return The singleton instance of {@link NewsServiceFactorySingleton}
     */
    public static NewsServiceFactorySingleton getSingleton() {
        return singleton;
    }

    /**
     * @return A newly created INewsService. The implementation will be the one specified jointly by the values of
     * the configuration properties NewsService.async and NewsService.fake. Can be null if both NewsService.async AND
     * NewsService.fake are false (which makes no sense - the *real* INewsService must be asynchronous).
     * @see FakeAsyncNewsService
     * @see FakeSyncNewsService
     * @see AsyncNewsService
     */
    public INewsService getNewsService() {
        INewsService result = null;

        boolean useFake = ConfigSingleton.getInstance().UseFakeNewsService();
        boolean callAsync = ConfigSingleton.getInstance().CallNewsServiceAsync();

        if (useFake && callAsync) {
            result = new FakeAsyncNewsService();
        } else if (useFake && (!callAsync)) {
            result = new FakeSyncNewsService();
        } else if ((!useFake) && callAsync) {
            result = new AsyncNewsService();
        } else {
            // Not supported. A real news service that is sync makes no sense.
        }

        return result;
    }
}
