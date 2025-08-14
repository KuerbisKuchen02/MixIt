package de.thm.mixit.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.databinding.FragmentPlaygroundBinding;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.domain.logic.ElementDiffCallback;
import de.thm.mixit.ui.activities.ArcadeVictoryActivity;
import de.thm.mixit.ui.activities.GameActivity;
import de.thm.mixit.ui.viewmodel.GameViewModel;

/**
 * Fragment class that provides a playground to place, move and combine {@link Element} freely.
 *
 * @author Oliver Schlalos
 */
public class PlaygroundFragment extends Fragment{

    private final static String TAG = PlaygroundFragment.class.getSimpleName();
    private GameViewModel viewModel;
    private FrameLayout playground;
    private LayoutInflater inflater;
    private FloatingActionButton clearElementsButton;
    private FloatingActionButton showElementListButton;

    private final List<ElementChip> currentElements = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
            @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        FragmentPlaygroundBinding binding =
                FragmentPlaygroundBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        GameActivity gameActivity = ((GameActivity) requireActivity());
        viewModel = new ViewModelProvider(gameActivity,
                new GameViewModel.Factory(gameActivity,
                        gameActivity.isArcade())).get(GameViewModel.class);

        playground = binding.layoutPlayground;
        clearElementsButton = binding.buttonClearElements;
        showElementListButton = binding.buttonOpenElementList;

        clearElementsButton.setOnClickListener(view -> viewModel.clearPlayground());

        showElementListButton.setOnClickListener(
                view -> {
                    if (BuildConfig.DEBUG) Log.d(TAG, "open element list fragment");
                    assert getActivity() != null;
                    ((GameActivity) getActivity()).setElementListVisible(true);
                    showElementListButton.hide();

                    // Apply OnClickListener to playground as long as the Elementlist is visible
                    playground.setOnClickListener(
                            playgroundView -> {
                                if (BuildConfig.DEBUG) Log.d(TAG, "tipped on playground fragment");
                                assert getActivity() != null;
                                ((GameActivity) getActivity()).setElementListVisible(false);
                                showElementListButton.show();
                                playground.setOnClickListener(null);
                            }
                    );
                });


        viewModel.getElementsOnPlayground().observe(getViewLifecycleOwner(), this::updateElements);
        viewModel.getCombineError().observe(getViewLifecycleOwner(), error -> {
            Log.d(TAG, "Registered new state after combining: " + error);
            String text = "Something went wrong! Please check your internet connection.";
            if (error != null) {
                Snackbar.make(playground, text, 6000)
                        .setBackgroundTint(Color.RED)
                        .show();
            }
        });

        viewModel.getIsWon().observe(getViewLifecycleOwner(), isWon -> {
            if (gameActivity.isArcade() && isWon) {
                Log.d(TAG, "The player found the goal word!");
                // TODO: Switch to the Arcade Victory Activity here

                List<ElementChip> list = viewModel.getElementsOnPlayground().getValue();
                Element goalElement = list.get(list.size()-1).getElement();

                Intent intent = new Intent(getActivity(), ArcadeVictoryActivity.class);
                intent.putExtra(ArcadeVictoryActivity.EXTRA_GOAL_WORD, goalElement);
                intent.putExtra(ArcadeVictoryActivity.EXTRA_NUM_TURNS, viewModel.getTurns().getValue());
                intent.putExtra(ArcadeVictoryActivity.EXTRA_PASSED_TIME, viewModel.passedTime().getValue());
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private View createElement(ElementChip chip) {
        TextView view = (TextView) inflater.inflate(R.layout.item_element_chip,
                playground, false);
        view.setText(chip.getElement().toString());
        view.setTag(chip.getId());

        if(chip.getX() == -1 && chip.getY() == -1){
            float[] freeSpace = getFreeSpace();
            chip.setX(freeSpace[0]);
            chip.setY(freeSpace[1]);
        }

        view.setX(chip.getX());
        view.setY(chip.getY());
        view.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        v.bringToFront();
                        v.invalidate();
                        whenItemIsPickedUp();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if(event.getRawX() + dX < playground.getWidth() - v.getWidth() &&
                                event.getRawX() + dX > 0) v.setX(event.getRawX() + dX);
                        if(event.getRawY() + dY < playground.getHeight() - v.getHeight() &&
                                event.getRawY() + dY > 0) v.setY(event.getRawY() + dY);
                        return true;

                    case MotionEvent.ACTION_UP:
                        viewModel.updateElementPositonOnPlayground(chip, v.getX(), v.getY());
                        whenItemIsDropped();
                        if (!overlapsWithDeleteButton(v)){
                            View other = checkOverlap((TextView) v);
                            if(other != null){
                                // while combining disable onTouchListener
                                v.setOnTouchListener((dummy, e) -> false);
                                other.setOnTouchListener((dummy, e) -> false);
                                try {
                                    viewModel.combineElements(
                                            Objects.requireNonNull(getChipById((int) v.getTag())),
                                            Objects.requireNonNull(
                                                    getChipById((int) other.getTag())));
                                    viewModel.increaseTurnCounter();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error while combining elements: "
                                            + e.getMessage());
                                }
                            }
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
        return view;
    }

    private ElementChip getChipById(int id) {
        for (ElementChip elementChip : currentElements) {
            if (elementChip.getId() == id) return elementChip;
        }
        return null;
    }

    private void updateElements(List<ElementChip> newElements) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "New elements size: " + newElements.size()
                    + " Old elements size: " + currentElements.size());
            Log.v(TAG, "UpdateElements\n new=" + newElements + "\nold=" + currentElements);
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ElementDiffCallback(currentElements, newElements)
        );

        // Not perfect, for more info see note on ElementListUpdateCallback class
        ElementListUpdateCallback callback =
                new ElementListUpdateCallback(currentElements, newElements);
        diffResult.dispatchUpdatesTo(callback);
        callback.finishInserts();
    }
    /**
     * Find free space on playground
     * @return x and y coordinates to free space
     */
    private float[] getFreeSpace(){
        TextView exampleElement = getAnyElement();

        float[] coordinates = new float[2];
        coordinates[0] = playground.getWidth() / 2f;
        coordinates[1] = playground.getHeight() / 2f;

        if(exampleElement != null){
            int innerRectLeftX =  (int) (playground.getWidth() * 0.1);
            int innerRectRightX = (int) (playground.getWidth() * 0.6);

            Rect test = new Rect();
            exampleElement.getHitRect(test);

            Random rand = new Random();

            // If playground has less than 50 elements, try to find a free space
            // otherwise place randomly disregarding overlapping
            final int ATTEMPTS = playground.getChildCount() < 50 ? playground.getChildCount() : 1;

            for(int attempt = 0; attempt < ATTEMPTS; attempt++){
                coordinates[0] = innerRectLeftX + (rand.nextFloat() * innerRectRightX);
                coordinates[1] = (rand.nextFloat() * (float) (playground.getHeight() * 0.75));

                test.offsetTo((int) coordinates[0], (int) coordinates[1]);

                if(!isOverlapping(test)) return coordinates;
            }
        }
        return coordinates;
    }

    private boolean isOverlapping(Rect test) {
        for(int i = 0; i < playground.getChildCount(); i++){
            View other = playground.getChildAt(i);
            if (!(other instanceof TextView)) continue;
            Rect otherRect = new Rect();
            other.getHitRect(otherRect);

            if(Rect.intersects(test, otherRect)) return true;
        }
        return false;
    }

    private TextView getAnyElement(){
        for(int i = 0; i < playground.getChildCount(); i++){
            if(playground.getChildAt(i) instanceof TextView){
                return (TextView) playground.getChildAt(i);
            }
        }
        return null;
    }

    /**
     * style buttons differently when an item is picked up
     */
    private void whenItemIsPickedUp(){
        clearElementsButton.setImageResource(R.drawable.ic_remove_24px);
        int color = ContextCompat.getColor(requireContext(), R.color.red);
        clearElementsButton.setBackgroundTintList(ColorStateList.valueOf(color));
        clearElementsButton.setAlpha(0.8f);
        showElementListButton.setClickable(false);
    }

    /**
     * style buttons differently when an item is dropped
     */
    private void whenItemIsDropped(){
        clearElementsButton.setImageResource(R.drawable.ic_broom_24px);
        clearElementsButton.requestLayout();
        clearElementsButton.setAlpha(1f);
        clearElementsButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        showElementListButton.setClickable(true);
    }

    /**
     * Check if View overlaps with delete button and therefore should be deleted
     * @param v View to check
     * @return boolean whether view has been deleted or not
     */
    private boolean overlapsWithDeleteButton(View v){
        Rect draggedRect = new Rect();
        v.getHitRect(draggedRect);

        Rect deleteButton = new Rect();
        clearElementsButton.getHitRect(deleteButton);

        if(Rect.intersects(draggedRect, deleteButton)){
            viewModel.removeElementFromPlayground(getChipById((int) v.getTag()));
            return true;
        }
        return false;
    }

    /**
     * Checks if two elements overlap and returns the other overlapping View
     * @param draggedView View to compare with
     * @return Other View if overlap was found
     */
    private View checkOverlap(TextView draggedView) {
        Rect draggedRect = new Rect();
        draggedView.getHitRect(draggedRect);

        ViewGroup parent = (ViewGroup) draggedView.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View other = parent.getChildAt(i);

            if (other == draggedView) continue;

            Rect otherRect = new Rect();
            other.getHitRect(otherRect);

            if (Rect.intersects(draggedRect, otherRect) && other instanceof TextView) {
                // Overlap detected
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "Dragged element (" + draggedView.getText() +
                            ") overlaps with: " + ((TextView) other).getText());
                }
                return other;
            }
        }
        return null;
    }

    /**
     * Convert the first list to the second list by applying the differences calculated by
     * {@link ElementDiffCallback}.
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
     * @author Josia Menger
     */
    private class ElementListUpdateCallback implements ListUpdateCallback {

        private final List<ElementChip> oldElements;
        private final List<ElementChip> newElements;
        private int inserts = 0;

        public ElementListUpdateCallback(List<ElementChip> oldElements,
                                         List<ElementChip> newElements) {
            this.oldElements = oldElements;
            this.newElements = newElements;
        }

        public void finishInserts() {
            if (inserts <= 0) return;

            if (BuildConfig.DEBUG) Log.d(TAG, "finishInserts inserts=" + inserts);

            ListIterator<ElementChip> oldListIterator = oldElements.listIterator();
            ListIterator<ElementChip> newListIterator = newElements.listIterator();

            while (inserts > 0 && oldListIterator.hasNext() && newListIterator.hasNext()) {
                ElementChip oldElement = oldListIterator.next();
                ElementChip newElement = newListIterator.next();

                if (oldElement == null) {
                    //Replaces the last element returned by next()
                    oldListIterator.set(newElement);
                    playground.addView(createElement(newElement));
                    if (BuildConfig.DEBUG) Log.d(TAG, "new Element: " + newElement);
                    inserts--;
                }
            }

            if (inserts > 0 || oldElements.contains(null)) {
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
                oldElements.add(position + i, null);
                inserts++;
            }
        }

        @Override
        public void onRemoved(int position, int count) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onRemoved position=" + position
                    + " count=" + count);
            for (int i = 0; i < count; i++) {
                ElementChip e = oldElements.remove(position);
                View viewToRemove = playground.findViewWithTag(e.getId());
                if (viewToRemove != null) playground.removeView(viewToRemove);
                if (BuildConfig.DEBUG) Log.d(TAG, "delete Element: " + e);
            }
        }

        // Not important
        @Override
        public void onMoved(int fromPosition, int toPosition) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onMoved fromPosition=" + fromPosition
                    + " toPosition=" + toPosition);
            oldElements.add(toPosition, oldElements.remove(fromPosition));
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onChanged position=" + position
                    + " count=" + count);
            for (int i = 0; i < count; i++ ) {
                ElementChip e = newElements.get(position + i);
                oldElements.set(position + i, e);
                View v = playground.findViewById(position + i);
                if (v != null) {
                    v.setX(e.getX());
                    v.setY(e.getY());
                }
            }
        }
    }
}
