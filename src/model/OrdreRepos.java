package model;

public class OrdreRepos extends Ordre {

    public OrdreRepos(Unite unite) {
        super(unite);
    }

    @Override
    public void executer() {
        System.out.println(unite.getNom() + " se repose pour récupérer des PV.");
        unite.seReposer(); // cette méthode doit exister dans ta classe Unite
    }
}
