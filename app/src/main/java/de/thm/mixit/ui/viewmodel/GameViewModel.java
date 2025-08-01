package de.thm.mixit.ui.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.domain.usecase.ElementUseCase;

/**
 * UI state for the {@link de.thm.mixit.ui.activities.GameActivity}
 *
 * Use the {@link Factory} to get a new GameViewModel instance
 *
 * @author Josia Menger
 */
public class GameViewModel extends ViewModel {

    private final ElementRepository elementRepository;
    private final ElementUseCase elementUseCase;
    private final MutableLiveData<List<Element>> elements = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Element>> filteredElements = new MediatorLiveData<>();
    private final MutableLiveData<List<ElementChip>> elementsOnPlayground = new MutableLiveData<>();
    private final MutableLiveData<Long> passedTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> turns = new MutableLiveData<>();
    private final String targetElement;

    /**
     * Use the {@link Factory} to get a new GameViewModel instance
     * @param elementRepository ElementRepository used for dependency injection
     * @param elementUseCase ElementUseCase used for dependency injection
     */
    @VisibleForTesting
    GameViewModel(ElementRepository elementRepository, ElementUseCase elementUseCase) {
        this.elementRepository = elementRepository;
        this.elementUseCase = elementUseCase;
        this.filteredElements.addSource(elements, list -> filter());
        this.filteredElements.addSource(searchQuery, query -> filter());
        loadElements();
        // TODO: GameStateManager: load saved playground state
        this.elementsOnPlayground.setValue(new ArrayList<>());
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
        elementUseCase.getElement(chip1.getElement(), chip2.getElement(),
                (element) -> handleCombineElements(chip1, chip2, element));
    }

    public void setPassedTime(Long time) {
        passedTime.setValue(time);
    }

    public LiveData<Long> getPassedTime() {
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
        // TODO save current game state
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
        this.elementRepository.getAll(elements::postValue);
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
        elementsOnPlayground.postValue(list);
        loadElements();
    }

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
