package model;

import javax.swing.*;

public enum Decoration {
    NONE(null),
    TREES("resources/pine-tile.png"),
    BUSH("resources/foret2.png"),
    SKULL("resources/pont.png");

    private final String path;
    private transient ImageIcon icon;

    Decoration(String path) {
        this.path = path;
        if (path != null) {
            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                System.err.println("Missing decoration file: " + path);
            }
            this.icon = new ImageIcon(path);
        }
    }
    

    public ImageIcon getIcon() {
        if (icon == null && path != null) {
            icon = new ImageIcon(path);
        }
        return icon;
    }
}
