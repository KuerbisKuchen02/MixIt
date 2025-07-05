package de.thm.mixit.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.ui.adapter.ElementRecyclerViewAdapter;

/**
 *
 * Fragment class that provides a searchable list of {@link ElementEntity}.
 *
 * @author Josia Menger
 */
public class ElementListFragment extends Fragment {

    public final static String ARGUMENT_ELEMENT_TO_LIST =
            "ARGUMENT_ELEMENT_TO_LIST";

    public final static String BUNDLE_NEW_ELEMENT =
            "BUNDLE_NEW_ELEMENT";

    private static final String TAG = ElementListFragment.class.getSimpleName();

    private ElementRecyclerViewAdapter recyclerViewAdapter;
    private ArrayAdapter<ElementEntity> arrayAdapter;

    private ElementRepository elementRepository;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        List<ElementEntity> elements = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Setup element list
        RecyclerView recyclerView = view.findViewById(R.id.recycler_game_item_list);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new ElementRecyclerViewAdapter(elements, this::onClickElement);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Setup search bar
        AutoCompleteTextView textView = view.findViewById(R.id.auto_text_game_item_list_search);
        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, elements);
        textView.setAdapter(arrayAdapter);

        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Apply filter for String: " + s);
                recyclerViewAdapter.filter(s.toString());
                if (s.toString().isEmpty()) {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search,
                            0, 0, 0);
                } else {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search,
                            0, R.drawable.ic_outline_cancel, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        textView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getX() >= textView.getWidth() - textView.getTotalPaddingEnd()) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Pressed cancel button inside search");
                    textView.setText("");
                }
            }
            return false;
        });

        // TODO: Move to ViewModel
        elementRepository = ElementRepository.create(requireContext());

        elementRepository.getAll(this::setElements);

        getParentFragmentManager().setFragmentResultListener(ARGUMENT_ELEMENT_TO_LIST,
                getViewLifecycleOwner(),
                ((requestKey, result) ->
                        elementRepository.getAll(this::setElements)
                ));

        return view;
    }

    // FIXME: Use DiffUtil or notifyItemChanged/Inserted/Removed to improve performance
    public void setElements(List<ElementEntity> elements) {
        recyclerViewAdapter.setElements(elements);
        arrayAdapter.addAll(elements);
        new Handler(Looper.getMainLooper()).post(() -> {
            recyclerViewAdapter.notifyDataSetChanged();
            arrayAdapter.notifyDataSetChanged();
        });
        if (BuildConfig.DEBUG) Log.d(TAG, "Loaded " + elements.size() + " Elements");
    }

    /**
     * Callback function for the onClick event of the element cards inside the list
     * <p>
     * The position parameter always depends on the current filter
     * and does not correspond to the index in the complete list.
     *
     * @param element The element that was clicked
     * @param position The positon of the element in the current view
     */
    public void onClickElement(ElementEntity element, int position) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Clicked on element: " + position);
        // FIXME: bundle should properly include the element object instead of only the string
        //        requires ElementEntity to implement serializable or parcelable
        Bundle result = new Bundle();
        result.putString(PlaygroundFragment.BUNDLE_ELEMENT, element.toString());
        getParentFragmentManager()
                .setFragmentResult(PlaygroundFragment.ARGUMENT_ADD_ELEMENT_TO_PLAYGROUND, result);
    }

}
