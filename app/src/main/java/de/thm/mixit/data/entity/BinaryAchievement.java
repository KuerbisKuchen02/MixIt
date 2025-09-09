package de.thm.mixit.data.entity;

/**
 * A BinaryAchievement represents an Achievement which can be either unlocked or not unlocked.
 *
 * @author Justin Wolek
 */
public class BinaryAchievement extends Achievement {
    private boolean isUnlocked;

    public BinaryAchievement(int id, int nameResId, int descriptionResId, boolean isUnlocked) {
        super(id, nameResId, descriptionResId);
        this.isUnlocked = isUnlocked;
    }

    /**
     * Returns {@code true} if the Achievement has been unlocked, or {@code false} if it is not
     * unlocked
     *
     * @return a boolean value representing if the Achievement is considered unlocked or not.
     */
    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }
}
