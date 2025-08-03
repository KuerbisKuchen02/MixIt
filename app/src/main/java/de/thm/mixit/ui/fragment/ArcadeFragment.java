package de.thm.mixit.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.thm.mixit.databinding.FragmentArcadeBinding;
import de.thm.mixit.ui.viewmodel.GameViewModel;

/**
 * Fragment class which holds information for the arcade game mode.
 *
 * @author Jannik Heimann
 */

public class ArcadeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentArcadeBinding binding = FragmentArcadeBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        GameViewModel viewModel = new ViewModelProvider(requireActivity(),
                new GameViewModel.Factory(requireActivity())).get(GameViewModel.class);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }
}
