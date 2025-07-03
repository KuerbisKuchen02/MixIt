package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;

import de.thm.mixit.data.daos.ElementDAO;
import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.source.AppDatabase;
import de.thm.mixit.data.source.ElementLocalDataSource;
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
        ElementDAO dao = db.elementDAO();
        return new ElementRepository(new ElementLocalDataSource(dao));
    }

    /**
     * Retrieves all ElementEntity objects asynchronously.
     *
     * @param callback The callback to receive the list of all elements.
     */
    public void getAll(ICallback<List<ElementEntity>> callback) {
        localDataSource.getAll(callback);
    }

    /**
     * Finds a {@link ElementEntity} by its id.
     *
     * @param id The id of the {@link ElementEntity}.
     * @param callback The callback to receive the found {@link ElementEntity}.
     */
    public void findById(int id, ICallback<ElementEntity> callback) {
       localDataSource.findById(id, callback);
    }

    /**
     * Inserts a new ElementEntity asynchronously.
     *
     * @param element The ElementEntity to insert.
     */
    public void insertElement(ElementEntity element) {
        localDataSource.insertElement(element);
    }

    /**
     * Deletes all ElementEntity records.
     */
    public void deleteAll() {
        localDataSource.deleteAll();
    }
}
