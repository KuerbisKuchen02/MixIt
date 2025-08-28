package de.thm.mixit.data.entities;


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
 * {@code arcadeGamesPlayed} Number of arcade games played by the player.
 * <br>
 *  {@code arcadeGamesWon} Number of arcade games won by the player.
 *  <br>
 *  {@code shortestArcadeTimeToBeat} Fastest time the player beat the arcade game.
 *  <br>
 *  {@code fewestArcadeTurnsToBeat} Fewest turns needed by the player to beat the arcade mode.
 *
 * @author Jannik Heimann
 */
// TODO clear comments as soon as the corresponding saving of data is done
public class Statistic {
    private long playtime;
    private long numberOfCombinations;
    private String longestElement;
    private int numberOfUnlockedElements;       // In Element UseCase getElement, could return bool, alternative also db query at closing
    private long numberOfDiscardedElements;
    private int mostDiscardedElements;
    private int mostCombinationsForOneElement;  // Needs an additional db query, implement in GameViewModel saveStatistic
    private int arcadeGamesPlayed;              // Missing Win Detection in this Branch
    private int arcadeGamesWon;                 // Missing Win Detection in this Branch
    private long shortestArcadeTimeToBeat;      // Missing Win Detection in this Branch
    private int fewestArcadeTurnsToBeat;        // Missing Win Detection in this Branch


    public Statistic(long playtime,
                     long numberOfCombinations,
                     String longestElement,
                     int numberOfUnlockedElements,
                     long numberOfDiscardedElements,
                     int mostDiscardedElements,
                     int mostCombinationsForOneElement,
                     int arcadeGamesPlayed,
                     int arcadeGamesWon,
                     long shortestArcadeTimeToBeat,
                     int fewestArcadeTurnsToBeat) {
        this.playtime = playtime;
        this.numberOfCombinations = numberOfCombinations;
        this.longestElement = longestElement;
        this.numberOfUnlockedElements = numberOfUnlockedElements;
        this.numberOfDiscardedElements = numberOfDiscardedElements;
        this.mostDiscardedElements = mostDiscardedElements;
        this.mostCombinationsForOneElement = mostCombinationsForOneElement;
        this.arcadeGamesPlayed = arcadeGamesPlayed;
        this.arcadeGamesWon = arcadeGamesWon;
        this.shortestArcadeTimeToBeat = shortestArcadeTimeToBeat;
        this.fewestArcadeTurnsToBeat = fewestArcadeTurnsToBeat;
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

    public int getArcadeGamesPlayed() { return this.arcadeGamesPlayed; }

    public int getArcadeGamesWon() { return this.arcadeGamesWon; }

    public long getShortestArcadeTimeToBeat() { return this.shortestArcadeTimeToBeat; }

    public int getFewestArcadeTurnsToBeat() { return this.fewestArcadeTurnsToBeat; }

    public void setPlaytime(long playTime) {
        this.playtime = playTime;
    }

    public void setNumberOfCombinations(long numberOfCombinations) {
        this.numberOfCombinations = numberOfCombinations;
    }

    public void setLongestElement(String longestElement) {
        if (longestElement.length() > this.longestElement.length())
            this.longestElement = longestElement;
    }

    public void setNumberOfUnlockedElements(int numberOfUnlockedElements) {
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
        this.mostCombinationsForOneElement = mostCombinationsForOneElement;
    }

    public void setArcadeGamesPlayed(int numberPlayed) {
        this.arcadeGamesPlayed = numberPlayed;
    }

    public void setArcadeGamesWon(int numberWon) {
        this.arcadeGamesWon = numberWon;
    }

    public void setShortestArcadeTimeToBeat(long time) {
        this.shortestArcadeTimeToBeat = time;
    }

    public void setFewestArcadeTurnsToBeat(int turns) {
        this.fewestArcadeTurnsToBeat = turns;
    }

    @Override
    /**
     * Override for Debug Purposes
     */
    public String toString() {
        return "Played Time: " + playtime + "\n" +
                "Number of Combinations: " + numberOfCombinations + "\n" +
                "Longest Element: " + longestElement + "\n" +
                "Number of unlocked Elememts: " + numberOfUnlockedElements + "\n" +
                "Total Discarded Elements: " +  numberOfDiscardedElements + "\n" +
                "Most Discarded Elements: " + mostDiscardedElements + "\n" +
                "Most Combinations for one Element: " + mostCombinationsForOneElement + "\n" +
                "Arcade Games Played: " + arcadeGamesPlayed + "\n" +
                "Arcade Games Won: " + arcadeGamesWon + "\n" +
                "Shortest Arcade Playtime: " + shortestArcadeTimeToBeat + "\n" +
                "Fewest Arcade Turns: " + fewestArcadeTurnsToBeat;
    }
}
