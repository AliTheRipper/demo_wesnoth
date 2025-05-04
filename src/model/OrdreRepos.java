package model;

public class OrdreRepos extends Ordre {

    public OrdreRepos(Unite unite) {
        super(unite);
    }

    @Override
    public void executer() {
        if (unite != null) {
            unite.seReposer();
        }
    }
}
