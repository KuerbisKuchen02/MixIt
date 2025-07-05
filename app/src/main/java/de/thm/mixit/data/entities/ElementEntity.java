package de.thm.mixit.data.entities;

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
public class ElementEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "output")
    public String output;

    @ColumnInfo(name = "emoji")
    public String emoji;

    /**
     * Constructor for a new ElementEntity. {@code id} is automatically generated.
     *
     * @param output A String representing the element's name.
     * @param emoji A String containing one or more emojis.
     */
    public ElementEntity(String output, String emoji) {
        this.output = output;
        this.emoji = emoji;
    }

    @NonNull
    @Override
    public String toString() {
        return emoji + " " + output;
    }
}
