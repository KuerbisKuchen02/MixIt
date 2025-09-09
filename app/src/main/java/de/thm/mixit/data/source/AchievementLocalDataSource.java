package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.data.entity.BinaryAchievement;
import de.thm.mixit.data.entity.ProgressAchievement;

/**
 * Local data source for accessing the Achievement data using SharedPreferences.
 * <p>
 * This class handles the saving and loading of Achievement Data.
 * It uses the ID attribute of an Achievement as key and uses them to safe the corresponding data
 * in a Shared preference.
 *
 * @author Jannik Heimann
 */
public class AchievementLocalDataSource {
    private static final String FILEPATH = "de.thm.mixit.ACHIEVEMENTS_FILE";
    private final SharedPreferences sp;
    private final Set<String> binaryAchievementIds;
    private final Set<String> progressAchievementIds;

    /**
     * Creates a SharedPreference to store a GameState object.
     * The SharedPreference object uses one of two pre-defined filenames depending on the isArcade
     * parameter.
     * @param context context of the Android application, needed to access SharedPreferences.
     *
     * @author Jannik Heimann
     */
    public AchievementLocalDataSource(Context context) {
        this.sp = context.getSharedPreferences(
                FILEPATH,
                Context.MODE_PRIVATE);
        this.binaryAchievementIds = sp.getStringSet("binaryAchievementIds",
                new HashSet<>());
        this.progressAchievementIds = sp.getStringSet("progressAchievementIds",
                new HashSet<>());
    }

    /**
     * Loads the saved Achievements from the SharedPreference object of the class.
     * @return {@code List<Achievement>}
     *
     * @author Jannik Heimann
     */
    public List<Achievement> loadAchievements() {
        Gson gson = new Gson();
        List<Achievement> achievements = new ArrayList<>();
        String rawJson;

        // Load from shared preferences based on the subtype
        for (String name: binaryAchievementIds) {
            rawJson = sp.getString(name, null);
            if (rawJson != null) achievements.add(gson.fromJson(rawJson,
                    BinaryAchievement.class));
        }
        for (String name: progressAchievementIds) {
            rawJson = sp.getString(name, null);
            if (rawJson != null) achievements.add(gson.fromJson(rawJson,
                    ProgressAchievement.class));
        }

        return achievements;
    }

    /**
     * Saves the given Achievement to the corresponding class Shared Preferences object.
     * @param achievements Achievements data to save.
     *
     * @author Jannik Heimann
     */
    public void saveAchievement(List<Achievement> achievements) {
        Gson gson = new Gson();
        SharedPreferences.Editor spEditor = sp.edit();

        // Save Achievements in shared preferences and also in name lists
        for (Achievement achievement: achievements) {
            if (achievement instanceof BinaryAchievement) {
                spEditor.putString(Integer.toString(achievement.getId()), gson.toJson(achievement,
                        BinaryAchievement.class));
                binaryAchievementIds.add(Integer.toString(achievement.getId()));
            } else if (achievement instanceof ProgressAchievement) {
                spEditor.putString(Integer.toString(achievement.getId()), gson.toJson(achievement,
                        ProgressAchievement.class));
                progressAchievementIds.add(Integer.toString(achievement.getId()));
            }
        }
        spEditor.putStringSet("binaryAchievementIds", binaryAchievementIds);
        spEditor.putStringSet("progressAchievementIds", progressAchievementIds);

        spEditor.apply();
    }

    /**
     * Deletes the saved data within the corresponding Shared Preferences Object.
     *
     * @author Jannik Heimann
     */
    public void deleteSavedAchievements() {
        // Delete from shared preferences
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
