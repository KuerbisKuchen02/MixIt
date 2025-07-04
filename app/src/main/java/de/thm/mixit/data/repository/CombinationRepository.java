package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.data.daos.CombinationDAO;
import de.thm.mixit.data.entities.CombinationEntity;
import de.thm.mixit.data.source.AppDatabase;
import de.thm.mixit.data.source.CombinationLocalDataSource;
import de.thm.mixit.data.source.ICallback;

/**
 * Repository class that provides access to Combination data.
 * <p>
 * Acts as a single source of truth for Combination data by delegating
 * data operations to a {@link CombinationLocalDataSource}.
 *
 * @author Justin Wolek
 */
public class CombinationRepository {
    private final CombinationLocalDataSource localDataSource;

    /**
     * Constructs a CombinationRepository with the specified local data source.
     * Can be used for Unit-Testing. Use {@code CombinationRepository.create()} when trying to do
     * regular database operations.
     *
     * @param localDataSource The local data source managing CombinationEntity persistence.
     */
    public CombinationRepository(CombinationLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create a CombinationRepository instance.
     * @param context The Android context used to get the database instance.
     * @return A new instance of {@code CombinationRepository}.
     */
    public static CombinationRepository create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        CombinationDAO dao = db.combinationDAO();
        return new CombinationRepository(new CombinationLocalDataSource(dao));
    }

    /**
     * Retrieves all CombinationEntity objects asynchronously.
     *
     * @param callback The callback to receive the list of all combinations.
     */
    public void getAll(ICallback<List<CombinationEntity>> callback) {
        localDataSource.getAll(callback);
    }

    /**
     * Finds a {@link CombinationEntity} by a combination of two input strings.
     * <p>
     * The input strings are alphabetically ordered to ensure consistent lookups since combinations
     * are always stored in alphabetical order.
     *
     * @param inputA   The first input string of the combination.
     * @param inputB   The second input string of the combination.
     * @param callback The callback to receive the found {@link CombinationEntity}.
     */
    public void findByCombination(String inputA, String inputB, ICallback<CombinationEntity> callback) {
        // Words of combination are always saved in alphabetical order.
        if (inputA.compareTo(inputB) > 0) {
            String temp = inputA;
            inputA = inputB;
            inputB = temp;
        }
        localDataSource.findByCombination(inputA, inputB, callback);
    }

    /**
     * Inserts a new CombinationEntity asynchronously.
     * <p>
     * Ensures that the inputs are alphabetically ordered before insertion
     * to prevent duplicate entries with switched input combinations.
     *
     * @param combination The CombinationEntity to insert.
     */
    public void insertCombination(CombinationEntity combination, Consumer<CombinationEntity> callback) {
        // Order alphabetically when necessary
        if (combination.inputA.compareTo(combination.inputB) > 0) {
            String temp = combination.inputA;
            combination.inputA = combination.inputB;
            combination.inputB = temp;
        }
        localDataSource.insertCombination(combination, callback);
    }

    /**
     * Deletes all CombinationEntity records.
     */
    public void deleteAll() {
        localDataSource.deleteAll();
    }
}
