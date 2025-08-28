package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Statistic;

/**
 * Local data source for accessing the Statistic data using ShardedPreferences.
 * <p>
 * This class handles the saving and loading of Statistic Data.
 * It defines Keys and uses them to safe the corresponding data in a Shared preference.
 *
 * @author Jannik Heimann
 */
public class StatisticDataSource {

    private final SharedPreferences sp;
    private static final String PREF_TOTAL_PLAYTIME = "TOTAL_PLAYTIME";
    private static final String PREF_TOTAL_COMBINATIONS = "TOTAL_COMBINATIONS";
    private static final String PREF_LONGEST_ELEMENT = "LONGEST_ELEMENT";
    private static final String PREF_NUM_UNLOCKED_ELEMENTS = "NUMBER_OF_UNLOCKED_ELEMENTS";
    private static final String PREF_NUM_DISCARDED_ELEMENTS = "NUMBER_OF_DISCARDED_ELEMENTS";
    private static final String PREF_MOST_DISCARDED_ELEMENTS = "MOST_DISCARDED_ELEMENTS";
    private static final String PREF_MOST_COMBINATIONS_FOR_ELEMENT = "MOST_COMBINATIONS_FOR_ELEMENT";
    private static final String PREF_ARCADE_GAMES_PLAYED = "ARCADE_GAMES_PLAYED";
    private static final String PREF_ARCADE_GAMES_WON = "ARCADE_GAMES_WON";
    private static final String PREF_SHORTEST_TIME_TO_BEAT = "SHORTEST_TIME_TO_BEAT";
    private static final String PREF_FEWEST_TURNS_TO_BEAT = "FEWEST_TURNS_TO_BEAT";

    /**
     * Creates a Shared Preference to store a GameState object.
     * The Shared Preference Object uses one of two pre defined filenames depending on the isArcade
     * param.
     * @param context Context of the Android application, needed to access shared preferences.
     *
     * @author Jannik Heimann
     */
    public StatisticDataSource(Context context) {
        String filepath = context.getString(R.string.statistic_shared_preferences);
        this.sp = context.getSharedPreferences(
                filepath,
                Context.MODE_PRIVATE);
    }

    /**
     * Loads the saved Statistic from the Shared Preference object of the class.
     * @return {@link Statistic}
     *
     * @author Jannik Heimann
     */
    public Statistic loadStatistic() {
        long playtime = sp.getLong(PREF_TOTAL_PLAYTIME, 0L);
        long combinations = sp.getLong(PREF_TOTAL_COMBINATIONS, 0);
        String longestElement = sp.getString(PREF_LONGEST_ELEMENT, "");
        int numUnlockedElements = sp.getInt(PREF_NUM_UNLOCKED_ELEMENTS, 0);
        long numDiscardedElements = sp.getLong(PREF_NUM_DISCARDED_ELEMENTS, 0L);
        int mostDiscardedElements = sp.getInt(PREF_MOST_DISCARDED_ELEMENTS, 0);
        int mostElementCombinations = sp.getInt(PREF_MOST_COMBINATIONS_FOR_ELEMENT, 0);
        int arcadeGamesPlayed = sp.getInt(PREF_ARCADE_GAMES_PLAYED, 0);
        int arcadeGamesWon = sp.getInt(PREF_ARCADE_GAMES_WON, 0);
        long shortestTimeToBeat = sp.getLong(PREF_SHORTEST_TIME_TO_BEAT, Long.MAX_VALUE);
        int fewestTurnsToBeat = sp.getInt(PREF_FEWEST_TURNS_TO_BEAT, Integer.MAX_VALUE);


        return new Statistic(playtime, combinations, longestElement, numUnlockedElements,
                numDiscardedElements, mostDiscardedElements, mostElementCombinations,
                arcadeGamesPlayed, arcadeGamesWon, shortestTimeToBeat, fewestTurnsToBeat);
    }

    /**
     * Saves the given Statistic to the corresponding class Shared Preferences object.
     * @param statistic Statistic data to save.
     *
     * @author Jannik Heimann
     */
    public void saveStatistic(Statistic statistic) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong(PREF_TOTAL_PLAYTIME, statistic.getPlaytime());
        spEditor.putLong(PREF_TOTAL_COMBINATIONS, statistic.getNumberOfCombinations());
        spEditor.putString(PREF_LONGEST_ELEMENT, statistic.getLongestElement());
        spEditor.putInt(PREF_NUM_UNLOCKED_ELEMENTS, statistic.getNumberOfUnlockedElements());
        spEditor.putLong(PREF_NUM_DISCARDED_ELEMENTS, statistic.getNumberOfDiscardedElements());
        spEditor.putInt(PREF_MOST_DISCARDED_ELEMENTS, statistic.getMostDiscardedElements());
        spEditor.putInt(PREF_MOST_COMBINATIONS_FOR_ELEMENT,
                statistic.getMostCombinationsForOneElement());
        spEditor.putInt(PREF_ARCADE_GAMES_PLAYED, statistic.getArcadeGamesPlayed());
        spEditor.putInt(PREF_ARCADE_GAMES_WON, statistic.getArcadeGamesWon());
        spEditor.putLong(PREF_SHORTEST_TIME_TO_BEAT, statistic.getShortestArcadeTimeToBeat());
        spEditor.putInt(PREF_FEWEST_TURNS_TO_BEAT, statistic.getFewestArcadeTurnsToBeat());
        spEditor.apply();
    }

    /**
     * Checks whether the corresponding Sharded Preferences Object has any data saved.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedStatistic() {
        return !sp.getAll().isEmpty();
    }

    /**
     * Deletes all saved data within the corresponding Shared Preferences Object.
     *
     * @author Jannik Heimann
     */
    public void deleteSavedStatistic() {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
