package de.thm.mixit.ui.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import de.thm.mixit.R;
import de.thm.mixit.ui.viewmodel.GameViewModel;

/**
 * Activity for the main view of the game.
 * <p>
 * This Activity contains 3 Fragments, Playground, ElementList and Arcade.
 * The Arcade Fragment can be visible or hidden depending on the selected game mode.
 * </p>
 *
 * @author Jannik Heimann
 */

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_IS_ARCADE = "isArcade";
    public static final String EXTRA_NEW_GAME = "isNewGame";
    private static final String TAG = GameActivity.class.getSimpleName();
    private GameViewModel viewModel;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private long startTime;
    private Handler timeHandler;
    private boolean isArcade = false;
    private boolean isNewGame = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_IS_ARCADE)) {
            isArcade = Objects.requireNonNull(intent.getExtras()).getBoolean(EXTRA_IS_ARCADE);
        } else {
            Log.w(TAG, "GameActivity received Intent without the " + EXTRA_IS_ARCADE
                    + " Extra attribute, default value is " + isArcade);
        }

        if (intent.hasExtra(EXTRA_NEW_GAME)) {
            isNewGame = Objects.requireNonNull(intent.getExtras()).getBoolean(EXTRA_NEW_GAME);
        } else {
            Log.w(TAG, "GameActivity received Intent without the " + EXTRA_NEW_GAME
                    + " Extra attribute, default value is " + isNewGame);
        }

        viewModel = new ViewModelProvider(this, new GameViewModel.Factory(this, isArcade))
                .get(GameViewModel.class);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_game);

        if (!isArcade) {
            Log.i(TAG, "GameActivity is hiding arcade fragment.");
            Fragment arcade_fragment = fragmentManager
                    .findFragmentById(R.id.fragment_container_arcade);
            if (arcade_fragment == null) {
                Log.e(TAG, "Error, arcade fragment reference is null!");
            } else {
                fragmentManager.beginTransaction().remove(arcade_fragment).commit();
            }
        }

        setElementListVisible(false);

        this.startTime = System.currentTimeMillis();
        this.timeHandler = new Handler(Looper.getMainLooper());
        this.timeHandler.post(updateTimerRunnable);

        Log.i(TAG, "GameActivity was created");
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.saveGameState();
        viewModel.saveStatistics();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.onDestroy();
        timeHandler.removeCallbacks(updateTimerRunnable);
    }

    /**
     * Shows or hides the ElementList Fragment within the Activity.
     * @param visible whether to show or hide the ElementList Fragment.
     */
    public void setElementListVisible(boolean visible) {
        Fragment elementlist_fragment = fragmentManager
                .findFragmentById(R.id.fragment_container_element_list);
        FrameLayout container = findViewById(R.id.fragment_container_element_list);
        if (elementlist_fragment == null) {
            Log.e(TAG, "Error, elementlist fragment reference is null!");
            return;
        }

        if (visible) {
            Log.i(TAG, "GameActivity is showing elementlist fragment.");
            container.setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction().show(elementlist_fragment).commit();
        } else {
            Log.i(TAG, "GameActivity is hiding elementlist fragment.");
            container.setVisibility(View.GONE);
            fragmentManager.beginTransaction().hide(elementlist_fragment).commit();
        }
    }

    public boolean isArcade() {
        return isArcade;
    }
    /**
     * Worker thread to measure current playtime
     */
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            viewModel.setPassedTime(System.currentTimeMillis() - startTime
                    + viewModel.getAlreadySavedPassedTime());

            // Handler calls it again every second
            timeHandler.postDelayed(this, 1000);
        }
    };
}
