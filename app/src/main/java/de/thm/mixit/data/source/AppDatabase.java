package de.thm.mixit.data.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import de.thm.mixit.data.dao.CombinationDao;
import de.thm.mixit.data.dao.ElementDao;
import de.thm.mixit.data.entity.Combination;
import de.thm.mixit.data.entity.Element;

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

    /**
     * Room databases are fairly expensive. Therefore, use the Singleton pattern to
     * only create one instance of AppDatabase for each game mode.
     * @param context The application context.
     * @param isArcade Whether the db is containing data of endless or arcade mode.
     * @return An AppDatabase object allowing access to the SQLite database
     */
    public static AppDatabase getInstance(Context context, boolean isArcade) {
        AppDatabase db;
        String db_name = isArcade ? "local-arcade-db" : "local-endless-db";
        synchronized (AppDatabase.class) {
            db = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            db_name
                    )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(new RoomDatabase.Callback() {
                        // OnCreate is called whenever the app is freshly installed.
                        // In that case populate the database with the initial four elements.
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            resetDatabase(db);
                        }
                    })
                    .build();
        }
        return db;
    }

    /**
     * Deletes all {@link Combination}s and {@link Element}s and populate the database with the
     * initial four elements. Should only be called when the app has been freshly installed
     * inside {@code onCreate()}.
     */
    private static void resetDatabase(SupportSQLiteDatabase db) {
        // DAOs can not be used here because this would recursively call getDatabase.
        // Use raw SQL instead.
        db.execSQL("DELETE FROM elements");
        db.execSQL("DELETE FROM combinations");
        db.execSQL("INSERT INTO elements (name, emoji) VALUES ('Wasser', '💧');");
        db.execSQL("INSERT INTO elements (name, emoji) VALUES ('Erde', '🌍');");
        db.execSQL("INSERT INTO elements (name, emoji) VALUES ('Feuer', '🔥');");
        db.execSQL("INSERT INTO elements (name, emoji) VALUES ('Luft', '🌬️');");
    }

    public abstract ElementDao elementDAO();

    public abstract CombinationDao combinationDAO();
}
