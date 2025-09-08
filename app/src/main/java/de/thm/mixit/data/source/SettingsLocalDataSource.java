package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import de.thm.mixit.data.entity.Settings;

/**
 * Local data source for accessing the Settings data using SharedPreferences.
 * <p>
 * This class handles the saving and loading of Settings Data.
 * It defines keys and uses them to safe the corresponding data in a SharedPreference.
 *
 * @author Jannik Heimann
 */
public class SettingsLocalDataSource {
    private static final String FILEPATH = "de.thm.mixit.SETTINGS_FILE";
    private static final String PREF_LANGUAGE = "LANGUAGE";
    private static final String PREF_THEME = "THEME";
    private final SharedPreferences sp;


    public SettingsLocalDataSource(Context context) {
        this.sp = context.getSharedPreferences(
                FILEPATH,
                Context.MODE_PRIVATE);
    }

    /**
     * Loads the saved Settings from the SharedPreference object of the class.
     * @return {@link Settings}
     *
     * @author Jannik Heimann
     */
    public Settings loadSettings() {
        String language = sp.getString(PREF_LANGUAGE, "system");
        String theme = sp.getString(PREF_THEME, "system");

        return new Settings(language, theme);
    }

    /**
     * Saves the given Settings to the corresponding class SharedPreferences object.
     * @param settings Settings data to save.
     *
     * @author Jannik Heimann
     */
    public void saveSettings(Settings settings) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(PREF_LANGUAGE, settings.getLanguage());
        spEditor.putString(PREF_THEME, settings.getTheme());
        spEditor.apply();
    }

    /**
     * Deletes all saved data within the corresponding SharedPreferences object.
     *
     * @author Jannik Heimann
     */
    public void deleteSavedSettings() {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
