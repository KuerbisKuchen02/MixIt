package de.thm.mixit.domain.logic;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.List;
import java.util.ListIterator;

import de.thm.mixit.BuildConfig;

/**
 * Convert the first list to the second list by applying the differences calculated by
 * {@link androidx.recyclerview.widget.DiffUtil}.
 * <br>
 * Use a {@link GenericListChangeHandler} to register callback methods for list updates.
 *
 * <p>
 * <b>Important</b>:<br>
 * After invoking with `dispatchUpdatesTo()` use the method {@link #finishInserts()}
 * to complete the transformation. See below for more details.
 * <p>
 * FIXME: Better alternative for synchronizing playground with viewmodel data
 * <p>
 * This is definitely not the cleanest solution more like a dirty workaround for the time being
 * <p>
 * Problem:<br>
 * It is not possible within onInserted for the inserted oldList item to
 * identify its related position in the newList.
 * Normally inserting new items causes the onInsert method to throw an IndexOutOfBoundsException
 * Also see: <a href="https://issuetracker.google.com/issues/115701827">Google Issue Tracker</a>
 * <p>
 * Solution:<br>
 * Insert a null-dummy list item in the oldList.
 * After diffResult.dispatchUpdatesTo is done,
 * replace the null-dummies with the newList items at same positions.
 * Also see: <a href="https://stackoverflow.com/questions/56670162/how-to-fix-incorrect-position-i-get-when-dispatching-an-update-to-listupdatecall">StackOverflow</a>
 * <p>
 * Since this Issue is marked as won't fix we cannot expect an official solution for this
 * anytime soon. As long as we don't find a better alternative we stick with the working
 * but bit ugly workaround
 * @param <T> List element type
 * @author Josia Menger
 */
public class GenericListUpdateCallback<T> implements ListUpdateCallback {

    private static final String TAG = GenericListUpdateCallback.class.getSimpleName();
    private final List<T> oldItems;
    private final List<T> newItems;

    private final GenericListChangeHandler<T> handler;
    private int inserts = 0;

    public GenericListUpdateCallback(List<T> oldItems, List<T> newItems,
                                     GenericListChangeHandler<T> handler) {
        this.oldItems = oldItems;
        this.newItems = newItems;
        this.handler = handler;
    }

    public void finishInserts() {
        if (inserts <= 0) return;

        if (BuildConfig.DEBUG) Log.d(TAG, "finishInserts inserts=" + inserts);

        ListIterator<T> oldIter = oldItems.listIterator();
        ListIterator<T> newIter = newItems.listIterator();

        while (inserts > 0 && oldIter.hasNext() && newIter.hasNext()) {
            T oldItem = oldIter.next();
            T newItem = newIter.next();

            if (oldItem == null) {
                //Replaces the last element returned by next()
                oldIter.set(newItem);
                handler.onItemInserted(newItem, oldIter.previousIndex());
                if (BuildConfig.DEBUG) Log.d(TAG, "new Element: " + newItem);
                inserts--;
            }
        }

        if (inserts > 0 || oldItems.contains(null)) {
            //There must be something wrong
            Log.e(TAG, "finishInserts inserts=" + inserts + " remaining");
        }
    }

    @Override
    public void onInserted(int position, int count) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onInserted position=" + position
                + " count=" + count);
        for (int i = 0; i < count; i++) {
            // We don't know the related position of the newList, so we add null
            oldItems.add(position + i, null);
            inserts++;
        }
    }

    @Override
    public void onRemoved(int position, int count) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onRemoved position=" + position
                + " count=" + count);
        for (int i = 0; i < count; i++) {
            T item = oldItems.remove(position);
            handler.onItemRemoved(item, position);
            if (BuildConfig.DEBUG) Log.d(TAG, "delete Item: " + item);
        }
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onMoved fromPosition=" + fromPosition
                + " toPosition=" + toPosition);
        oldItems.add(toPosition, oldItems.remove(fromPosition));
    }

    @Override
    public void onChanged(int position, int count, @Nullable Object payload) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onChanged position=" + position
                + " count=" + count);
        for (int i = 0; i < count; i++ ) {
            T item = newItems.get(position + i);
            oldItems.set(position + i, item);
            handler.onItemChanged(item, position + i);
        }
    }
}