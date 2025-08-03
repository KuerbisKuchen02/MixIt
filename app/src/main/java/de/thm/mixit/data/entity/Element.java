package de.thm.mixit.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents one Element inside the database
 * <p>
 * {@code id} The primary key. Is automatically generated
 * <br>
 * {@code output} A String representing the element's name.
 * <br>
 * {@code emoji} A String containing one or more emojis.
 *
 * @author Justin Wolek
 */
@Entity(tableName = "elements")
public class Element {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "emoji")
    public String emoji;

    /**
     * Constructor for a new Element. {@code id} is automatically generated.
     *
     * @param name A String representing the element's name.
     * @param emoji A String containing one or more emojis.
     */
    public Element(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    @NonNull
    @Override
    public String toString() {
        return emoji + " " + name;
    }
}
