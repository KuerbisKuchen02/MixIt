package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.thm.mixit.data.daos.RecipeDAO;
import de.thm.mixit.data.entities.RecipeEntity;

/**
 * Local data source for accessing and modifying {@link RecipeEntity} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * recipes, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class RecipeLocalDataSource {
    private final RecipeDAO recipeDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code RecipeLocalDataSource} with the given {@link RecipeDAO}.
     * @param recipeDAO The Data Access Object used to perform database operation
     *                   on {@link RecipeEntity} objects.
     */
    public RecipeLocalDataSource(RecipeDAO recipeDAO) {
        this.recipeDAO = recipeDAO;
    }

    /**
     * Asynchronously retrieves all RecipeEntity records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link ICallback} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link RecipeEntity} objects when
     *                 loading the data is done.
     */
    public void getAll(ICallback<List<RecipeEntity>> callback) {
        executor.execute(() -> {
            List<RecipeEntity> recipes = recipeDAO.getAll();
            callback.onDataLoaded(recipes);
        });
    }

    /**
     * Asynchronously finds a RecipeEntity by its combination
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link ICallback} once the data is loaded.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @param callback The callback to receive the found RecipeEntity.
     */
    public void findByCombination(String inputA, String inputB, ICallback<RecipeEntity> callback) {
        executor.execute(() -> {
            RecipeEntity recipe = recipeDAO.findByCombination(inputA, inputB);
            callback.onDataLoaded(recipe);
        });
    }

    /**
     * Asynchronously inserts a RecipeEntity into the database.
     *
     * @param recipe The RecipeEntity to insert.
     */
    public void insertRecipe(RecipeEntity recipe) {
        executor.execute(() -> recipeDAO.insertRecipe(recipe));
    }

    /**
     * Asynchronously deletes all RecipeEntity records from the database.
     */
    public void deleteAll() {
        executor.execute(() -> recipeDAO.deleteAll());
    }
}
