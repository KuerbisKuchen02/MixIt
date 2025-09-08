package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;

import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.data.source.AchievementDataSource;

/**
 * Repository class that provides access to Achievement data.
 * <p>
 * Acts as a single source of truth for Achievement data by delegating
 * data operations to a {@link AchievementDataSource}.
 *
 * @author Jannik Heimann
 */
public class AchievementRepository {
    private final AchievementDataSource datasource;

    private AchievementRepository(AchievementDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @return {@link AchievementRepository}
     *
     * @author Jannik Heimann
     */
    public static AchievementRepository create(Context context) {
        return new AchievementRepository(new AchievementDataSource(context));
    }

    /**
     * Loads all saved Achievements by calling the load Method in the corresponding datasource.
     * @return {@code List<Achievement>}
     *
     * @author Jannik Heimann
     */
    public List<Achievement> loadAchievements() {
        return datasource.loadAchievements();
    }

    /**
     * Saves the given Achievement by calling the save Method in the corresponding datasource.
     * @param achievements The Achievements which should be safed.
     * @author Jannik Heimann
     */
    public void saveAchievements(List<Achievement> achievements) {
        datasource.saveAchievement(achievements);
    }

    /**
     * Whether there is an existing saved data for an Achievement with the given name.
     * @param name The name of the Achievement which should be checked.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedAchievement(String name) { return datasource.hasSavedAchievement(name); }

    /**
     * Deletes the saved Achievements.
     */
    public void deleteSavedAchievements() { datasource.deleteSavedAchievements(); }
}
