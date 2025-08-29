package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.thm.mixit.data.entities.Achievement;
import de.thm.mixit.data.entities.BinaryAchievement;
import de.thm.mixit.data.entities.ProgressAchievement;
import de.thm.mixit.data.repository.AchievementRepository;

/**
 * UI state for the {@link de.thm.mixit.ui.activities.AchievementActivity}
 *
 * Use the {@link AchievementViewModel.Factory} to get a new AchievementViewModel instance
 *
 * @author Justin Wolek
 */
public class AchievementViewModel extends ViewModel {
    private final static String TAG = AchievementViewModel.class.getSimpleName();
    private final AchievementRepository achievementRepository;
    private final MutableLiveData<List<Achievement>> achievements;
    private final MutableLiveData<Integer> sizeOfUnlockedAchievement = new MutableLiveData<>();
    private final MutableLiveData<Integer> sizeOfAllAchievements = new MutableLiveData<>();

    /**
     * Use the {@link AchievementViewModel.Factory} to get a new GameViewModel instance
     * @param achievementRepository AchievementRepository used for dependency injection
     */
    private AchievementViewModel(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
        this.achievements = new MutableLiveData<>();

        loadAchievements();

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
                Objects.requireNonNull(achievements.getValue()).toString());
        achievementRepository.saveAchievements(achievements.getValue());
    }

    /**
     * Tries to load all achievements from the AchievementRepository.
     * When no achievements could be loaded, initialise them.
     */
    private void loadAchievements() {
        achievements.setValue(achievementRepository.loadAchievements());
        Log.d(TAG, "Loaded achievements: " +
                Objects.requireNonNull(achievements.getValue()).toString());

        // No achievements saved yet. Initialise them the first time
        if (achievements.getValue().isEmpty()) {
            initAchievements();
        }
    }

    /**
     * Initialise a list of predefined achievements.
     * Should only be called once when no achievements are present on the device.
     */
    private void initAchievements() {
        Log.d(TAG, "Initialising achievements");

        achievements.setValue(Arrays.asList(
                new ProgressAchievement("Hooked", "Play 1 hour", 0, 1),
                new ProgressAchievement("Addicted", "Play 3 hours", 0, 3),
                new ProgressAchievement("Get a life", "Play 5 hours", 0, 5),
                new ProgressAchievement("Know the drill", "Combine 200 elements", 0, 200),
                new ProgressAchievement("The journey begins", "Discover 10 new elements", 0, 10),
                new ProgressAchievement("Word collector", "Discover 100 new elements", 0, 100),
                new ProgressAchievement("I like it clean", "Delete 50 elements from the playground", 0, 50),
                new ProgressAchievement("Challenge accepted!", "Win 10 arcade games", 0, 10),
                new ProgressAchievement("Multiple recipes", "Get 5 combinations for a single element", 0, 15),
                new BinaryAchievement("Winner ", "Win your first arcade game", false),
                new BinaryAchievement("Fast as fuck boy!", "Win a arcade game in under 1 minute", false),
                new BinaryAchievement("Mastermind", "Win an arcade game in less then 10 turns.", false),
                new BinaryAchievement("Kärcher", "Delete 10 elements at once", false),
                new BinaryAchievement("How did we get here?", "Discover a word containing at least 25 characters", false),
                new BinaryAchievement("The cake is a lie", "Discover chocolate cake", false)
        ));
    }

    /**
     * Creates a new {@link AchievementViewModel} instance or returns an existing one
     * @author Justin Wolek
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final AchievementRepository achievementRepository;

        public Factory(Context context) {
            this.achievementRepository = AchievementRepository.create(context);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == AchievementViewModel.class) {
                return (T) new AchievementViewModel(achievementRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
