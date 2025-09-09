package de.thm.mixit.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import de.thm.mixit.R;
import de.thm.mixit.data.repository.AchievementRepository;
import de.thm.mixit.data.repository.CombinationRepository;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;
import de.thm.mixit.data.repository.StatisticRepository;

/**
 * DialogFragment for resetting the game progress.
 * <p>
 * Ref: <a href="https://developer.android.com/develop/ui/views/components/dialogs#CustomLayout">
 *     Custom Dialog Layout</a>
 *
 * @author Jonathan Hildebrandt
 */
public class ResetProgressDialogFragment extends DialogFragment {

    private static final String TAG = ResetProgressDialogFragment.class.getSimpleName();
    GameStateRepository gameStateRepository;
    ElementRepository elementRepository;
    GameStateRepository arcadeGameStateRepository;
    ElementRepository arcadeElementRepository;
    StatisticRepository statisticRepository;
    AchievementRepository achievementRepository;
    CombinationRepository combinationRepository;
    CombinationRepository arcadeCombinationRepository;


    /**
     * Creates the dialog with a custom layout.
     * The dialog contains an input field where the user must type "MixIt"
     * to confirm the reset action. If the input is correct, the game state,
     * statistics, and achievements are reset. If the input is incorrect,
     * the input field is highlighted in red.
     *
     * @return The created Dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        Context context = requireContext();

        gameStateRepository = GameStateRepository.create(context,false);
        elementRepository = ElementRepository.create(context, false);
        combinationRepository = CombinationRepository.create(context, false);
        arcadeGameStateRepository = GameStateRepository.create(context,true);
        arcadeElementRepository = ElementRepository.create(context, true);
        arcadeCombinationRepository = CombinationRepository.create(context, true);



        statisticRepository = StatisticRepository.create(context);
        achievementRepository = AchievementRepository.create(context);

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_reset_progress_dialog, null);

        TextInputLayout inputContainer = view.findViewById(R.id.textInputLayout_reset_progress);
        TextInputEditText input = view.findViewById(R.id.editText_reset_progress_dialog_confirm);
        MaterialButton confirm = view.findViewById(R.id.button_reset_progress_dialog_confirm);
        ImageButton close = view.findViewById(R.id.imageButton_reset_progress_dialog_close);

        final String REQUIRED = getString(R.string.mix_it);

        // Store initial colors to reset them when user types
        ColorStateList initialHintColor = inputContainer.getHintTextColor();
        int initialTextColor = input.getCurrentTextColor();

        // Close dialog when clicking the close button
        close.setOnClickListener(v -> dismiss());

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
            { /* no-op */ }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // reset colors as user types
                input.setTextColor(initialTextColor);
                inputContainer.setHintTextColor(initialHintColor);
                inputContainer.setDefaultHintTextColor(initialHintColor);
            }

            @Override public void afterTextChanged(android.text.Editable s) { /* no-op */ }
        });

        confirm.setOnClickListener((View _view) -> {
            if (Objects.requireNonNull(input.getText()).toString().equals(REQUIRED)) {
                // Reset free play
                elementRepository.reset();
                gameStateRepository.deleteSavedGameState();
                combinationRepository.deleteAll();

                // Reset arcade
                arcadeElementRepository.reset();
                arcadeGameStateRepository.deleteSavedGameState();
                arcadeCombinationRepository.deleteAll();

                // Reset statistics and achievements
                statisticRepository.deleteSavedStatistic();
                achievementRepository.deleteSavedAchievements();

                dismiss();

                Toast.makeText(context, R.string.reset_success, Toast.LENGTH_SHORT).show();

            } else {
                // Set the Error highlighting based on the current theme
                int errorColor;
                int nightModeFlag = getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                if (nightModeFlag == Configuration.UI_MODE_NIGHT_NO) {
                    errorColor =
                            ContextCompat.getColor(context, R.color.md_theme_light_errorContainer);
                } else if (nightModeFlag == Configuration.UI_MODE_NIGHT_YES) {
                    errorColor =
                            ContextCompat.getColor(context, R.color.md_theme_dark_errorContainer);
                } else {
                    Log.w(TAG,
                            "Could not get the current configuration " +
                                    "of the App Theme using light error colors.");
                    errorColor = ContextCompat.getColor(context, R.color.md_theme_light_error);
                }

                input.setTextColor(errorColor);
                inputContainer.setHintTextColor(ColorStateList.valueOf(errorColor));
                inputContainer.setDefaultHintTextColor(ColorStateList.valueOf(errorColor));
            }
        });

        builder.setView(view);

        return builder.create();
    }
}