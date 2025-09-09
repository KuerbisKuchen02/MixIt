package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.thm.mixit.R;
import de.thm.mixit.data.entity.Achievement;
import de.thm.mixit.data.entity.BinaryAchievement;
import de.thm.mixit.data.entity.ProgressAchievement;
import de.thm.mixit.data.entity.Statistic;
import de.thm.mixit.data.repository.AchievementRepository;
import de.thm.mixit.data.repository.StatisticRepository;

/**
 * UI state for the {@link de.thm.mixit.ui.activity.AchievementActivity}
 *
 * Use the {@link AchievementViewModel.Factory} to get a new AchievementViewModel instance
 *
 * @author Justin Wolek
 */
public class AchievementViewModel extends ViewModel {
    private final static String TAG = AchievementViewModel.class.getSimpleName();
    private final AchievementRepository achievementRepository;
    private final StatisticRepository statisticRepository;
    private final MutableLiveData<List<Achievement>> achievements;
    private final MutableLiveData<Integer> sizeOfUnlockedAchievement = new MutableLiveData<>();
    private final MutableLiveData<Integer> sizeOfAllAchievements = new MutableLiveData<>();

    /**
     * Use the {@link AchievementViewModel.Factory} to get a new GameViewModel instance
     * @param achievementRepository AchievementRepository used for dependency injection
     */
    private AchievementViewModel(AchievementRepository achievementRepository,
                                 StatisticRepository statisticRepository) {
        this.achievementRepository = achievementRepository;
        this.achievements = new MutableLiveData<>();
        this.statisticRepository = statisticRepository;

        loadAchievements();
        updateProgress();

        int counter = 0;
        for (Achievement a : Objects.requireNonNull(achievements.getValue())) {
            if (a.isUnlocked()) counter++;
        }
        this.sizeOfUnlockedAchievement.setValue(counter);
        this.sizeOfAllAchievements.setValue(achievements.getValue().size());
    }

    /**
     * Gets the all achievements, unlocked or not unlocked
     * @return  A list of all achievements
     */
    public MutableLiveData<List<Achievement>> getAchievements() {
        return achievements;
    }

    /**
     * Gets the number of unlocked achievements
     * @return The amount of unlocked achievements
     */
    public MutableLiveData<Integer> getSizeOfUnlockedAchievement() {
        return sizeOfUnlockedAchievement;
    }

    /**
     * Gets the total number of achievements
     * @return  The amount of all achievements
     */
    public MutableLiveData<Integer> getSizeOfAllAchievement() {
        return sizeOfAllAchievements;
    }

    /**
     * Saves the current achievements on the device.
     */
    public void saveAchievements() {
        Log.d(TAG, "Saving achievements: " +
                Objects.requireNonNull(achievements.getValue()));
        achievementRepository.saveAchievements(achievements.getValue());
    }

    /**
     * Tries to load all achievements from the AchievementRepository.
     * When no achievements could be loaded, initialise them.
     */
    private void loadAchievements() {
        // Sort the list so unlocked achievements are always last.
        List<Achievement> achievementsList = achievementRepository.loadAchievements();
        achievements.setValue(achievementsList);

        Log.d(TAG, "Loaded achievements: " +
                Objects.requireNonNull(achievements.getValue()));

        // No achievements saved yet. Initialise them the first time
        if (achievementsList.isEmpty()) {
            initAchievements();
        }
    }

    /**
     * Update the progress of each achievement based on the saved statistics.
     */
    private void updateProgress() {
        Statistic statistic = statisticRepository.loadStatistic();
        int nameId;
        for (Achievement achievement: Objects.requireNonNull(achievements.getValue())) {
            nameId = achievement.getNameResId();
            if (nameId == R.string.achievement_name_hooked ||
                    nameId == R.string.achievement_name_addicted ||
                    nameId == R.string.achievement_name_get_a_life)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        (int) statistic.getPlaytime());

            else if (nameId == R.string.achievement_name_know_the_drill)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        (int) statistic.getNumberOfCombinations());

            else if (nameId == R.string.achievement_name_the_journey_begins ||
            nameId == R.string.achievement_name_word_collector)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        statistic.getNumberOfUnlockedElements() - 4);

            else if (nameId == R.string.achievement_name_I_like_it_clean)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        (int) statistic.getNumberOfDiscardedElements());

            else if (nameId == R.string.achievement_name_challenge_accepted)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        statistic.getArcadeGamesWon());

            else if (nameId == R.string.achievement_name_again_really)
                ((ProgressAchievement) achievement).setCurrentProgress(
                        statistic.getMostCombinationsForOneElement());

            else if (nameId == R.string.achievement_name_winner)
                ((BinaryAchievement) achievement).setUnlocked(statistic.getArcadeGamesWon() > 0);

            else if (nameId == R.string.achievement_name_fast_as_fuck_boy)
                ((BinaryAchievement) achievement).setUnlocked(
                        statistic.getShortestArcadeTimeToBeat() < 60);

            else if (nameId == R.string.achievement_name_mastermind)
                ((BinaryAchievement) achievement).setUnlocked(
                        statistic.getFewestArcadeTurnsToBeat() < 10);

            else if (nameId == R.string.achievement_name_kaercher)
                ((BinaryAchievement) achievement).setUnlocked(
                        statistic.getMostDiscardedElements() > 10);

            else if (nameId == R.string.achievement_name_how_did_we_get_here)
                ((BinaryAchievement) achievement).setUnlocked(
                        statistic.getLongestElement().length() >= 20);

            else if (nameId == R.string.achievement_name_the_cake_is_a_lie)
                ((BinaryAchievement) achievement).setUnlocked(
                        statistic.getFoundChocolateCake());
        }

        List<Achievement> achievementsList = achievements.getValue();
        Collections.sort(achievementsList);
        achievements.setValue(achievementsList);
    }

    /**
     * Initialise a list of predefined achievements.
     * Should only be called once when no achievements are present on the device.
     */
    private void initAchievements() {
        Log.d(TAG, "Initialising achievements");
        achievements.setValue(Arrays.asList(
                new ProgressAchievement(0, R.string.achievement_name_hooked,
                        R.string.achievement_desc_hooked, 0, 3600),
                new ProgressAchievement(1, R.string.achievement_name_addicted,
                        R.string.achievement_desc_addicted, 0, 3 * 3600),
                new ProgressAchievement(2, R.string.achievement_name_get_a_life,
                        R.string.achievement_desc_get_a_life, 0, 5 * 3600),
                new ProgressAchievement(3, R.string.achievement_name_know_the_drill,
                        R.string.achievement_desc_know_the_drill, 0, 200),
                new ProgressAchievement(4, R.string.achievement_name_the_journey_begins,
                        R.string.achievement_desc_the_journey_begins, 0, 10),
                new ProgressAchievement(5, R.string.achievement_name_word_collector,
                        R.string.achievement_desc_word_collector, 0, 100),
                new ProgressAchievement(6, R.string.achievement_name_I_like_it_clean,
                        R.string.achievement_desc_I_like_it_clean, 0, 50),
                new ProgressAchievement(7, R.string.achievement_name_challenge_accepted,
                        R.string.achievement_desc_challenge_accepted, 0, 10),
                new ProgressAchievement(8, R.string.achievement_name_again_really,
                        R.string.achievement_desc_again_really, 0, 15),
                new BinaryAchievement(9, R.string.achievement_name_winner,
                        R.string.achievement_desc_winner, false),
                new BinaryAchievement(10, R.string.achievement_name_fast_as_fuck_boy,
                        R.string.achievement_desc_fast_as_fuck_boy, false),
                new BinaryAchievement(11, R.string.achievement_name_mastermind,
                        R.string.achievement_desc_mastermind, false),
                new BinaryAchievement(12, R.string.achievement_name_kaercher,
                        R.string.achievement_desc_kaercher, false),
                new BinaryAchievement(13, R.string.achievement_name_how_did_we_get_here,
                        R.string.achievement_desc_how_did_we_get_here, false),
                new BinaryAchievement(14, R.string.achievement_name_the_cake_is_a_lie,
                        R.string.achievement_desc_the_cake_is_a_lie, false)
        ));
    }

    /**
     * Creates a new {@link AchievementViewModel} instance or returns an existing one
     * @author Justin Wolek
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final AchievementRepository achievementRepository;
        private final StatisticRepository statisticRepository;

        public Factory(Context context) {
            this.achievementRepository = AchievementRepository.create(context);
            this.statisticRepository = StatisticRepository.create(context);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == AchievementViewModel.class) {
                return (T) new AchievementViewModel(achievementRepository, statisticRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
