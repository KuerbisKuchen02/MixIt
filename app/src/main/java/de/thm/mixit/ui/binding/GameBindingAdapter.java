package de.thm.mixit.ui.binding;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.ui.adapter.ElementRecyclerViewAdapter;

public class GameBindingAdapter {

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

    @BindingAdapter("intToText")
    public static void setTextFromInteger(@NonNull TextView view,
                                          Integer integer ) {
        if (integer == null) view.setText("");
        else view.setText(String.valueOf(integer));
    }

    @BindingAdapter("timestampText")
    public static void setTimestampAsHumanReadableString(@NonNull TextView view,
                                                         @NonNull Long timestamp) {
        int seconds = (int) (timestamp / 1000) % 60;
        int minutes = (int) (timestamp / (1000 * 60)) % 60;
        int hours = (int) (timestamp / (1000 * 60 * 60));

        String time = String.format(
                Locale.getDefault(),
                view.getContext().getString(R.string.arcade_fragment_time),
                hours, minutes, seconds);
        view.setText(time);
    }
}
