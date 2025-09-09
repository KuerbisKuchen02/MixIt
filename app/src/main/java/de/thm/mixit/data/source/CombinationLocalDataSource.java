package de.thm.mixit.data.source;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.thm.mixit.BuildConfig;
import de.thm.mixit.data.dao.CombinationDao;
import de.thm.mixit.data.entity.Combination;
import de.thm.mixit.data.exception.CombinationException;
import de.thm.mixit.data.model.Result;

/**
 * Local data source for accessing and modifying {@link Combination} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * combinations, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class CombinationLocalDataSource {

    private static final String TAG = CombinationLocalDataSource.class.getSimpleName();
    private final CombinationDao combinationDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code CombinationLocalDataSource} with the given {@link CombinationDao}.
     * @param combinationDao The data access object used to perform database operation
     *                   on {@link Combination} objects.
     */
    public CombinationLocalDataSource(CombinationDao combinationDao) {
        this.combinationDao = combinationDao;
    }

    /**
     * Asynchronously retrieves all combination records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link Consumer} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link Combination} objects when
     *                 loading the data is done.
     */
    public void getAll(Consumer<List<Combination>> callback) {
        executor.execute(() -> {
            List<Combination> combinations = combinationDao.getAll();
            callback.accept(combinations);
        });
    }

    /**
     * Asynchronously finds a Combination by its combination
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link Consumer} once the data is loaded.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @param callback The callback to receive the found Combination.
     */
    public void findByCombination(String inputA, String inputB,
                                  Consumer<Combination> callback) {
        executor.execute(() -> {
            Combination combination = combinationDao.findByCombination(inputA, inputB);
            callback.accept(combination);
        });
    }

    /**
     * Asynchronously finds the amount of the most occurring output id.
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link Consumer} once the data is loaded.
     *
     * @param callback The callback to receive the found Combination.
     */
    public void getAmountOfMostOccurringOutputId(Consumer<Integer> callback) {
        executor.execute(() -> {
            Integer i = combinationDao.getAmountOfMostOccurringOutputId();
            if (i == null) {
                i = 0;
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "getAmountOfMostOccurringOutputId returned null");
                }
            }
            callback.accept(i);
        });
    }

    /**
     * Asynchronously inserts a Combination into the database.
     *
     * @param combination The Combination to insert.
     */
    public void insertCombination(Combination combination,
                                  Consumer<Result<Combination>> callback) {
        executor.execute(() -> {
            try {
                combinationDao.insertCombination(combination);
                callback.accept(Result.success(combination));
            } catch (SQLiteConstraintException e) {
                callback.accept(Result.failure(
                        new CombinationException("Combination already exists in database!", e)));
            }
        });
    }

    /**
     * Asynchronously deletes all Combination records from the database.
     */
    public void deleteAll() {
        executor.execute(combinationDao::deleteAll);
    }
}
