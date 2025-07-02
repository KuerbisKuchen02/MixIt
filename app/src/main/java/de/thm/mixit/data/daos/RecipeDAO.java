package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.entities.RecipeEntity;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM recipes")
    public List<RecipeEntity> getAll();

    @Query("SELECT * FROM recipes WHERE inputA LIKE :firstWord AND inputB LIKE :secondWord LIMIT 1")
    public RecipeEntity findByCombination(String firstWord, String secondWord);

    @Insert
    public void insertRecipe(RecipeEntity recipe);

    @Query("DELETE FROM recipes")
    public void deleteAll();
}
