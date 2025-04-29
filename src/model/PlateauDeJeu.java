package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PlateauDeJeu {
    private int largeur;
    private int hauteur;
    private Hexagone[][] hexagones;

    public PlateauDeJeu(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int y = 0;

            while ((line = reader.readLine()) != null) {
                if (hexagones == null) {
                    largeur = line.length();
                    hexagones = new Hexagone[100][largeur]; // max 100 lignes
                }
                for (int x = 0; x < line.length(); x++) {
                    TypeTerrain terrain = convertirSymbole(line.charAt(x));
                    hexagones[y][x] = new Hexagone(x, y, terrain);
                }
                y++;
            }
            hauteur = y;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TypeTerrain convertirSymbole(char c) {
        switch (c) {
            case 'P': return TypeTerrain.PLAINE;
            case 'F': return TypeTerrain.FORET;
            case 'M': return TypeTerrain.MONTAGNE;
            case 'C': return TypeTerrain.COLLINE;
            case 'V': return TypeTerrain.VILLAGE;
            case 'E': return TypeTerrain.EAU_PROFONDE;
            case 'T': return TypeTerrain.FORTERESSE;
            default: return TypeTerrain.PLAINE;
        }
    }

    public Hexagone getHexagone(int x, int y) {
        return hexagones[y][x];
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public int getCoutDeplacement(TypeTerrain terrain) {
        switch (terrain) {
            case PLAINE: return 1;
            case FORET: return 2;
            case MONTAGNE: return 3;
            case EAU_PROFONDE: return 999; // infranchissable
            case FORTERESSE: return 2;
            case COLLINE: return 2;
            case VILLAGE: return 1;
            default: return 1;
        }
    }

}
