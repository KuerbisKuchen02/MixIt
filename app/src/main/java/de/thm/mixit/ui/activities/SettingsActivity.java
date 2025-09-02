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
public class SettingsActivity
        extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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

        preselectLanguage();

        autoCompleteTextViewLanguage.setOnItemClickListener(this::onItemSelected);

        // Theme
        autoCompleteTextViewTheme = findViewById(R.id.dropdown_settings_theme);

        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(
                this, R.array.theme_array, android.R.layout.simple_spinner_item
        );

        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autoCompleteTextViewTheme.setAdapter(adapterTheme);

        // set initial selection based on current mode
        preselectTheme();

        autoCompleteTextViewTheme.setOnItemClickListener(this::onItemSelected);

        Log.i(TAG, "SettingsActivity was created");
    }

    /**
     * This method preselects the language based on the set app language.
     */
    private void preselectLanguage() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();

        if (!currentLocales.isEmpty()) {
            String langTag = Objects.requireNonNull(currentLocales.get(0)).toLanguageTag();

            switch (langTag) {
                case "en":
                    autoCompleteTextViewLanguage.setSelection(1);
                    break;
                case "de":
                    autoCompleteTextViewLanguage.setSelection(2);
                    break;
                default:
                    autoCompleteTextViewLanguage.setSelection(0);
                    break;
            }
        }
    }

    /**
     * This Method preselects the theme based on the uiModeManager mode.
     * ref:
     * <a href="https://developer.android.com/develop/ui/views/theming/darktheme#change-themes">
     *     In app dark theme changes
     * </a>
     */
    private void preselectTheme() {
        int selection = 0; // System
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_NO:  selection = 1; break; // Light
            case AppCompatDelegate.MODE_NIGHT_YES: selection = 2; break; // Dark
            default: // System
        }
        autoCompleteTextViewTheme.setSelection(selection);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = view.getId();

        if (viewId == R.id.dropdown_settings_language) {
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
        } else if (viewId == R.id.dropdown_settings_theme) {
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
