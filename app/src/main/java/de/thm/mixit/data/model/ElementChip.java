package de.thm.mixit.data.model;

import androidx.annotation.NonNull;

import de.thm.mixit.data.entity.Element;


/**
 * Represents a single element chip on the playground
 * Wraps a single {@link Element} object
 *
 * @author Josia Menger
 */
public class ElementChip {

    /**
     * Unique id for an element chip
     * We need this id to correctly map the view objects on the playground
     * to the data objects inside the viewmodel.
     * Since an element can have multiple instances on the playground
     * and we don't want to store views inside the viewmodel
     */
    private static int ID = 0;

    private boolean isAnimated = false;
    private final int id;
    private final Element element;
    private float x;
    private float y;

    public ElementChip(int id, Element element, float x, float y) {
        this.id = id;
        this.element = element;
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new chip representing an element on the playground
     * @param element element model
     * @param x current x position on the playground
     * @param y current y position on the playground
     */
    public ElementChip(Element element, float x, float y) {
        this.id = ID++;
        this.element = element;
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new chip representing an element on the playground
     * This chip will be placed at a random spot on the canvas
     * @param element element model
     */
    public ElementChip(Element element) {
        this.id = ID++;
        this.element = element;
        this.x = -1;
        this.y = -1;
    }

    public int getId() {
        return id;
    }

    public static void setId(int id) {
        ID = id;
    }

    public Element getElement() {
        return element;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    /**
     * Returns same ElementChip with updated position
     * @param x new x coordinate
     * @param y new y coordinate
     * @return self
     */
    public ElementChip withPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "ElementChip{" +
                "id=" + id +
                ", element=" + element +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ElementChip)) return false;
        return this.id == ((ElementChip) o).id
                && this.element == ((ElementChip) o).element
                && this.x == ((ElementChip) o).x
                && this.y == ((ElementChip) o).y;
    }
}
