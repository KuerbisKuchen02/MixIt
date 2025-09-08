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
 * It uses the name attribute of an Achievement as key and uses them to safe the corresponding data
 * in a Shared preference.
 *
 * @author Jannik Heimann
 */
public class AchievementLocalDataSource {
    private static final String FILEPATH = "de.thm.mixit.ACHIEVEMENTS_FILE";
    private final Context context;
    private final SharedPreferences sp;
    private final Set<String> binaryAchievementNames;
    private final Set<String> progressAchievementNames;

    /**
     * Creates a SharedPreference to store a GameState object.
     * The SharedPreference object uses one of two pre-defined filenames depending on the isArcade
     * parameter.
     * @param context context of the Android application, needed to access SharedPreferences.
     *
     * @author Jannik Heimann
     */
    public AchievementLocalDataSource(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences(
                FILEPATH,
                Context.MODE_PRIVATE);
        this.binaryAchievementNames = sp.getStringSet("binaryAchievementNames",
                new HashSet<>());
        this.progressAchievementNames = sp.getStringSet("progressAchievementNames",
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
        for (String name: binaryAchievementNames) {
            rawJson = sp.getString(name, null);
            if (rawJson != null) achievements.add(gson.fromJson(rawJson,
                    BinaryAchievement.class));
        }
        for (String name: progressAchievementNames) {
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
                spEditor.putString(context.getString(achievement.getNameResId()), gson.toJson(achievement,
                        BinaryAchievement.class));
                binaryAchievementNames.add(context.getString(achievement.getNameResId()));
            } else if (achievement instanceof ProgressAchievement) {
                spEditor.putString(context.getString(achievement.getNameResId()), gson.toJson(achievement,
                        ProgressAchievement.class));
                progressAchievementNames.add(context.getString(achievement.getNameResId()));
            }

        }

        spEditor.putStringSet("binaryAchievementNames", binaryAchievementNames);
        spEditor.putStringSet("progressAchievementNames", progressAchievementNames);

        spEditor.apply();
    }

    /**
     * Checks whether the corresponding ShardedPreferences object has any data under the
     * achievement name saved.
     * @param name Name of the Achievement.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedAchievement(String name) {
        return sp.contains(name);
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
