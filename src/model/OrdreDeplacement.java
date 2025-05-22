package model;

/**
 * Classe abstraite représentant un ordre donné à une unité. Chaque ordre est
 * associé à une unité et doit être exécuté.
 */

public class OrdreDeplacement extends Ordre {

    private Hexagone destination;

    /**
     * Constructeur de l'ordre de déplacement.
     */
    public OrdreDeplacement(Unite unite, Hexagone destination) {
        super(unite);
        this.destination = destination;
    }

    /**
     * Exécute l'ordre de déplacement en déplaçant l'unité vers la destination.
     */
    @Override
    public void executer() {
        if (unite != null && destination != null) {
            unite.seDeplacer(destination);
            unite.setPosition(destination);
        }
    }

    /**
     * Retourne la destination de l'ordre de déplacement.
     */
    public Hexagone getDestination() {
        return destination;
    }
}
