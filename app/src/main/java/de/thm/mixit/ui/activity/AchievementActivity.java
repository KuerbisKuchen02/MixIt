package de.thm.mixit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import de.thm.mixit.BuildConfig;
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

        Log.i(TAG, "AchievementActivity was created");
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.saveAchievements();
    }

    /**
     * Is called when the back button in the activity has been clicked on
     * @param view  The view which received the click
     */
    public void onBackButtonClicked(View view){
        if (BuildConfig.DEBUG) Log.d(TAG, "Return button was clicked");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
