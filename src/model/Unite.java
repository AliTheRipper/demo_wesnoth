package model;

import javax.swing.ImageIcon;

import view.BoardPanel;

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
    private final String nom;
    private final TypeUnite typeUnite;
    private transient ImageIcon icone;

    private final String cheminImage;
    private final int defenseBase;

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
    private boolean aBougeCeTour = false;



    private int calculDegats(Unite cible, TypeTerrain terrain) {
    if (armes.isEmpty()) return 0;

    Arme arme = armes.get(0);
    int pAtt = arme.getDegats();
    int pDef = cible.getDefense();
    int bonus = terrain.getBonusDefense();

    // Défense effective = ⌈pDef + pDef * bonus%⌉
    double defenseEff = pDef + (pDef * (bonus / 100.0));
    int defenseFinale = (int) Math.ceil(defenseEff);

    // Dégâts bruts
    int dBrut = Math.max(0, pAtt - defenseFinale);

    // Aléa ∈ [-50%, +50%]
    Random rnd = new Random();
    int alea = rnd.nextInt(dBrut + 1) - (dBrut / 2);  // de -dBrut/2 à +dBrut/2

    return Math.max(0, dBrut + alea);
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

   public void setPosition(Hexagone hex) {
    this.position = hex;
}


    /* ------------------------ Manip -------------------------------- */

    /** Alias demandé par OrdreRepos.java */
    public void seReposer() {
        System.out.println(">>>> [Repos] " + nom + " est sur : " + (position != null ? position.getTypeTerrain() : "null"));
    
        if (!aAttaqueCeTour && position != null) {
            TypeTerrain type = position.getTypeTerrain();
            System.out.println("  - Type de terrain : " + type);
            System.out.println("  - PV actuels : " + pointsVie + " / " + pointsVieMax);
    
            if (type == TypeTerrain.REGULAR_TILE) {
                int recuperation = (int) Math.ceil(pointsVieMax * 0.10);
                int oldPv = pointsVie;
                pointsVie = Math.min(pointsVie + recuperation, pointsVieMax);
                pcs.firePropertyChange("pv", oldPv, pointsVie);

                int healed = pointsVie - oldPv;
                if (healed > 0) {
                    pcs.firePropertyChange("healed", null, healed);  // 🔔 Trigger healing animation
                }

                System.out.println("  ✔ Récupère " + (pointsVie - oldPv) + " PV !");
            } else {
                System.out.println("  ❌ Terrain non valide pour repos");
            }
        } else {
            System.out.println("  ❌ Ne peut pas se reposer (attaque ou pas de position)");
        }
    
        aBougeCeTour = false;
        aAttaqueCeTour = false;
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
    if (!peutAttaquer() || armes.isEmpty())
        return 0;

    Arme arme = armes.get(0);
    Random rnd = new Random();

    if (rnd.nextInt(100) >= arme.getPrecision())
        return 0; // Missed

    int variation = rnd.nextInt(arme.getDegats() + 1) - arme.getDegats() / 2;
    int defense = cible.getDefense();
    double bonus = terrain.getBonusDefense() / 100.0;

    int degats = arme.getDegats() + variation - (int) (defense * bonus);
    degats = Math.max(0, degats);

    int oldPv = cible.pointsVie;
    cible.pointsVie = Math.max(0, cible.pointsVie - degats);
    cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);

    this.aAttaqueCeTour = true; // Mark as having attacked

    return degats;
}

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
        this.aBougeCeTour = true;
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
        this.aBougeCeTour = false;
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


