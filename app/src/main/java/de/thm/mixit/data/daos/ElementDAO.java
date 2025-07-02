package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.ElementEntity;

/**
 * @author Justin Wolek
 * @version 1.0.0
 */
@Dao
public interface ElementDAO {
    @Query("SELECT * FROM elements")
    public List<ElementEntity> getAll();

    @Query("SELECT * FROM elements WHERE id LIKE :id")
    public ElementEntity findById(int id);

    @Insert
    public void insertElement(ElementEntity element);

    @Query("DELETE FROM elements")
    public void deleteAll();
}
