package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.repository.ElementRepository;

public class GameViewModel extends ViewModel {

    private final ElementRepository elementRepository;
    private final MutableLiveData<List<Element>> elements = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Element>> filteredElements = new MediatorLiveData<>();

    private GameViewModel(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
        filteredElements.addSource(elements, list -> filter());
        filteredElements.addSource(searchQuery, query -> filter());
        this.elementRepository.getAll((e) -> runInMainThread(() -> elements.setValue(e)));
    }

    public LiveData<List<Element>> getElements() {
        return elements;
    }

    public LiveData<List<Element>> getFilteredElements() {
        return filteredElements;
    }

    public void onSearchQueryChanged(String query) {
        searchQuery.setValue(query);
    }

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

    private void runInMainThread(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final ElementRepository elementRepository;

        public Factory(Context context) {
            this.elementRepository = ElementRepository.create(context);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == GameViewModel.class) {
                return (T) new GameViewModel(elementRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }


}
