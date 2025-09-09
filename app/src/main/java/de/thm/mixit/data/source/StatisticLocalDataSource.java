package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.data.entity.Statistic;

/**
 * Local data source for accessing the Statistic data using SharedPreferences.
 * <p>
 * This class handles the saving and loading of statistic data.
 * It defines keys and uses them to save the corresponding data in a SharedPreference.
 *
 * @author Jannik Heimann
 */
public class StatisticLocalDataSource {

    private static final String FILEPATH = "de.thm.mixit.STATISTIC_FILE";
    private static final String PREF_TOTAL_PLAYTIME = "TOTAL_PLAYTIME";
    private static final String PREF_TOTAL_COMBINATIONS = "TOTAL_COMBINATIONS";
    private static final String PREF_LONGEST_ELEMENT = "LONGEST_ELEMENT";
    private static final String PREF_NUM_UNLOCKED_ELEMENTS = "NUMBER_OF_UNLOCKED_ELEMENTS";
    private static final String PREF_NUM_DISCARDED_ELEMENTS = "NUMBER_OF_DISCARDED_ELEMENTS";
    private static final String PREF_MOST_DISCARDED_ELEMENTS = "MOST_DISCARDED_ELEMENTS";
    private static final String PREF_MOST_COMBINATIONS_FOR_ELEMENT = "MOST_COMBINATIONS_FOR_ELEMENT";
    private static final String PREF_ARCADE_GAMES_WON = "ARCADE_GAMES_WON";
    private static final String PREF_SHORTEST_TIME_TO_BEAT = "SHORTEST_TIME_TO_BEAT";
    private static final String PREF_FEWEST_TURNS_TO_BEAT = "FEWEST_TURNS_TO_BEAT";
    private static final String PREF_FOUND_CHOCOLATE_CAKE = "FOUND_CHOCOLATE_CAKE";
    private static final String PREF_LAST_GOAL_WORDS = "LAST_GOAL_WORDS";
    private final SharedPreferences sp;

    /**
     * Creates a SharedPreference object to store a GameState object.
     * The SharedPreference object uses one of two pre-defined filenames depending on the isArcade
     * parameter.
     * @param context Context of the Android application, needed to access shared preferences.
     *
     * @author Jannik Heimann
     */
    public StatisticLocalDataSource(Context context) {
        this.sp = context.getSharedPreferences(
                FILEPATH,
                Context.MODE_PRIVATE);
    }

    /**
     * Loads the saved Statistic from the SharedPreference object of the class.
     * @return {@link Statistic}
     *
     * @author Jannik Heimann
     */
    public Statistic loadStatistic() {
        Gson gson = new Gson();
        float playtime = sp.getFloat(PREF_TOTAL_PLAYTIME, 0L);
        long combinations = sp.getLong(PREF_TOTAL_COMBINATIONS, 0);
        String longestElement = sp.getString(PREF_LONGEST_ELEMENT, "");
        int numUnlockedElements = sp.getInt(PREF_NUM_UNLOCKED_ELEMENTS, 0);
        long numDiscardedElements = sp.getLong(PREF_NUM_DISCARDED_ELEMENTS, 0L);
        int mostDiscardedElements = sp.getInt(PREF_MOST_DISCARDED_ELEMENTS, 0);
        int mostElementCombinations = sp.getInt(PREF_MOST_COMBINATIONS_FOR_ELEMENT, 0);
        int arcadeGamesWon = sp.getInt(PREF_ARCADE_GAMES_WON, 0);
        long shortestTimeToBeat = sp.getLong(PREF_SHORTEST_TIME_TO_BEAT, Long.MAX_VALUE);
        int fewestTurnsToBeat = sp.getInt(PREF_FEWEST_TURNS_TO_BEAT, Integer.MAX_VALUE);
        boolean foundChocolateCake = sp.getBoolean(PREF_FOUND_CHOCOLATE_CAKE, false);
        String rawJson = sp.getString(PREF_LAST_GOAL_WORDS, null);
        List<String> lastTargetWords;
        if (rawJson != null) {
            lastTargetWords = gson.fromJson(rawJson, new TypeToken<ArrayList<String>>(){}.getType());
        } else {
            lastTargetWords = new ArrayList<>();
        }

        return new Statistic(playtime, combinations, longestElement, numUnlockedElements,
                numDiscardedElements, mostDiscardedElements, mostElementCombinations,
                arcadeGamesWon, shortestTimeToBeat, fewestTurnsToBeat, foundChocolateCake,
                lastTargetWords);
    }

    /**
     * Saves the given Statistic to the corresponding class SharedPreferences object.
     * @param statistic Statistic data to save.
     *
     * @author Jannik Heimann
     */
    public void saveStatistic(Statistic statistic) {
        Gson gson = new Gson();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putFloat(PREF_TOTAL_PLAYTIME, statistic.getPlaytime());
        spEditor.putLong(PREF_TOTAL_COMBINATIONS, statistic.getNumberOfCombinations());
        spEditor.putString(PREF_LONGEST_ELEMENT, statistic.getLongestElement());
        spEditor.putInt(PREF_NUM_UNLOCKED_ELEMENTS, statistic.getNumberOfUnlockedElements());
        spEditor.putLong(PREF_NUM_DISCARDED_ELEMENTS, statistic.getNumberOfDiscardedElements());
        spEditor.putInt(PREF_MOST_DISCARDED_ELEMENTS, statistic.getMostDiscardedElements());
        spEditor.putInt(PREF_MOST_COMBINATIONS_FOR_ELEMENT,
                statistic.getMostCombinationsForOneElement());
        spEditor.putInt(PREF_ARCADE_GAMES_WON, statistic.getArcadeGamesWon());
        spEditor.putLong(PREF_SHORTEST_TIME_TO_BEAT, statistic.getShortestArcadeTimeToBeat());
        spEditor.putInt(PREF_FEWEST_TURNS_TO_BEAT, statistic.getFewestArcadeTurnsToBeat());
        spEditor.putBoolean(PREF_FOUND_CHOCOLATE_CAKE, statistic.getFoundChocolateCake());
        spEditor.putString(PREF_LAST_GOAL_WORDS, gson.toJson(statistic.getLastTargetWords()));
        spEditor.apply();
    }

    /**
     * Checks whether the corresponding SharedPreferences object has any data saved.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedStatistic() {
        return !sp.getAll().isEmpty();
    }

    /**
     * Deletes all saved data within the corresponding SharedPreferences object.
     *
     * @author Jannik Heimann
     */
    public void deleteSavedStatistic() {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
