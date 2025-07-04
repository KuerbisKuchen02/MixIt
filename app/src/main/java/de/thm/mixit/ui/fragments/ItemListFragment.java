package de.thm.mixit.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

public class ItemListFragment extends Fragment {

    private static final String TAG = ItemListFragment.class.getSimpleName();

    private ElementRecyclerViewAdapter recyclerViewAdapter;
    private ArrayAdapter<ElementEntity> arrayAdapter;
    private List<ElementEntity> elements = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        ElementRepository.create(requireContext()).getAll((elements) ->
                new Handler(Looper.getMainLooper()).post(() -> setElements(elements)));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_game_item_list);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new ElementRecyclerViewAdapter(elements);
        recyclerView.setAdapter(recyclerViewAdapter);

        AutoCompleteTextView textView = view.findViewById(R.id.auto_text_game_item_list_search);
        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, elements);
        textView.setAdapter(arrayAdapter);

        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Apply filter for String: " + s);
                recyclerViewAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        return view;
    }

    public void setElements(List<ElementEntity> elements) {
        this.elements = elements;
        // FIXME: Use DiffUtil or notifyItemChanged/Inserted/Removed to improve performance
        recyclerViewAdapter.setElements(elements);
        arrayAdapter.addAll(elements);
        recyclerViewAdapter.notifyDataSetChanged();
        arrayAdapter.notifyDataSetChanged();
        if (BuildConfig.DEBUG) Log.d(TAG, "Loaded " + elements.size() + " Elements");
    }

}
