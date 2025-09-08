package de.thm.mixit.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entity.Element;

/**
 * Filterable recycler view adapter for element entities
 * FIXME: Use DiffUtil or notifyItemChanged/Inserted/Removed to improve performance
 * @author Josia Menger
 */
public class ElementRecyclerViewAdapter extends
        RecyclerView.Adapter<ElementRecyclerViewAdapter.ElementViewHolder> {

    private List<Element> elements;
    private final List<Element> filteredElements;
    private final OnElementClickListener listener;

    public interface OnElementClickListener {
        void onElementClick(Element element, int positon);
    }

    /**
     * Create a new ElementRecyclerViewAdapter
     *
     * @param listener Callback method to call when an element card is clicked
     */
    public ElementRecyclerViewAdapter(OnElementClickListener listener) {
        this.filteredElements = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_element_chip, parent, false);
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
        Element element = filteredElements.get(position);
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
    @SuppressLint("NotifyDataSetChanged")
    public void setElements(List<Element> elements) {
        this.elements = elements;
        this.filteredElements.clear();
        // on initial call elements might be null
        if(elements != null) this.filteredElements.addAll(elements);
        notifyDataSetChanged();
    }

    /**
     * Filter elements by a string (case insensitive)
     * <p>
     * if the query is empty or only contains whitespaces the complete list is shown
     * <p>
     * @param query String to filter by
     */
    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        filteredElements.clear();
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) {
            filteredElements.addAll(elements);
            notifyDataSetChanged();
            return;
        }

        for (Element element : elements) {
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

        void bind(Element element, int positon, OnElementClickListener listener) {
            textView.setText(element.toString());
            itemView.setOnClickListener(v -> listener.onElementClick(element, positon));
        }
    }
}
