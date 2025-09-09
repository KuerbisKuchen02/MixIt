package de.thm.mixit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import de.thm.mixit.R;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.repository.SettingsRepository;

/**
 * Activity for the main menu.
 * <p>
 * This Activity contains the MixIt icon and 5 Buttons.
 * Endless -> Starts the GameActivity in endless mode.
 * Arcade -> Starts the GameActivity in arcade mode.
 * Delete Arcade Save State -> Deletes the saved GameState of the Arcade Mode
 * Achievements -> Starts the AchievementsActivity.
 * Settings -> Starts the SettingsActivity.
 * </p>
 *
 * @author Jannik Heimann
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GameStateRepository gameStateRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        gameStateRepository = GameStateRepository.create(this,true);
        SettingsRepository settingsRepository = SettingsRepository.create(this);

        String currentTheme = settingsRepository.loadSettings().getTheme();

        switch (currentTheme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainActivity was created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show or hide delete Button for Arcade GameState depending on an existing saved GameState
        setVisibilityOfDeleteArcadeSaveStateButton(gameStateRepository.hasSavedGameState());
    }

    /**
     * Is called when the button to continue the endless game has been clicked on
     * @param view   The view which received the click
     */
    public void onEndlessGameButtonClicked(View view) {
        Log.i(TAG, "Endless Game Button was clicked.");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, false);
        startActivity(intent);
    }

    /**
     * Is called when the button to start a new arcade game has been clicked on
     * @param view  The view which received the click
     */
    public void onArcadeButtonClicked(View view) {
        Log.i(TAG, "Arcade Button was clicked.");
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_IS_ARCADE, true);
        startActivity(intent);
    }

    /**
     * Handles clicks on the button to delete a saved arcade game
     * @param view  The view which received the click
     */
    public void onArcadeDeleteSaveStateButtonClicked(View view) {
        Log.i(TAG, "Arcade delete Savestate Button was clicked.");
        gameStateRepository.deleteSavedGameState();
        ElementRepository elementRepository = ElementRepository.create(this, true);
        elementRepository.reset();
        setVisibilityOfDeleteArcadeSaveStateButton(false);
    }

    /**
     * Handles clicks on the achievement button
     * @param view  The view which received the click
     */
    public void onAchievementsButtonClicked(View view) {
        Log.i(TAG, "Achievements Button was clicked.");
        Intent intent = new Intent(this, AchievementActivity.class);
        startActivity(intent);
    }

    /**
     * Handles clicks on the settings button
     * @param view  The view which received the click
     */
    public void onSettingsButtonClicked(View view) {
        Log.i(TAG, "Settings Button was clicked.");

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Sets the visibility of the button to delete a saved arcade game
     * @param visible   if true, the button is visible, else not
     */
    public void setVisibilityOfDeleteArcadeSaveStateButton(boolean visible) {
        Button button = findViewById(R.id.button_main_menu_delete_arcade_savestate);
        button.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
