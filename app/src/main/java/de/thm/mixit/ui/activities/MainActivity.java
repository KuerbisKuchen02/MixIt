package de.thm.mixit.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import de.thm.mixit.R;

/**
 * Activity for the main menu.
 * <p>
 * This Activity contains the MixIt icon and 4 Buttons.
 * Endless -> Starts the GameActivity in endless mode.
 * Arcade -> Starts the GameActivity in arcade mode.
 * Achievements -> Starts the AchievementsActivity. (Not implemented yet)
 * Settings _> Starts the SettingsActivity. (Not implemented yet)
 * </p>
 *
 * @author Jannik Heimann
 */

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainActivity was created");
    }

    public void onEndlessGameButtonClicked(View v) {
        Log.i(TAG, "Endless Game Button was clicked.");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, false);
        startActivity(intent);
    }

    public void onArcadeButtonClicked(View view) {
        Log.i(TAG, "Arcade Button was clicked.");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, true);
        startActivity(intent);
    }

    public void onAchievementsButtonClicked(View view) {
        Log.i(TAG, "Achievements Button was clicked.");
        // TODO implement when Achievement Activity is added
    }

    public void onSettingsButtonClicked(View view) {
        Log.i(TAG, "Settings Button was clicked.");
        // TODO implement when Setting Activity is added
    }
}
