package model;

import java.util.List;

public class Partie {
    private String nom;
    private int tourCourant;
    private List<Joueur> joueurs;
    private PlateauDeJeu plateau;
    private Scenario scenario;

    public Partie(String nom, List<Joueur> joueurs, PlateauDeJeu plateau, Scenario scenario) {
        this.nom = nom;
        this.tourCourant = 1;
        this.joueurs = joueurs;
        this.plateau = plateau;
        this.scenario = scenario;
    }

    public void demarrerPartie() {
        System.out.println("La partie commence !");
        for (Joueur j : joueurs) {
            j.jouerTour(); // humain ou IA
        }
    }

    public void passerAuTourSuivant() {
        System.out.println("Tour " + tourCourant + " terminé.");
        tourCourant++;
    
        for (Joueur joueur : joueurs) {
            joueur.jouerTour(); // IA ou humain
        }
    
        scenario.verifierConditionsVictoire(this); // 👈 vérifie après chaque tour
        mettreAJourBrouillard();
    }

    public void verifierConditionsVictoire() {
        for (ConditionVictoire condition : scenario.getConditionsVictoire()) {
            if (condition.estSatisfaite(this)) {
                afficherEcranVictoire(); // 👈 déclenche le popup
                break;
            }
        }
    }
    

    // Getters
    public int getTourCourant() {
        return tourCourant;
    }

    public PlateauDeJeu getPlateau() {
        return plateau;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public Scenario getScenario() {
        return scenario;
    }

    private void afficherEcranVictoire() {
        Joueur gagnant = null;
        for (Joueur joueur : joueurs) {
            if (!joueur.getUnites().isEmpty()) {
                gagnant = joueur;
                break;
            }
        }
    
        String message;
        if (gagnant != null) {
            message = "Victoire du joueur : " + gagnant.getNom();
        } else {
            message = "Match nul ! Aucun joueur survivant.";
        }
    
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0); // Tu peux remplacer par retour au menu
        });
    }

    public void mettreAJourBrouillard() {
        // Réinitialise la visibilité
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                plateau.getHexagone(x, y).resetVisibilite();
            }
        }
    
        for (Joueur joueur : joueurs) {
            for (Unite unite : joueur.getUnites()) {
                Hexagone pos = unite.getPosition();
                int porteeVision = 3; // ou une méthode unite.getPorteeVision()
                for (int dx = -porteeVision; dx <= porteeVision; dx++) {
                    for (int dy = -porteeVision; dy <= porteeVision; dy++) {
                        int nx = pos.getX() + dx;
                        int ny = pos.getY() + dy;
                        if (nx >= 0 && ny >= 0 && nx < plateau.getLargeur() && ny < plateau.getHauteur()) {
                            plateau.getHexagone(nx, ny).setVisiblePourJoueur(joueur.getId(), true);
                        }
                    }
                }
            }
        }
    }
    
    
}
