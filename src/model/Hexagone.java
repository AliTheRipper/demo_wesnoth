package model;

public class Hexagone {
    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private boolean isVisible = false;

    public Hexagone(int x, int y, TypeTerrain typeTerrain) {
        this.x = x;
        this.y = y;
        this.typeTerrain = typeTerrain;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public TypeTerrain getTypeTerrain() { return typeTerrain; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }

    public int getCoutDeplacement() {
        return typeTerrain.getCoutDeplacement();
    }

    public boolean estAccessible() {
        return typeTerrain != TypeTerrain.EAU_PROFONDE;
    }
}
