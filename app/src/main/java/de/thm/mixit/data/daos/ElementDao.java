package de.thm.mixit.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.thm.mixit.data.entities.Element;

/**
 * This DAO (data access object) defines methods to be used on {@link Element}'s.
 *
 * @author Justin Wolek
 */
@Dao
public interface ElementDao {

    /**
     * Returns all elements inside the database.
     *
     * @return A list of {@link Element}
     */
    @Query("SELECT * FROM elements")
    List<Element> getAll();

    /**
     * Returns one {@link Element} which has a specific {@code id}.
     *
     * @param id The id the {@link Element} must have.
     * @return One {@link Element} which satisfies the condition
     * or {@code null} if none does.
     */
    @Query("SELECT * FROM elements WHERE id LIKE :id")
    Element findById(int id);

    /**
     * Returns one {@link Element} which has a specific {@code name}.
     *
     * @param name The name the {@link Element} must have.
     * @return One {@link Element} which
     */
    @Query("SELECT * FROM elements WHERE LOWER(name) = LOWER(:name)")
    Element findByName(String name);

    /**
     * Inserts one {@link Element} into the database.
     *
     * @param element Element to insert into the database.
     */
    @Insert
    long insertElement(Element element);

    /**
     * Deletes all {@link Element}'s from the database.
     */
    @Query("DELETE FROM elements")
    void deleteAll();
}
