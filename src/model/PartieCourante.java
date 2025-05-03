package model;

public class PartieCourante {
    private static PartieCourante instance;
    private Partie partie;

    private PartieCourante() {}

    public static PartieCourante getInstance() {
        if (instance == null) {
            instance = new PartieCourante();
        }
        return instance;
    }

    public Partie getPartie() {
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }
}
