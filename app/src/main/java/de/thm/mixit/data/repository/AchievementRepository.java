package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;

import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.data.source.AchievementLocalDataSource;

/**
 * Repository class that provides access to Achievement data.
 * <p>
 * Acts as a single source of truth for Achievement data by delegating
 * data operations to a {@link AchievementLocalDataSource}.
 *
 * @author Jannik Heimann
 */
public class AchievementRepository {
    private final AchievementLocalDataSource localDataSource;

    private AchievementRepository(AchievementLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @return {@link AchievementRepository}
     */
    public static AchievementRepository create(Context context) {
        return new AchievementRepository(new AchievementLocalDataSource(context));
    }

    /**
     * Loads all saved Achievements by calling the load Method in the corresponding datasource.
     * @return {@code List<Achievement>}
     */
    public List<Achievement> loadAchievements() {
        return localDataSource.loadAchievements();
    }

    /**
     * Saves the given Achievement by calling the save Method in the corresponding datasource.
     * @param achievements The Achievements which should be safed.
     */
    public void saveAchievements(List<Achievement> achievements) {
        localDataSource.saveAchievement(achievements);
    }

    /**
     * Deletes the saved Achievements.
     */
    public void deleteSavedAchievements() {
        localDataSource.deleteSavedAchievements();
    }
}
