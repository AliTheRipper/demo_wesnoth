package model;

import javax.swing.*;

public enum TypeTerrain {
    PLAINE("resources/plaine.png", 0.2, 1),
    FORET("resources/foret.png", 0.4, 2),
    COLLINE("resources/colline.png", 0.3, 2),
    MONTAGNE("resources/montagne.png", 0.5, 3),
    VILLAGE("resources/village.png", 0.2, 1),
    FORTERESSE("resources/forteresse.png", 0.6, 2),
    EAU_PROFONDE("resources/eau.png", 0.1, 99); // pratiquement inaccessible

    private final ImageIcon icon;
    private final double bonusDefense;
    private final int coutDeplacement;

    TypeTerrain(String imagePath, double bonusDefense, int coutDeplacement) {
        this.icon = new ImageIcon(imagePath);
        this.bonusDefense = bonusDefense;
        this.coutDeplacement = coutDeplacement;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public double getBonusDefense() {
        return bonusDefense;
    }

    public int getCoutDeplacement() {
        return coutDeplacement;
    }
}
