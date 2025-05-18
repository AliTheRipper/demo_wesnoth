package model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Joueur implements Serializable {
    private String nom;
    private boolean estIA;
    private List<Unite> unites;
    private String couleur;

    public Joueur(String nom, boolean estIA, String couleur) {
        this.nom = nom;
        this.estIA = estIA;
        this.couleur = couleur;
        this.unites = new ArrayList<>();
    }

    public List<Unite> getUnites() {
        return unites;
    }

    public void ajouterUnite(Unite u) {
        unites.add(u);
    }

    public String getNom() {
        return nom;
    }

    public boolean estIA() {
        return estIA;
    }

    public String getCouleur() {
        return couleur;
    }

    // Méthode appelée pour exécuter un tour complet (à implémenter selon ton contrôleur)
    public void jouerTour() {
        for (Unite u : unites) {
            u.reinitialiserDeplacement();
        }
        // Si c'est une IA, elle pourrait jouer automatiquement ici
    }

    // Exemple simple d'IA : créer un ordre de repos
    public Ordre creerOrdreIA() {
        if (estIA && !unites.isEmpty()) {
            Unite unite = unites.get(0);
            return new OrdreRepos(unite); // repose l’unité par défaut
        }
        return null;
    }
}
