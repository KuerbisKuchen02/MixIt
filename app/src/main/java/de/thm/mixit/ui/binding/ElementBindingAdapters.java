package de.thm.mixit.ui.binding;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thm.mixit.data.entities.Element;
import de.thm.mixit.ui.adapter.ElementRecyclerViewAdapter;

public class ElementBindingAdapters {

    @BindingAdapter("elements")
    public static void setElements(@NonNull RecyclerView recyclerView,
                                   @NonNull List<Element> elements) {
        ElementRecyclerViewAdapter adapter = (ElementRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setElements(elements);
        }
    }

    @BindingAdapter("elements")
    public static void setElements(@NonNull AutoCompleteTextView view,
                                   @NonNull List<Element> elements) {
        ArrayAdapter<Element> adapter = new ArrayAdapter<>(
                view.getContext(), android.R.layout.simple_dropdown_item_1line,
                elements);
        view.setAdapter(adapter);
    }
}
