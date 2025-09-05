package de.thm.mixit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private AutoCompleteTextView autoCompleteTextViewTheme;
    private AutoCompleteTextView autoCompleteTextViewLanguage;

    /**
     * Binding spinner items to spinners.
     * ref: <a href="https://developer.android.com/develop/ui/views/components/spinner">Spinners</a>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // Language
        autoCompleteTextViewLanguage = findViewById(R.id.dropdown_settings_language);

        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item
        );

        adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autoCompleteTextViewLanguage.setAdapter(adapterLanguage);
        // Workaround Solution to prevent options to disappear
        autoCompleteTextViewLanguage.setThreshold(Integer.MAX_VALUE);

        preselectLanguage(adapterLanguage);

        autoCompleteTextViewLanguage.setOnItemClickListener(this::onLanguageSelected);

        // Theme
        autoCompleteTextViewTheme = findViewById(R.id.dropdown_settings_theme);

        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(
                this, R.array.theme_array, android.R.layout.simple_spinner_item
        );

        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autoCompleteTextViewTheme.setAdapter(adapterTheme);
        // Workaround Solution to prevent options to disappear
        autoCompleteTextViewTheme.setThreshold(Integer.MAX_VALUE);

        // set initial selection based on current mode
        preselectTheme(adapterTheme);

        autoCompleteTextViewTheme.setOnItemClickListener(this::onThemeSelected);

        Log.i(TAG, "SettingsActivity was created");
    }

    /**
     * This method preselects the language based on the set app language.
     */
    private void preselectLanguage(ArrayAdapter<CharSequence> adapter) {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();

        autoCompleteTextViewLanguage.post(() -> {
            int selection;
            String langTag;

            if (!currentLocales.isEmpty()) {
                langTag = Objects.requireNonNull(currentLocales.get(0)).toLanguageTag();
            } else {
                langTag = "system";
            }

            switch (langTag) {
                case "en": selection = 1; break;
                case "de": selection = 2; break;
                default: selection = 0; break;
            }

            if (selection < adapter.getCount()) {
                autoCompleteTextViewLanguage.setText(adapter.getItem(selection).toString(),
                        false);
                Log.d(TAG, "Preselected language: " + adapter.getItem(selection));
            } else {
                Log.e(TAG, "Index " + selection + " out of bounds, adapter count = " +
                        adapter.getCount());
            }
        });
    }

    /**
     * This Method preselects the theme based on the uiModeManager mode.
     * ref:
     * <a href="https://developer.android.com/develop/ui/views/theming/darktheme#change-themes">
     *     In app dark theme changes
     * </a>
     */
    private void preselectTheme(ArrayAdapter<CharSequence> adapter) {
        autoCompleteTextViewTheme.post(() -> {
            int selection = 0;
            switch (AppCompatDelegate.getDefaultNightMode()) {
                case AppCompatDelegate.MODE_NIGHT_NO: selection = 1; break;
                case AppCompatDelegate.MODE_NIGHT_YES: selection = 2; break;
                default: // System

            }
            if (selection < adapter.getCount()) {
                autoCompleteTextViewTheme.setText(adapter.getItem(selection).toString(),
                        false);
                Log.d(TAG, "Preselected theme: " + adapter.getItem(selection));
            } else {
                Log.e(TAG, "Index " + selection + " out of bounds, adapter count = " +
                        adapter.getCount());
            }

        });
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
        recreate();
    }

    public void onResetProgressClicked(View view) {
        Log.i(TAG, "Reset Progress Button was clicked.");
        new DialogResetProgress().show(getSupportFragmentManager(), "DialogResetProgress");
    }

    public void onLanguageSelected(AdapterView<?> parent, View view, int position, long id) {
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
    }

    public void onThemeSelected(AdapterView<?> parent, View view, int position, long id) {
        // "system","light","dark"
        String[] values = getResources().getStringArray(R.array.theme_values);

        String selectedTheme = values[position];

        applyTheme(selectedTheme);

        Log.d(TAG, "Theme selected: " + selectedTheme);
    }

    public void onBackButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "Return button clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
