package de.thm.mixit.data.repository;

import android.content.Context;

import java.util.List;

import de.thm.mixit.data.daos.ElementDAO;
import de.thm.mixit.data.entities.ElementEntity;
import de.thm.mixit.data.source.AppDatabase;
import de.thm.mixit.data.source.ElementLocalDataSource;
import de.thm.mixit.data.source.ICallback;

/**
 * @author Justin Wolek
 * @version 1.0.0
 */
public class ElementRepository {
    private final ElementLocalDataSource localDataSource;

    public ElementRepository(ElementLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    public static ElementRepository create(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        ElementDAO dao = db.elementDAO();
        return new ElementRepository(new ElementLocalDataSource(dao));
    }

    public void getAll(ICallback<List<ElementEntity>> callback) {
        localDataSource.getAll(callback);
    }

    public void findById(int id, ICallback<ElementEntity> callback) {
       localDataSource.findById(id, callback);
    }

    public void insertElement(ElementEntity element) {
        localDataSource.insertElement(element);
    }

    public void deleteAll() {
        localDataSource.deleteAll();
    }
}
