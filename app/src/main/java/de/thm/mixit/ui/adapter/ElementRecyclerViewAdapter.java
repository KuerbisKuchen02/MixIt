package de.thm.mixit.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.ElementEntity;

/**
 * Filterable recycler view adapter for element entities
 * @author Josia Menger
 */
public class ElementRecyclerViewAdapter extends
        RecyclerView.Adapter<ElementRecyclerViewAdapter.ElementViewHolder> {

    private List<ElementEntity> elements;
    private final List<ElementEntity> filteredElements;
    private final OnElementClickListener listener;

    public interface OnElementClickListener {
        void onElementClick(ElementEntity element, int positon);
    }

    /**
     * Create a new ElementRecyclerViewAdapter
     *
     * @param elements Element list to show
     * @param listener Callback method to call when an element card is clicked
     */
    public ElementRecyclerViewAdapter(List<ElementEntity> elements,
                                      OnElementClickListener listener) {
        this.elements = new ArrayList<>(elements);
        this.filteredElements = new ArrayList<>(elements);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.text_item_element, parent, false);
        view.setBackgroundResource(R.drawable.background_item);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(16, 16, 16, 16);
        }
        view.setLayoutParams(params);
        view.requestLayout();
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        ElementEntity element = filteredElements.get(position);
        holder.bind(element, position, listener);
    }

    @Override
    public int getItemCount() {
        return filteredElements.size();
    }


    /**
     * Set the elements to the new list and reset the filter
     * @param elements new elements
     */
    public void setElements(List<ElementEntity> elements) {
        this.elements = elements;
        this.filteredElements.clear();
        this.filteredElements.addAll(elements);
    }

    /**
     * Filter elements by a string (case insensitive)
     * <p>
     * if the query is empty or only contains whitespaces the complete list is shown
     * <p>
     * FIXME: Use DiffUtil or notifyItemChanged/Inserted/Removed to improve performance
     * @param query String to filter by
     */
    public void filter(String query) {
        filteredElements.clear();
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) {
            filteredElements.addAll(elements);
            notifyDataSetChanged();
            return;
        }

        for (ElementEntity element : elements) {
            if (element.toString().toLowerCase().contains(q)) {
                filteredElements.add(element);
            }
        }
        notifyDataSetChanged();
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item_element);
        }

        void bind(ElementEntity element, int positon, OnElementClickListener listener) {
            textView.setText(element.toString());
            itemView.setOnClickListener(v -> listener.onElementClick(element, positon));
        }
    }
}
