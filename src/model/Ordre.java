package model;

public abstract class Ordre {
    protected Unite unite;

    public Ordre(Unite unite) {
        this.unite = unite;
    }

    public abstract void executer();
}
