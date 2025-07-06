package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;
import java.util.function.Consumer;

import de.thm.mixit.data.daos.ElementDao;
import de.thm.mixit.data.entities.Element;
import de.thm.mixit.data.source.AppDatabase;
import de.thm.mixit.data.source.ElementLocalDataSource;
import de.thm.mixit.data.source.ElementRemoteDataSource;
import de.thm.mixit.data.source.ICallback;

/**
 * Repository class that provides access to Element data.
 * <p>
 * Acts as a single source of truth for Element data by delegating
 * data operations to a {@link ElementLocalDataSource}.
 *
 * @author Justin Wolek
 */
public class ElementRepository {
    private final ElementLocalDataSource localDataSource;

    /**
     * Constructs an ElementRepository with the specified local data source.
     * Can be used for Unit-Testing. Use {@code ElementRepository.create()} when trying to do
     * regular database operations.
     *
     * @param localDataSource The local data source managing ElementEntity persistence.
     */
    public ElementRepository(ElementLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create an ElementRepository instance.
     * @param context The Android context used to get the database instance.
     * @return A new instance of {@code ElementRepository}.
     */
    public static ElementRepository create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        ElementDao dao = db.elementDAO();
        return new ElementRepository(new ElementLocalDataSource(dao));
    }

    /**
     * Generates a new ElementEntity by combining two existing elements.
     *
     * @param element1 The first ElementEntity to combine.
     * @param element2 The second ElementEntity to combine.
     * @param callback A callback to receive the newly generated ElementEntity.
     */
    public void generateNew(String element1, String element2,
                            Consumer<Element> callback) {
        // TODO - implement error handling for remote data source
        ElementRemoteDataSource.combine(element1, element2, callback);
    }

    /**
     * Finds an ElementEntity by its name asynchronously.
     *
     * @param name The name of the ElementEntity to find.
     * @param callback The callback to receive the found ElementEntity.
     */
    public void findByName(String name, Consumer<Element> callback) {
        localDataSource.findByName(name, callback);
    }

    /**
     * Retrieves all ElementEntity objects asynchronously.
     *
     * @param callback The callback to receive the list of all elements.
     */
    public void getAll(ICallback<List<Element>> callback) {
        localDataSource.getAll(callback);
    }

    /**
     * Finds a {@link Element} by its id.
     *
     * @param id The id of the {@link Element}.
     * @param callback The callback to receive the found {@link Element}.
     */
    public void findById(int id, ICallback<Element> callback) {
       localDataSource.findById(id, callback);
    }

    /**
     * Inserts a new ElementEntity asynchronously.
     *
     * @param element The ElementEntity to insert.
     */
    public void insertElement(Element element, Consumer<Element> callback) {
        localDataSource.insertElement(element, callback);
    }

    /**
     * Deletes all ElementEntity records.
     */
    public void deleteAll() {
        localDataSource.deleteAll();
    }
}
