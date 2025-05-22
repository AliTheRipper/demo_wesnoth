package model;

/**
 * Condition de victoire : élimination de toutes les unités ennemies.
 *
 */
public interface ConditionVictoire {

    boolean estRemplie(PlateauDeJeu plateau, Joueur joueurActif);

    String getMessageVictoire();
}
