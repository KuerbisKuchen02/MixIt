package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.thm.mixit.data.daos.RecipeDAO;
import de.thm.mixit.data.entities.RecipeEntity;

/**
 * @author Justin Wolek
 * @version 1.0.0
 */
public class RecipeLocalDataSource {
    private final RecipeDAO recipeDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public RecipeLocalDataSource(RecipeDAO recipeDAO) {
        this.recipeDAO = recipeDAO;
    }

    public void getAll(ICallback<List<RecipeEntity>> callback) {
        executor.execute(() -> {
            List<RecipeEntity> recipes = recipeDAO.getAll();
            callback.onDataLoaded(recipes);
        });
    }

    public void findByCombination(String firstWord, String secondWord, ICallback<RecipeEntity> callback) {
        executor.execute(() -> {
            RecipeEntity recipe = recipeDAO.findByCombination(firstWord, secondWord);
            callback.onDataLoaded(recipe);
        });
    }

    public void insertRecipe(RecipeEntity recipe) {
        executor.execute(() -> recipeDAO.insertRecipe(recipe));
    }

    public void deleteAll() {
        executor.execute(() -> recipeDAO.deleteAll());
    }
}
