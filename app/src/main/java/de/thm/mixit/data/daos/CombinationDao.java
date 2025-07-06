package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.Combination;

/**
 * This DAO (data access object) defines methods to be used on {@link Combination}'s.
 *
 * @author Justin Wolek
 */
@Dao
public interface CombinationDAO {

    /**
     * Returns all combinations inside the database.
     *
     * @return A list of {@link Combination}
     */
    @Query("SELECT * FROM combinations")
    List<Combination> getAll();

    /**
     * Returns one {@link Combination} which has {@code inputA} and {@code inputB}.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @return One {@link Combination} which satisfies
     * the condition or {@code null} if none does.
     */
    @Query("SELECT * FROM combinations WHERE inputA LIKE :inputA AND inputB LIKE :inputB LIMIT 1")
    Combination findByCombination(String inputA, String inputB);

    /**
     * Inserts one {@link Combination} into the database.
     *
     * @param combination CombinationEntity to insert into the database.
     */
    @Insert
    void insertCombination(Combination combination);

    /**
     * Deletes all {@link Combination}'s from the database.
     */
    @Query("DELETE FROM combinations")
    void deleteAll();
}
