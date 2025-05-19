package model;

// package model ou scenario
public interface ConditionVictoire {
    boolean estRemplie(PlateauDeJeu plateau, Joueur joueurActif);
    String getMessageVictoire(); // Affiché quand on gagne
}

