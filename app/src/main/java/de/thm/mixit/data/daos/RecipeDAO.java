package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.RecipeEntity;

/**
 * This DAO (data access object) defines methods to be used on {@link RecipeEntity}'s.
 *
 * @author Justin Wolek
 */
@Dao
public interface RecipeDAO {

    /**
     * Returns all recipes inside the database.
     *
     * @return A list of {@link RecipeEntity}
     */
    @Query("SELECT * FROM recipes")
    public List<RecipeEntity> getAll();

    /**
     * Returns one {@link RecipeEntity} which has {@code inputA} and {@code inputB}.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @return One {@link RecipeEntity} which satisfies the condition or {@code null} if none does.
     */
    @Query("SELECT * FROM recipes WHERE inputA LIKE :inputA AND inputB LIKE :inputA LIMIT 1")
    public RecipeEntity findByCombination(String inputA, String inputB);

    /**
     * Inserts one {@link RecipeEntity} into the database.
     *
     * @param recipe RecipeEntity to insert into the database.
     */
    @Insert
    public void insertRecipe(RecipeEntity recipe);

    /**
     * Deletes all {@link RecipeEntity}'s from the database.
     */
    @Query("DELETE FROM recipes")
    public void deleteAll();
}
