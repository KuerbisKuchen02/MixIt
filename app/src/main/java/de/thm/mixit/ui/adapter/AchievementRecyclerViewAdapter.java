package de.thm.mixit.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.thm.mixit.R;
import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.data.entity.ProgressAchievement;

/**
 * RecyclerViewAdapter used to display Achievement Entities
 *
 * @author Justin Wolek
 */
public class AchievementRecyclerViewAdapter extends
        RecyclerView.Adapter<AchievementRecyclerViewAdapter.AchievementCardViewHolder> {

    private List<Achievement> achievements = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setElements(List<Achievement> newAchievements) {
        achievements.clear();
        if (newAchievements != null) {
            achievements.addAll(newAchievements);
        }
        notifyDataSetChanged();
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
        return new AchievementCardViewHolder(view);
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
            // Convert IDs from string.xml to their corresponding string representation
            Context context = itemView.getContext();
            name.setText(context.getString(achievement.getNameResId()));
            description.setText(context.getString(achievement.getDescriptionResId()));

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

            // Set the color of the achievement card based on current app theme
            boolean isDarkTheme =
                    (context.getResources().getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

            // Setting the correct color explicitly whenever a view is recycled, so a view
            // which showed an unlocked achievement previously does not apply to the new view.
            if (achievement.isUnlocked()) {
                if (isDarkTheme) {
                    card.setBackgroundColor(context.getColor(R.color.md_theme_dark_tertiary));
                } else {
                    card.setBackgroundColor(context.getColor(R.color.md_theme_light_tertiary));
                }
            } else {
                // Set the correct color for locked achievements
                if (isDarkTheme) {
                    card.setBackgroundColor(context.getColor(R.color.md_theme_dark_surface));
                } else {
                    card.setBackgroundColor(context.getColor(R.color.md_theme_light_surface));
                }
            }
        }
    }
}
