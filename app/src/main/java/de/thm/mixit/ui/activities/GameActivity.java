package de.thm.mixit.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import de.thm.mixit.ui.fragments.PlaygroundFragment;


public class GameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO read intent (either endless mode or arcade)
        PlaygroundFragment playground = new PlaygroundFragment();
        View v = playground.onCreateView(LayoutInflater.from(this), null,
                savedInstanceState);
        setContentView(v);
    }

}
