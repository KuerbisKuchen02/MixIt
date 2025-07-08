package de.thm.mixit.data.model;

import androidx.annotation.NonNull;

import de.thm.mixit.data.entities.Element;

public class ElementChip {

    private static int ID = 0;

    private final int id;
    private final Element element;
    private float x;
    private float y;

    public ElementChip(Element element, float x, float y) {
        this.id = ID++;
        this.element = element;
        this.x = x;
        this.y = y;
    }

    public ElementChip(Element element) {
        this.id = ID++;
        this.element = element;
        this.x = -1;
        this.y = -1;
    }

    public int getId() {
        return id;
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

    public ElementChip withPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "ElementChip{" +
                "element=" + element +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ElementChip)) return false;
        return this.id == ((ElementChip) o).id;
    }
}
