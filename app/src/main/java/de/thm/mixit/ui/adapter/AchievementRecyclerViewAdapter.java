package de.thm.mixit.ui.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.thm.mixit.R;
import de.thm.mixit.data.entities.Achievement;
import de.thm.mixit.data.entities.ProgressAchievement;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerViewAdapter used to display Achievement Entities
 *
 * @author Justin Wolek
 */
public class AchievementRecyclerViewAdapter extends
        RecyclerView.Adapter<AchievementRecyclerViewAdapter.AchievementCardViewHolder> {
    private final List<Achievement> achievements;

    /**
     * Creates a new AchievementRecyclerViewAdapter
     *
     * @param achievements  {@link Achievement} entities to be display by
     *                      the AchievementRecyclerViewAdapter
     */
    public AchievementRecyclerViewAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public AchievementCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_achievement, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(16, 16, 16, 16);
        }
        view.setLayoutParams(params);
        view.requestLayout();
        return new AchievementRecyclerViewAdapter.AchievementCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementCardViewHolder holder, int position) {
        holder.bind(achievements.get(position));
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }


    public static class AchievementCardViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView progress_text;
        ProgressBar progress_bar;
        View card;

        public AchievementCardViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_achievement_name);
            description = itemView.findViewById(R.id.text_achievement_desc);
            progress_bar = itemView.findViewById(R.id.progress_achievement_bar);
            progress_text = itemView.findViewById(R.id.text_achievement_progress_text);
            card = itemView.findViewById(R.id.card_achievement);
        }

        void bind(Achievement achievement) {
            name.setText(achievement.getName());
            description.setText(achievement.getDescription());

            // BinaryAchievements do not need a ProgressBar and can either
            // be unlocked or not unlocked. Default State is GONE.
            progress_bar.setVisibility(View.GONE);
            progress_text.setVisibility(View.GONE);

            if (achievement instanceof ProgressAchievement) {
                ProgressAchievement a = (ProgressAchievement) achievement;

                progress_bar.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.VISIBLE);
                progress_bar.setMax(a.getTargetProgress());
                progress_bar.setProgress(a.getCurrentProgress());

                Resources res = itemView.getResources();
                progress_text.setText(String.format(
                        res.getString(R.string.achievement_card_progress_text),
                        a.getCurrentProgress(),
                        a.getTargetProgress())
                );
            }

            // TODO: Use the correct MaterialUI color here
            if (achievement.isUnlocked()) card.setBackgroundColor(Color.GREEN);
        }
    }
}
