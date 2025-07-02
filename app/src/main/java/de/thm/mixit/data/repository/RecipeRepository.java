package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;

import de.thm.mixit.data.daos.RecipeDAO;
import de.thm.mixit.data.entities.RecipeEntity;
import de.thm.mixit.data.source.AppDatabase;
import de.thm.mixit.data.source.RecipeLocalDataSource;
import de.thm.mixit.data.source.ICallback;

/**
 * Repository class that provides access to Recipe data.
 * <p>
 * Acts as a single source of truth for Recipe data by delegating
 * data operations to a {@link RecipeLocalDataSource}.
 *
 * @author Justin Wolek
 * @version 1.0.0
 */
public class RecipeRepository {
    private final RecipeLocalDataSource localDataSource;

    /**
     * Constructs a RecipeRepository with the specified local data source.
     * Can be used for Unit-Testing. Use {@code RecipeRepository.create()} when trying to do
     * regular database operations.
     *
     * @param localDataSource The local data source managing RecipeEntity persistence.
     */
    public RecipeRepository(RecipeLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create a RecipeRepository instance.
     * @param context The Android context used to get the database instance.
     * @return A new instance of {@code RecipeRepository}.
     */
    public static RecipeRepository create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        RecipeDAO dao = db.recipeDAO();
        return new RecipeRepository(new RecipeLocalDataSource(dao));
    }

    /**
     * Retrieves all RecipeEntity objects asynchronously.
     *
     * @param callback The callback to receive the list of all recipes.
     */
    public void getAll(ICallback<List<RecipeEntity>> callback) {
        localDataSource.getAll(callback);
    }

    /**
     * Finds a {@link RecipeEntity} by a combination of two input strings.
     * <p>
     * The input strings are alphabetically ordered to ensure consistent lookups since combinations
     * are always stored in alphabetical order.
     *
     * @param inputA   The first input string of the combination.
     * @param inputB   The second input string of the combination.
     * @param callback The callback to receive the found {@link RecipeEntity}.
     */
    public void findByCombination(String inputA, String inputB, ICallback<RecipeEntity> callback) {
        // Words of combination are always saved in alphabetical order.
        if (inputA.compareTo(inputB) > 0) {
            String temp = inputA;
            inputA = inputB;
            inputB = temp;
        }
        localDataSource.findByCombination(inputA, inputB, callback);
    }

    /**
     * Inserts a new RecipeEntity asynchronously.
     * <p>
     * Ensures that the inputs are alphabetically ordered before insertion
     * to prevent duplicate entries with switched input combinations.
     *
     * @param recipe The RecipeEntity to insert.
     */
    public void insertRecipe(RecipeEntity recipe) {
        // Order alphabetically when necessary
        if (recipe.inputA.compareTo(recipe.inputB) > 0) {
            String temp = recipe.inputA;
            recipe.inputA = recipe.inputB;
            recipe.inputB = temp;
        }
        localDataSource.insertRecipe(recipe);
    }


    /**
     * Deletes all RecipeEntity records.
     */
    public void deleteAll() {
        localDataSource.deleteAll();
    }
}
