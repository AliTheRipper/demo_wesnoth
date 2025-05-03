package model;

import java.io.Serializable;

public class Hexagone implements Serializable {
    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private boolean visibleParJoueur1 = false;
    private boolean visibleParJoueur2 = false;
    private Unite unite; // peut être null

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

    public int getX() { return x; }
    public int getY() { return y; }
    public TypeTerrain getTypeTerrain() { return typeTerrain; }

    // public boolean isVisible() { return isVisible; }
    // public void setVisible(boolean visible) { this.isVisible = visible; }

    public void setVisiblePourJoueur(int joueur, boolean visible) {
        if (joueur == 1) visibleParJoueur1 = visible;
        if (joueur == 2) visibleParJoueur2 = visible;
    }
    
    public boolean estVisiblePourJoueur(int joueur) {
        if (joueur == 1) return visibleParJoueur1;
        if (joueur == 2) return visibleParJoueur2;
        return false;
    }
    
    public void resetVisibilite() {
        visibleParJoueur1 = false;
        visibleParJoueur2 = false;
    }
    
}