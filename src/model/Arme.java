package model;

public class Arme {
    private String nom;
    private int portee;
    private int potentielAttaque;
    private boolean estDistance;

    public Arme(String nom, int portee, int potentielAttaque, boolean estDistance) {
        this.nom = nom;
        this.portee = portee;
        this.potentielAttaque = potentielAttaque;
        this.estDistance = estDistance;
    }


    public String getNom() {
        return nom;
    }

    public int getPortee() {
        return portee;
    }

    public int getPotentielAttaque() {
        return potentielAttaque;
    }

    public boolean isEstDistance() {
        return estDistance;
    }

    @Override
    public String toString() {
        return nom + " (Attaque: " + potentielAttaque + ", Portée: " + portee + ", Distance: " + estDistance + ")";
    }
}
