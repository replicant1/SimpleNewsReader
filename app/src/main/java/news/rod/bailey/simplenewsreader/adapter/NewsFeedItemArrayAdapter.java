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
import android.widget.FrameLayout;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Data source behind the list of news items. Takes the JSON data already obtained from
 * the server and inits it into each item in the list. Also kicks off the async load of
 * an image, if there is one.
 */
public class NewsFeedItemArrayAdapter extends ArrayAdapter<NewsFeedItem> {

    private static final String LOG_TAG = NewsFeedItemArrayAdapter.class.getSimpleName();

    private final ImageLoader imageLoader;

    public NewsFeedItemArrayAdapter(List<NewsFeedItem> newsFeedItems, ImageLoader imageLoader) {
        super(SimpleNewsReaderApplication.context, 0, newsFeedItems);
        this.imageLoader = imageLoader;
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
        final ImageView imageView = (ImageView) newView.findViewById(R.id.news_item_image);
        final FrameLayout imageViewFrame = (FrameLayout) newView.findViewById(R.id.news_item_image_frame);
        final TextView imageMessage = (TextView) newView.findViewById(R.id.new_item_image_substitute_message);

        heading.setText(item.title == null ? "" : item.title);
        subHeading.setText(item.description == null ? "" : item.description);

        Log.d(LOG_TAG, "Raw imageHref=" + item.imageHref);


        if (item.imageHref != null) {

            imageLoader.displayImage(item.imageHref, imageView,
                                     new NewsItemImageLoadingListener(imageViewFrame, imageView, imageMessage));
        } else {
            imageViewFrame.setVisibility(View.GONE);
        }

        return newView;
    }

    private static class NewsItemImageLoadingListener implements ImageLoadingListener {
        private final FrameLayout imageViewFrame;

        private final ImageView imageView;

        private final TextView imageMessage;

        public NewsItemImageLoadingListener(FrameLayout imageViewFrame, ImageView imageView, TextView imageMessage) {
            this.imageViewFrame = imageViewFrame;
            this.imageView = imageView;
            this.imageMessage = imageMessage;
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            imageViewFrame.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageMessage.setVisibility(View.VISIBLE);
            imageMessage.setText(R.string.image_load_cancelled);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            imageViewFrame.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageMessage.setVisibility(View.INVISIBLE);
            imageMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            imageViewFrame.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageMessage.setVisibility(View.VISIBLE);

            FailReason.FailType type = failReason.getType();
            int  failMessage;

            switch (type) {
                case DECODING_ERROR:
                    failMessage = R.string.image_bad;
                    break;

                case IO_ERROR:
                    failMessage = R.string.image_not_found;
                    break;

                case NETWORK_DENIED:
                    failMessage = R.string.image_no_network;
                    break;

                case OUT_OF_MEMORY:
                    failMessage = R.string.image_out_of_memory;
                    break;

                default:
                case UNKNOWN:
                    failMessage = R.string.image_unknown;
                    break;
            }

            imageMessage.setText(failMessage);
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            imageViewFrame.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageMessage.setVisibility(View.VISIBLE);
            imageMessage.setText(R.string.image_loading);
        }
    }
}
