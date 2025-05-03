package model;

public class ConditionVictoireDestructionArmee implements ConditionVictoire {

    @Override
    public boolean estSatisfaite(Partie partie) {
        int joueursAvecUnites = 0;

        for (Joueur joueur : partie.getJoueurs()) {
            if (!joueur.getUnites().isEmpty()) {
                joueursAvecUnites++;
            }
        }

        // Si un seul joueur a encore des unités, il gagne
        return joueursAvecUnites <= 1;
    }
}
