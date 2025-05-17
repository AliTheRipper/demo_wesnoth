package model;

import javax.swing.ImageIcon;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;

/**
 * Implémentation unifiée :
 * • constructeur “moteur” (String, TypeUnite, Joueur, List<Arme>)
 * • constructeur “vue”    (String nom, String img, int idJoueur, pv, att, dep)
 * Les deux mondes (moteur / IHM) cohabitent sans se gêner.
 */
public class Unite implements Serializable {
    /* --- Données de base -------------------------------------------------- */
    private final String nom;
    private final TypeUnite typeUnite; // peut être null quand créé par la vue
    private transient ImageIcon icone;

    private final String cheminImage;
    private final int defenseBase; // ← NOUVEAU

    private final int pointsVieMax;
    private int pointsVie;

    private final int pointsDeplacementMax;
    private int pointsDeplacement;

    private final int champDeVision;
    private Hexagone position; // null = pas encore placée
    private final List<Arme> armes = new ArrayList<>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /* Références “joueur”  */
    private final Joueur joueur; // ← keep only this
    private boolean aAttaqueCeTour = false; // Nouveau champ


    private int calculDegats(Unite cible, TypeTerrain terrain) {
        if (armes.isEmpty())
            return 0;
        Arme arme = armes.get(0);
        Random rnd = new Random();
        if (rnd.nextInt(100) >= arme.getPrecision())
            return 0;

        int variation = rnd.nextInt(arme.getDegats() + 1) - arme.getDegats() / 2;
        int defense = cible.getDefense();
        double bonus = terrain.getBonusDefense() / 100.0;
        return Math.max(0, arme.getDegats() + variation - (int) (defense * bonus));
    }

    /* -------------------- CONSTRUCTEURS --------------------------------- */

    /** Constructeur complet (moteur) */
    public Unite(String nom, TypeUnite typeUnite,
            Joueur joueur, List<Arme> armes) {

        this.nom = nom;
        this.typeUnite = typeUnite;
        this.joueur = joueur;

        this.defenseBase = typeUnite.getDefense(); // ← corriger ici

        this.cheminImage = "resources/" + nom.toLowerCase() + ".png";
        this.icone = new ImageIcon(cheminImage);

        this.pointsVieMax = typeUnite.getPointsVieMax();
        this.pointsVie = pointsVieMax;
        this.pointsDeplacementMax = typeUnite.getDeplacement();
        this.pointsDeplacement = pointsDeplacementMax;
        this.champDeVision = typeUnite.getChampDeVision();

        this.armes.addAll(armes);
    }

    /** Constructeur simplifié (créations rapides dans la vue) */
    public Unite(String nom, String imagePath, Joueur joueur,
            int pv, int att, int dep) {

        this.nom = nom;
        this.typeUnite = null;

        this.cheminImage = imagePath;
        this.icone = new ImageIcon(imagePath);
        this.joueur = joueur; // ✅
        this.defenseBase = Math.max(1, att / 2); // petite heuristique

        this.pointsVieMax = pv;
        this.pointsVie = pv;
        this.pointsDeplacementMax = dep;
        this.pointsDeplacement = dep;
        this.champDeVision = 5; // valeur par défaut

    }

    /* ------------------------ Getters ----------------------------------- */
    public String getNom() {
        return nom;
    }

    public ImageIcon getIcone() {
        return icone;
    }

    public int getPointsVie() {
        return pointsVie;
    }

    public int getAttaque() {
        return armes.isEmpty() ? 0 : armes.get(0).getDegats();
    }

    public int getDeplacementRestant() {
        return pointsDeplacement;
    }

    public int getChampDeVision() {
        return champDeVision;
    }

    public List<Arme> getArmes() {
        return armes;
    }

    public Joueur getJoueur() {
        return joueur;
    } // moteur

    public Hexagone getPosition() {
        return position;
    }

    public int getDefense() {
        return (typeUnite != null) ? typeUnite.getDefense() : defenseBase;
    }

    public void setPosition(Hexagone h) {
        position = h;
    }

    /* ------------------------ Manip -------------------------------- */

    /** Alias demandé par OrdreRepos.java */
    public void seReposer() {
        resetDeplacement();
    }

    /** Ajoute une arme (appelé par PlateauManager) */
    public void ajouterArme(Arme a) {
        if (a != null)
            armes.add(a);
    }
    public void reinitialiserIcone() {
    this.icone = new ImageIcon(this.cheminImage); // cheminImage should be stored
}


    /* ------------------------ Mouvements -------------------------------- */
    public void resetDeplacement() {
        int oldValue = pointsDeplacement;
        pointsDeplacement = pointsDeplacementMax;
        pcs.firePropertyChange("deplacement", oldValue, pointsDeplacement);
    }

    public void reduireDeplacement(int cout) {
        int oldValue = pointsDeplacement;
        pointsDeplacement = Math.max(0, pointsDeplacement - cout);
        pcs.firePropertyChange("deplacement", oldValue, pointsDeplacement);
    }

    /* -------------------------- Combat ---------------------------------- */
    /** Renvoie la quantité de dégâts réellement infligés (0 si raté). */
    public int attaquer(Unite cible, TypeTerrain terrain) {
        if (armes.isEmpty())
            return 0;

        Arme arme = armes.get(0);
        Random rnd = new Random();

        if (rnd.nextInt(100) >= arme.getPrecision())
            return 0; // coup manqué

        int variation = rnd.nextInt(arme.getDegats() + 1) - arme.getDegats() / 2;
        int defense = cible.getDefense();
        double bonus = terrain.getBonusDefense() / 100.0;

        int degats = arme.getDegats() + variation - (int) (defense * bonus);
        degats = Math.max(0, degats);

        int oldPv = cible.pointsVie;
        cible.pointsVie = Math.max(0, cible.pointsVie - degats);
        cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);
        return degats;

    }

    /*public boolean frapper(Unite cible, TypeTerrain terrain) {
        int degats = calculDegats(cible, terrain); // ↙︎ extraction du calcul
        int oldPv = cible.pointsVie;
        cible.pointsVie = Math.max(0, cible.pointsVie - degats);
        cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);
        return cible.pointsVie == 0;
    }*/

    public void reinitialiserDeplacement() {
        resetDeplacement();
    }

    /** Déplacement simple (utilisé par OrdreDeplacement) */
    public boolean seDeplacer(Hexagone destination) {
        if (destination == null)
            return false;
        int cout = destination.getTypeTerrain().getCoutDeplacement();
        if (pointsDeplacement < cout)
            return false;
        if (destination.getUnite() != null)
            return false; // occupé
        pointsDeplacement -= cout;
        this.position = destination;
        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    // Ajoutez ces nouvelles méthodes :
    public boolean peutAttaquer() {
        return !aAttaqueCeTour && getDeplacementRestant() > 0;
    }

    public void setAAttaqueCeTour(boolean aAttaque) {
        boolean oldValue = this.aAttaqueCeTour;
        this.aAttaqueCeTour = aAttaque;
        this.pcs.firePropertyChange("aAttaqueCeTour", oldValue, aAttaque);
    }

    public void resetTour() {
        this.aAttaqueCeTour = false;
        // Ne pas reset les points de déplacement ici, c'est fait ailleurs
    }

    // Modifiez la méthode frapper() pour marquer l'attaque :
    public boolean frapper(Unite cible, TypeTerrain terrain) {
        if (!peutAttaquer()) {
            return false;
        }

        int degats = calculDegats(cible, terrain);
        int oldPv = cible.pointsVie;
        cible.pointsVie = Math.max(0, cible.pointsVie - degats);
        cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);

        // Marquer que l'unité a attaqué ce tour
        this.aAttaqueCeTour = true;

        return cible.pointsVie == 0;
    }
    public boolean aAttaqueCeTour() {
        return aAttaqueCeTour;
    }
}


