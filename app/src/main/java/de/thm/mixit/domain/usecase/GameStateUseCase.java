package de.thm.mixit.domain.usecase;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.entities.GameState;
import de.thm.mixit.data.entities.Statistic;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.data.model.Result;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.repository.StatisticRepository;

public class GameStateUseCase {
    private static final String TAG = GameStateUseCase.class.getSimpleName();
    private final CombinationRepository combinationRepository;
    private final ElementRepository elementRepository;
    private final GameStateRepository gameStateRepository;
    private final StatisticRepository statisticRepository;
    private Statistic statistics;
    private GameState gameState;

    public GameStateUseCase(CombinationRepository combinationRepository,
                            ElementRepository elementRepository,
                            GameStateRepository gameStateRepository,
                            StatisticRepository statisticRepository) {
        this.combinationRepository = combinationRepository;
        this.elementRepository = elementRepository;
        this.gameStateRepository = gameStateRepository;
        this.statisticRepository = statisticRepository;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void getAllElements(Consumer<List<Element>> callback) {
        elementRepository.getAll(callback);
    }

    public Statistic getStatistics() {
        return statistics;
    }

    public GameState load(Consumer<Result<GameState>> callback) {
        statistics = statisticRepository.loadStatistic();
        gameState = gameStateRepository.loadGameState();
        ElementChip.setId(gameState.getHighestElementChipID() + 1);

        if (gameState.getGoalElement() == null) {
            elementRepository.generateNewGoalWord(statistics.getLastGoalWords(), res -> {
                if (res.isError()) {
                    Log.e(TAG, "Couldn't fetch new goal word: " + res.getError());
                    callback.accept(Result.failure(res.getError()));
                    return;
                }
                Log.i(TAG, "Fetched new Goal Word\n" + Arrays.toString(res.getData()));
                gameState.setGoalElement(res.getData());
                callback.accept(Result.success(gameState));
            });

        }

        return gameState;
    }

    public void save(GameState gameState, Statistic statistics) {
        // Add playtime of session to sum of playtime
        statistics.addPlaytime(gameState.getTime() - this.gameState.getTime());
        // If the goal word wasn't recorded, add it to the list of the last goal words
        List<String> lastGoalWords = statistics.getLastGoalWords();
        if (lastGoalWords.isEmpty()
                || gameState.getGoalElement() != null
                &&!gameState.getGoalElement()[0]
                .equals(lastGoalWords.get(lastGoalWords.size() - 1))) {
            statistics.addGoalWord(gameState.getGoalElement()[0]);
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
