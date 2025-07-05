package de.thm.mixit.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Represents a Combination of two inputs to create a new element. Contains one composite
 * primary-key. Can contain a reference to one {@link ElementEntity}
 * <p>
 * {@code inputA} First part of the primary-key.
 * <br>
 * {@code inputB} Second part of the primary-key.
 * <br>
 * {@code outputId} Foreign-Key which contains a reference to one {@link ElementEntity}.
 *
 * @author Justin Wolek
 */
@Entity(
        tableName = "combinations",
        primaryKeys = {"inputA", "inputB"},
        foreignKeys = @ForeignKey(
                entity = ElementEntity.class,
                parentColumns = "id",
                childColumns = "outputId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"outputId"})}
)
public class CombinationEntity {
    @NonNull
    public String inputA;

    @NonNull
    public String inputB;

    public Integer outputId;

    /**
     * Constructor for a new CombinationEntity.
     * The composite-key made up from {@code inputA} and {@code inputB} must be unique.
     *
     * @param inputA First part of the primary-key.
     * @param inputB Second part of the primary-key.
     * @param outputId Foreign-Key which contains a reference to one {@link ElementEntity}.
     */
    public CombinationEntity(@NonNull String inputA, @NonNull String inputB, int outputId) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.outputId = outputId;
    }
}
