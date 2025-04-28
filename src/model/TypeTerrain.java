package model;

import javax.swing.*;

public enum TypeTerrain {
    PLAINE("resources/plaine.png"),
    FORET("resources/foret.png"),
    COLLINE("resources/colline.png"), // pour plus tard quand tu auras une image
    MONTAGNE("resources/montagne.png"),
    VILLAGE("resources/village.png"),
    FORTERESSE("resources/forteresse.png"),
    EAU_PROFONDE("resources/eau.png");

    private final ImageIcon icon;

    TypeTerrain(String imagePath) {
        this.icon = new ImageIcon(imagePath);
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
