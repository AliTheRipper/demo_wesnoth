package model;

import java.awt.Point;
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

    public PlateauDeJeu(String terrainFile) {
        this(terrainFile, null);
    }

    public PlateauDeJeu(String terrainFile, String decorFile) {
        try {
            List<String> terrainLines = Files.readAllLines(Paths.get(terrainFile));
            List<String> decorLines = null;

            hauteur = terrainLines.size();
            largeur = terrainLines.get(0).length();
            hexagones = new Hexagone[largeur][hauteur];

            for (int y = 0; y < hauteur; y++) {
                String terrainLine = terrainLines.get(y);
                String decorLine = decorLines != null && y < decorLines.size() ? decorLines.get(y) : null;

                for (int x = 0; x < largeur; x++) {
                    TypeTerrain terrain = convertirSymbole(terrainLine.charAt(x));
                    hexagones[x][y] = new Hexagone(x, y, terrain);

                    if (decorLine != null && x * 2 + 1 < decorLine.length()) {
                        char typeChar = decorLine.charAt(x * 2);
                        char posChar = decorLine.charAt(x * 2 + 1);

                        Decoration decor = convertirDecoration(typeChar);
                        Point offset = getDecorationOffset(posChar);

                        hexagones[x][y].setDecoration(decor);
                        hexagones[x][y].setDecorOffset(offset);
                    } else {
                        hexagones[x][y].setDecoration(Decoration.NONE);
                        hexagones[x][y].setDecorOffset(new Point(0, 0));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TypeTerrain convertirSymbole(char c) {
        switch (c) {
            case 'P':
                return TypeTerrain.PLAINE;
            case 'F':
                return TypeTerrain.FORET;
            case 'M':
                return TypeTerrain.MONTAGNE;
            case 'E':
                return TypeTerrain.EAU_PROFONDE;
            case 'V':
                return TypeTerrain.VILLAGE;
            case 'T':
                return TypeTerrain.FUNGUS;
            case 'C':
                return TypeTerrain.CHATEAU;
            default:
                return TypeTerrain.PLAINE;
        }
    }

    private Decoration convertirDecoration(char c) {
        switch (c) {
            case 't':
                return Decoration.TREES;
            case 'b':
                return Decoration.BUSH;
            case 's':
                return Decoration.SKULL;
            case '.':
                return Decoration.NONE;
            default:
                System.out.println("Décor inconnu: " + c);
                return Decoration.NONE;
        }
    }

    private Point getDecorationOffset(char pos) {
        switch (pos) {
            case '1':
                return new Point(-10, -10); // top-left
            case '2':
                return new Point(0, -15); // top-center
            case '3':
                return new Point(10, -10); // top-right
            case '4':
                return new Point(-10, 10); // bottom-left
            case '5':
                return new Point(0, 10); // bottom-center
            case '6':
                return new Point(10, 10); // bottom-right
            default:
                return new Point(0, 0); // center
        }
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
            case PLAINE:
                return 1;
            case FORET:
                return 2;
            case MONTAGNE:
                return 3;
            case EAU_PROFONDE:
                return 999;
            case FUNGUS:
                return 2;
            case COLLINE:
                return 2;
            case VILLAGE:
                return 1;
            default:
                return 1;
        }
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

}
