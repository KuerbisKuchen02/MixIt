package de.thm.mixit.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Achievement;
import de.thm.mixit.data.entities.BinaryAchievement;
import de.thm.mixit.data.entities.ProgressAchievement;
import de.thm.mixit.ui.adapter.AchievementRecyclerViewAdapter;

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
public class AchievementActivity extends Activity {
    private static final String TAG = AchievementActivity.class.getSimpleName();
    private int curAchievements = 10;  // TODO: Get actual value from AchievementViewModel
    private int allAchievements = 20;  // TODO: Get actual value from AchievementViewModel

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_achievement);
        setOverallProgress();

        // TODO: Temporary Dummy Data! Remove once AchievementViewModel is implemented
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new BinaryAchievement(
                "Beginner",
                "Finish your first Arcade Game",
                true
        ));

        achievements.add(new BinaryAchievement(
                "The cake is a lie",
                "Manage to get Chocolate cake",
                false
        ));

        achievements.add(new BinaryAchievement(
                "Speed Runner",
                "Finish an Arcade Game in under 2 minutes.",
                true
        ));

        achievements.add(new ProgressAchievement(
                "Master Combinator",
                "Combine 500 Elements",
                0,
                500
        ));

        // Initialize and populate the RecyclerView with Achievements
        RecyclerView recyclerView = findViewById(R.id.recycler_achievements_card_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        @SuppressLint("RestrictedApi")
        AchievementRecyclerViewAdapter adapter = new AchievementRecyclerViewAdapter(achievements);

        recyclerView.setAdapter(adapter);

        Log.i(TAG, "AchievementActivity was created");
    }

    /**
     * Sets the overall progress in the UI by updating the ProgressBar and TextView
     */
    private void setOverallProgress() {
        ProgressBar progressBar = findViewById(R.id.progress_achievement_overall_progress);
        progressBar.setMax(allAchievements);
        progressBar.setProgress(curAchievements);

        TextView progressText = findViewById(R.id.text_achievements_sub_heading_progress);
        Resources res = getResources();
        progressText.setText(String.format(
                res.getString(R.string.achievements_sub_heading_progress),
                curAchievements,
                allAchievements)
        );
    }
}
