package de.thm.mixit.data.entity;

/**
 * Represents a generic Achievement. Meant to be extended to implement individual logic.
 * The attributes nameResId and descriptionResId are meant to be set to an ID from the strings.xml
 * file in order to achieve multiple language support.
 * @author Justin Wolek
 */
public abstract class Achievement {
    private int nameResId;
    private int descriptionResId;

    public Achievement(int nameResId, int descriptionResId) {
        this.nameResId = nameResId;
        this.descriptionResId = descriptionResId;
    }

    public abstract boolean isUnlocked();

    public int getNameResId() {
        return nameResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public void setDescriptionResId(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }
}
