package de.thm.mixit.data.entities;

/**
 * Represents all savable data from the user Settings
 * <p>
 * {@code language} The language of the Application
 * <br>
 * {@code theme} The theme of teh Application (Light or Dark)
 *
 * @author Jannik Heimann
 */
public class Settings {
    private String language;
    private String theme;

    public Settings(String language, String theme) {
        this.language = language;
        this.theme = theme;
    }

    public String getLanguage() { return this.language; }

    public String getTheme() { return this.theme; }

    public void setLanguage(String language) { this.language = language; }

    public void setTheme(String theme) { this.theme = theme; }

}
