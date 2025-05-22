package model;

/**
 * Condition de victoire : élimination de toutes les unités ennemies.
 *
 */
public class ConditionElimination implements ConditionVictoire {

    @Override
    public boolean estRemplie(PlateauDeJeu plateau, Joueur joueurActif) {

        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.getJoueur() != joueurActif) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getMessageVictoire() {
        return "Tous les ennemis ont été éliminés !";
    }
}
