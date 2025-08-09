package de.thm.mixit.data.entities;

import java.util.List;

import de.thm.mixit.data.model.ElementChip;

/**
 * Represents the data of the GameState of the Arcade and Endless Game mode
 * <p>
 * {@code time} Passed playtime.
 * <br>
 * {@code turns} Taken turns.
 * <br>
 * {@code goalElement} The goal element and its synonyms in arcade mode.
 * <br>
 * {@code elementchips} The positioning of all placed {@link ElementChip}.
 *
 * @author Jannik Heimann
 */
public class GameState {
    private long time;
    private int turns;
    private String[] goalElement;
    private List<ElementChip> elementChips;

    public GameState(long time,
                     int turns,
                     String[] goalElement,
                     List<ElementChip> elementchips) {
        this.time = time;
        this.turns = turns;
        this.goalElement = goalElement;
        this.elementChips = elementchips;
    }

    public long getTime() {
        return this.time;
    }

    public int getTurns() {
        return this.turns;
    }

    public String[] getGoalElement() {
        return this.goalElement;
    }

    public List<ElementChip> getElementChips() {
        return this.elementChips;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public void setGoalElement(String[] element) {
        this.goalElement = element;
    }

    public void setElementChips(List<ElementChip> elementChips) {
        this.elementChips = elementChips;
    }
}
