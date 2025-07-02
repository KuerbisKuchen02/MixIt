package de.thm.mixit.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import de.thm.mixit.R;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
