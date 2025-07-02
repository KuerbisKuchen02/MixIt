package de.thm.mixit.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents an Element and contains two Strings representing the name and emojis.
 *
 * @author Justin Wolek
 * @version 1.0.0
 */
@Entity(tableName = "elements")
public class ElementEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "output")
    public String output;

    @ColumnInfo(name = "emoji")
    public String emoji;

    public ElementEntity(String output, String emoji) {
        this.output = output;
        this.emoji = emoji;
    }
}
