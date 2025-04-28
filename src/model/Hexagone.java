package model;

public class Hexagone {
    private int x;
    private int y;
    private TypeTerrain typeTerrain;

    public Hexagone(int x, int y, TypeTerrain typeTerrain) {
        this.x = x;
        this.y = y;
        this.typeTerrain = typeTerrain;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public TypeTerrain getTypeTerrain() { return typeTerrain; }
}
