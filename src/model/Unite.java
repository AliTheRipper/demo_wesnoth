
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Unite implements Serializable {
    private Joueur joueur;
    private String nom;
    private transient ImageIcon icone;
    private String cheminImage;
    private int joueurID;
    private int pointsVie;
    private int attaque;
    private int pointsDeplacementMax;
    private int pointsDeplacementActuels;
    private List<Arme> armes = new ArrayList<>();


    public Unite(String nom, String cheminImage, int joueur, int pv, int attaque, int deplacement) {
        this.nom = nom;
        this.cheminImage = cheminImage;
        this.icone = new ImageIcon(cheminImage);
        this.joueurID = joueur;
        this.pointsVie = pv;
        this.attaque = attaque;
        this.pointsDeplacementMax = deplacement;
        this.pointsDeplacementActuels = deplacement;
    }

    public String getNom() { return nom; }
    public Joueur getJoueur() { return joueur; }
    public int getPointsVie() { return pointsVie; }
    public int getAttaque() { return attaque; }

    public int getDeplacementRestant() {
        return pointsDeplacementActuels;
    }

    public void reduireDeplacement(int cout) {
        pointsDeplacementActuels = Math.max(0, pointsDeplacementActuels - cout);
    }

    public void resetDeplacement() {
        pointsDeplacementActuels = pointsDeplacementMax;
    }

    public void subirDegats(int degats) {
        pointsVie -= degats;
    }

    public boolean estEnVie() {
        return pointsVie > 0;
    }
    public ImageIcon getIcone() {
        if (icone == null && cheminImage != null) {
            icone = new ImageIcon(cheminImage);
        }
        return icone;
    }
    public void ajouterArme(Arme a) {
        armes.add(a);
    }

    public List<Arme> getArmes() {
        return armes;
    }
    // Ajout des liens avec le joueur et sa position
    private Hexagone position;

    // utile pour initialisation depuis PlateauManager

    public int getJoueurID() {
        return joueurID;
    }

    public void setJoueurID(int joueurID) {
        this.joueurID = joueurID;
    }
    
    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;              
        this.joueurID = joueur.getId();    
    }
    
    // position sur le plateau
    public Hexagone getPosition() {
        return position;
    }

    public void setPosition(Hexagone position) {
        this.position = position;
    }

    // déplacement simple
    public void seDeplacer(Hexagone destination) {
        if (this.position != null) {
            this.position.setUnite(null); // vide ancienne case
        }
        destination.setUnite(this);
        this.setPosition(destination);
    }

    // repos = régénère un peu de vie
    public void seReposer() {
        this.pointsVie = Math.min(this.pointsVie + 5, 40); // simple exemple
    }

    // pour IA (pour l'instant retourne toujours vrai)
    public boolean estVisiblePar(Joueur joueur) {
        return true; // simplifié
    }

    public void attaquer(Unite cible) {
        int degats = 10; // temporaire, à améliorer avec armes
        cible.pointsVie -= degats;
        if (cible.pointsVie <= 0) {
            cible.getPosition().setUnite(null);
        }
    }
    

}
