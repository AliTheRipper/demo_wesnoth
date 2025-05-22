package model;

/**
 * Classe abstraite représentant un ordre donné à une unité. Chaque ordre est
 * associé à une unité et doit être exécuté.
 */
public abstract class Ordre {

    protected Unite unite;

    public Ordre(Unite unite) {
        this.unite = unite;
    }

    public Unite getUnite() {
        return unite;
    }

    public abstract void executer();
}
