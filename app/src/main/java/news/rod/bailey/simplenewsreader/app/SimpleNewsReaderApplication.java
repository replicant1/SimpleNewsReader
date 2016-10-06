package news.rod.bailey.simplenewsreader.app;

import android.app.Application;
import android.content.Context;

/**
 * The application representing all of the SimpleNewsReader app. This class exists solely for
 * the purposes of providing global access to the context without having to continually pass it
 * around.
 */
public class SimpleNewsReaderApplication extends Application {

  public static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    context = base;
  }
}