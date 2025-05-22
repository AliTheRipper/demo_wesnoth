package model;

/**
 * Classe abstraite représentant un ordre donné à une unité. Chaque ordre est
 * associé à une unité et doit être exécuté.
 */
public class OrdreRepos extends Ordre {

    /**
     * Constructeur de l'ordre de repos.
     */
    public OrdreRepos(Unite unite) {
        super(unite);
    }

    /**
     * Exécute l'ordre de repos en appelant la méthode seReposer de l'unité.
     */
    @Override
    public void executer() {
        if (unite != null) {
            unite.seReposer();
        }
    }
}
