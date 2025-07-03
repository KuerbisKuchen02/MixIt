package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.thm.mixit.data.daos.ElementDAO;
import de.thm.mixit.data.entities.ElementEntity;

/**
 * Local data source for accessing and modifying {@link ElementEntity} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * elements, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class ElementLocalDataSource {
    private final ElementDAO elementDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code ElementLocalDataSource} with the given {@link ElementDAO}.
     * @param elementDAO The Data Access Object used to perform database operation
     *                   on {@link ElementEntity} objects.
     */
    public ElementLocalDataSource(ElementDAO elementDAO) {
        this.elementDAO = elementDAO;
    }

    /**
     * Asynchronously retrieves all ElementEntity records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link ICallback} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link ElementEntity} objects when
     *                 loading the data is done.
     */
    public void getAll(ICallback<List<ElementEntity>> callback) {
        executor.execute(() -> {
            List<ElementEntity> elements = elementDAO.getAll();
            callback.onDataLoaded(elements);
        });
    }

    /**
     * Asynchronously finds an ElementEntity by its ID.
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link ICallback} once the data is loaded.
     *
     * @param id The ID of the element to find.
     * @param callback The callback to receive the found ElementEntity.
     */
    public void findById(int id, ICallback<ElementEntity> callback) {
        executor.execute(() -> {
            ElementEntity element = elementDAO.findById(id);
            callback.onDataLoaded(element);
        });
    }

    /**
     * Asynchronously inserts an ElementEntity into the database.
     *
     * @param element The ElementEntity to insert.
     */
    public void insertElement(ElementEntity element) {
        executor.execute(() -> elementDAO.insertElement(element));
    }

    /**
     * Asynchronously deletes all ElementEntity records from the database.
     */
    public void deleteAll() {
        executor.execute(() -> elementDAO.deleteAll());
    }
}
