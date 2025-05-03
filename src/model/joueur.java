package model;

import java.util.ArrayList;
import java.util.List;

public class Joueur {
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

    public void jouerTour() {
        System.out.println(nom + " joue son tour.");
        if (estIA) {
            Ordre ordre = creerOrdreAI();
            if (ordre != null) {
                ordre.executer();
            } else {
                System.out.println("IA " + nom + " ne trouve pas d'ordre à exécuter.");
            }
        } else {
            System.out.println("C'est au tour du joueur " + nom);
            // Interface utilisateur ici
        }
    }
    
    public Ordre creerOrdreAI() {
        for (Unite u : unites) {
            for (Joueur adversaire : PartieCourante.getInstance().getPartie().getJoueurs()) {
                if (!adversaire.equals(this)) {
                    for (Unite cible : adversaire.getUnites()) {
                        if (u.estVisiblePar(this) && estADistanceDAttaque(u, cible)) {
                            return new OrdreAttaque(u, cible);
                        } else {
                            Hexagone destination = chercherDestinationProche(u, cible);
                            if (destination != null) {
                                return new OrdreDeplacement(u, destination);
                            }
                        }
                    }
                }
            }
            // Si aucune cible visible ou déplacement possible : repos
            return new OrdreRepos(u);
        }
        return null;
    }
    

    private Hexagone chercherDestinationProche(Unite u, Unite cible) {
        Hexagone positionActuelle = u.getPosition();
        Hexagone positionCible = cible.getPosition();
    
        int dx = Integer.compare(positionCible.getX(), positionActuelle.getX());
        int dy = Integer.compare(positionCible.getY(), positionActuelle.getY());
    
        PlateauDeJeu plateau = PartieCourante.getInstance().getPartie().getPlateau();
        int newX = positionActuelle.getX() + dx;
        int newY = positionActuelle.getY() + dy;
    
        try {
            Hexagone prochainHex = plateau.obtenirHexagone(newX, newY);
            if (prochainHex.estAccessible()) {
                return prochainHex;
            }
        } catch (Exception e) {
            // hors des limites ou inaccessible
        }
    
        return null;
    }
    

    private boolean estADistanceDAttaque(Unite attaquant, Unite cible) {
        Hexagone pos1 = attaquant.getPosition();
        Hexagone pos2 = cible.getPosition();
        int dx = Math.abs(pos1.getX() - pos2.getX());
        int dy = Math.abs(pos1.getY() - pos2.getY());
        return dx + dy <= 1; // attaque au corps-à-corps
    }

    // Getters et setters utiles
    public List<Unite> getUnites() {
        return unites;
    }

    public void ajouterUnite(Unite unite) {
        unites.add(unite);
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
}
