package model;

public class OrdreAttaque extends Ordre {
    private Unite cible;

    public OrdreAttaque(Unite unite, Unite cible) {
        super(unite);
        this.cible = cible;
    }

    @Override
    public void executer() {
        System.out.println(unite.getNom() + " attaque " + cible.getNom());
        unite.attaquer(cible);
    }
}
