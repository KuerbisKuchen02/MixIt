package de.thm.mixit.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import de.thm.mixit.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
