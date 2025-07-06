package de.thm.mixit.data.source;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.thm.mixit.data.daos.CombinationDao;
import de.thm.mixit.data.daos.ElementDao;
import de.thm.mixit.data.entities.Combination;
import de.thm.mixit.data.entities.Element;

/**
 * Abstract Room database class
 * <p>
 * Contains the database configuration including a list of entities and versions.
 *
 * @author Justin Wolek
 * @version 1.0.0
 */
@Database(entities = {Element.class, Combination.class},
        version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    /**
     * Room Databases are fairly expensive. Therefore, use the Singleton pattern to
     * only create one instance of AppDatabase.
     * @param context The application context.
     * @return An AppDatabase object allowing access to the SQLite database
     */
    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            synchronized (AppDatabase.class) {
                db = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "local-db"
                )
                        .fallbackToDestructiveMigration(true)
                        .build();
            }
        }
        return db;
    }

    public abstract ElementDao elementDAO();

    public abstract CombinationDao combinationDAO();
}
