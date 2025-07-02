package de.thm.mixit.ui.fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;

public class PlaygroundFragment extends Fragment {
    private final static String TAG = PlaygroundFragment.class.getName();

    private ArrayList<TextView> elements;
    private FrameLayout playground;
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        elements = new ArrayList<>();
        this.inflater = inflater;
        this.playground = (FrameLayout) inflater.inflate(R.layout.view_playground, null,
                false);
        return playground;
    }

    @SuppressLint("ClickableViewAccessibility")
    public TextView addElementToPlayground(String text, float x, float y) {
        TextView newElement = (TextView) inflater.inflate(R.layout.text_item_element, playground, false);
        newElement.setText(text);

        if(x == -1 && y == -1){
            float[] freeSpace = getFreeSpace();
            x = freeSpace[0];
            y = freeSpace[1];
        }
        newElement.setX(x);
        newElement.setY(y);

        elements.add(newElement);
        playground.addView(newElement);

        if(BuildConfig.DEBUG){
            Log.d(TAG, "new element " + text + " has been created");
        }

        newElement.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // TODO Show singular delete field
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        v.bringToFront();
                        v.invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        View other = checkOverlap(v);
                        if(other != null){
                            // TODO combine elements
                            playground.removeView(v);
                            playground.removeView(other);
                            elements.remove(v);
                            elements.remove(other);

                            addElementToPlayground("<combined element>", v.getX(), v.getY());
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        return newElement;
    }

    private float[] getFreeSpace(){
        return new float[]{0f,0f};
    }


    private View checkOverlap(View draggedView) {
        Rect draggedRect = new Rect();
        draggedView.getHitRect(draggedRect);

        ViewGroup parent = (ViewGroup) draggedView.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View other = parent.getChildAt(i);

            if (other == draggedView) continue;

            Rect otherRect = new Rect();
            other.getHitRect(otherRect);

            if (Rect.intersects(draggedRect, otherRect)) {
                // Overlap detected
                if(BuildConfig.DEBUG) {
                    Log.d("Overlap", "Dragged view overlaps with: " + other);
                }
                return other;
            }
        }
        return null;
    }
}
