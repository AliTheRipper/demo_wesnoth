package model;

import javax.swing.*;

public class Unite {
    private String nom;
    private ImageIcon icone;
    private int joueur;
    private int pointsVie;
    private int attaque;
    private int pointsDeplacementMax;
    private int pointsDeplacementActuels;

    public Unite(String nom, String cheminImage, int joueur, int pv, int attaque, int deplacement) {
        this.nom = nom;
        this.icone = new ImageIcon(cheminImage);
        this.joueur = joueur;
        this.pointsVie = pv;
        this.attaque = attaque;
        this.pointsDeplacementMax = deplacement;
        this.pointsDeplacementActuels = deplacement;
    }

    public String getNom() { return nom; }
    public ImageIcon getIcone() { return icone; }
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
}
