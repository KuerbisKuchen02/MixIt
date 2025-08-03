package de.thm.mixit.data.source;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.thm.mixit.data.dao.ElementDao;
import de.thm.mixit.data.entity.Element;

/**
 * Local data source for accessing and modifying {@link Element} data.
 * <p>
 * This class handles all interactions with the local Room database related to
 * elements, including asynchronous reads and writes using a background thread.
 *
 * @author Justin Wolek
 */
public class ElementLocalDataSource {
    private final ElementDao elementDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new {@code ElementLocalDataSource} with the given {@link ElementDao}.
     * @param elementDAO The Data Access Object used to perform database operation
     *                   on {@link Element} objects.
     */
    public ElementLocalDataSource(ElementDao elementDAO) {
        this.elementDAO = elementDAO;
    }

    /**
     * Asynchronously retrieves all Element records from the database.
     * <p>
     * The query runs on a background thread, and the results are delivered
     * via the provided {@link Consumer} interface once loading is complete.
     *
     * @param callback A callback to retrieve the list of {@link Element} objects when
     *                 loading the data is done.
     */
    public void getAll(Consumer<List<Element>> callback) {
        executor.execute(() -> {
            List<Element> elements = elementDAO.getAll();
            callback.accept(elements);
        });
    }

    /**
     * Asynchronously finds an Element by its ID.
     * <p>
     * The query runs on a background thread, and the result is delivered
     * via the provided {@link Consumer} once the data is loaded.
     *
     * @param id The ID of the element to find.
     * @param callback The callback to receive the found Element.
     */
    public void findById(int id, Consumer<Element> callback) {
        executor.execute(() -> {
            Element element = elementDAO.findById(id);
            callback.accept(element);
        });
    }

    /**
     * Asynchronously finds an Element by its name.
     *
     * @param name The name of the element to find.
     * @param callback The callback to receive the found Element.
     */
    public void findByName(String name, Consumer<Element> callback) {
        executor.execute(() -> {
            Element element = elementDAO.findByName(name);
            callback.accept(element);
        });
    }

    /**
     * Asynchronously inserts an Element into the database.
     *
     * @param element The Element to insert.
     */
    public void insertElement(Element element, Consumer<Element> callback) {
        executor.execute(() -> {
            long elementId = elementDAO.insertElement(element);

            Element newElement = elementDAO.findById((int) elementId);

            callback.accept(newElement);
        });
    }

    /**
     * Asynchronously deletes all Elements and recreates the four starter-elements.
     */
    public void reset() {
        executor.execute(() -> {
            elementDAO.deleteAll();
            List<Element> elements = Arrays.asList(
                    new Element("Wasser", "\uD83D\uDCA7"),
                    new Element("Erde", "\uD83C\uDF0D"),
                    new Element("Feuer", "\uD83D\uDD25"),
                    new Element("Luft", "\uD83C\uDF2C\uFE0F")
            );
            elementDAO.insertAll(elements);
        });
    }

    /**
     * Asynchronously deletes all Element records from the database.
     */
    public void deleteAll() {
        executor.execute(elementDAO::deleteAll);
    }
}
