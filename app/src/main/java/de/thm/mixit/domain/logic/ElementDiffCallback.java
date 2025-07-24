package de.thm.mixit.domain.logic;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import de.thm.mixit.data.model.ElementChip;

/**
 * Utility class used to calculate the difference of two element lists
 *
 * @author Josia Menger
 */
public class ElementDiffCallback extends DiffUtil.Callback {
    private final List<ElementChip> oldList;
    private final List<ElementChip> newList;

    public ElementDiffCallback(List<ElementChip> oldList,
                               List<ElementChip> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
