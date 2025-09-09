package de.thm.mixit.domain.usecase;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.entity.GameState;
import de.thm.mixit.data.entity.Statistic;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.data.model.Result;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.repository.StatisticRepository;

/**
 * Use case for handling the game state for the game view model.
 *
 * @author Josia Menger
 */
public class GameStateUseCase {
    private static final String TAG = GameStateUseCase.class.getSimpleName();
    private final CombinationRepository combinationRepository;
    private final ElementRepository elementRepository;
    private final GameStateRepository gameStateRepository;
    private final StatisticRepository statisticRepository;
    private Statistic statistics;
    private GameState gameState;

    /**
     * Constructor for CombinationUseCase.
     * Initializes the repositories needed for game state operations
     * @param combinationRepository The combination repository
     *                              that is used for managing combinations.
     * @param elementRepository The element repository that is used for managing elements.
     * @param gameStateRepository The game state repository that is used for managing combinations.
     * @param statisticRepository The statistic repository that is used for managing combinations.
     */
    public GameStateUseCase(CombinationRepository combinationRepository,
                            ElementRepository elementRepository,
                            GameStateRepository gameStateRepository,
                            StatisticRepository statisticRepository) {
        this.combinationRepository = combinationRepository;
        this.elementRepository = elementRepository;
        this.gameStateRepository = gameStateRepository;
        this.statisticRepository = statisticRepository;
    }

    /**
     * Returns the loaded or saved game state
     * @return {@link GameState}
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns all elements from the repository
     * @param callback the current list of elements
     */
    public void getAllElements(Consumer<List<Element>> callback) {
        elementRepository.getAll(callback);
    }

    /**
     * Returns the loaded or saved statistics
     * @return {@link Statistic}
     */
    public Statistic getStatistics() {
        return statistics;
    }

    /**
     * Loads the game state and statistics from the repository.
     * If there is no target word, a new one will be fetched from the repository.
     * @param callback the loaded game state or an error
     */
    public void load(Consumer<Result<GameState>> callback) {
        statistics = statisticRepository.loadStatistic();
        gameState = gameStateRepository.loadGameState();
        ElementChip.setId(gameState.getHighestElementChipID() + 1);

        if (gameState.getTargetElement() == null) {
            elementRepository.generateNewTargetWord(statistics.getLastTargetWords(),
                    res -> {
                if (res.isError()) {
                    Log.e(TAG, "Couldn't fetch new target word: " + res.getError());
                    callback.accept(Result.failure(res.getError()));
                    return;
                }
                Log.i(TAG, "Fetched new Target Word\n" + Arrays.toString(res.getData()));
                gameState.setTargetElement(res.getData());
                callback.accept(Result.success(gameState));
            });
        }
    }

    /**
     * Saves the game state and statistics to the repository.
     * Updates the statistics with the playtime of the session,
     * the last target words, the most combinations for one element,
     * the number of unlocked elements and whether the chocolate cake was found.
     * @param gameState the current game state
     * @param statistics the current statistics
     */
    public void save(GameState gameState, Statistic statistics) {
        // Add playtime of session to sum of playtime
        statistics.addPlaytime(gameState.getTime() - this.gameState.getTime());
        // If the target word wasn't recorded, add it to the list of the last target words
        List<String> lastTargetWords = statistics.getLastTargetWords();
        if (gameState.getTargetElement() != null
                && (lastTargetWords.isEmpty() || !gameState.getTargetElement()[0]
                .equals(lastTargetWords.get(lastTargetWords.size() - 1)))) {
            statistics.addTargetWord(gameState.getTargetElement()[0]);
        }
        this.gameState = gameState;
        this.statistics = statistics;
        gameStateRepository.saveGameState(gameState);
        statisticRepository.saveStatistic(statistics);

        // Check via db query for a new Record for the most combinations for one element
        combinationRepository.getAmountOfMostOccurringOutputId( res -> {
            this.statistics.setMostCombinationsForOneElement(res);
            statisticRepository.saveStatistic(this.statistics);
        });

        // Get via db query the amount of unlocked elements
        elementRepository.getAll(res -> {
            this.statistics.setNumberOfUnlockedElements(res.size());
            this.statistics.setFoundChocolateCake(res.stream()
                    .anyMatch(e -> e.name.equals("Schokokuchen")));
            statisticRepository.saveStatistic(statistics);
        });
    }
}
