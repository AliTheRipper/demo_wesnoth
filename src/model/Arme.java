package model;

import java.io.Serializable;

public class Arme implements Serializable {
    private String nom;
    private String type; // "melee" ou "ranged"
    private int degats;
    private int coups;
    private int precision;

    public Arme(String nom, String type, int degats, int coups, int precision) {
        this.nom = nom;
        this.type = type;
        this.degats = degats;
        this.coups = coups;
        this.precision = precision;
    }

    public String getNom() { return nom; }
    public String getType() { return type; }
    public int getDegats() { return degats; }
    public int getCoups() { return coups; }
    public int getPrecision() { return precision; }
    public String getDescription() {
        return nom + " (" + type + ") - " + degats + " x" + coups + " (" + precision + "%)";
    }

}
