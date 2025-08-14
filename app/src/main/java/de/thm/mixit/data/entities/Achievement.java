package de.thm.mixit.data.entities;

/**
 * Represents a generic Achievement. Meant to be extended to implement individual logic.
 *
 * @author Justin Wolek
 */
public abstract class Achievement {
    private String name;
    private String description;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract boolean isUnlocked();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
