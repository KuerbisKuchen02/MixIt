package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entity.GameState;
import de.thm.mixit.data.source.GameStateLocalDataSource;

/**
 * Repository class that provides access to GameState data.
 * <p>
 * Acts as a single source of truth for GameState data by delegating
 * data operations to a {@link GameStateLocalDataSource}.
 *
 * @author Jannik Heimann
 */
public class GameStateRepository {

    private final GameStateLocalDataSource localDataSource;

    private GameStateRepository(GameStateLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @param isArcade Whether the to be saved GameState data belongs to the arcade or endless
     *                 game mode.
     * @return {@link GameStateRepository}
     */
    public static GameStateRepository create(Context context, boolean isArcade) {
        return new GameStateRepository(new GameStateLocalDataSource(context, isArcade));
    }

    /**
     * Loads the last saved GameState by calling the load Method in the corresponding datasource.
     * @return {@link GameState}
     */
    public GameState loadGameState() {
        return localDataSource.loadGameState();
    }

    /**
     * Saves the given gameState by calling the save Method in the corresponding datasource.
     */
   public void saveGameState(GameState gameState) {
        localDataSource.saveGameState(gameState);
   }

    /**
     * Whether there is an existing saved GameState.
     * @return boolean
     */
   public boolean hasSavedGameState() { return localDataSource.hasSavedGameState(); }

    /**
     * Deletes the last saved GameState.
     */
    public void deleteSavedGameState() { localDataSource.deleteSavedGameState(); }
}
