package model;

import javax.swing.*;

public enum TypeTerrain {
    PLAINE("resources/plaine.png"),
    FORET("resources/foret.png"),
    COLLINE("resources/colline.png"),
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
