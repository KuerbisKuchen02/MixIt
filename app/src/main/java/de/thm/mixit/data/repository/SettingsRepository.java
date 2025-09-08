package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entity.Settings;
import de.thm.mixit.data.source.SettingsLocalDataSource;

/**
 * Repository class that provides access to saved Settings data.
 * <p>
 * Acts as a single source of truth for Settings data by delegating
 * data operations to a {@link SettingsLocalDataSource}.
 *
 * @author Jannik Heimann
 */
public class SettingsRepository {

    private final SettingsLocalDataSource datasource;

    private SettingsRepository(SettingsLocalDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @return {@link SettingsRepository}
     *
     * @author Jannik Heimann
     */
    public static SettingsRepository create(Context context) {
        return new SettingsRepository(new SettingsLocalDataSource(context));
    }

    /**
     * Loads the last saved Settings by calling the load Method in the corresponding datasource.
     * @return {@link Settings}
     *
     * @author Jannik Heimann
     */
    public Settings loadSettings() {
        return datasource.loadSettings();
    }

    /**
     * Saves the given Settings by calling the save Method in the corresponding datasource.
     *
     * @author Jannik Heimann
     */
    public void saveSettings(Settings settings) {
        datasource.saveSettings(settings);
    }


    /**
     * Deletes the saved Settings.
     */
    public void deleteSavedSettings() { datasource.deleteSavedSettings(); }
}
