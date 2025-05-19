package model;

import javax.swing.*;

public enum Decoration {
    NONE(null, '.'),

    TREES("resources/deco/pine-tile.png", 'T'),
    FLOWERS("resources/deco/flowers-mixed.png", 'F'),
    MUSHROOMS("resources/deco/mushroom.png", 'M'),
    STONES("resources/deco/stones-small7.png", 'S'),
    HUT("resources/deco/hut-tile.png", 'H'),
    CABIN("resources/deco/log-cabin-tile.png", 'C'),
    HUMAN_HOUSE("resources/deco/human-tile.png", 'U'),
    CITY_RUIN("resources/deco/human-city-ruin-tile.png", 'R'),
    HILLS_HOUSE("resources/deco/human-hills-tile.png", 'I'),
    HILLS_RUIN("resources/deco/human-hills-ruin-tile.png", 'J'),
    FARM("resources/deco/farm-veg-spring-icon.png", 'V'),
    CLOUD("resources/deco/cloud-tile.png", 'L'),
    CAMP("resources/deco/camp-tile.png", 'A'),
    DETRITUS("resources/deco/detritusC-1.png", 'D'),
    WATER_LILIES("resources/deco/water-lilies-flower-tile.png", 'W'),
    WINDMILL("resources/deco/windmill-embellishment-tile.png", 'X'),

    // Bridges and fences with orientation
    FENCE_SE("resources/deco/fence-se-nw-01.png", '/'),
    WOOD_NS("resources/deco/wood-n-s.png", '|'),
    WOOD_SE("resources/deco/wood-se-nw.png", '1'),
    WOOD_SW("resources/deco/wood-ne-sw.png", '2'),
    STONE_BRIDGE_NS("resources/deco/stonebridge-n-s-tile.png", '#'),

    // Trees and terrain types
    DECIDUOUS("resources/deco/deciduous-summer-tile.png", 'E'),
    MIXED_SUMMER("resources/deco/mixed-summer-tile.png", 'Z');

    private final String path;
    private final char symbol;
    private transient ImageIcon icon;

    Decoration(String path, char symbol) {
        this.path = path;
        this.symbol = symbol;
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

    public char getSymbol() {
        return symbol;
    }

    public static Decoration fromSymbol(char c) {
        for (Decoration deco : values()) {
            if (deco.symbol == c) return deco;
        }
        return NONE;
    }
}