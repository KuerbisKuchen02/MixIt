package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.thm.mixit.data.entity.Element;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.domain.usecase.ElementUseCase;

/**
 * UI state for the {@link de.thm.mixit.ui.activity.GameActivity}
 *
 * Use the {@link Factory} to get a new GameViewModel instance
 *
 * @author Josia Menger
 */
public class GameViewModel extends ViewModel {
    private final static String TAG = GameViewModel.class.getSimpleName();

    private final ElementRepository elementRepository;
    private final ElementUseCase elementUseCase;
    private final MutableLiveData<List<Element>> elements = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Element>> filteredElements = new MediatorLiveData<>();
    private final MutableLiveData<List<ElementChip>> elementsOnPlayground = new MutableLiveData<>();
    private final MutableLiveData<Throwable> combineError = new MutableLiveData<>();
    private final Handler timeHandler;

    private final long startTime;
    private final MutableLiveData<Long> passedTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> turns = new MutableLiveData<>();
    private final String targetElement;

    /**
     * Use the {@link Factory} to get a new GameViewModel instance
     * @param elementRepository ElementRepository used for dependency injection
     * @param elementUseCase ElementUseCase used for dependency injection
     */
    private GameViewModel(ElementRepository elementRepository, ElementUseCase elementUseCase) {
        this.elementRepository = elementRepository;
        this.elementUseCase = elementUseCase;
        this.filteredElements.addSource(elements, list -> filter());
        this.filteredElements.addSource(searchQuery, query -> filter());
        loadElements();
        this.startTime = System.currentTimeMillis();
        this.timeHandler = new Handler();
        this.timeHandler.post(updateTimerRunnable);
        // TODO: GameStateManager: load saved playground state
        this.elementsOnPlayground.setValue(new ArrayList<>());
        this.combineError.setValue(null);
        this.turns.setValue(0);
        this.passedTime.setValue(0L);
        this.targetElement = "Schokokuchen";
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
    public LiveData<List<ElementChip>> getElementsOnPlayground() {
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
        List<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.add(element);
        elementsOnPlayground.setValue(list);
    }

    public void updateElementPositonOnPlayground(ElementChip chip, float x, float y) {
        List<ElementChip> current = elementsOnPlayground.getValue();
        List<ElementChip> updated = new ArrayList<>();
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
        List<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.remove(element);
        elementsOnPlayground.setValue(list);
    }

    public void clearPlayground() {
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
                combineError.postValue(result.getError());
            } else {
                Log.d(TAG, "Elements successfully combined.");
                combineError.postValue(null);
                new Handler(Looper.getMainLooper()).post(() ->
                        handleCombineElements(chip1, chip2, result.getData()));
            }
        });
    }

    public LiveData<Throwable> getCombineError() {
        return combineError;
    }

    public LiveData<Long> passedTime() {
        return passedTime;
    }

    public LiveData<Integer> getTurns() {
        return turns;
    }

    public String getTargetElement() {
        return targetElement;
    }

    public void increaseTurnCounter() {
        assert turns.getValue() != null;
        turns.setValue(turns.getValue() + 1);
    }

    /**
     * Removes all callbacks
     * <p>
     * Should be called in onDestroy method of LifecycleOwner
     */
    public void onDestroy() {
        timeHandler.removeCallbacks(updateTimerRunnable);
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
    private void loadElements() {
        this.elementRepository.getAll((list) ->
                new Handler(Looper.getMainLooper()).post(() -> this.elements.setValue(list)));
    }

    /**
     * Handle playground changes after successful combination
     * @param chip1 reactant 1
     * @param chip2 reactant 2
     * @param newElement product
     */
    private void handleCombineElements(ElementChip chip1, ElementChip chip2, Element newElement) {
        List<ElementChip> list = elementsOnPlayground.getValue();
        assert list != null;
        list.remove(chip1);
        list.remove(chip2);
        list.add(new ElementChip(newElement, chip1.getX(), chip1.getY()));
        elementsOnPlayground.setValue(list);
        loadElements();
    }

    /**
     * Worker thread to measure current playtime
     */
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            passedTime.setValue(System.currentTimeMillis() - startTime);

            // Handler calls it again every second
            timeHandler.postDelayed(this, 1000);
        }
    };

    /**
     * Creates a new {@link GameViewModel} instance or returns an existing one
     * @author Josia Menger
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final ElementRepository elementRepository;
        private final ElementUseCase elementUseCase;

        public Factory(Context context) {
            this.elementRepository = ElementRepository.create(context);
            this.elementUseCase = new ElementUseCase(context);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == GameViewModel.class) {
                return (T) new GameViewModel(elementRepository, elementUseCase);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }


}
