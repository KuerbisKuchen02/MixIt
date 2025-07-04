package de.thm.mixit.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.ElementEntity;

public class ElementRecyclerViewAdapter extends
        RecyclerView.Adapter<ElementRecyclerViewAdapter.ElementViewHolder> {

    private List<ElementEntity> elements;

    public ElementRecyclerViewAdapter(List<ElementEntity> elements) {
        this.elements = elements;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.text_item_element, parent, false);
        view.setBackgroundResource(R.drawable.background_item);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        ElementEntity element = elements.get(position);
        holder.textView.setText(element.toString());
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void setElements(List<ElementEntity> elements) {
        this.elements = elements;
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item_element);
        }
    }
}
