package model;

public class PlateauDeJeu {
    private final int largeur;
    private final int hauteur;
    private Hexagone[][] hexagones;

    public PlateauDeJeu(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        hexagones = new Hexagone[hauteur][largeur];

        // Créer des hexagones par défaut (tous Plaine par exemple)
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                hexagones[y][x] = new Hexagone(x, y, TypeTerrain.PLAINE);
            }
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
}
