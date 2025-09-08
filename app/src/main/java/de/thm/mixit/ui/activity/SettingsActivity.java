package de.thm.mixit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.R;
import de.thm.mixit.databinding.ActivitySettingsBinding;
import de.thm.mixit.ui.fragment.DialogResetProgress;
import de.thm.mixit.ui.viewmodel.SettingsViewModel;

/**
 * Activity for the settings view.
 * <p>
 * This Activity implements the logic for setting the app theme and language.
 * Also it contains the feature to reset the free play progress.
 * </p>
 *
 * @author Jonathan Hildebrandt
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SettingsViewModel viewModel;

    /**
     * Binding spinner items to spinners.
     * ref: <a href="https://developer.android.com/develop/ui/views/components/spinner">Spinners</a>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init ViewModel with Databinding
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_settings);

        viewModel = new ViewModelProvider(this, new SettingsViewModel.Factory(this))
                .get(SettingsViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Language adapter
        ArrayAdapter<CharSequence> adapterLanguage = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item
        );
        adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdownSettingsLanguage.setAdapter(adapterLanguage);
        // Workaround Solution to prevent options to disappear
        binding.dropdownSettingsLanguage.setThreshold(Integer.MAX_VALUE);

        // Observer to notify view model when user chooses an language option
        binding.dropdownSettingsLanguage.setOnItemClickListener((parent, view, position, id) -> {
            String[] values = getResources().getStringArray(R.array.language_values);
            viewModel.setLanguage(values[position]);
        });

        // Update UI when viewModel language attribute updates
        viewModel.getLanguage().observe(this, lang -> {
            String[] values = getResources().getStringArray(R.array.language_values);
            String[] labels = getResources().getStringArray(R.array.language_array);

            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(lang)) {
                    int finalI = i;
                    binding.dropdownSettingsLanguage.post(() -> {
                        binding.dropdownSettingsLanguage.setText(labels[finalI], false);
                    });
                    break;
                }
            }
        });

        // Set initial value of Dropdown
        viewModel.preSelectLanguage();


        // Theme adapter
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(
                this, R.array.theme_array, android.R.layout.simple_spinner_item
        );
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdownSettingsTheme.setAdapter(adapterTheme);
        // Workaround Solution to prevent options to disappear
        binding.dropdownSettingsTheme.setThreshold(Integer.MAX_VALUE);

        // Observer to notify view model when user chooses an theme option
        binding.dropdownSettingsTheme.setOnItemClickListener((parent, view, position, id) -> {
            String[] values = getResources().getStringArray(R.array.theme_values);
            viewModel.setTheme(values[position]);
        });

        // Update UI when viewModel theme attribute updates
        viewModel.getTheme().observe(this, theme -> {
            String[] values = getResources().getStringArray(R.array.theme_values);
            String[] labels = getResources().getStringArray(R.array.theme_array);

            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(theme)) {
                    int finalI = i;
                    binding.dropdownSettingsTheme.post(() -> {
                        binding.dropdownSettingsTheme.setText(labels[finalI], false);
                    });
                    break;
                }
            }
        });

        // set initial selection based on current mode
        viewModel.preSelectTheme();


        Log.i(TAG, "SettingsActivity was created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.load();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.save();
    }

    public void onResetProgressClicked(View view) {
        Log.i(TAG, "Reset Progress Button was clicked.");
        new DialogResetProgress().show(getSupportFragmentManager(), "DialogResetProgress");
    }

    public void onBackButtonClicked(View view){
        if(BuildConfig.DEBUG) Log.d(TAG, "Return button clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
