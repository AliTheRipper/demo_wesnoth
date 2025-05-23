package model;

import javax.swing.*;

/**
 * Enumération représentant les différents types de terrains dans le jeu. Chaque
 * type de terrain possède une image, un coût de déplacement, et un bonus de
 * défense. Certains terrains (comme REGULAR_TILE) sont utilisés comme villages
 * pour la récupération.
 */
public enum TypeTerrain {
    BASIC("resources/terrain/basic-tile.png", 3, 60),
    BEACH("resources/terrain/beach.png", 1, 15),
    COAST_GREY("resources/terrain/coast-grey-tile.png", 999, 0),
    COAST("resources/terrain/coast-tile.png", 999, 0),
    FORD("resources/terrain/ford-tile.png", 999, 0),
    KELP("resources/terrain/kelp-tile.png", 999, 0),
    OCEAN("resources/terrain/ocean-tile.png", 999, 0),
    SUNKEN_RUIN("resources/terrain/sunken-ruin-tile.png", 999, 0),
    DIRT("resources/terrain/dirt.png", 1, 20),
    DRY("resources/terrain/dry-symbol.png", 1, 20),
    GREEN("resources/terrain/green-symbol.png", 1, 20),
    LEAF("resources/terrain/leaf-litter.png", 2, 40),
    REGULAR("resources/terrain/regular.png", 2, 50),
    REGULAR_TILE("resources/terrain/regular-tile.png", 1, 40),
    RUINED_KEEP("resources/terrain/ruined-keep-tile.png", 1, 60),
    RUIN("resources/terrain/ruin-tile.png", 1, 60),
    SEMI_DRY("resources/terrain/semi-dry.png", 1, 20),
    STONE_PATH("resources/terrain/stone-path.png", 1, 20),
    WATER("resources/terrain/water-tile.png", 999, 0);

    private final String cheminImage;
    private final int coutDeplacement;
    private final int bonusDefense;
    private transient ImageIcon icon;

    /**
     * Constructeur privé de l'enum.
     *
     * @param cheminImage Chemin d’accès à l’image du terrain
     * @param coutDeplacement Coût de déplacement pour une unité traversant ce
     * terrain
     * @param bonusDefense Bonus défensif accordé à une unité sur ce terrain
     */
    TypeTerrain(String cheminImage, int coutDeplacement, int bonusDefense) {
        this.cheminImage = cheminImage;
        this.coutDeplacement = coutDeplacement;
        this.bonusDefense = bonusDefense;
        this.icon = new ImageIcon(cheminImage);
    }

    /**
     * Retourne l’icône (image) associée à ce type de terrain. L’icône est
     * chargée à la première demande si elle ne l’a pas déjà été.
     *
     * @return ImageIcon représentant le terrain
     */
    public ImageIcon getIcon() {
        if (icon == null) {
            icon = new ImageIcon(cheminImage);
        }
        return icon;
    }

    /**
     * Retourne le coût de déplacement pour ce terrain. Une valeur élevée (comme
     * 999) signifie que le terrain est infranchissable.
     *
     * @return Coût en points de déplacement
     */
    public int getCoutDeplacement() {
        return coutDeplacement;
    }

    /**
     * Retourne le bonus de défense accordé à une unité présente sur ce terrain.
     *
     * @return Pourcentage de bonus défensif
     */
    public int getBonusDefense() {
        return bonusDefense;
    }

    /**
     * Indique si ce type de terrain est considéré comme un village. Les
     * villages permettent aux unités de se soigner.
     *
     * @return true si ce terrain est un village, false sinon
     */
    public boolean estVillage() {
        return this == REGULAR_TILE;
    }

}
