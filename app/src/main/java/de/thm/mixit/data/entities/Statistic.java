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
 *
 * @author Jannik Heimann
 */
public class Statistic {
    private long playtime;
    private long numberOfCombinations;
    private String longestElement;
    private int numberOfUnlockedElements;
    private long numberOfDiscardedElements;
    private int mostDiscardedElements;
    private int mostCombinationsForOneElement;

    public Statistic(long playtime,
                     long numberOfCombinations,
                     String longestElement,
                     int numberOfUnlockedElements,
                     long numberOfDiscardedElements,
                     int mostDiscardedElements,
                     int mostCombinationsForOneElement) {
        this.playtime = playtime;
        this.numberOfCombinations = numberOfCombinations;
        this.longestElement = longestElement;
        this.numberOfUnlockedElements = numberOfUnlockedElements;
        this.numberOfDiscardedElements = numberOfDiscardedElements;
        this.mostDiscardedElements = mostDiscardedElements;
        this.mostCombinationsForOneElement = mostCombinationsForOneElement;
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

    public void setPlaytimeBy(long playTime) {
        this.playtime = playTime;
    }

    public void setNumberOfCombinations(long numberOfCombinations) {
        this.numberOfCombinations = numberOfCombinations;
    }

    public void setLongestElement(String longestElement) {
        this.longestElement = longestElement;
    }

    public void setNumberOfUnlockedElements(int numberOfUnlockedElements) {
        this.numberOfUnlockedElements = numberOfUnlockedElements;
    }

    public void setNumberOfDiscardedElements(long numberOfDiscardedElements) {
        this.numberOfDiscardedElements = numberOfDiscardedElements;
    }

    public void setMostDiscardedElements(int mostDiscardedElements) {
        this.mostDiscardedElements = mostDiscardedElements;
    }

    public void setMostCombinationsForOneElement(int mostCombinationsForOneElement) {
        this.mostCombinationsForOneElement = mostCombinationsForOneElement;
    }
}
