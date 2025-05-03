package model;

public class ConditionVictoireDefenseTourMax implements ConditionVictoire {

    private int tourMax;

    public ConditionVictoireDefenseTourMax(int tourMax) {
        this.tourMax = tourMax;
    }

    @Override
    public boolean estSatisfaite(Partie partie) {
        if (partie.getTourCourant() >= tourMax) {
            for (Joueur joueur : partie.getJoueurs()) {
                if (!joueur.getUnites().isEmpty()) {
                    return true; // Un joueur a tenu jusqu’à la fin
                }
            }
        }
        return false;
    }
}
