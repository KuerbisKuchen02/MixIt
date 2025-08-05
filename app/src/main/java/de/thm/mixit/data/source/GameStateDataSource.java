package de.thm.mixit.data.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.entities.GameState;
import de.thm.mixit.data.model.ElementChip;

/**
 * Local data source for accessing the GameState data using ShardedPreferences.
 * <p>
 * This class handles the saving and loading of GameState Data.
 * It defines Keys and uses them to safe the corresponding data in a Shared preference.
 * It also uses the GSON library to transform Classes into JSON Format.
 *
 * @author Jannik Heimann
 */
public class GameStateDataSource {

    private final SharedPreferences sp;
    private static final String PREF_TIME = "TIME";
    private static final String PREF_TURNS = "TURNS";
    private static final String PREF_GOAL_ELEMENT = "GOAL_ELEMENT";
    private static final String PREF_ELEMENTCHIPS = "ELEMENTCHIPS";

    /**
     * Creates a Shared Preference to store a GameState object.
     * The Shared Preference Object uses one of two pre defined filenames depending on the isArcade
     * param.
     * @param context Context of the Android application, needed to access shared preferences.
     * @param isArcade Whether the GameState data belongs to the arcade or endless game mode.
     *
     * @author Jannik Heimann
     */
    public GameStateDataSource(Context context, boolean isArcade) {
        String filepath = context.getString(
                isArcade ? R.string.gamestate_shared_preferences_arcade :
                R.string.gamestate_shared_preferences_endless);
        this.sp = context.getSharedPreferences(
                filepath,
                Context.MODE_PRIVATE);
    }

    /**
     * Loads the last saved GameState from the Shared Preference object of the class.
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
        Element goalElement = null;
        List<ElementChip> elementChips = null;

        time = sp.getLong(PREF_TIME, 0L);
        turns = sp.getInt(PREF_TURNS, 0);

        rawJson = sp.getString(PREF_GOAL_ELEMENT, null);
        if (rawJson != null) goalElement = gson.fromJson(rawJson, Element.class);

        rawJson = sp.getString(PREF_ELEMENTCHIPS, null);
        if (rawJson != null) elementChips = gson.fromJson(rawJson, elementListType);

        return new GameState(time, turns, goalElement, elementChips);
    }

    /**
     * Saves the given GameState to the corresponding class Shared Preferences object.
     * @param gameState GameState data to save.
     *
     * @author Jannik Heimann
     */
    public void saveGameState(GameState gameState) {
        Gson gson = new Gson();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong(PREF_TIME, gameState.getTime());
        spEditor.putInt(PREF_TURNS, gameState.getTurns());
        spEditor.putString(PREF_GOAL_ELEMENT, gson.toJson(gameState.getGoalElement()));
        spEditor.putString(PREF_ELEMENTCHIPS, gson.toJson(gameState.getElementChips()));
        spEditor.apply();
    }
}
