package de.thm.mixit.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.thm.mixit.R;
import de.thm.mixit.data.repository.ElementRepository;
import de.thm.mixit.data.repository.GameStateRepository;

/**
 * Ref: Custom Dialog Layout
 * <a href="https://developer.android.com/develop/ui/views/components/dialogs#CustomLayout">...</a>
 */
public class DialogResetProgress extends DialogFragment {

    GameStateRepository gameStateRepository;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        Context context = requireContext();
        gameStateRepository = GameStateRepository.create(context,false);

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reset_progress, null);

        EditText input = view.findViewById(R.id.editText_reset_progress_dialog_confirm);
        Button confirm = view.findViewById(R.id.button_reset_progress_dialog_confirm);

        final String REQUIRED = getString(R.string.mix_it);

        int initialHintColor = input.getCurrentHintTextColor();
        int initialTextColor = input.getCurrentTextColor();
        ColorStateList initialBackgroundTintList = input.getBackgroundTintList();

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
            { /* no-op */ }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // reset colors as user types
                input.setBackgroundTintList(initialBackgroundTintList);
                input.setTextColor(initialTextColor);
                input.setHintTextColor(initialHintColor);
            }

            @Override public void afterTextChanged(android.text.Editable s) { /* no-op */ }
        });

        confirm.setOnClickListener((View _view) -> {
            if (input.getText().toString().equals(REQUIRED)) {
                gameStateRepository.deleteSavedGameState();
                ElementRepository elementRepository =
                        ElementRepository.create(context, false);
                elementRepository.reset();
                // TODO also delete statistics and achievements ?
                dismiss();

                Toast.makeText(context, R.string.reset_success, Toast.LENGTH_SHORT).show();

            } else {
                input.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                input.setTextColor(Color.RED);
                input.setHintTextColor(Color.RED);
            }
        });

        builder.setView(view);

        return builder.create();
    }
}
