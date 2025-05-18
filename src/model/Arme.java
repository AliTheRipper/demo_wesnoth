package model;

/**
 * Arme générique.
 * – type : "mêlée" ou "distance" (utilisé par l’IHM)
 * – portee : portée max en hexagones
 * – degats : dégâts d’un coup
 * – coups : nombre de coups par attaque (1 par défaut)
 * – precision : % de réussite (0‑100)
 */
public class Arme implements java.io.Serializable {
    private final String nom;
    private final String type;
    private final int portee;
    private final int degats;
    private final int coups;
    private final int precision;

    /* ► NOUVEAU constructeur “complet” – utilisé dans Platea uManager */
    public Arme(String nom, String type, int portee, int degats, int precision) {
        this.nom = nom;
        this.type = type;
        this.portee = portee;
        this.degats = degats;
        this.coups = 1;
        this.precision = precision;
    }

    /*
     * ► ANCIEN constructeur conservé pour compatibilité – appelé partout ailleurs
     */
    public Arme(String nom, int portee, int degats, boolean estDistance) {
        this(nom, estDistance ? "distance" : "mêlée", portee, degats, 60);
    }

    /* Getters */
    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public int getPortee() {
        return portee;
    }

    public int getDegats() {
        return degats;
    }

    public int getCoups() {
        return coups;
    }

    public int getPrecision() {
        return precision;
    }

    /** Description lisible par l’IHM */
    public String getDescription() {
        return String.format("%s (%s, %dx%d, %d%%)",
                nom, type, degats, coups, precision);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
