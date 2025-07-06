package de.thm.mixit.data.source;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.thm.mixit.data.daos.ElementDAO;
import de.thm.mixit.data.entities.Element;

/**
 * Local data source for accessing and modifying {@link Element} data.
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
     *                   on {@link Element} objects.
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
     * @param callback A callback to retrieve the list of {@link Element} objects when
     *                 loading the data is done.
     */
    public void getAll(ICallback<List<Element>> callback) {
        executor.execute(() -> {
            List<Element> elements = elementDAO.getAll();
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
    public void findById(int id, ICallback<Element> callback) {
        executor.execute(() -> {
            Element element = elementDAO.findById(id);
            callback.onDataLoaded(element);
        });
    }

    /**
     * Asynchronously finds an ElementEntity by its name.
     *
     * @param name The name of the element to find.
     * @param callback The callback to receive the found ElementEntity.
     */
    public void findByName(String name, Consumer<Element> callback) {
        executor.execute(() -> {
            Element element = elementDAO.findByName(name);
            callback.accept(element);
        });
    }

    /**
     * Asynchronously inserts an ElementEntity into the database.
     *
     * @param element The ElementEntity to insert.
     */
    public void insertElement(Element element, Consumer<Element> callback) {
        executor.execute(() -> {
            long outputId = elementDAO.insertElement(element);

            Element newElement = elementDAO.findById((int) elementId);

            callback.accept(newElement);
        });
    }

    /**
     * Asynchronously deletes all ElementEntity records from the database.
     */
    public void deleteAll() {
        executor.execute(elementDAO::deleteAll);
    }
}
