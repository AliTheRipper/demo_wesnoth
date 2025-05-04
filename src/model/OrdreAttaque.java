package model;

public class OrdreAttaque extends Ordre {
    private Unite cible;

    public OrdreAttaque(Unite unite, Unite cible) {
        super(unite);
        this.cible = cible;
    }

    @Override
    public void executer() {
        if (unite == null || cible == null) return;

        Hexagone hexCible = cible.getPosition();
        TypeTerrain terrain = hexCible.getTypeTerrain();

        int distance = Math.abs(unite.getPosition().getX() - hexCible.getX())
                     + Math.abs(unite.getPosition().getY() - hexCible.getY());

        if (distance <= unite.getArmes().get(0).getPortee() ) {
            unite.attaquer(cible, terrain);
        }
    }
}
