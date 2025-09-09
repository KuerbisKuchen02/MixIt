package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.data.entity.GameState;
import de.thm.mixit.data.model.ElementChip;

/**
 * Local data source for accessing the GameState data using ShardedPreferences.
 * <p>
 * This class handles the saving and loading of GameState Data.
 * It defines Keys and uses them to safe the corresponding data in a SharedPreference.
 * It also uses the GSON library to transform classes into JSON Format.
 *
 * @author Jannik Heimann
 */
public class GameStateLocalDataSource {

    private static final String FILEPATH_ENDLESS = "de.thm.mixit.GAMESTATE_ENDLESS_FILE";
    private static final String FILEPATH_ARCADE = "de.thm.mixit.GAMESTATE_ARCADE_FILE";
    private static final String PREF_TIME = "TIME";
    private static final String PREF_TURNS = "TURNS";
    private static final String PREF_GOAL_ELEMENT = "GOAL_ELEMENT";
    private static final String PREF_ELEMENTCHIPS = "ELEMENTCHIPS";
    private final SharedPreferences sp;

    /**
     * Creates a Shared Preference to store a GameState object.
     * The SharedPreference object uses one of two pre-defined filenames depending on the isArcade
     * parameter.
     * @param context context of the Android application, needed to access SharedPreferences.
     * @param isArcade whether the GameState data belongs to the arcade or endless game mode.
     *
     * @author Jannik Heimann
     */
    public GameStateLocalDataSource(Context context, boolean isArcade) {
        String filepath = isArcade ? FILEPATH_ARCADE : FILEPATH_ENDLESS;
        this.sp = context.getSharedPreferences(
                filepath,
                Context.MODE_PRIVATE);
    }

    /**
     * Loads the last saved GameState from the SharedPreference object of the class.
     * @return {@link GameState}
     *
     * @author Jannik Heimann
     */
    public GameState loadGameState() {
        Gson gson = new Gson();
        Type elementListType = new TypeToken<List<ElementChip>>() {}.getType();
        long time;
        int turns;
        String rawJson;
        String[] targetElement = null;
        ArrayList<ElementChip> elementChips = new ArrayList<>();

        time = sp.getLong(PREF_TIME, 0L);
        turns = sp.getInt(PREF_TURNS, 0);

        rawJson = sp.getString(PREF_GOAL_ELEMENT, null);
        if (rawJson != null) targetElement = gson.fromJson(rawJson, String[].class);

        rawJson = sp.getString(PREF_ELEMENTCHIPS, null);
        if (rawJson != null) elementChips = gson.fromJson(rawJson, elementListType);

        return new GameState(time, turns, targetElement, elementChips);
    }

    /**
     * Saves the given GameState to the corresponding class SharedPreferences object.
     * @param gameState GameState data to save.
     *
     * @author Jannik Heimann
     */
    public void saveGameState(GameState gameState) {
        Gson gson = new Gson();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong(PREF_TIME, gameState.getTime());
        spEditor.putInt(PREF_TURNS, gameState.getTurns());
        spEditor.putString(PREF_GOAL_ELEMENT, gson.toJson(gameState.getTargetElement()));
        spEditor.putString(PREF_ELEMENTCHIPS, gson.toJson(gameState.getElementChips()));
        spEditor.apply();
    }

    /**
     * Checks whether the corresponding ShardedPreferences object has any data saved.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedGameState() {
        return !sp.getAll().isEmpty();
    }

    /**
     * Deletes all saved data within the corresponding Shared Preferences Object.
     *
     * @author Jannik Heimann
     */
    public void deleteSavedGameState() {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
