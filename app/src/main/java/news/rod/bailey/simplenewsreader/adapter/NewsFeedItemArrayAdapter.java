package news.rod.bailey.simplenewsreader.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import news.rod.bailey.simplenewsreader.R;
import news.rod.bailey.simplenewsreader.app.SimpleNewsReaderApplication;
import news.rod.bailey.simplenewsreader.json.NewsFeedItem;

/**
 * Data source behind the list of news items. Takes the JSON data already obtained from the server and inits it into
 * each item in the list. Also kicks off the async load of an image, if there is one. The data for each list item
 * comes from a single instance of domain object NewsFeedItem.
 *
 * @see NewsFeedItem
 */
public class NewsFeedItemArrayAdapter extends ArrayAdapter<NewsFeedItem> {

    private static final String LOG_TAG = NewsFeedItemArrayAdapter.class.getSimpleName();

    /**
     * Provided by Universal Image Loader library
     */
    private final ImageLoader imageLoader;

    public NewsFeedItemArrayAdapter(List<NewsFeedItem> newsFeedItems, ImageLoader imageLoader) {
        super(SimpleNewsReaderApplication.context, 0, newsFeedItems);
        this.imageLoader = imageLoader;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsFeedItem item = getItem(position);

        View newView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);

        TextView heading = (TextView) newView.findViewById(R.id.news_item_heading);
        TextView subHeading = (TextView) newView.findViewById(R.id.news_item_subheading);
        ImageView imageView = (ImageView) newView.findViewById(R.id.news_item_image);
        FrameLayout imageViewFrame = (FrameLayout) newView.findViewById(R.id.news_item_image_frame);
        TextView imageMessage = (TextView) newView.findViewById(R.id.new_item_image_substitute_message);

        // Given a non-null title and description are mandatory, the "== null" clauses should never fire
        heading.setText(item.title == null ? "" : item.title);
        subHeading.setText(item.description == null ? "" : item.description);

        Log.d(LOG_TAG, "Raw imageHref=" + item.imageHref);

        if (item.imageHref != null) {
            imageLoader.displayImage(item.imageHref, // URL of image to load
                                     imageView, // ImageView to load image into
                                     new NewsItemImageLoadingListener(imageViewFrame, imageView, imageMessage));
        } else {
            // The image in a news item is optional. Hide it to let description text occupy full width of list item.
            imageViewFrame.setVisibility(View.GONE);
        }

        return newView;
    }

    /**
     * Instance of this is registered with the ImageLoader to receive lifecycle callbacks during the loading of each
     * image.
     */
    private static class NewsItemImageLoadingListener implements ImageLoadingListener {

        /**
         * Contains imageView and imageMessage. Displays one or the other.
         */
        private final FrameLayout imageViewFrame;

        /**
         * Displays the image if loaded OK
         */
        private final ImageView imageView;

        /**
         * Displays a status message like "Loading..." or "Load failed"
         */
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

        /**
         * Translate the failure to load an image into a friendly message to display where the image should be.
         */
        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            imageViewFrame.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageMessage.setVisibility(View.VISIBLE);

            FailReason.FailType type = failReason.getType();
            int failMessage;

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
