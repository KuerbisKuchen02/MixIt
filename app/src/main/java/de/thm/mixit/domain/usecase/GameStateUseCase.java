package de.thm.mixit.domain.usecase;

import android.content.Context;

import de.thm.mixit.data.entities.GameState;
import de.thm.mixit.data.repository.GameStateRepository;

/**
 * Use case for loading and persisting game state, statistics and achievement progress
 * <p>
 * Before using other class methods use {@link #init(boolean)} to initialize the repositories
 * @author Josia Menger
 */
public class GameStateUseCase {

    private final Context context;
    private GameStateRepository repository;

    public GameStateUseCase(Context context) {
        this.context = context;
    }

    public void init(boolean isArcade) {
        this.repository = GameStateRepository.create(context, isArcade);
    }

    public GameState load() {
        if (repository == null) {
            throw new RuntimeException("GameStateUseCase has not been initialized");
        }
        return repository.loadGameState();
        // TODO load statistics
    }

    public void save(GameState state) {
        if (repository == null) {
            throw new RuntimeException("GameStateUseCase has not been initialized");
        }
        repository.saveGameState(state);
        // TODO save achievements and statistics
    }
}
