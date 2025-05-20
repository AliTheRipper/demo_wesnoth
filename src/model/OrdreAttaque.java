package model;

import view.BoardPanel;

public class OrdreAttaque extends Ordre {
    private Unite cible;
    private BoardPanel board;

    public OrdreAttaque(Unite unite, Unite cible, BoardPanel board) {
        super(unite);
        this.cible = cible;
        this.board = board;
    }

    @Override
    public void executer() {
        board.lancerCombat(unite, cible);
        System.out.println("💥 Attaque de " + unite.getNom() + " contre " + cible.getNom());
    }

    @Override
    public String toString() {
        return "OrdreAttaque → " + cible.getNom();
    }
}
