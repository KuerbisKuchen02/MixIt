package de.thm.mixit.data.entities;

/**
 * A ProgressAchievement represents an Achievement which has a progress associated which needs
 * to reach the targetProgress to be considered as unlocked.
 *
 * @author Justin Wolek
 */
public class ProgressAchievement extends Achievement {
    private int currentProgress;
    private final int targetProgress;

    public ProgressAchievement(int nameResId, int descriptionResId, int currentProgress,
                               int targetProgress) {
        super(nameResId, descriptionResId);
        this.currentProgress = currentProgress;
        this.targetProgress = targetProgress;
    }

    /**
     * Returns {@code true} if the current progress is greater than or equal to the targetProgress,
     * otherwise returns {@code false}
     *
     * @return a boolean value representing if the Achievement is considered unlocked or not.
     */
    @Override
    public boolean isUnlocked() {
        return currentProgress >= targetProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }
    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getTargetProgress() {
        return targetProgress;
    }
}
