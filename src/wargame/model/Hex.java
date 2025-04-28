package wargame.model;

/**
 * Représente une case hexagonale du plateau.
 */
public class Hex {
    private final int x;
    private final int y;
    private final TerrainType terrain;
    private Unit unit; // Peut être null si pas d'unité

    public Hex(int x, int y, TerrainType terrain) {
        this.x = x;
        this.y = y;
        this.terrain = terrain;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isOccupied() {
        return unit != null;
    }
}
