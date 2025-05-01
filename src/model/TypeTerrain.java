package model;

import javax.swing.*;

public enum TypeTerrain {
    PLAINE("resources/plaine.png", 1, 20),
    FORET("resources/foret.png", 2, 40),
    COLLINE("resources/colline.png", 2, 50),
    MONTAGNE("resources/montagne.png", 3, 60),
    VILLAGE("resources/village.png", 1, 40),
    CHATEAU("resources/chateau.png", 1, 60), // similaire à forteresse
    FORTERESSE("resources/forteresse.png", 1, 60),
    EAU_PROFONDE("resources/eau.png", 999, 0); // infranchissable

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

}
