package model;

public class Hexagone {
    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private Unite unite;
    private boolean visible = false;

    public Hexagone(int x, int y, TypeTerrain typeTerrain) {
        this.x = x;
        this.y = y;
        this.typeTerrain = typeTerrain;
        this.unite = null;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public TypeTerrain getTypeTerrain() { return typeTerrain; }

    public Unite getUnite() { return unite; }

    public void setUnite(Unite unite) { this.unite = unite; }

    public boolean estAccessible() {
        return typeTerrain != TypeTerrain.EAU_PROFONDE;
    }

    public double obtenirBonusDefense() {
        return typeTerrain.getBonusDefense();
    }

    public int obtenirCoutDeplacement() {
        return typeTerrain.getCoutDeplacement();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
