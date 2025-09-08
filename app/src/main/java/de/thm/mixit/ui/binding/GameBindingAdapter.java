package de.thm.mixit.ui.binding;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.thm.mixit.data.entity.Element;
import de.thm.mixit.ui.adapter.ElementRecyclerViewAdapter;

/**
 * Data binding adapters for various elements inside the GameActivity or related fragments
 *
 * @author Josia Menger
 */
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
        int seconds = (int) (timestamp % 60);
        int minutes = (int) (timestamp / 60 % 60);
        int hours = (int) (timestamp / 3600);

        String time = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours, minutes, seconds);
        view.setText(time);
    }
}
