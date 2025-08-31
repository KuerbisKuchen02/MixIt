package de.thm.mixit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Objects;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.ui.fragments.DialogResetProgress;

/**
 * Activity for the settings view.
 * <p>
 * This Activity implements the logic for setting the app theme and language.
 * Also it contains the feature to reset the free play progress.
 * </p>
 *
 * @author Jonathan Hildebrandt
 */
public class SettingsActivity
        extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Spinner spinnerTheme;
    private Spinner spinnerLanguage;

    /**
     * Binding spinner items to spinners.
     * ref: <a href="https://developer.android.com/develop/ui/views/components/spinner">Spinners</a>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_settings);

        // Language Spinner

        spinnerLanguage = findViewById(R.id.spinner_settings_language);

        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item
        );

        adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLanguage.setAdapter(adapterLanguage);

        preselectLanguageSpinner();

        // Theme Spinner

        spinnerTheme = findViewById(R.id.spinner_settings_theme);

        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(
                this, R.array.theme_array, android.R.layout.simple_spinner_item
        );

        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTheme.setAdapter(adapterTheme);

        // set initial selection based on current mode
        preselectThemeSpinner();

        spinnerLanguage.setOnItemSelectedListener(this);
        spinnerTheme.setOnItemSelectedListener(this);

        Log.i(TAG, "SettingsActivity was created");
    }

    /**
     * This method preselects the language spinner based on the set app language.
     */
    private void preselectLanguageSpinner() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();

        if (!currentLocales.isEmpty()) {
            String langTag = Objects.requireNonNull(currentLocales.get(0)).toLanguageTag();

            switch (langTag) {
                case "en":
                    spinnerLanguage.setSelection(1);
                    break;
                case "de":
                    spinnerLanguage.setSelection(2);
                    break;
                default:
                    spinnerLanguage.setSelection(0);
                    break;
            }
        }
    }

    /**
     * This Method preselects the theme spinner based on the uiModeManager mode.
     * ref:
     * <a href="https://developer.android.com/develop/ui/views/theming/darktheme#change-themes">
     *     In app dark theme changes
     * </a>
     */
    private void preselectThemeSpinner() {
        int selection = 0; // System
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_NO:  selection = 1; break; // Light
            case AppCompatDelegate.MODE_NIGHT_YES: selection = 2; break; // Dark
            default: // System
        }
        spinnerTheme.setSelection(selection, false);
    }

    /**
     * This Method applies the selected spinner item to the app theme
     * ref:
     * <a href="https://developer.android.com/develop/ui/views/theming/darktheme#change-themes">
     *     In app dark theme changes
     * </a>
     */
    private void applyTheme(String themeValue) {
        AppCompatDelegate.setDefaultNightMode(
                "light".equals(themeValue) ? AppCompatDelegate.MODE_NIGHT_NO : // Light
                        "dark".equals(themeValue)  ? AppCompatDelegate.MODE_NIGHT_YES : // Dark
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System
        );
    }

    public void onResetProgressClicked(View view) {
        Log.i(TAG, "Reset Progress Button was clicked.");
        new DialogResetProgress().show(getSupportFragmentManager(), "DialogResetProgress");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();

        if (viewId == R.id.spinner_settings_language) {
            // "system", "en", "de"
            String[] values = getResources().getStringArray(R.array.language_values);
            String selectedLangTag = values[position];

            Log.d(TAG, "Language selected (tag): " + selectedLangTag);

            // if local is system, clear locale list if not set it to selected locale
            LocaleListCompat locales = "system".equals(selectedLangTag)
                    ? LocaleListCompat.getEmptyLocaleList()
                    : LocaleListCompat.forLanguageTags(selectedLangTag);

            // ref:
            // https://developer.android.com/guide/topics/resources/app-languages#androidx-impl
            AppCompatDelegate.setApplicationLocales(locales);
        } else if (viewId == R.id.spinner_settings_theme) {
            // "system","light","dark"
            String[] values = getResources().getStringArray(R.array.theme_values);

            String selectedTheme = values[position];

            applyTheme(selectedTheme);

            Log.d(TAG, "Theme selected: " + selectedTheme);
        }
    }

    public void onBackButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "Return button clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override public void onNothingSelected(AdapterView<?> parent) {}
}
