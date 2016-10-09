package news.rod.bailey.simplenewsreader;

import android.content.pm.ActivityInfo;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import news.rod.bailey.simplenewsreader.json.NewsFeedItem;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;


/**
 * Espresso interactive tests for basic app functioning.
 * <p>
 * NOTE: config.properties must have the following properties set as indicated for this test to pass:
 * <pre>
 *   NewsService.fake=true
 *   NewsService.fake.asset=sample.json
 * </pre>
 * It doesn't matter what the value of the NewsService.async property is set to.
 */
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void actionBarHasExpectedTitle() {
        // Component containing the title text is shown (in the action bar)
        onView(withText("About Canada")).check(matches(isDisplayed()));
    }

    @Test
    public void actionBarHasRefreshButton() {
        // Refresh button in action bar is found by its Hint text (long press)
        onView(withResourceName("action_refresh")).check(matches(isDisplayed()));
    }

    @Test
    public void firstListItemIsAboutBeavers() {
        // Check first item in list has a child TextView with text "Beavers" = title of first news item
        onData(allOf(is(instanceOf(NewsFeedItem.class))))
                .inAdapterView(withId(R.id.news_item_list)).atPosition(0).check(
                selectedDescendantsMatch(
                        withText("Beavers"),
                        isAssignableFrom(TextView.class)));

        // Check first item in list has a child TextView with text "Beavers are second ... called a colony"
        onData(allOf(is(instanceOf(NewsFeedItem.class))))
                .inAdapterView(withId(R.id.news_item_list)).atPosition(0).check(
                selectedDescendantsMatch(
                        withText(startsWith("Beavers are second")),
                        isAssignableFrom(TextView.class)));
    }

    @Test
    public void lastListItemisAboutLanguage() {
        // Scroll to the 11th list item
        onData(instanceOf(NewsFeedItem.class))
                .inAdapterView(withId(R.id.news_item_list))
                .atPosition(11)
                .check(matches(isDisplayed()));

        // Check 11th item in list has a child TextView with text "Language". This is the title of the item.
        onData(instanceOf(NewsFeedItem.class))
                .inAdapterView(withId(R.id.news_item_list))
                .atPosition(11)
                .check(selectedDescendantsMatch(
                        withText("Language"),
                        isAssignableFrom(TextView.class)));

        // Check 11th item in list has a child TextView text beginning "Nous parlons tous..."
        onData(instanceOf(NewsFeedItem.class))
                .inAdapterView(withId(R.id.news_item_list))
                .atPosition(11)
                .check(selectedDescendantsMatch(
                        withText(CoreMatchers.startsWith("Nous parlons tous")),
                        isAssignableFrom(TextView.class)));
    }

    @Test
    public void newItemListIsVisible() {
        onView(withId(R.id.news_item_list)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void newsItemListHasExpectedContent() {
        // Ensure we start in Portrait orientation
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Test
    public void secondListItemIsAboutTransportation() {
        // Check second item in list has a child TextView with text "Transportation is..." = title of first news item
        onData(allOf(is(instanceOf(NewsFeedItem.class))))
                .inAdapterView(withId(R.id.news_item_list)).atPosition(1).check(
                selectedDescendantsMatch(
                        withText("Transportation is a really great way of getting around"),
                        isAssignableFrom(TextView.class)));

        // Check second item in list has a child TextView with text beginning "It is a well known fact..."
        onData(allOf(is(instanceOf(NewsFeedItem.class))))
                .inAdapterView(withId(R.id.news_item_list)).atPosition(1).check(
                selectedDescendantsMatch(
                        withText(startsWith("It is a well known fact")),
                        isAssignableFrom(TextView.class)));
    }

    @Test
    public void swipeRefreshLayoutIsVisible() {
        onView(withId(R.id.news_item_list_swipe_refresh)).check(matches(isDisplayed()));
    }
}
