package com.openclassroom.go4lunch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.ui.HomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


@RunWith(AndroidJUnit4.class)
public class Go4LunchInstrumentedTest {
    private MutableLiveData<List<Restaurant>> restaurantsList;

    @Rule
    public ActivityScenarioRule<HomeActivity> activityScenarioRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setup() {
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        restaurantsList = restaurantRepository.getAllRestaurant();
    }

    private Activity getCurrentActivity() {
        final Activity[] currentActivity = new Activity[1];
        activityScenarioRule.getScenario().onActivity(activity -> currentActivity[0] = activity);
        return currentActivity[0];
    }

    @Test
    public void checkIfWeGetTheListOfRestaurantFromApi() {
        // create a countDown
        final CountDownLatch latch = new CountDownLatch(1);
        // Observe through the list of restaurants
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> restaurantsList.observeForever(restaurants -> {
            // Check if the list is null and if it has at least one item
            assertNotNull(restaurants);
            assertTrue(restaurants.size() > 0);
            latch.countDown();
        }));
    }

    @Test
    public void testRestaurantDetailPage() {
        // Click on the "list view" button in the BottomNavigationView
        onView(allOf(withId(R.id.navigation_list), isDisplayed())).perform(click());
        // Perform a click on a restaurant item in the recycler view
        onView(allOf(withId(R.id.list_recycler_view), isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));
        // Check that the detailed page is displayed
        onView(withId(R.id.detailed_page_activity)).check(matches(isDisplayed()));
        // Check if the name is the same as the one on the item of the recycler view
        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.list_recycler_view);
        TextView restaurantNameTextView = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(0))
                .itemView.findViewById(R.id.restaurant_name);
        String restaurantName = restaurantNameTextView.getText().toString();
        onView(withId(R.id.detailed_page_name)).check(matches(withText(restaurantName)));
    }

    @Test
    public void checkTheAlphabeticalOrderFilter() {
        Espresso.onView(ViewMatchers.withId(R.id.navigation_list))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.filter_list_btn))
                .perform(ViewActions.click());
        Espresso.onView(withText(R.string.alphabetical_filter))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.list_recycler_view))
                .check(matches(isDisplayed()))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(hasDescendant(withText(containsString("A")))));
    }

    @Test
    public void checkTheRatingFilter() throws InterruptedException {
        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.navigation_list))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.filter_list_btn))
                .perform(ViewActions.click());
        Espresso.onView(withText(R.string.rating_filter))
                .perform(ViewActions.click());

        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.list_recycler_view);
        RatingBar ratingBar1 = recyclerView.getChildAt(0).findViewById(R.id.restaurant_rating);
        RatingBar ratingBar2 = recyclerView.getChildAt(1).findViewById(R.id.restaurant_rating);
        RatingBar ratingBar3 = recyclerView.getChildAt(2).findViewById(R.id.restaurant_rating);
        float rating1 = ratingBar1.getRating();
        float rating2 = ratingBar2.getRating();
        float rating3 = ratingBar3.getRating();

        assertThat(rating1, greaterThanOrEqualTo(rating2));
        assertThat(rating2, greaterThanOrEqualTo(rating3));
    }

    @Test
    public void checkTheDistanceFilter() throws InterruptedException {
        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.navigation_list))
                .perform(ViewActions.click());
        Thread.sleep(3000);
        Espresso.onView(ViewMatchers.withId(R.id.filter_list_btn))
                .perform(ViewActions.click());
        Espresso.onView(withText(R.string.distance_filter))
                .perform(ViewActions.click());

        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.list_recycler_view);
        TextView textView1 = recyclerView.getChildAt(0).findViewById(R.id.restaurant_distance);
        TextView textView2 = recyclerView.getChildAt(1).findViewById(R.id.restaurant_distance);
        TextView textView3 = recyclerView.getChildAt(2).findViewById(R.id.restaurant_distance);
        String distance1 = (String) textView1.getText();
        String distance2 = (String) textView2.getText();
        String distance3 = (String) textView3.getText();
        int numDistance1 = Integer.parseInt(distance1.replaceAll("[^0-9]", ""));
        int numDistance2 = Integer.parseInt(distance2.replaceAll("[^0-9]", ""));
        int numDistance3 = Integer.parseInt(distance3.replaceAll("[^0-9]", ""));

        Thread.sleep(3000);

        assertThat(numDistance1, greaterThanOrEqualTo(numDistance2));
        assertThat(numDistance2, greaterThanOrEqualTo(numDistance3));
    }

    @Test
    public void checkTheNumOfWorkmatesFilter() throws InterruptedException {
        Thread.sleep(3000);

        Espresso.onView(ViewMatchers.withId(R.id.navigation_list))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.filter_list_btn))
                .perform(ViewActions.click());
        Espresso.onView(withText(R.string.workmates_filter))
                .perform(ViewActions.click());

        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.list_recycler_view);
        TextView textView1 = recyclerView.getChildAt(0).findViewById(R.id.restaurant_numOfColleagues);
        TextView textView2 = recyclerView.getChildAt(1).findViewById(R.id.restaurant_numOfColleagues);
        TextView textView3 = recyclerView.getChildAt(2).findViewById(R.id.restaurant_numOfColleagues);
        String workmates1 = (String) textView1.getText();
        String workmates2 = (String) textView2.getText();
        String workmates3 = (String) textView3.getText();
        int numOfWorkmates1 = Integer.parseInt(workmates1.replaceAll("[^0-9]", ""));
        int numOfWorkmates2 = Integer.parseInt(workmates2.replaceAll("[^0-9]", ""));
        int numOfWorkmates3 = Integer.parseInt(workmates3.replaceAll("[^0-9]", ""));

        assertThat(numOfWorkmates1, greaterThanOrEqualTo(numOfWorkmates2));
        assertThat(numOfWorkmates2, greaterThanOrEqualTo(numOfWorkmates3));
    }

    @Test
    public void checkIfTheUserIsDisconnected() throws InterruptedException {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Logout")).perform(click());
        onView(withText("Oui")).perform(click());

        Thread.sleep(5000);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assertNull(user);
    }

    @Test
    public void checkIfTheSearchSegmentForWorkmatesIsWorking() throws InterruptedException {
        Thread.sleep(8000);
        // Click on the "list view" button in the BottomNavigationView
        onView(allOf(withId(R.id.navigation_workmates), isDisplayed())).perform(click());
        // Click on the search button
        onView(withId(R.id.autocomplete_search_view)).perform(click());

        // Typing the name of a false workmates
        SearchView searchView = getCurrentActivity().findViewById(R.id.workmates_searchView);
        searchView.setQuery("axel", false);
        assertEquals("axel", searchView.getQuery().toString());
        Thread.sleep(3000);

        // Get the RecyclerView
        RecyclerView recyclerView = getCurrentActivity().findViewById(R.id.workmates_recycler_view);
        // Get the first item from the RecyclerView
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(0);

        // Check that the name of the first item matches the search query
        assert viewHolder != null;
        TextView workmatesTextView = viewHolder.itemView.findViewById(R.id.workmates_infos);
        String[] workmatesName = workmatesTextView.getText().toString().split(" ");
        String firstWorkmateName = workmatesName[0];
        assertEquals("axel", firstWorkmateName);
    }
}

