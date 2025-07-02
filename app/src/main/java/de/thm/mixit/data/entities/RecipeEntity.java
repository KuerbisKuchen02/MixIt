package de.thm.mixit.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Represents a Recipe or Combination of two inputs to create a new element.
 * Also contains a reference to an {@link ElementEntity}
 *
 * @author Justin Wolek
 * @version 1.0.0
 */
@Entity(
        tableName = "recipes",
        primaryKeys = {"inputA", "inputB"},
        foreignKeys = @ForeignKey(
                entity = ElementEntity.class,
                parentColumns = "id",
                childColumns = "outputId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"outputId"})}
)
public class RecipeEntity {
    @NonNull
    public String inputA;

    @NonNull
    public String inputB;

    public int outputId;

    public RecipeEntity(@NonNull String inputA, @NonNull String inputB, int outputId) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.outputId = outputId;
    }
}
