package de.thm.mixit.data.entity;

import java.util.ArrayList;

import de.thm.mixit.data.model.ElementChip;

/**
 * Represents the data of the GameState of the Arcade and Endless Game mode
 * <p>
 * {@code time} Passed playtime.
 * <br>
 * {@code turns} Taken turns.
 * <br>
 * {@code targetElement} The target element and its synonyms in arcade mode.
 * <br>
 * {@code elementchips} The positioning of all placed {@link ElementChip}.
 *
 * @author Jannik Heimann
 */
public class GameState {
    private long time;
    private int turns;
    private String[] targetElement;
    private ArrayList<ElementChip> elementChips;

    public GameState(long time,
                     int turns,
                     String[] targetElement,
                     ArrayList<ElementChip> elementchips) {
        this.time = time;
        this.turns = turns;
        this.targetElement = targetElement;
        this.elementChips = elementchips;
    }

    public long getTime() {
        return this.time;
    }

    public int getTurns() {
        return this.turns;
    }

    public String[] getTargetElement() {
        return this.targetElement;
    }

    public ArrayList<ElementChip> getElementChips() {
        return this.elementChips;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public void setTargetElement(String[] element) {
        this.targetElement = element;
    }

    public void setElementChips(ArrayList<ElementChip> elementChips) {
        this.elementChips = elementChips;
    }

    public int getHighestElementChipID() {
        return this.elementChips.stream()
                .mapToInt(ElementChip::getId)
                .max()
                .orElse(0);
    }
}
