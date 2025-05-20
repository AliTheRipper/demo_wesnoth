package model;

import javax.swing.*;

public enum TypeTerrain {
   BASIC("resources/terrain/basic-tile.png", 3, 60),           // montagne
BEACH("resources/terrain/beach.png", 1, 15),                // plage (comme plaine, mais - defense)
COAST_GREY("resources/terrain/coast-grey-tile.png", 999, 0),
COAST("resources/terrain/coast-tile.png", 999, 0),
FORD("resources/terrain/ford-tile.png", 999, 0),
KELP("resources/terrain/kelp-tile.png", 999, 0),
OCEAN("resources/terrain/ocean-tile.png", 999, 0),
SUNKEN_RUIN("resources/terrain/sunken-ruin-tile.png", 999, 0),
DIRT("resources/terrain/dirt.png", 1, 20),                  // comme grass
DRY("resources/terrain/dry-symbol.png", 1, 20),             // comme grass
GREEN("resources/terrain/green-symbol.png", 1, 20),         // grass
LEAF("resources/terrain/leaf-litter.png", 2, 40),       // foret
REGULAR("resources/terrain/regular.png", 2, 50),        // colline
REGULAR_TILE("resources/terrain/regular-tile.png", 1, 40),  // village
RUINED_KEEP("resources/terrain/ruined-keep-tile.png", 1, 60), // chateau
RUIN("resources/terrain/ruin-tile.png", 1, 60),             // chateau
SEMI_DRY("resources/terrain/semi-dry.png", 1, 20),          // comme grass
STONE_PATH("resources/terrain/stone-path.png", 1, 20),    // chemin
WATER("resources/terrain/water-tile.png", 999, 0); 


    private final String cheminImage;
    private final int coutDeplacement;
    private final int bonusDefense;
    private transient ImageIcon icon;

    TypeTerrain(String cheminImage, int coutDeplacement, int bonusDefense) {
        this.cheminImage = cheminImage;
        this.coutDeplacement = coutDeplacement;
        this.bonusDefense = bonusDefense;
        this.icon = new ImageIcon(cheminImage);
    }

    public ImageIcon getIcon() {
        if (icon == null) {
            icon = new ImageIcon(cheminImage);
        }
        return icon;
    }

    public int getCoutDeplacement() {
        return coutDeplacement;
    }

    public int getBonusDefense() {
        return bonusDefense;
    }

    public boolean estVillage() {
        return this == REGULAR_TILE;
    }
    

}
