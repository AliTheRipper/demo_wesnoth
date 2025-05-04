package model;

import java.util.List;
import java.util.Random;

public class Unite {
    private String nom;
    private TypeUnite typeUnite;
    private int pointsVie;
    private int pointsVieMax;
    private int pointsDeplacement;
    private int pointsDeplacementMax;
    private int champDeVision;
    private Joueur joueur;
    private Hexagone position;
    private List<Arme> armes;

    public Unite(String nom, TypeUnite typeUnite, Joueur joueur, List<Arme> armes) {
        this.nom = nom;
        this.typeUnite = typeUnite;
        this.pointsVieMax = typeUnite.getPointsVieMax();
        this.pointsVie = this.pointsVieMax;
        this.pointsDeplacementMax = typeUnite.getDeplacement();
        this.pointsDeplacement = this.pointsDeplacementMax;
        this.champDeVision = typeUnite.getChampDeVision();
        this.joueur = joueur;
        this.armes = armes;
        this.position = null;
    }

    public String getNom() {
        return nom;
    }

    public int getPointsVie() {
        return pointsVie;
    }

    public Hexagone getPosition() {
        return position;
    }

    public List<Arme> getArmes() {
        return armes;
    }

    public void setPosition(Hexagone pos) {
        this.position = pos;
    }

    public void reinitialiserDeplacement() {
        this.pointsDeplacement = this.pointsDeplacementMax;
    }

    public void attaquer(Unite cible, TypeTerrain terrain) {
        if (armes == null || armes.isEmpty()) {
            System.out.println(nom + " n'a aucune arme pour attaquer.");
            return;
        }

        Arme arme = armes.get(0); // on utilise la première arme par défaut

        int attaque = arme.getPotentielAttaque();
        int defense = cible.typeUnite.getDefense();
        double bonusTerrain = terrain.getBonusDefense();

        int variation = new Random().nextInt(attaque + 1) - attaque / 2;

        int degats = attaque + variation - (int)(defense * bonusTerrain);
        degats = Math.max(0, degats);

        cible.pointsVie -= degats;
        if (cible.pointsVie < 0) {
            cible.pointsVie = 0;
        }

        System.out.println(joueur.getNom() + " : " + nom + " attaque " + cible.joueur.getNom() + " , " + cible.getNom()
        + " avec " + arme.getNom() + " et inflige " + degats + " dégâts.");
        }

    public boolean seDeplacer(Hexagone destination) {
        int cout = destination.getCoutDeplacement();
        if (pointsDeplacement >= cout && destination.estAccessible()) {
            this.position = destination;
            pointsDeplacement -= cout;
            return true;
        }
        return false;
    }

    public void seReposer() {
        pointsDeplacement = pointsDeplacementMax;
    }
    public int getChampDeVision() {
        return champDeVision;
    }
    
    public boolean estVisiblePar(Joueur joueur) {
        for (Unite u : joueur.getUnites()) {
            int dx = Math.abs(u.getPosition().getX() - this.position.getX());
            int dy = Math.abs(u.getPosition().getY() - this.position.getY());
            int distance = dx + dy;
    
            if (distance <= u.getChampDeVision()) {
                return true;
            }
        }
        return false;
    }
    
}
