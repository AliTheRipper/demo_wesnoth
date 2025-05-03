
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Unite implements Serializable {
    private String nom;
    private transient ImageIcon icone;
    private String cheminImage;
    private int joueur;
    private int pointsVie;
    private int attaque;
    private int pointsDeplacementMax;
    private int pointsDeplacementActuels;
    private List<Arme> armes = new ArrayList<>();


    public Unite(String nom, String cheminImage, int joueur, int pv, int attaque, int deplacement) {
        this.nom = nom;
        this.cheminImage = cheminImage;
        this.icone = new ImageIcon(cheminImage);
        this.joueur = joueur;
        this.pointsVie = pv;
        this.attaque = attaque;
        this.pointsDeplacementMax = deplacement;
        this.pointsDeplacementActuels = deplacement;
    }

    public String getNom() { return nom; }
    public int getJoueur() { return joueur; }
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

}
