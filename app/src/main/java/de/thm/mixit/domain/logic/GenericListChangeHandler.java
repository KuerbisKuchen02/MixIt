package de.thm.mixit.domain.logic;

/**
 * Interface used to register callbacks on list updates for {@link GenericListUpdateCallback}
 * @param <T> List element type
 * @author Josia Menger
 */
public interface GenericListChangeHandler<T> {
    void onItemInserted(T item, int position);
    void onItemRemoved(T item, int position);
    void onItemChanged(T item, int position);
}
