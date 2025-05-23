package model;

/**
 * Enumération représentant les différents types d’unités dans le jeu. Chaque
 * type d’unité possède des caractéristiques de combat et de mobilité : attaque,
 * défense, déplacement, champ de vision et points de vie maximum.
 */
public enum TypeUnite {
    INFANTERIE(5, 3, 6, 4, 28),
    INFANTERIE_LOURDE(10, 10, 4, 4, 38),
    CAVALERIE(8, 3, 8, 6, 38),
    MAGE(5, 1, 5, 5, 24),
    ARCHER(6, 2, 5, 7, 33);

    private final int attaque;
    private final int defense;
    private final int deplacement;
    private final int vision;
    private final int pointsVieMax;

    /**
     * Constructeur interne pour initialiser les caractéristiques d'un type
     * d’unité.
     *
     * @param attaque Valeur d’attaque de base
     * @param defense Valeur de défense de base
     * @param deplacement Points de déplacement disponibles par tour
     * @param vision Champ de vision de l’unité
     * @param pointsVieMax Points de vie maximum
     */
    TypeUnite(int attaque, int defense, int deplacement, int vision, int pointsVieMax) {
        this.attaque = attaque;
        this.defense = defense;
        this.deplacement = deplacement;
        this.vision = vision;
        this.pointsVieMax = pointsVieMax;
    }

    /**
     * Retourne la valeur d’attaque de l’unité.
     *
     * @return Points d’attaque
     */
    public int getAttaque() {
        return attaque;
    }

    /**
     * Retourne la valeur de défense de l’unité.
     *
     * @return Points de défense
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Retourne le nombre de points de déplacement que possède l’unité par tour.
     *
     * @return Points de déplacement
     */
    public int getDeplacement() {
        return deplacement;
    }

    /**
     * Retourne la portée de vision de l’unité.
     *
     * @return Nombre de cases visibles autour
     */
    public int getChampDeVision() {
        return vision;
    }

    /**
     * Retourne les points de vie maximum de l’unité.
     *
     * @return PV max
     */
    public int getPointsVieMax() {
        return pointsVieMax;
    }
}
