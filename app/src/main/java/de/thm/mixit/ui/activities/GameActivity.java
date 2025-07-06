package de.thm.mixit.ui.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import de.thm.mixit.R;

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
    private boolean isArcade = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_IS_ARCADE)) {
            isArcade = Objects.requireNonNull(intent.getExtras()).getBoolean(EXTRA_IS_ARCADE);
        } else {
            Log.w(TAG, "GameActivity received Intent without the " + EXTRA_IS_ARCADE
                    + " Extra attribute, default value is " + isArcade);
        }

        if (!isArcade) {
            Log.i(TAG, "GameActivity is hiding arcade fragment.");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment arcade_fragment = fragmentManager
                    .findFragmentById(R.id.fragment_container_arcade);
            if (arcade_fragment == null) {
                Log.e(TAG, "Error, arcade fragment reference is null!");
            } else {
                fragmentManager.beginTransaction().hide(arcade_fragment).commit();
            }
        }

        Log.i(TAG, "GameActivity was created");
    }
}
