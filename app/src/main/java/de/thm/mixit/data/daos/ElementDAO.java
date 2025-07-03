package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.ElementEntity;

/**
 * This DAO (data access object) defines methods to be used on {@link ElementEntity}'s.
 *
 * @author Justin Wolek
 */
@Dao
public interface ElementDAO {

    /**
     * Returns all elements inside the database.
     *
     * @return A list of {@link ElementEntity}
     */
    @Query("SELECT * FROM elements")
    public List<ElementEntity> getAll();

    /**
     * Returns one {@link ElementEntity} which has a specific {@code id}.
     *
     * @param id The id the {@link ElementEntity} must have.
     * @return One {@link ElementEntity} which satisfies the condition or {@code null} if none does.
     */
    @Query("SELECT * FROM elements WHERE id LIKE :id")
    public ElementEntity findById(int id);

    /**
     * Inserts one {@link ElementEntity} into the database.
     *
     * @param element ElementEntity to insert into the database.
     */
    @Insert
    public void insertElement(ElementEntity element);

    /**
     * Deletes all {@link ElementEntity}'s from the database.
     */
    @Query("DELETE FROM elements")
    public void deleteAll();
}
