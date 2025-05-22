package model;

import view.BoardPanel;

/**
 * Classe représentant un ordre d'attaque donné à une unité. Cet ordre est
 * associé à une unité et à une cible (une autre unité). Il est exécuté en
 * lançant un combat entre l'unité et la cible.
 */
public class OrdreAttaque extends Ordre {

    private Unite cible;
    private BoardPanel board;

    /**
     * Constructeur de l'ordre d'attaque.
     */
    public OrdreAttaque(Unite unite, Unite cible, BoardPanel board) {
        super(unite);
        this.cible = cible;
        this.board = board;
    }

    /**
     * Exécute l'ordre d'attaque en lançant un combat entre l'unité et la cible.
     */
    @Override
    public void executer() {
        board.lancerCombat(unite, cible);
    }

    /**
     * Retourne la cible de l'ordre d'attaque.
     */
    @Override
    public String toString() {
        return "OrdreAttaque → " + cible.getNom();
    }
}
