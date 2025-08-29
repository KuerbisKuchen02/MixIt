package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.thm.mixit.data.daos.CombinationDao;
import de.thm.mixit.data.entities.Combination;

/**
 * Local data source for accessing and modifying {@link Combination} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * combinations, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class CombinationLocalDataSource {
    private final CombinationDao combinationDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code CombinationLocalDataSource} with the given {@link CombinationDao}.
     * @param combinationDAO The Data Access Object used to perform database operation
     *                   on {@link Combination} objects.
     */
    public CombinationLocalDataSource(CombinationDao combinationDAO) {
        this.combinationDAO = combinationDAO;
    }

    /**
     * Asynchronously retrieves all Combination records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link Consumer} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link Combination} objects when
     *                 loading the data is done.
     */
    public void getAll(Consumer<List<Combination>> callback) {
        executor.execute(() -> {
            List<Combination> combinations = combinationDAO.getAll();
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
            Combination combination = combinationDAO.findByCombination(inputA, inputB);
            callback.accept(combination);
        });
    }

    /**
     * Asynchronously finds the Amount of the most occurring output id.
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link Consumer} once the data is loaded.
     *
     * @param callback The callback to receive the found Combination.
     */
    public void getAmountOfMostOccurringOutputId(Consumer<Integer> callback) {
        executor.execute(() -> {
           callback.accept(combinationDAO.getAmountOfMostOccurringOutputId());
        });
    }

    /**
     * Asynchronously inserts a Combination into the database.
     *
     * @param combination The Combination to insert.
     */
    public void insertCombination(Combination combination,
                                  Consumer<Combination> callback) {
        executor.execute(() -> {
            combinationDAO.insertCombination(combination);
            callback.accept(combination);
        });
    }

    /**
     * Asynchronously deletes all Combination records from the database.
     */
    public void deleteAll() {
        executor.execute(combinationDAO::deleteAll);
    }
}
