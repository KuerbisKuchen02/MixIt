package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.CombinationEntity;

/**
 * This DAO (data access object) defines methods to be used on {@link CombinationEntity}'s.
 *
 * @author Justin Wolek
 */
@Dao
public interface CombinationDAO {

    /**
     * Returns all combinations inside the database.
     *
     * @return A list of {@link CombinationEntity}
     */
    @Query("SELECT * FROM combinations")
    public List<CombinationEntity> getAll();

    /**
     * Returns one {@link CombinationEntity} which has {@code inputA} and {@code inputB}.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @return One {@link CombinationEntity} which satisfies the condition or {@code null} if none does.
     */
    @Query("SELECT * FROM combinations WHERE inputA LIKE :inputA AND inputB LIKE :inputB LIMIT 1")
    public CombinationEntity findByCombination(String inputA, String inputB);

    /**
     * Inserts one {@link CombinationEntity} into the database.
     *
     * @param combination CombinationEntity to insert into the database.
     */
    @Insert
    public void insertCombination(CombinationEntity combination);

    /**
     * Deletes all {@link CombinationEntity}'s from the database.
     */
    @Query("DELETE FROM combinations")
    public void deleteAll();
}
