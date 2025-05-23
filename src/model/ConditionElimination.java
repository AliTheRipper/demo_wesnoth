package model;

/**
 * Condition de victoire : élimination de toutes les unités ennemies.
 *
 */
public class ConditionElimination implements ConditionVictoire {

    /**
     * Vérifie si la condition de victoire est remplie pour le joueur actif. La
     * victoire est acquise si aucune unité ennemie n'est présente sur le
     * plateau.
     *
     * @param plateau Plateau de jeu contenant les unités
     * @param joueurActif Le joueur dont on vérifie la victoire
     * @return true si toutes les unités ennemies ont été éliminées, false sinon
     */
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

    /**
     * Retourne le message à afficher lorsqu’un joueur remporte la partie par
     * élimination.
     *
     * @return Message de victoire
     */
    @Override
    public String getMessageVictoire() {
        return "Tous les ennemis ont été éliminés !";
    }
}
