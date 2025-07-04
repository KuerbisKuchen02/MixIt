package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.thm.mixit.data.daos.CombinationDAO;
import de.thm.mixit.data.entities.CombinationEntity;

/**
 * Local data source for accessing and modifying {@link CombinationEntity} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * combinations, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class CombinationLocalDataSource {
    private final CombinationDAO combinationDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code CombinationLocalDataSource} with the given {@link CombinationDAO}.
     * @param combinationDAO The Data Access Object used to perform database operation
     *                   on {@link CombinationEntity} objects.
     */
    public CombinationLocalDataSource(CombinationDAO combinationDAO) {
        this.combinationDAO = combinationDAO;
    }

    /**
     * Asynchronously retrieves all CombinationEntity records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link ICallback} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link CombinationEntity} objects when
     *                 loading the data is done.
     */
    public void getAll(ICallback<List<CombinationEntity>> callback) {
        executor.execute(() -> {
            List<CombinationEntity> combinations = combinationDAO.getAll();
            callback.onDataLoaded(combinations);
        });
    }

    /**
     * Asynchronously finds a CombinationEntity by its combination
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link ICallback} once the data is loaded.
     *
     * @param inputA The first input string.
     * @param inputB The second input string.
     * @param callback The callback to receive the found CombinationEntity.
     */
    public void findByCombination(String inputA, String inputB, ICallback<CombinationEntity> callback) {
        executor.execute(() -> {
            CombinationEntity combination = combinationDAO.findByCombination(inputA, inputB);
            callback.onDataLoaded(combination);
        });
    }

    /**
     * Asynchronously inserts a CombinationEntity into the database.
     *
     * @param combination The CombinationEntity to insert.
     */
    public void insertCombination(CombinationEntity combination, Consumer<CombinationEntity> callback) {
        executor.execute(() -> {
            combinationDAO.insertCombination(combination);
            callback.accept(combination);
        });
    }

    /**
     * Asynchronously deletes all CombinationEntity records from the database.
     */
    public void deleteAll() {
        executor.execute(() -> combinationDAO.deleteAll());
    }
}
