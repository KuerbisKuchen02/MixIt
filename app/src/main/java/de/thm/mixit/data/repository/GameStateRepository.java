package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entities.GameState;
import de.thm.mixit.data.source.GameStateDatasource;

/**
 * TODO Write description
 * @return
 */
public class GameStateRepository {

    private final GameStateDatasource datasource;

    private GameStateRepository(GameStateDatasource datasource) {
        this.datasource = datasource;
    }

    /**
     * TODO Write description
     * @return
     */
    public GameStateRepository create(Context context, boolean isArcade) {
        return new GameStateRepository(new GameStateDatasource(context, isArcade));
    }

    /**
     * TODO Write description
     * @return
     */
    public GameState loadGameState() {
        return datasource.loadGameState();
    }

    /**
     * TODO Write description
     * @return
     */
   public void saveGameState(GameState gameState) {
        datasource.saveGameState(gameState);
   }

}
