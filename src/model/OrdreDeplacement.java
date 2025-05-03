package model;

public class OrdreDeplacement extends Ordre {
    private Hexagone destination;

    public OrdreDeplacement(Unite unite, Hexagone destination) {
        super(unite);
        this.destination = destination;
    }

    @Override
    public void executer() {
        System.out.println(unite.getNom() + " se déplace vers (" + destination.getX() + ", " + destination.getY() + ")");
        unite.seDeplacer(destination); // méthode existante dans `Unite`
    }
}
