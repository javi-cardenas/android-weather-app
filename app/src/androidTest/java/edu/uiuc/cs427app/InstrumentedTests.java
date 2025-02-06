package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import edu.uiuc.cs427app.ui.LoginActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstrumentedTests {

    private static String username = UUID.randomUUID().toString();
    private static String password = UUID.randomUUID().toString();

    private View decorView;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Sets up the test by retrieving the decor view of LoginActivity for use in the test.
     */
    public void setUp() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<LoginActivity>() {
            public void perform(LoginActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    /**
     * Util function to signup with a unique user before each test.
     */
    public void signup() {
        onView(withId(R.id.textViewSignup)).perform(click()); // click signup button
        onView(withId(R.id.editTextUsername)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.buttonSignup)).perform(click()); // click signup button
        onView(withId(R.id.textView4)).check(matches(isDisplayed())); // ui assertion
    }

    /**
     * Tests that user signup works as expected
     */
    @Test
    public void testA_checkUserSignup() {
        // Test Execution
        signup();
    }

    /**
     * Tests that add city functionality works
     * @throws Exception
     */
    @Test
    public void testB_checkAddCity() throws Exception {
        // Test Pre-condition setup
        signup();

        // Test Execution
        Thread.sleep(3000);
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.editTextCity))
                .perform(typeText("New York City"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonAddNewLocation)).perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.listViewLocations)).check(
                matches(hasDescendant(withText(containsString("New York")))));

    }

    /**
     * Tests the functionality of adding and then deleting a city location and verifies the UI
     * after each action.
     * @throws Exception
     */
    @Test
    public void testC_checkDeleteCity() throws Exception {
        // Test Pre-condition setup
        signup();
        Thread.sleep(3000);
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.editTextCity))
                .perform(typeText("New York City"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonAddNewLocation)).perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.listViewLocations)).check(
                matches(hasDescendant(withText(containsString("New York")))));

        // Test Execution
        Thread.sleep(3000);
        onView(withId(R.id.buttonDelete)).perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.textView4)).check(matches(isDisplayed()));

    }

    /**
     * Tests the user logoff functionality by verifying the login screen is displayed after logout.
     * @throws Exception
     */
    @Test
    public void testD_checkUserLogoff() throws Exception {
        // Test Pre-condition setup
        signup();
        // Test Execution
        Thread.sleep(3000);
        onView(withId(R.id.buttonLogout)).perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.textViewLogin)).check(matches(isDisplayed())); // assertion

    }

    /**
     * Tests both valid and invalid user login functionality, verifying proper login and error
     * handling
     * @throws Exception
     */
    @Test
    public void testE_checkUserLogin() throws Exception {
        // Test Pre-condition setup
        signup();
        Thread.sleep(3000);
        onView(withId(R.id.buttonLogout)).perform(click());
        // Valid Login Test Execution
        Thread.sleep(3000);
        onView(withId(R.id.editTextUsername)).perform(typeText(username), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.textView4)).check(matches(isDisplayed())); // assertion

        Thread.sleep(3000);
        onView(withId(R.id.buttonLogout)).perform(click());

        // Invalid Login Test Execution
        Thread.sleep(3000);
        onView(withId(R.id.editTextUsername)).perform(typeText("invalidUser"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.editTextPassword)).perform(typeText("invalidPassword"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(200);
        onView(withText("Invalid username or password")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed())); // assertion
    }

    @Test
    /**
     * Tests the weather feature by adding locations (Chicago and Los Angeles) and
     * verifying weather details are displayed for each.
     */
    public void testF_checkWeatherFeature() throws Exception {
        // Test Pre-condition setup
        signup();
        // add Chicago to the list
        Thread.sleep(3000);
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.editTextCity))
                .perform(typeText("Chicago"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonAddNewLocation)).perform(click());

        // add Los Angeles to the list
        Thread.sleep(3000);
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.editTextCity))
                .perform(typeText("Los Angeles"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonAddNewLocation)).perform(click());

        // Test Execution
        // click on Chicago weather button
        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.listViewLocations))
                .atPosition(0)
                .onChildView(withId(R.id.buttonShowWeather))
                .perform(click());
        Thread.sleep(5000); // more time to load the weather

        onView(withId(R.id.textDate)).check(matches(not(withText(""))));
        onView(withId(R.id.textTime)).check(matches(not(withText(""))));
        onView(withId(R.id.textTemperature)).check(matches(not(withText(""))));
        onView(withId(R.id.textWeather)).check(matches(not(withText(""))));
        onView(withId(R.id.textHumidity)).check(matches(not(withText(""))));
        onView(withId(R.id.textWindCondition)).check(matches(not(withText(""))));

        pressBack(); // go back to main page

        // click on Los Angeles weather button
        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.listViewLocations))
                .atPosition(1)
                .onChildView(withId(R.id.buttonShowWeather))
                .perform(click());
        Thread.sleep(5000); // more time to load the weather

        onView(withId(R.id.textDate)).check(matches(not(withText(""))));
        onView(withId(R.id.textTime)).check(matches(not(withText(""))));
        onView(withId(R.id.textTemperature)).check(matches(not(withText(""))));
        onView(withId(R.id.textWeather)).check(matches(not(withText(""))));
        onView(withId(R.id.textHumidity)).check(matches(not(withText(""))));
        onView(withId(R.id.textWindCondition)).check(matches(not(withText(""))));

        pressBack(); // go back to main page
        Thread.sleep(3000);
    }

    @Test
    /**
     * Verifies the location feature by adding cities (Chicago and Los Angeles) and checking that
     * the map displays the correct location for each city.
     */
    public void testH_checkLocationFeature() throws Exception {
        // Test Pre-condition setup
        List<String> cities = Arrays.asList("Chicago", "Los Angeles");

        signup();
        // add to the list
        for (String city : cities) {
            Thread.sleep(3000);
            onView(withId(R.id.buttonAddLocation)).perform(click());

            Thread.sleep(1000);
            onView(withId(R.id.editTextCity))
                    .perform(typeText(city), closeSoftKeyboard());

            Thread.sleep(1000);
            onView(withId(R.id.buttonAddNewLocation)).perform(click());
        }
        // Test Execution

        // click on map button
        for (int i = 0; i < cities.size(); i++) {
            String city = cities.get(i);

            // Click on the map button for the city at position i
            Thread.sleep(3000);
            onData(anything())
                    .inAdapterView(withId(R.id.listViewLocations))
                    .atPosition(i)
                    .onChildView(withId(R.id.buttonShowMap))
                    .perform(click());

            Thread.sleep(5000); // Wait for the map to load
            onView(withId(R.id.location_info))
                    .check(matches(withText(containsString(city))));

            // Return to the main page
            pressBack();
        }
        Thread.sleep(3000);
    }

    /**
     * Verifies the AI weather insights feature by adding a city, viewing its weather, and
     * interacting with AI-generated insights and responses.
     * @throws Exception
     */
    @Test
    public void testI_checkAIWeatherInsights() throws Exception {
        // Test Pre-condition setup
        signup();
        // add Normal to the list
        Thread.sleep(3000);
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.editTextCity))
                .perform(typeText("Normal IL"), closeSoftKeyboard());

        Thread.sleep(1000);
        onView(withId(R.id.buttonAddNewLocation)).perform(click());

        // Test Execution

        // click on Normal weather button
        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.listViewLocations))
                .atPosition(0)
                .onChildView(withId(R.id.buttonShowWeather))
                .perform(click());
        Thread.sleep(5000); // more time to load the weather

        // click on weather insights button
        onView(withId(R.id.weatherDetailsButton)).perform(click());
        Thread.sleep(3000); // more time to load AI responses


        // Assert prompt popups exist
        onView(withId(R.id.recyclerView))
                .check(matches(hasChildCount(3)));
        // Click popup
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(5000); // more time to load AI response
        // Assert question and response and rendered in the chat pane
        onView(withId(R.id.recyclerView))
                .check(matches(hasChildCount(5)));
    }

}