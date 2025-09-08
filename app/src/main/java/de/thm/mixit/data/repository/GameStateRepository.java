package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entity.GameState;
import de.thm.mixit.data.source.GameStateDataSource;

/**
 * Repository class that provides access to GameState data.
 * <p>
 * Acts as a single source of truth for GameState data by delegating
 * data operations to a {@link GameStateDataSource}.
 *
 * @author Jannik Heimann
 */
public class GameStateRepository {

    private final GameStateDataSource datasource;

    private GameStateRepository(GameStateDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @param isArcade Whether the to be saved GameState data belongs to the arcade or endless
     *                 game mode.
     * @return {@link GameStateRepository}
     *
     * @author Jannik Heimann
     */
    public static GameStateRepository create(Context context, boolean isArcade) {
        return new GameStateRepository(new GameStateDataSource(context, isArcade));
    }

    /**
     * Loads the last saved GameState by calling the load Method in the corresponding datasource.
     * @return {@link GameState}
     *
     * @author Jannik Heimann
     */
    public GameState loadGameState() {
        return datasource.loadGameState();
    }

    /**
     * Saves the given gameState by calling the save Method in the corresponding datasource.
     *
     * @author Jannik Heimann
     */
   public void saveGameState(GameState gameState) {
        datasource.saveGameState(gameState);
   }

    /**
     * Whether there is an existing saved GameState.
     * @return boolean
     *
     * @author Jannik Heimann
     */
   public boolean hasSavedGameState() { return datasource.hasSavedGameState(); }

    /**
     * Deletes the last saved GameState.
     */
    public void deleteSavedGameState() { datasource.deleteSavedGameState(); }
}
