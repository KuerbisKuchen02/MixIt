package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Achievement;
import de.thm.mixit.data.entities.BinaryAchievement;
import de.thm.mixit.data.entities.ProgressAchievement;

/**
 * Local data source for accessing the Achievement data using ShardedPreferences.
 * <p>
 * This class handles the saving and loading of Achievement Data.
 * It uses the name attribute of an Achievement as key and uses them to safe the corresponding data
 * in a Shared preference.
 *
 * @author Jannik Heimann
 */
public class AchievementDataSource {
    private final SharedPreferences sp;
    private final Set<String> binaryAchievementNames;
    private final Set<String> progressAchievementNames;

    /**
     * Creates a Shared Preference to store a GameState object.
     * The Shared Preference Object uses one of two pre defined filenames depending on the isArcade
     * param.
     * @param context Context of the Android application, needed to access shared preferences.
     *
     * @author Jannik Heimann
     */
    public AchievementDataSource(Context context) {
        String filepath = context.getString(R.string.achievements_shared_preferences);
        this.sp = context.getSharedPreferences(
                filepath,
                Context.MODE_PRIVATE);
        this.binaryAchievementNames = sp.getStringSet("binaryAchievementNames",
                new HashSet<>());
        this.progressAchievementNames = sp.getStringSet("progressAchievementNames",
                new HashSet<>());
    }

    /**
     * Loads the saved Achievements from the Shared Preference object of the class.
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
                spEditor.putString(achievement.getName(), gson.toJson(achievement,
                        BinaryAchievement.class));
                binaryAchievementNames.add(achievement.getName());
            } else if (achievement instanceof ProgressAchievement) {
                spEditor.putString(achievement.getName(), gson.toJson(achievement,
                        ProgressAchievement.class));
                progressAchievementNames.add(achievement.getName());
            }

        }

        spEditor.putStringSet("binaryAchievementNames", binaryAchievementNames);
        spEditor.putStringSet("progressAchievementNames", progressAchievementNames);

        spEditor.apply();
    }

    /**
     * Checks whether the corresponding Sharded Preferences Object has any data under the
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
    public void deleteSavedAchievement(String name) {
        // Delete from shared preferences
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.remove(name);
        spEditor.apply();

        // Delete also from names list
        binaryAchievementNames.remove(name);
        progressAchievementNames.remove(name);
    }
}
