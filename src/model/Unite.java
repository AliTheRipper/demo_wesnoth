package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;
import javax.swing.ImageIcon;

/**
 * Implémentation unifiée : • constructeur
 * “moteur” (String, TypeUnite, Joueur, List<Arme>) • constructeur
 * “vue”    (String nom, String img, int idJoueur, pv, att, dep) Les deux mondes
 * (moteur / IHM) cohabitent sans se gêner.
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
    private Hexagone position;
    private final List<Arme> armes = new ArrayList<>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final Joueur joueur;
    private boolean aAttaqueCeTour = false;

    private boolean enFuiteDansVillage = false;
    private boolean aBougeCeTour;

    private int calculDegats(Unite cible, TypeTerrain terrain) {
        if (armes.isEmpty()) {
            return 0;
        }

        Arme arme = armes.get(0);
        int pAtt = arme.getDegats();
        int pDef = cible.getDefense();
        int bonus = terrain.getBonusDefense();

        double defenseEff = pDef + (pDef * (bonus / 100.0));
        int defenseFinale = (int) Math.ceil(defenseEff);

        int dBrut = Math.max(0, pAtt - defenseFinale);

        Random rnd = new Random();
        int alea = rnd.nextInt(dBrut + 1) - (dBrut / 2);

        return Math.max(0, dBrut + alea);
    }

    /**
     * Constructeur principal pour le moteur de jeu.
     *
     * @param nom Nom de l’unité
     * @param typeUnite Type d’unité (définit les stats de base)
     * @param joueur Joueur propriétaire de l’unité
     * @param armes Liste des armes associées à l’unité
     */
    public Unite(String nom, TypeUnite typeUnite,
            Joueur joueur, List<Arme> armes) {

        this.nom = nom;
        this.typeUnite = typeUnite;
        this.joueur = joueur;

        this.defenseBase = typeUnite.getDefense();

        this.cheminImage = "resources/" + nom.toLowerCase() + ".png";
        this.icone = new ImageIcon(cheminImage);

        this.pointsVieMax = typeUnite.getPointsVieMax();
        this.pointsVie = pointsVieMax;
        this.pointsDeplacementMax = typeUnite.getDeplacement();
        this.pointsDeplacement = pointsDeplacementMax;
        this.champDeVision = typeUnite.getChampDeVision();

        this.armes.addAll(armes);
    }

    /**
     * Constructeur simplifié pour l’interface graphique.
     *
     * @param nom Nom de l’unité
     * @param imagePath Chemin de l’image de l’unité
     * @param joueur Joueur propriétaire
     * @param pv Points de vie
     * @param att Attaque
     * @param dep Déplacement
     */
    public Unite(String nom, String imagePath, Joueur joueur,
            int pv, int att, int dep) {

        this.nom = nom;
        this.typeUnite = null;

        this.cheminImage = imagePath;
        this.icone = new ImageIcon(imagePath);
        this.joueur = joueur;
        this.defenseBase = Math.max(1, att / 2);

        this.pointsVieMax = pv;
        this.pointsVie = pv;
        this.pointsDeplacementMax = dep;
        this.pointsDeplacement = dep;
        this.champDeVision = 5;

    }

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
    }

    public Hexagone getPosition() {
        return position;
    }

    public int getDefense() {
        return (typeUnite != null) ? typeUnite.getDefense() : defenseBase;
    }

    public void setPosition(Hexagone hex) {
        this.position = hex;
    }

    public boolean estEnFuiteDansVillage() {
        return enFuiteDansVillage;
    }

    public void setEnFuiteDansVillage(boolean enFuite) {
        this.enFuiteDansVillage = enFuite;
    }

    /**
     * Repos de l’unité sur un village (récupération de PV). Réinitialise l’état
     * de déplacement et d’attaque si possible.
     */
    public void seReposer() {

        if (!aAttaqueCeTour && position != null) {
            TypeTerrain type = position.getTypeTerrain();
            if (type == TypeTerrain.REGULAR_TILE) {
                int recuperation = (int) Math.ceil(pointsVieMax * 0.10);
                int oldPv = pointsVie;
                pointsVie = Math.min(pointsVie + recuperation, pointsVieMax);
                pcs.firePropertyChange("pv", oldPv, pointsVie);

                int healed = pointsVie - oldPv;
                if (healed > 0) {
                    pcs.firePropertyChange("healed", null, healed);
                }

                if (enFuiteDansVillage && pointsVie >= pointsVieMax / 2) {
                    System.out.println("💪 " + nom + " a récupéré ≥ 50% PV, quitte le village");
                    enFuiteDansVillage = false;
                }
            }

        } else {
            System.out.println("  Ne peut pas se reposer (attaque ou pas de position)");
        }

        aBougeCeTour = false;
        aAttaqueCeTour = false;
        System.out.println("💤 " + nom + " se repose");
        resetDeplacement();
    }

    /**
     * Ajoute une arme (appelé par PlateauManager)
     */
    public void ajouterArme(Arme a) {
        if (a != null) {
            armes.add(a);
        }
    }

    public void reinitialiserIcone() {
        this.icone = new ImageIcon(this.cheminImage);
    }

    /**
     * Réinitialise les points de déplacement de l’unité à leur maximum.
     */
    public void resetDeplacement() {
        int oldValue = pointsDeplacement;
        pointsDeplacement = pointsDeplacementMax;
        pcs.firePropertyChange("deplacement", oldValue, pointsDeplacement);
    }

    /**
     * Réduit les points de déplacement restants d’un certain coût.
     *
     * @param cout Coût en points de déplacement
     */
    public void reduireDeplacement(int cout) {
        int oldValue = pointsDeplacement;
        pointsDeplacement = Math.max(0, pointsDeplacement - cout);
        pcs.firePropertyChange("deplacement", oldValue, pointsDeplacement);
    }

    /**
     * Effectue une attaque contre une unité cible. Tient compte de la
     * précision, des dégâts de l’arme, et du terrain.
     *
     * @param cible Unité ciblée
     * @param terrain Terrain sur lequel la cible se trouve
     * @return Dégâts réellement infligés
     */
    public int attaquer(Unite cible, TypeTerrain terrain) {
        if (!peutAttaquer() || armes.isEmpty()) {
            return 0;
        }

        Arme arme = armes.get(0);
        Random rnd = new Random();

        if (rnd.nextInt(100) >= arme.getPrecision()) {
            return 0;
        }

        int variation = rnd.nextInt(arme.getDegats() + 1) - arme.getDegats() / 2;
        int defense = cible.getDefense();
        double bonus = terrain.getBonusDefense() / 100.0;

        int degats = arme.getDegats() + variation - (int) (defense * bonus);
        degats = Math.max(0, degats);

        int oldPv = cible.pointsVie;
        cible.pointsVie = Math.max(0, cible.pointsVie - degats);
        cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);

        this.aAttaqueCeTour = true;

        return degats;
    }

    public void reinitialiserDeplacement() {
        resetDeplacement();
    }

    /**
     * Tente de déplacer l’unité vers l’hexagone donné.
     *
     * @param destination Case cible
     * @return true si le déplacement a réussi, false sinon
     */
    public boolean seDeplacer(Hexagone destination) {
        if (destination == null) {
            return false;
        }

        int cout = destination.getTypeTerrain().getCoutDeplacement();
        if (pointsDeplacement < cout) {
            return false;
        }

        if (destination.getUnite() != null) {
            return false;
        }

        if (this.position != null) {
            this.position.setUnite(null);
        }

        destination.setUnite(this);
        this.setPosition(destination);

        pointsDeplacement -= cout;
        this.position = destination;
        this.aBougeCeTour = true;
        return true;
    }

    /**
     * Ajoute un écouteur d’événements pour les changements de propriétés (PV,
     * déplacement, etc).
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Vérifie si l’unité peut encore attaquer pendant ce tour.
     *
     * @return true si elle peut attaquer, false sinon
     */
    public boolean peutAttaquer() {
        return !aAttaqueCeTour;
    }

    public void setAAttaqueCeTour(boolean aAttaque) {
        boolean oldValue = this.aAttaqueCeTour;
        this.aAttaqueCeTour = aAttaque;
        this.pcs.firePropertyChange("aAttaqueCeTour", oldValue, aAttaque);
    }

    /**
     * Réinitialise les états de tour de l’unité (attaque et déplacement).
     */
    public void resetTour() {
        this.aAttaqueCeTour = false;
        this.aBougeCeTour = false;
    }

    /**
     * Frappe l’unité cible avec une attaque calculée sans vérifier la
     * précision.
     *
     * @param cible Unité à frapper
     * @param terrain Terrain de la cible
     * @return true si la cible est tuée, false sinon
     */
    public boolean frapper(Unite cible, TypeTerrain terrain) {
        if (!peutAttaquer()) {
            return false;
        }

        int degats = calculDegats(cible, terrain);
        int oldPv = cible.pointsVie;
        cible.pointsVie = Math.max(0, cible.pointsVie - degats);
        cible.pcs.firePropertyChange("pv", oldPv, cible.pointsVie);

        this.aAttaqueCeTour = true;

        return cible.pointsVie == 0;
    }

    public boolean aAttaqueCeTour() {
        return aAttaqueCeTour;
    }

    public boolean estVivant() {
        return this.pointsVie > 0;
    }

    public int getPv() {
        return this.pointsVie;
    }

    public PlateauDeJeu getPlateau() {
        return (position != null) ? position.getPlateau() : null;
    }

    public int getPointsVieMax() {
        return this.pointsVieMax;
    }

    /**
     * Vérifie si l’unité peut se déplacer vers une case donnée.
     *
     * @param destination Hexagone cible
     * @return true si la case est atteignable
     */
    public boolean peutAller(Hexagone destination) {
        if (destination == null) {
            return false;
        }
        if (destination.getUnite() != null) {
            return false;
        }
        int cout = destination.getTypeTerrain().getCoutDeplacement();
        return cout <= pointsDeplacement;
    }

}
