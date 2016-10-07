package news.rod.bailey.simplenewsreader.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import news.rod.bailey.simplenewsreader.R;
import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;
import news.rod.bailey.simplenewsreader.json.NewsFeedItem;
import news.rod.bailey.simplenewsreader.service.FakeAsyncNewsService;
import news.rod.bailey.simplenewsreader.util.AssetUtils;
import news.rod.bailey.simplenewsreader.util.ConfigSingleton;

/**
 * Created by rodbailey on 7/10/2016.
 */

public class NewsFeedItemArrayAdapter extends ArrayAdapter<NewsFeedItem> {

    private static final String LOG_TAG = NewsFeedItemArrayAdapter.class.getSimpleName();

    public NewsFeedItemArrayAdapter(List<NewsFeedItem> newsFeedItems) {
        super(SimpleNewsReaderApplication.context, 0, newsFeedItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NewsFeedItem item = getItem(position);

        View newView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);

//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(
//                    R.layout.news_item,
//                    parent,
//                    false);
//        }

        TextView heading = (TextView) newView.findViewById(R.id.news_item_heading);
        TextView subHeading = (TextView) newView.findViewById(R.id.news_item_subheading);
        final ImageView image = (ImageView) newView.findViewById(R.id.news_item_image);

        heading.setText(item.title == null ? "" : item.title);
        subHeading.setText(item.description == null ? "" : item.description);

        Log.d(LOG_TAG, "Raw imageHref=" + item.imageHref);


        if (item.imageHref != null) {
//            image.setImageResource(R.drawable.beaver);

            GetBitmapAsync job = new GetBitmapAsync(item.imageHref, image);
            job.execute();
        }
        else {
            image.setVisibility(View.GONE);
        }

        return newView;
    }

    /**
     * Params, Progress, Result
     */
    private class GetBitmapAsync extends AsyncTask<Void, Void, Bitmap> {

        private final String LOG_TAG = GetBitmapAsync.class.getSimpleName();

        private final String imageHref;

        private final ImageView imageView;

        public GetBitmapAsync(String imageHref, ImageView imageView) {
            this.imageHref = imageHref;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap img = null;

            // Switch to HttpUrlConnection or use Google's image loader library
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(imageHref);
            HttpResponse response;
            try {
                response = (HttpResponse) client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);

                if (img != null)
                    Log.d(LOG_TAG, String.format("Height=%d,width=%d", img.getHeight(), img.getWidth()));
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d(LOG_TAG, "Result = " + result);
            if (result != null) {
                imageView.setImageBitmap(result);
                imageView.invalidate();
            }
        }
    }


}
