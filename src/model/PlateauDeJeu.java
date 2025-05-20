package model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlateauDeJeu implements Serializable {
    private int largeur;
    private int hauteur;
    private Hexagone[][] hexagones;
    private Joueur joueur1;
    private Joueur joueur2;
    public PlateauDeJeu(String terrainFile) {
        this(terrainFile, null);
    }

    public PlateauDeJeu(String terrainFile, String decorFile) {
        try {
            List<String> terrainLines = Files.readAllLines(Paths.get(terrainFile));
            List<String> decorLines = decorFile != null ? Files.readAllLines(Paths.get(decorFile)) : null;

            hauteur = terrainLines.size();
            largeur = terrainLines.get(0).length();
            hexagones = new Hexagone[largeur][hauteur];

            for (int y = 0; y < hauteur; y++) {
                String terrainLine = terrainLines.get(y);
                String decorLine = decorLines != null && y < decorLines.size() ? decorLines.get(y) : null;

                for (int x = 0; x < largeur; x++) {
                    TypeTerrain terrain = convertirSymbole(terrainLine.charAt(x));

                    //IA
                    Hexagone hex = new Hexagone(x, y, terrain);
                    hex.setPlateau(this); // pour l'IA
                    hexagones[x][y] = hex;  


                    if (decorLine != null && x < decorLine.length()) {
                        char decoChar = decorLine.charAt(x);
                        Decoration decor = convertirDecoration(decoChar);
                        hexagones[x][y].setDecoration(decor);
                    } else {
                        hexagones[x][y].setDecoration(Decoration.NONE);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TypeTerrain convertirSymbole(char c) {
        switch (c) {
            case 'G': return TypeTerrain.GREEN;
            case 'P': return TypeTerrain.STONE_PATH;
            case 'F': return TypeTerrain.LEAF;
            case 'M': return TypeTerrain.BASIC;
            case 'H': return TypeTerrain.REGULAR;
            case 'V': return TypeTerrain.REGULAR_TILE;
            case 'C': return TypeTerrain.RUINED_KEEP;
            case 'R': return TypeTerrain.RUIN;
            case 'B': return TypeTerrain.BEACH;
            case 'D': return TypeTerrain.DIRT;
            case 'Y': return TypeTerrain.DRY;
            case 'J': return TypeTerrain.SEMI_DRY;
            case 'L': return TypeTerrain.LEAF;
            case 'O': return TypeTerrain.OCEAN;
            case 'K': return TypeTerrain.KELP;
            case 'S': return TypeTerrain.SUNKEN_RUIN;
            case 'W': return TypeTerrain.COAST;
            case 'E': return TypeTerrain.COAST_GREY;
            case 'X': return TypeTerrain.FORD;
            case 'A': return TypeTerrain.WATER;
            default: return TypeTerrain.GREEN;
        }
    }

    private Decoration convertirDecoration(char c) {
        return Decoration.fromSymbol(c);
    }


    public Hexagone getHexagone(int x, int y) {
        return hexagones[x][y];
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public int getCoutDeplacement(TypeTerrain terrain) {
        switch (terrain) {
            case GREEN:
            case DIRT:
            case DRY:
            case SEMI_DRY:
                return 1;

            case STONE_PATH:
            case BEACH:
                return 1;

            case LEAF:
                return 2;

            case BASIC:
                return 3;

            case REGULAR:
                return 2;

            case REGULAR_TILE:
                return 1;

            case RUINED_KEEP:
            case RUIN:
                return 1;

            case OCEAN:
            case KELP:
            case SUNKEN_RUIN:
            case COAST:
            case COAST_GREY:
            case FORD:
            case WATER:
                return 999;

            default:
                return 1;
        }
    }

public int getBonusDefense(Hexagone hex) {
    if (hex.getTypeTerrain().getCoutDeplacement() >= 999 &&
        (hex.getDecoration() == Decoration.WOOD_NS ||
         hex.getDecoration() == Decoration.WOOD_SE ||
         hex.getDecoration() == Decoration.WOOD_SW ||
         hex.getDecoration() == Decoration.STONE_BRIDGE_NS)) {
        return TypeTerrain.GREEN.getBonusDefense(); // Same as grass
    }
    return hex.getTypeTerrain().getBonusDefense();
}


    public List<Unite> getToutesLesUnites() {
        List<Unite> unites = new ArrayList<>();
        for (int y = 0; y < getHauteur(); y++) {
            for (int x = 0; x < getLargeur(); x++) {
                Unite u = getHexagone(x, y).getUnite();
                if (u != null) {
                    unites.add(u);
                }
            }
        }
        return unites;
    }


    public Joueur getJoueur1() {
        return joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public void setJoueur1(Joueur joueur) {
        this.joueur1 = joueur;
    }

    public void setJoueur2(Joueur joueur) {
        this.joueur2 = joueur;
    }


}