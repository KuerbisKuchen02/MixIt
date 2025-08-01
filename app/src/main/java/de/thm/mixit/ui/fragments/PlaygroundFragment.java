package de.thm.mixit.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.databinding.FragmentPlaygroundBinding;
import de.thm.mixit.data.model.ElementChip;
import de.thm.mixit.domain.logic.ElementDiffCallback;
import de.thm.mixit.domain.logic.GenericListUpdateCallback;
import de.thm.mixit.domain.logic.GenericListChangeHandler;
import de.thm.mixit.ui.viewmodel.GameViewModel;

/**
 * Fragment class that provides a playground to place, move and combine {@link Element} freely.
 *
 * @author Oliver Schlalos
 */
public class PlaygroundFragment extends Fragment implements GenericListChangeHandler<ElementChip> {

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

        viewModel = new ViewModelProvider(requireActivity(),
                new GameViewModel.Factory(requireActivity())).get(GameViewModel.class);

        playground = binding.layoutPlayground;
        clearElementsButton = binding.buttonClearElements;
        showElementListButton = binding.buttonOpenElementList;

        clearElementsButton.setOnClickListener(view -> viewModel.clearPlayground());

        showElementListButton.setOnClickListener(
                view -> {
                    // TODO implement fragment communication to ElementListFragment or GameActivity
                    if (BuildConfig.DEBUG) Log.d(TAG, "open element list fragment");
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


        return binding.getRoot();
    }

    @Override
    public void onItemInserted(ElementChip item, int position) {
        playground.addView(createElement(item));
    }

    @Override
    public void onItemRemoved(ElementChip item, int position) {
        View view = playground.findViewWithTag(item.getId());
        if (view != null) {
            playground.removeView(view);
        }
    }

    @Override
    public void onItemChanged(ElementChip item, int position) {
        View view = playground.findViewWithTag(item.getId());
        if (view != null) {
            view.setX(item.getX());
            view.setY(item.getY());
        }
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

        GenericListUpdateCallback<ElementChip> callback =
                new GenericListUpdateCallback<>(currentElements, newElements, this);
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
}
