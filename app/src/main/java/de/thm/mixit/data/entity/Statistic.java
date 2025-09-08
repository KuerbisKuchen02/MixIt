package de.thm.mixit.data.entity;


import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents all collectable data of the Arcade and Endless Game mode.
 * It also serves as raw data used to track achievement progress.
 * <p>
 * {@code playtime} Total passed playtime.
 * <br>
 * {@code numberOfCombinations} Total number of combinations made while playing.
 * <br>
 * {@code longestElement} Longest element created by combining.
 * <br>
 * {@code numberOfUnlockedElements} Total number of unlocked Elements.
 * <br>
 * {@code totalDiscardedElements} Total number of discarded Elements.
 * <br>
 * {@code mostDiscardedElements} Max number of discarded elements in one time.
 * <br>
 * {@code mostCombinationsForOneElement} Max unlocked combinations to create the same element.
 * <br>
 *  {@code arcadeGamesWon} Number of arcade games won by the player.
 *  <br>
 *  {@code shortestArcadeTimeToBeat} Fastest time the player beat the arcade game.
 *  <br>
 *  {@code fewestArcadeTurnsToBeat} Fewest turns needed by the player to beat the arcade mode.
 *
 * @author Jannik Heimann
 */

public class Statistic {

    private static final int NUMBER_OF_GOAL_WORDS_SAVED = 20;
    private long playtime;
    private long numberOfCombinations;
    private String longestElement;
    private int numberOfUnlockedElements;
    private long numberOfDiscardedElements;
    private int mostDiscardedElements;
    private int mostCombinationsForOneElement;
    private int arcadeGamesWon;
    private long shortestArcadeTimeToBeat;
    private int fewestArcadeTurnsToBeat;
    private boolean foundChocolateCake;

    private final List<String> lastTargetWords;

    public Statistic(long playtime,
                     long numberOfCombinations,
                     String longestElement,
                     int numberOfUnlockedElements,
                     long numberOfDiscardedElements,
                     int mostDiscardedElements,
                     int mostCombinationsForOneElement,
                     int arcadeGamesWon,
                     long shortestArcadeTimeToBeat,
                     int fewestArcadeTurnsToBeat,
                     boolean foundChocolateCake,
                     List<String> lastTargetWords) {
        this.playtime = playtime;
        this.numberOfCombinations = numberOfCombinations;
        this.longestElement = longestElement;
        this.numberOfUnlockedElements = numberOfUnlockedElements;
        this.numberOfDiscardedElements = numberOfDiscardedElements;
        this.mostDiscardedElements = mostDiscardedElements;
        this.mostCombinationsForOneElement = mostCombinationsForOneElement;
        this.arcadeGamesWon = arcadeGamesWon;
        this.shortestArcadeTimeToBeat = shortestArcadeTimeToBeat;
        this.fewestArcadeTurnsToBeat = fewestArcadeTurnsToBeat;
        this.foundChocolateCake = foundChocolateCake;
        this.lastTargetWords = lastTargetWords;
    }

    public long getPlaytime() {
        return this.playtime;
    }

    public long getNumberOfCombinations() {
        return this.numberOfCombinations;
    }

    public String getLongestElement() {
        return this.longestElement;
    }

    public long getNumberOfDiscardedElements() { return this.numberOfDiscardedElements; }

    public int getNumberOfUnlockedElements() {
        return this.numberOfUnlockedElements;
    }

    public int getMostDiscardedElements() {
        return this.mostDiscardedElements;
    }

    public int getMostCombinationsForOneElement() {
        return this.mostCombinationsForOneElement;
    }

    public int getArcadeGamesWon() { return this.arcadeGamesWon; }

    public long getShortestArcadeTimeToBeat() { return this.shortestArcadeTimeToBeat; }

    public int getFewestArcadeTurnsToBeat() { return this.fewestArcadeTurnsToBeat; }

    public boolean getFoundChocolateCake() { return this.foundChocolateCake; }

    public List<String> getLastTargetWords() {
        return lastTargetWords;
    }

    public void addPlaytime(long playTime) {
        this.playtime += playTime;
    }

    public void setNumberOfCombinations(long numberOfCombinations) {
        this.numberOfCombinations = numberOfCombinations;
    }

    public void setLongestElement(String longestElement) {
        if (longestElement.length() > this.longestElement.length())
            this.longestElement = longestElement;
    }

    public void setNumberOfUnlockedElements(int numberOfUnlockedElements) {
        // If statement needed, because max number could be from arcade or endless mode
        if (numberOfUnlockedElements > this.numberOfUnlockedElements)
            this.numberOfUnlockedElements = numberOfUnlockedElements;
    }

    public void setNumberOfDiscardedElements(long numberOfDiscardedElements) {
        this.numberOfDiscardedElements = numberOfDiscardedElements;
    }

    public void setMostDiscardedElements(int mostDiscardedElements) {
        if (mostDiscardedElements > this.mostDiscardedElements)
            this.mostDiscardedElements = mostDiscardedElements;
    }

    public void setMostCombinationsForOneElement(int mostCombinationsForOneElement) {
        if (mostCombinationsForOneElement > this.mostCombinationsForOneElement)
            this.mostCombinationsForOneElement = mostCombinationsForOneElement;
    }

    public void setArcadeGamesWon(int numberWon) {
        this.arcadeGamesWon = numberWon;
    }

    public void setShortestArcadeTimeToBeat(long time) {
        if (time < shortestArcadeTimeToBeat)
            this.shortestArcadeTimeToBeat = time;
    }

    public void setFewestArcadeTurnsToBeat(int turns) {
        if (turns < fewestArcadeTurnsToBeat)
            this.fewestArcadeTurnsToBeat = turns;
    }

    public void setFoundChocolateCake(boolean foundChocolateCake) {
        this.foundChocolateCake = foundChocolateCake;
    }

    public void addTargetWord(String targetWords) {
        lastTargetWords.add(targetWords);
        if (lastTargetWords.size() > NUMBER_OF_GOAL_WORDS_SAVED) lastTargetWords.remove(0);
    }

    /**
     * Override for Debug Purposes
     */
    @NonNull
    @Override
    public String toString() {
        return "Played Time: " + playtime + "\n" +
                "Number of Combinations: " + numberOfCombinations + "\n" +
                "Longest Element: " + longestElement + "\n" +
                "Number of unlocked Elememts: " + numberOfUnlockedElements + "\n" +
                "Total Discarded Elements: " +  numberOfDiscardedElements + "\n" +
                "Most Discarded Elements: " + mostDiscardedElements + "\n" +
                "Most Combinations for one Element: " + mostCombinationsForOneElement + "\n" +
                "Arcade Games Won: " + arcadeGamesWon + "\n" +
                "Shortest Arcade Playtime: " + shortestArcadeTimeToBeat + "\n" +
                "Fewest Arcade Turns: " + fewestArcadeTurnsToBeat + "\n" +
                "Discovered Chocolate Cake: " + foundChocolateCake + "\n" +
                "Last " + NUMBER_OF_GOAL_WORDS_SAVED + " target words: " + lastTargetWords.toString();
    }
}
