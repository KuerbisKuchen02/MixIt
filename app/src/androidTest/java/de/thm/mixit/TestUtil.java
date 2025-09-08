package de.thm.mixit;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Utility class for Ui tests
 *
 * @author Josia Menger
 */
public class TestUtil {

    /**
     * Matcher that matches a view that has exactly the given item {@code count}
     * @param count number of items
     * @return matcher
     */
    public static Matcher<View> hasItemCount(final int count) {
        return new BoundedMatcher<>(RecyclerView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have " + count + " items");
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                return recyclerView.getAdapter() != null
                        && recyclerView.getAdapter().getItemCount() == count;
            }
        };
    }

    /**
     * Matcher that matches a view with the given number of children that match the childMatcher
     * @param expectedCount number of children
     * @param childMatcher matcher for children
     * @return matcher
     */
    public static Matcher<View> withMatchingChildCount(final int expectedCount,
                                                       final Matcher<View> childMatcher) {
        return new BoundedMatcher<>(ViewGroup.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with " + expectedCount + " children matching: ");
                childMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(ViewGroup viewGroup) {
                int matchCount = 0;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    if (childMatcher.matches(child)) {
                        matchCount++;
                    }
                }
                return matchCount == expectedCount;
            }
        };
    }

    /**
     * Matcher to match a view that has any non null tag
     * @return matcher
     */
    public static Matcher<View> hasTag() {
        return new BoundedMatcher<>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has non-null tag");
            }

            @Override
            protected boolean matchesSafely(View view) {
                return view.getTag() != null;
            }
        };
    }

    /**
     * Move an view to the center of the target view
     * @param target view to move to
     * @return view action
     */
    public static ViewAction dragFromTo(final View target) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Drag one view to intersect with another view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                int[] toLocation = new int[2];

                target.getLocationOnScreen(toLocation);

                float toX = toLocation[0] + target.getWidth() / 2f;
                float toY = toLocation[1] + target.getHeight() / 2f;

                // simulate drag by sending swipe
                (new GeneralSwipeAction(
                        Swipe.FAST,
                        GeneralLocation.CENTER,
                        view1 -> new float[]{toX, toY},
                        Press.FINGER
                )).perform(uiController, view);
            }
        };
    }

    /**
     * Get the actual view instance from a ViewInteraction
     * @param interaction to extract the view from
     * @return view
     */
    public static View getView(ViewInteraction interaction) {
        final View[] view = new View[1];
        interaction.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "get view";
            }

            @Override
            public void perform(UiController uiController, View v) {
                view[0] = v;
            }
        });
        return view[0];
    }
}
