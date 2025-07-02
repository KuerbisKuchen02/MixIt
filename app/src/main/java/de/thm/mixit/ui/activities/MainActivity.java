package de.thm.mixit.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (BuildConfig.DEBUG) Log.d(TAG, BuildConfig.API_KEY);

//        Example usage of ElementRemoteDataSource
//        ElementRemoteDataSource.combine("Erde", "Wasser", response -> {
//            System.out.println("New Element: " + response);
//        });
    }
}
