package model;

import java.util.List;

public class Scenario {
    private String nom;
    private List<ConditionVictoire> conditionsVictoire;
    private int toursMax;

    public Scenario(String nom, List<ConditionVictoire> conditionsVictoire, int toursMax) {
        this.nom = nom;
        this.conditionsVictoire = conditionsVictoire;
        this.toursMax = toursMax;
    }

    public void verifierConditionsVictoire(Partie partie) {
        for (ConditionVictoire condition : conditionsVictoire) {
            if (condition.estSatisfaite(partie)) {
                System.out.println("Condition de victoire atteinte ! Fin de la partie.");
                // Ici tu pourrais appeler une méthode pour afficher l'écran de victoire ou geler le jeu
            }
        }

        if (partie.getTourCourant() >= toursMax) {
            System.out.println("Tours max atteints !");
            // Tu pourrais vérifier si des unités sont encore en vie et déclarer un vainqueur
        }
    }

    public int getToursMax() {
        return toursMax;
    }

    public String getNom() {
        return nom;
    }
}
