package news.rod.bailey.simplenewsreader.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;

/**
 * A collection of static utility methods for operating on the contents of the "assets" directory for this app.
 */
public class AssetUtils {

    /**
     *
     * @param assetFileName
     * @return
     * @throws IOException
     */
    public static String loadAssetFileAsString(String assetFileName) throws IOException {
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = SimpleNewsReaderApplication.context.getAssets().open(assetFileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            return new String(buffer);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

}
