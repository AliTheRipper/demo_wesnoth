package model;

public class OrdreDeplacement extends Ordre {
    private Hexagone destination;

    public OrdreDeplacement(Unite unite, Hexagone destination) {
        super(unite);
        this.destination = destination;
    }

    @Override
    public void executer() {
        if (unite != null && destination != null) {
            unite.seDeplacer(destination);
            unite.setPosition(destination);
        }
    }
}
