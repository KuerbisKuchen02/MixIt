package de.thm.mixit.data.entity;

/**
 * Represents a generic Achievement. Meant to be extended to implement individual logic.
 * The attributes nameResId and descriptionResId are meant to be set to an ID from the strings.xml
 * file in order to achieve multiple language support.
 *
 * @author Justin Wolek
 */
public abstract class Achievement implements Comparable<Achievement> {
    private final int id;
    private final int nameResId;
    private final int descriptionResId;

    public Achievement(int id, int nameResId, int descriptionResId) {
        this.id = id;
        this.nameResId = nameResId;
        this.descriptionResId = descriptionResId;
    }

    /**
     * Compares this {@link Achievement} to another {@link Achievement} object.
     * The comparison is based on the unlocked state:
     *
     * @param o the {@link Achievement} to be compared.
     * @return a negative integer if this is considered smaller,
     *         zero if both are equal,
     *         or a positive integer if this is considered greater.
     */
    @Override
    public int compareTo(Achievement o) {
        if (this.isUnlocked() && !o.isUnlocked()) return 1;
        else if (!this.isUnlocked() && o.isUnlocked()) return -1;
        else return 0;
    }

    public abstract boolean isUnlocked();

    public int getId() { return this.id; }
    public int getNameResId() {
        return nameResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

}
