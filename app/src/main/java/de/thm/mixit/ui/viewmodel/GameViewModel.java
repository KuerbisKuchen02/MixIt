package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.entities.GameState;
import de.thm.mixit.data.entities.Statistic;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.repository.StatisticRepository;
import de.thm.mixit.domain.logic.ArcadeGoalChecker;
import de.thm.mixit.domain.usecase.ElementUseCase;
import de.thm.mixit.domain.usecase.GameStateUseCase;

/**
 * UI state for the {@link de.thm.mixit.ui.activities.GameActivity}
 *
 * Use the {@link Factory} to get a new GameViewModel instance
 *
 * @author Josia Menger
 */
public class GameViewModel extends ViewModel {
    private final static String TAG = GameViewModel.class.getSimpleName();
    private final ElementUseCase elementUseCase;
    private final GameStateUseCase gameStateUseCase;
    private Statistic statistics;
    private final MutableLiveData<List<Element>> elements = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Element>> filteredElements = new MediatorLiveData<>();
    private final MutableLiveData<ArrayList<ElementChip>> elementsOnPlayground =
            new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private long alreadySavedPassedTime;
    private final MutableLiveData<Long> passedTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> turns = new MutableLiveData<>();
    private final MutableLiveData<String[]> targetElement = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isWon = new MutableLiveData<>();

    /**
     * Use the {@link Factory} to get a new GameViewModel instance
     * @param elementUseCase ElementUseCase used for dependency injection
     * @param gameStateUseCase ElementUseCase used for dependency injection
     */
    @VisibleForTesting
    GameViewModel(ElementUseCase elementUseCase,
                  GameStateUseCase gameStateUseCase) {
        this.elementUseCase = elementUseCase;
        this.gameStateUseCase = gameStateUseCase;
        this.filteredElements.addSource(elements, list -> filter());
        this.filteredElements.addSource(searchQuery, query -> filter());
        this.elementsOnPlayground.setValue(new ArrayList<>());
        this.error.setValue(null);
        this.alreadySavedPassedTime = 0;
        this.turns.setValue(0);
        this.passedTime.setValue(0L);
        this.isWon.setValue(false);
    }

    /**
     * Get all discovered elements
     * @return all elements
     */
    public LiveData<List<Element>> getElements() {
        return elements;
    }

    /**
     * Get all elements filtered by last query set using {@link #onSearchQueryChanged}
     * @return filtered elements
     */
    public LiveData<List<Element>> getFilteredElements() {
        return filteredElements;
    }

    /**
     * Get all element chips currently on the playground
     * @return element chips
     */
    public LiveData<ArrayList<ElementChip>> getElementsOnPlayground() {
        return elementsOnPlayground;
    }

    /**
     * Set a new filter for the element list returned by {@link #filteredElements}
     * @param query filter
     */
    public void onSearchQueryChanged(String query) {
        searchQuery.setValue(query);
    }

    /**
     * Add a new element to a random position to the playground
     * @param element element to add
     */
    public void addElementToPlayground(Element element) {
        addElementToPlayground(new ElementChip(element));
    }

    /**
     * Add an existing element chip back to the playground
     * @param element chip to add
     */
    public void addElementToPlayground(ElementChip element) {
        ArrayList<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.add(element);
        elementsOnPlayground.setValue(list);
    }

    public void updateElementPositonOnPlayground(ElementChip chip, float x, float y) {
        List<ElementChip> current = elementsOnPlayground.getValue();
        ArrayList<ElementChip> updated = new ArrayList<>();
        assert current != null;
        for (ElementChip e : current) {
            if (e.equals(chip)) {
                updated.add(e.withPosition(x, y));
            } else {
                updated.add(e);
            }
        }
        elementsOnPlayground.setValue(updated);
    }

    public void removeElementFromPlayground(ElementChip element) {
        ArrayList<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.remove(element);
        elementsOnPlayground.setValue(list);
    }

    public void clearPlayground() {
        int numCleared = Objects.requireNonNull(elementsOnPlayground.getValue()).size();
        statistics.setNumberOfDiscardedElements(statistics.getNumberOfDiscardedElements() +
                numCleared);
        statistics.setMostDiscardedElements(numCleared);
        elementsOnPlayground.setValue(new ArrayList<>());
    }

    /**
     * Combine to Elements and add product
     * <p>
     * Will remove reactant and add product to the playground if succeeds
     * @param chip1 reactant 1
     * @param chip2 reactant 2
     */
    public void combineElements(ElementChip chip1, ElementChip chip2) {
        elementUseCase.getElement(chip1.getElement(), chip2.getElement(), (result) -> {
            // combineError contains null or the last error while trying to combine two elements.
            if (result.isError()) {
                Log.e(TAG, "An error occurred while combining: " + result.getError());
                // Flag the elements to remove the ongoing animation
                chip1.setAnimated(false);
                chip2.setAnimated(false);
                error.postValue(result.getError());
            } else {
                Log.d(TAG, "Elements successfully combined.");
                error.postValue(null);
                handleCombineElements(chip1, chip2, result.getData());
                statistics.setLongestElement(result.getData().name);
            }
        });
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void setPassedTime(Long time) {
        passedTime.postValue(time);
    }

    public LiveData<Long> getPassedTime() {
        return passedTime;
    }

    public long getAlreadySavedPassedTime() {
        return alreadySavedPassedTime;
    }


    public LiveData<Integer> getTurns() {
        return turns;
    }

    public MutableLiveData<String[]> getTargetElement() {
        return targetElement;
    }

    public MutableLiveData<Boolean> getIsWon() {
        return isWon;
    }

    public void increaseTurnCounter() {
        assert turns.getValue() != null;
        turns.setValue(turns.getValue() + 1);
        statistics.setNumberOfCombinations(statistics.getNumberOfCombinations() + 1);
    }

    public void load() {
        loadElements();

        GameState gameState = gameStateUseCase.load(res -> {
            if (res.isError()){
                this.error.postValue(res.getError());
            }
            this.targetElement.postValue(res.getData().getGoalElement());
        });
        this.elementsOnPlayground.postValue(gameState.getElementChips());
        this.turns.postValue(gameState.getTurns());
        this.alreadySavedPassedTime = gameState.getTime();
        this.targetElement.postValue(gameState.getGoalElement());

        this.statistics = gameStateUseCase.getStatistics();
        Log.d(TAG, statistics.toString());
    }

    public void save() {
        assert turns.getValue() != null;
        assert passedTime.getValue() != null;
        assert elementsOnPlayground.getValue() != null;

        // We need to reset this flag before persisting the elements to ensure
        // that elements that are in an ongoing combination do not get stuck in an invalid state
        // and can be recombined if the game is restarted
        elementsOnPlayground.getValue().forEach(e -> e.setAnimated(false));
        gameStateUseCase.save(new GameState(
                passedTime.getValue(),
                turns.getValue(),
                targetElement.getValue(),
                elementsOnPlayground.getValue()),
                statistics);
    }

    /**
     * Transform {@link #elements} using {@link #searchQuery} to {@link #filteredElements}
     */
    private void filter() {
        List<Element> all = elements.getValue();
        String query = searchQuery.getValue();
        if (all == null || query == null || query.isEmpty()) {
            filteredElements.setValue(all);
        } else {
            List<Element> filtered = all.stream()
                    .filter(e -> e.toString().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            filteredElements.setValue(filtered);
        }
    }

    /**
     * Get all elements from the element repository
     */
    @VisibleForTesting
    void loadElements() {
        this.gameStateUseCase.getAllElements(elements::postValue);
    }

    /**
     * Takes a sequence of target words and returns true if the Game has been won.
     * @param targetWords       The sequence of words to check for the newWord
     * @param newWord           The word which must be inside targetElements in order to win.
     */
    private void checkIsWon(String[] targetWords, String newWord) {
        if (targetWords == null) return;
        if (ArcadeGoalChecker.matchesTargetElement(targetWords, newWord)) {
            Log.d(TAG, newWord + " matches " + Arrays.toString(targetElement.getValue()));
            isWon.postValue(true);

            // Set Statistics
            statistics.setArcadeGamesWon(statistics.getArcadeGamesWon() + 1);
            statistics.setShortestArcadeTimeToBeat(passedTime.getValue() / 1000);
            statistics.setFewestArcadeTurnsToBeat(turns.getValue());
        }
    }

    /**
     * Handle playground changes after successful combination
     * @param chip1 reactant 1
     * @param chip2 reactant 2
     * @param newElement product
     */
    private void handleCombineElements(ElementChip chip1, ElementChip chip2, Element newElement) {
        ArrayList<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.remove(chip1);
        list.remove(chip2);
        list.add(new ElementChip(newElement, chip1.getX(), chip1.getY()));
        elementsOnPlayground.postValue(list);
        loadElements();
        checkIsWon(targetElement.getValue(), newElement.name);
    }

    /**
     * Creates a new {@link GameViewModel} instance or returns an existing one
     * @author Josia Menger
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final ElementUseCase elementUseCase;
        private final GameStateUseCase gameStateUseCase;

        public Factory(Context context, boolean isArcade) {
            CombinationRepository combinationRepository =
                    CombinationRepository.create(context, isArcade);
            ElementRepository elementRepository = ElementRepository.create(context, isArcade);
            GameStateRepository gameStateRepository = GameStateRepository.create(context, isArcade);
            StatisticRepository statisticRepository = StatisticRepository.create(context);

            this.elementUseCase = new ElementUseCase(combinationRepository, elementRepository);
            this.gameStateUseCase = new GameStateUseCase(combinationRepository, elementRepository,
                    gameStateRepository, statisticRepository);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == GameViewModel.class) {
                return (T) new GameViewModel(elementUseCase, gameStateUseCase);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
