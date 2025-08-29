package de.thm.mixit.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import de.thm.mixit.R;
import de.thm.mixit.databinding.ActivityAchievementBinding;
import de.thm.mixit.ui.adapter.AchievementRecyclerViewAdapter;
import de.thm.mixit.ui.viewmodel.AchievementViewModel;

/**
 * Activity for the Achievement view of the game.
 * </p>
 * The Activity contains a ProgressBar, showing the progress of unlocked Achievement, a TextView
 * displaying the amount of unlocked Achievements and a RecyclerView containing the Achievements
 * themselves.
 * </p>
 *
 * @author Justin Wolek
 */
public class AchievementActivity extends AppCompatActivity {
    private static final String TAG = AchievementActivity.class.getSimpleName();
    private AchievementViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAchievementBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_achievement);

        viewModel = new ViewModelProvider(this, new AchievementViewModel.Factory(this))
                .get(AchievementViewModel.class);

        AchievementRecyclerViewAdapter adapter = new AchievementRecyclerViewAdapter();
        binding.recyclerAchievementsCardList.setAdapter(adapter);
        binding.recyclerAchievementsCardList.setLayoutManager(new LinearLayoutManager(this));

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.saveAchievements();
    }
}
