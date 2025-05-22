package model;

import java.awt.Point;
import java.io.Serializable;

/**
 * Représente un hexagone sur le plateau de jeu. Chaque hexagone a des
 * coordonnées (x, y), un type de terrain, une unité (peut être null), une
 * décoration, un décalage pour la décoration et une visibilité.
 */
public class Hexagone implements Serializable {

    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private boolean isVisible = false;
    private Unite unite;
    private Decoration decoration = Decoration.NONE;
    private Point decorOffset = new Point(0, 0);
    private PlateauDeJeu plateau;

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
        if (unite != null) {
            unite.setPosition(this);
        }
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

    public void setTypeTerrain(TypeTerrain typeTerrain) {
        this.typeTerrain = typeTerrain;
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

    public void setPlateau(PlateauDeJeu p) {
        this.plateau = p;
    }

    public PlateauDeJeu getPlateau() {
        return plateau;
    }

}
