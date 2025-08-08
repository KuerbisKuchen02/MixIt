package de.thm.mixit.ui.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
    private static final String TAG = GameActivity.class.getSimpleName();
    private GameViewModel viewModel;
    private long startTime;
    private Handler timeHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new GameViewModel.Factory(this))
                .get(GameViewModel.class);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_IS_ARCADE)) {
            viewModel.setArcade(Objects.requireNonNull(intent.getExtras())
                    .getBoolean(EXTRA_IS_ARCADE));
        } else {
            Log.w(TAG, "GameActivity received Intent without the " + EXTRA_IS_ARCADE
                    + " Extra attribute, default value is " + viewModel.isArcade());
        }

        if (!viewModel.isArcade()) {
            Log.i(TAG, "GameActivity is hiding arcade fragment.");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment arcade_fragment = fragmentManager
                    .findFragmentById(R.id.fragment_container_arcade);
            if (arcade_fragment == null) {
                Log.e(TAG, "Error, arcade fragment reference is null!");
            } else {
                fragmentManager.beginTransaction().remove(arcade_fragment).commit();
            }
        }

        this.startTime = System.currentTimeMillis();
        // FIXME: new Handler() is deprecated
        this.timeHandler = new Handler();
        this.timeHandler.post(updateTimerRunnable);

        Log.i(TAG, "GameActivity was created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeHandler.post(updateTimerRunnable);
        viewModel.load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeHandler.removeCallbacks(updateTimerRunnable);
        viewModel.persist();
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
