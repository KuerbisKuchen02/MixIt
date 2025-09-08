package de.thm.mixit.ui.binding;


import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.ui.adapter.AchievementRecyclerViewAdapter;

/**
 * Data binding adapters for the AchievementsActivity
 *
 * @author Justin Wolek
 */
public class AchievementBindingAdapter {
    @BindingAdapter("achievements")
    public static void setAchievements(@NonNull RecyclerView recyclerView,
                                       @NonNull List<Achievement> achievements) {
        AchievementRecyclerViewAdapter adapter = (AchievementRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setElements(achievements);
        }
    }
}
