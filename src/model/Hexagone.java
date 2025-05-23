package model;

import java.awt.Point;
import java.io.Serializable;

/**
 * Représente un hexagone sur le plateau de jeu. Chaque hexagone a des
 * coordonnées (x, y), un type de terrain, une unité (peut être null), une
 * décoration, un décalage pour la décoration et une visibilité.
 */
public class Hexagone implements Serializable {

    private int x;
    private int y;
    private TypeTerrain typeTerrain;
    private boolean isVisible = false;
    private Unite unite;
    private Decoration decoration = Decoration.NONE;
    private Point decorOffset = new Point(0, 0);
    private PlateauDeJeu plateau;

    /**
     * Retourne l’unité présente sur cet hexagone (ou null s’il n’y en a pas).
     */
    public Unite getUnite() {
        return unite;
    }

    /**
     * Place une unité sur cet hexagone. Met également à jour la position
     * interne de l’unité.
     *
     * @param unite Unité à placer sur la case
     */
    public void setUnite(Unite unite) {
        this.unite = unite;
        if (unite != null) {
            unite.setPosition(this);
        }
    }

    /**
     * Initialise un hexagone avec ses coordonnées et son type de terrain.
     *
     * @param x Position horizontale (colonne)
     * @param y Position verticale (ligne)
     * @param typeTerrain Type de terrain de la case
     */
    public Hexagone(int x, int y, TypeTerrain typeTerrain) {
        this.x = x;
        this.y = y;
        this.typeTerrain = typeTerrain;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setTypeTerrain(TypeTerrain typeTerrain) {
        this.typeTerrain = typeTerrain;
    }

    public TypeTerrain getTypeTerrain() {
        return typeTerrain;
    }

    /**
     * Indique si cet hexagone est actuellement visible pour le joueur actif
     * (brouillard de guerre).
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Définit la visibilité de cet hexagone.
     *
     * @param visible true si visible, false sinon
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Retourne la décoration appliquée à cet hexagone (par exemple un pont, des
     * ruines...).
     */
    public Decoration getDecoration() {
        return decoration;
    }

    /**
     * Définit la décoration graphique à appliquer sur l’hexagone.
     *
     * @param decoration Décoration choisie
     */
    public void setDecoration(Decoration decoration) {
        this.decoration = decoration;
    }

    /**
     * Définit le décalage graphique de la décoration (utilisé pour
     * l’affichage).
     *
     * @param p Point représentant le décalage en pixels
     */
    public void setDecorOffset(Point p) {
        this.decorOffset = p;
    }

    /**
     * Retourne le décalage graphique appliqué à la décoration.
     */
    public Point getDecorOffset() {
        return decorOffset;
    }

    /**
     * Définit le plateau auquel cet hexagone appartient.
     *
     * @param p Référence vers l'objet PlateauDeJeu
     */
    public void setPlateau(PlateauDeJeu p) {
        this.plateau = p;
    }

    /**
     * Retourne le plateau auquel cet hexagone est associé.
     */
    public PlateauDeJeu getPlateau() {
        return plateau;
    }

}
