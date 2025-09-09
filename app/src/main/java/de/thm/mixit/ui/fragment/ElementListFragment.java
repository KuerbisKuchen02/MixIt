package de.thm.mixit.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.data.entity.Element;
import de.thm.mixit.databinding.FragmentElementListBinding;
import de.thm.mixit.ui.activity.GameActivity;
import de.thm.mixit.ui.adapter.ElementRecyclerViewAdapter;
import de.thm.mixit.ui.viewmodel.GameViewModel;

/**
 *
 * Fragment class that provides a searchable list of {@link Element}.
 *
 * @author Josia Menger
 */
public class ElementListFragment extends Fragment {

    private static final String TAG = ElementListFragment.class.getSimpleName();

    private GameViewModel viewModel;
    private FragmentElementListBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentElementListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        GameActivity gameActivity = ((GameActivity) requireActivity());

        // Gets the viewmodel and applies it to the activity
        viewModel = new ViewModelProvider(gameActivity,
                new GameViewModel.Factory(gameActivity,
                        gameActivity.isArcade())).get(GameViewModel.class);
        binding.setViewModel(viewModel);

        binding.setLayoutManager(getLayoutManager());
        binding.setRecyclerViewAdapter(new ElementRecyclerViewAdapter(this::onClickElement));
        
        AutoCompleteTextView search = binding.autoTextGameItemListSearch;
        // Filter list based on search
        search.addTextChangedListener(getTextWatcher());
        // Clear search on click cancel
        search.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getX() >= search.getWidth() - search.getTotalPaddingEnd()) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Pressed cancel button inside search");
                    search.setText("");
                    viewModel.onSearchQueryChanged("");
                }
            }
            return false;
        });
        
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // set search to empty text so element list will update on it's own
        binding.autoTextGameItemListSearch.setText("");
    }

    /**
     * Creates and configures a {@link FlexboxLayoutManager} for arranging items
     * with wrapping and content which is aligned left.
     *
     * @return a configured {@link FlexboxLayoutManager} instance.
     */
    private FlexboxLayoutManager getLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    /**
     * Creates a {@link TextWatcher} for the search input field.
     * <p>
     * Updates the ViewModels search query on text change and dynamically switches
     * the search/cancel icons based on if the input is empty.
     *
     * @return a {@link TextWatcher} that handles search field changes.
     */
    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            private final AutoCompleteTextView view = binding.autoTextGameItemListSearch;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Apply filter for String: " + s);
                viewModel.onSearchQueryChanged(s.toString());
                if (s.toString().isEmpty()) {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_search,
                            0, 0, 0);
                } else {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_search,
                            0, R.drawable.ic_cancel, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };
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
    private void onClickElement(Element element, int position) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Clicked on element: " + position);
        viewModel.addElementToPlayground(element);
    }

}
