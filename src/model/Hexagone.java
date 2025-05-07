package model;

import java.awt.Point;
import java.io.Serializable;

public class Hexagone implements Serializable {
    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private boolean isVisible = false;
    private Unite unite; // peut être null
    private Decoration decoration = Decoration.NONE;
    private Point decorOffset = new Point(0, 0);

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }

    public Hexagone(int x, int y, TypeTerrain typeTerrain) {
        this.x = x;
        this.y = y;
        this.typeTerrain = typeTerrain;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TypeTerrain getTypeTerrain() {
        return typeTerrain;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public Decoration getDecoration() {
        return decoration;
    }

    public void setDecoration(Decoration decoration) {
        this.decoration = decoration;
    }

    public void setDecorOffset(Point p) {
        this.decorOffset = p;
    }

    public Point getDecorOffset() {
        return decorOffset;
    }

}
