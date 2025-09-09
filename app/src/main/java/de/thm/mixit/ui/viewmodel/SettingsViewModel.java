package de.thm.mixit.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import de.thm.mixit.data.entity.Settings;
import de.thm.mixit.data.repository.SettingsRepository;

/**
 * UI state for the {@link de.thm.mixit.ui.activity.SettingsActivity}
 *
 * Use the {@link SettingsViewModel.Factory} to get a new GameViewModel instance
 *
 * @author Jannik Heimann
 */
public class SettingsViewModel extends ViewModel {
    private final static String TAG = SettingsViewModel.class.getSimpleName();
    private final SettingsRepository settingsRepository;
    private final MutableLiveData<String> language = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();
    private Settings settings;

    /**
     * Use the {@link SettingsViewModel.Factory} to get a new GameViewModel instance
     * @param settingsRepository SettingRepository used for dependency injection
     */
    SettingsViewModel(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        this.settings = settingsRepository.loadSettings();
        this.language.setValue(settings.getLanguage());
        this.theme.setValue(settings.getTheme());
    }

    public LiveData<String> getLanguage() {
        return language;
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public void setLanguage(String language) {
        this.language.postValue(language);
        this.applyLanguage(language);
    }

    public void setTheme(String theme) {
        this.theme.postValue(theme);
        this.applyTheme(theme);
    }

    /**
     * Set the initial language based on the application local
     */
    public void preSelectLanguage() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();

        if (!currentLocales.isEmpty()) {
            String langTag = Objects.requireNonNull(currentLocales.get(0)).toLanguageTag();
            switch (langTag) {
                case "en":
                    setLanguage("en");
                    break;
                case "de":
                    setLanguage("de");
                    break;
                default:
                    setLanguage("system");
                    break;
            }
        } else {
            language.setValue("system");
        }
    }

    /**
     * Set the initial theme based on the application theme
     */
    public void preSelectTheme() {
        int current = AppCompatDelegate.getDefaultNightMode();
        switch (current) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                setTheme("dark");
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                setTheme("light");
                break;
            default:
                setTheme("system");
        }
    }

    /**
     * This Method applies the selected dropdown item to the language setting
     * <p>
     * ref:
     *  <a href="https://developer.android.com/guide/topics/resources/app-languages#androidx-impl">
     *     Android Documentation
     *  </a>
     */
    public void applyLanguage(String langTag) {
        // if local is system, clear locale list if not set it to selected locale
        LocaleListCompat locales = "system".equals(langTag)
                ? LocaleListCompat.getEmptyLocaleList()
                : LocaleListCompat.forLanguageTags(langTag);

        AppCompatDelegate.setApplicationLocales(locales);
    }

    /**
     * This Method applies the selected spinner item to the app theme
     * ref:
     * <a href="https://developer.android.com/develop/ui/views/theming/darktheme#change-themes">
     *     In app dark theme changes
     * </a>
     */
    public void applyTheme(String themeValue) {
        AppCompatDelegate.setDefaultNightMode(
                "light".equals(themeValue) ? AppCompatDelegate.MODE_NIGHT_NO : // Light
                        "dark".equals(themeValue)  ? AppCompatDelegate.MODE_NIGHT_YES : // Dark
                                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System
        );
    }

    public void load() {
        Log.d(TAG, "Loading saved Settings.");
        this.settings = settingsRepository.loadSettings();
        setLanguage(settings.getLanguage());
        setTheme(settings.getTheme());
    }

    public void save() {
        Log.d(TAG, "Saving Settings.");
        assert this.language != null;
        assert this.theme != null;
        settings.setLanguage(this.language.getValue());
        settings.setTheme(this.theme.getValue());
        settingsRepository.saveSettings(settings);
    }

    /**
     * Creates a new {@link SettingsViewModel} instance or returns an existing one
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final SettingsRepository settingsRepository;

        public Factory(Context context) {
            this.settingsRepository = SettingsRepository.create(context);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == SettingsViewModel.class) {
                return (T) new SettingsViewModel(settingsRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
