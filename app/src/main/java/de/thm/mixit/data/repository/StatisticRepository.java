package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entity.Statistic;
import de.thm.mixit.data.source.StatisticLocalDataSource;

/**
 * Repository class that provides access to Statistics data.
 * <p>
 * Acts as a single source of truth for Statistics data by delegating
 * data operations to a {@link StatisticLocalDataSource}.
 *
 * @author Jannik Heimann
 */
public class StatisticRepository {

    private final StatisticLocalDataSource localDataSource;

    private StatisticRepository(StatisticLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @return {@link StatisticRepository}
     *
     * @author Jannik Heimann
     */
    public static StatisticRepository create(Context context) {
        return new StatisticRepository(new StatisticLocalDataSource(context));
    }

    /**
     * Loads the last saved Statistic by calling the load Method in the corresponding datasource.
     * @return {@link Statistic}
     *
     * @author Jannik Heimann
     */
    public Statistic loadStatistic() {
        return localDataSource.loadStatistic();
    }

    /**
     * Saves the given Statistic by calling the save Method in the corresponding datasource.
     *
     * @author Jannik Heimann
     */
    public void saveStatistic(Statistic statistic) {
        localDataSource.saveStatistic(statistic);
    }

    /**
     * Deletes the saved Statistic.
     */
    public void deleteSavedStatistic() { localDataSource.deleteSavedStatistic(); }
}
