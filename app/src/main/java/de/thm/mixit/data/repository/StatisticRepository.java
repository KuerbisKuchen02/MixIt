package de.thm.mixit.data.repository;

import android.content.Context;

import de.thm.mixit.data.entities.Statistic;
import de.thm.mixit.data.source.StatisticDataSource;

/**
 * Repository class that provides access to Statistics data.
 * <p>
 * Acts as a single source of truth for Statistics data by delegating
 * data operations to a {@link StatisticDataSource}.
 *
 * @author Jannik Heimann
 */
public class StatisticRepository {

    private final StatisticDataSource datasource;

    private StatisticRepository(StatisticDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Method to create an instance of the class.
     * @param context Context of the Android application.
     * @return {@link StatisticRepository}
     *
     * @author Jannik Heimann
     */
    public static StatisticRepository create(Context context) {
        return new StatisticRepository(new StatisticDataSource(context));
    }

    /**
     * Loads the last saved Statistic by calling the load Method in the corresponding datasource.
     * @return {@link Statistic}
     *
     * @author Jannik Heimann
     */
    public Statistic loadStatistic() {
        return datasource.loadStatistic();
    }

    /**
     * Saves the given Statistic by calling the save Method in the corresponding datasource.
     *
     * @author Jannik Heimann
     */
    public void saveStatistic(Statistic statistic) {
        datasource.saveStatistic(statistic);
    }

    /**
     * Whether there is an existing saved Statistic.
     * @return boolean
     *
     * @author Jannik Heimann
     */
    public boolean hasSavedStatistic() { return datasource.hasSavedStatistic(); }

    /**
     * Deletes the saved Statistic.
     */
    public void deleteSavedStatistic() { datasource.deleteSavedStatistic(); }
}
