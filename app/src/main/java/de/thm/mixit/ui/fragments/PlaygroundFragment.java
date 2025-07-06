package de.thm.mixit.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.domain.usecase.ElementUseCase;

/**
 * Fragment class that provides a playground to place, move and combine {@link Element} freely.
 *
 * @author Oliver Schlalos
 */
public class PlaygroundFragment extends Fragment{
    public final static String BUNDLE_ELEMENT = "BUNDLE_ELEMENT";
    public final static String ARGUMENT_ADD_ELEMENT_TO_PLAYGROUND =
            "ARGUMENT_ADD_ELEMENT_TO_PLAYGROUND";
    private final static String TAG = PlaygroundFragment.class.getSimpleName();
    private FrameLayout playground;
    private LayoutInflater inflater;
    private FloatingActionButton clearElementsButton;
    private FloatingActionButton showElementListButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
            @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        this.playground = (FrameLayout) inflater.inflate(R.layout.fragment_playground, container,
                false);

        clearElementsButton = playground.findViewById(R.id.button_clear_elements);
        clearElementsButton.setOnClickListener(
                view -> this.removeAllElements()
        );

        showElementListButton = playground.findViewById(R.id.button_open_element_list);
        showElementListButton.setOnClickListener(
                view -> {
                    // TODO implement fragment communication to ElementListFragment or GameActivity
                    Log.d(TAG, "open element list fragment");
                });

        getParentFragmentManager().setFragmentResultListener(ARGUMENT_ADD_ELEMENT_TO_PLAYGROUND,
                getViewLifecycleOwner(),
                ((requestKey, result) ->
                        addElementToPlayground(result.getString(BUNDLE_ELEMENT))
                ));

        return playground;
    }

    /**
     * Adds new item as TextView with param text to playground at a free place
     * @param text Element text with icon an description
     * @return The new element as TextView item on the playground
     */
    public TextView addElementToPlayground(String text) {
        return addElementToPlayground(text, -1, -1);
    }

    /**
     * Adds new item as TextView with param text to playground at position x, y.
     * If x and y both are -1 a free place on the playground will be found
     * @param text Element text with icon an description
     * @param x x coordinate
     * @param y y coordinate
     * @return The new element as TextView item on the playground
     */
    @SuppressLint("ClickableViewAccessibility")
    public TextView addElementToPlayground(String text, float x, float y) {
        TextView newElement = (TextView) inflater.inflate(R.layout.text_item_element, 
                playground, false);
        newElement.setText(text);

        if(x == -1 && y == -1){
            float[] freeSpace = getFreeSpace();
            x = freeSpace[0];
            y = freeSpace[1];
        }

        newElement.setX(x);
        newElement.setY(y);

        new Handler(Looper.getMainLooper()).post(() -> {
            playground.addView(newElement);
        });

        if(BuildConfig.DEBUG){
            Log.d(TAG, "new element " + text + " has been added to playground");
        }

        newElement.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

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
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        return true;

                    case MotionEvent.ACTION_UP:
                        whenItemIsDropped();
                        if (!overlapsWithDeleteButton(v)){
                            View other = checkOverlap((TextView) v);
                            if(other != null){
                                ElementUseCase elementUseCase = new ElementUseCase(
                                        requireContext());

                                try {
                                    elementUseCase.getElement(
                                            ((TextView) v).getText().toString(),
                                            ((TextView) other).getText().toString(),
                                            combinationCallback(v, other));
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

            private Consumer<Element> combinationCallback(View v, View other){
                return newElement-> {
                    try {
                        if (newElement != null) {
                            addElementToPlayground(newElement.toString(),
                                    v.getX(), v.getY());
                            new Handler(Looper.getMainLooper()).post(() -> {
                                removeElement(v);
                                removeElement(other);
                            });

                            Bundle result = new Bundle();
                            result.putString(ElementListFragment.BUNDLE_NEW_ELEMENT,
                                    newElement.toString());
                            getParentFragmentManager().setFragmentResult(
                                    ElementListFragment.ARGUMENT_ELEMENT_TO_LIST, result);
                        } else {
                            Log.e(TAG, "Failed to create new element from " +
                                    "combination of " + ((TextView) v).getText() +
                                    " and " + ((TextView) other).getText());
                        }
                    } catch (Exception e){
                        Log.e(TAG, "while requesting new element " +
                                ", exception occured");
                    }
                };
            }

        });
        return newElement;
    }

    /**
     * Find free space on playground
     * @return x and y coordinates to free space
     */
    private float[] getFreeSpace(){
        return new float[]{0f,0f};
    }


    /**
     * style buttons differently when an item is picked up
     */
    @SuppressLint("ResourceAsColor")
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
            removeElement(v);
            return true;
        }
        return false;
    }


    private void removeElement(View v){
        playground.removeView(v);
        if(BuildConfig.DEBUG){
            if(v instanceof TextView){
                Log.d(TAG, "element " + ((TextView) v).getText() + " has been removed");
            }
        }
    }


    /**
     * removes all current elements on the playground
     */
    public void removeAllElements(){
        int i = 0;
        while(i<playground.getChildCount()){
            if(playground.getChildAt(i) instanceof TextView){
                removeElement(playground.getChildAt(i));
                i = 0;
            }
            else{
                i++;
            }
        }
        if(BuildConfig.DEBUG){
            Log.d(TAG, "All elements have been removed from playground");
        }
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
                    Log.d("Overlap", "Dragged element (" + draggedView.getText() +
                            ") overlaps with: " + ((TextView) other).getText());
                }
                return other;
            }
        }
        return null;
    }
}
