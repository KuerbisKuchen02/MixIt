package de.thm.mixit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static de.thm.mixit.TestUtil.dragFromTo;
import static de.thm.mixit.TestUtil.getView;
import static de.thm.mixit.TestUtil.hasItemCount;
import static de.thm.mixit.TestUtil.hasTag;
import static de.thm.mixit.TestUtil.withMatchingChildCount;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.source.ElementRemoteDataSource;
import de.thm.mixit.ui.activities.MainActivity;

@RunWith(AndroidJUnit4.class)
public class GameActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        IdlingRegistry.getInstance()
                .register(ElementRemoteDataSource.getIdlingResource());

        ElementChip.setId(0);

        // Clear database and shared preferences
        Context context = ApplicationProvider.getApplicationContext();
        ElementRepository.create(context, false).reset();
        GameStateRepository.create(context, false).deleteSavedGameState();
        ElementRepository.create(context, true).reset();
        GameStateRepository.create(context, true).deleteSavedGameState();
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance()
                .unregister(ElementRemoteDataSource.getIdlingResource());
    }

    @Test
    public void testEndlessModeInitialization() {
        startEndlessGame();
        onView(withId(R.id.textview_time_since_start))
                .check(doesNotExist());
        onView(withId(R.id.fragment_container_element_list))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testArcadeModeInitialization() {
        onView(withId(R.id.button_main_menu_delete_arcade_savestate))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.button_main_menu_arcade))
                .perform(click());
        onView(withId(R.id.textview_time_since_start))
                .check(matches(isDisplayed()));
        onView(withId(R.id.textview_turns))
                .check(matches(withText(containsString("0"))));
        onView(withId(R.id.textview_target_element))
                .check(matches(not(withText(""))));
        onView(withId(R.id.fragment_container_element_list))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testItemListVisibilityAndFiltering() {
        startEndlessGame();
        ViewInteraction itemListFragment = onView(withId(R.id.fragment_container_element_list));

        // Open list
        onView(withId(R.id.button_open_element_list))
                .perform(click());
        // Check list open
        itemListFragment.check(matches(isDisplayed()));
        // Check list contains starter elements
        onView(withId(R.id.recycler_game_item_list))
                .check(matches(hasItemCount(4)));
        // Search for Feuer
        onView(withId(R.id.auto_text_game_item_list_search))
                .perform(typeText("Feuer"));
        // Check only one element visible
        onView(withId(R.id.recycler_game_item_list))
                .check(matches(hasItemCount(1)));
        // Close item list
        onView(withId(R.id.layout_playground))
                .perform(click());
        // Check item list closed
        itemListFragment.check(matches(not(isDisplayed())));
    }

    @Test
    public void testItemPlacementDuplication() {
        startEndlessGame();
        // Open item list
        onView(withId(R.id.button_open_element_list))
                .perform(click());
        // Add Feuer to playground
        onView(withId(R.id.recycler_game_item_list))
                .perform(RecyclerViewActions.actionOnItem(
                        withText(containsString("Feuer")), click()));
        // Check item on playground and duplicate
        onView(withTagValue(is(0)))
                .check(matches(isDisplayed()))
                .perform(doubleClick());
        // Check for two elements
        onView(withId(R.id.layout_playground))
                .check(matches(withMatchingChildCount(2, hasTag())));
    }

    @Test
    public void testItemDeletionAndBoardClearing() {
        startEndlessGame();
        ViewInteraction clearButton = onView(withId(R.id.button_clear_elements));
        ViewInteraction playground = onView(withId(R.id.layout_playground));

        // Open item list
        onView(withId(R.id.button_open_element_list))
                .perform(click());
        // Add three Feuer to playground
        onView(withId(R.id.recycler_game_item_list))
                .perform(RecyclerViewActions.actionOnItem(
                        withText(containsString("Feuer")), click()));
        onView(withTagValue(is(0)))
                .perform(doubleClick())
                .perform(doubleClick())
                .perform(dragFromTo(getView(clearButton)));
        // Check if item is deleted
        playground.check(matches(withMatchingChildCount(2, hasTag())));
        // Clear board and check no items left
        clearButton.perform(click());
        playground.check(matches(withMatchingChildCount(0, hasTag())));
    }

    private void startEndlessGame() {
        onView(withId(R.id.button_main_menu_endless))
                .perform(click());
    }
}
