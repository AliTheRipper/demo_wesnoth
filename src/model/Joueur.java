package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import view.BoardPanel;

/**
 * Représente un joueur dans le jeu. Chaque joueur a un nom, une couleur, une
 * liste d'unités et peut être contrôlé par l'IA.
 */
public class Joueur implements Serializable {

    private String nom;
    private boolean estIA;
    private List<Unite> unites;
    private String couleur;
    private static transient List<Joueur> tousLesJoueurs = new ArrayList<>();
    private PlateauDeJeu plateau;

    public static void setTousLesJoueurs(List<Joueur> joueurs) {
        Joueur.tousLesJoueurs = joueurs;
    }

    private List<Joueur> getAutresJoueurs() {
        List<Joueur> adversaires = new ArrayList<>();
        for (Joueur j : tousLesJoueurs) {
            if (j != this) {
                adversaires.add(j);
            }
        }
        return adversaires;
    }

    public void setPlateau(PlateauDeJeu plateau) {
        this.plateau = plateau;
    }

    public PlateauDeJeu getPlateau() {
        return plateau;
    }
/**
 * Constructeur du joueur.
 *
 * @param nom     Nom du joueur
 * @param estIA   true si le joueur est contrôlé par l’IA, false sinon
 * @param couleur Couleur associée au joueur (code hexadécimal)
 */
    public Joueur(String nom, boolean estIA, String couleur) {
        this.nom = nom;
        this.estIA = estIA;
        this.couleur = couleur;
        this.unites = new ArrayList<>();
    }

    public List<Unite> getUnites() {
        return unites;
    }
/**
 * Ajoute une unité à la liste des unités du joueur.
 *
 * @param u Unité à ajouter
 */
    public void ajouterUnite(Unite u) {
        unites.add(u);
    }

    public String getNom() {
        return nom;
    }

    public boolean estIA() {
        return estIA;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCouleur() {
        return couleur;
    }
/**
 * Exécute le tour du joueur.
 * Si le joueur est une IA, elle évalue ses unités et prend des décisions :
 * déplacement, attaque, fuite vers village ou repos.
 *
 * @param board Référence au plateau graphique pour déclencher des actions visuelles
 */
    public void jouerTour(BoardPanel board) {
        Set<Hexagone> hexasOccupes = new HashSet<>();
        Unite cibleCommune = null;
        if (estIA) {
            cibleCommune = trouverCibleGlobale();
        }
        for (Unite unite : unites) {
            if (unite.estVivant() && unite.getPosition() != null && unite.getDeplacementRestant() > 0) {
                Ordre ordre = creerOrdrePourUnite(unite, cibleCommune, board, hexasOccupes);
                ordre.executer();
                if (ordre instanceof OrdreDeplacement) {
                    hexasOccupes.add(((OrdreDeplacement) ordre).getDestination());
                }
            }
        }

        for (Unite unite : unites) {
            String pos = (unite.getPosition() != null)
                    ? "(" + unite.getPosition().getX() + "," + unite.getPosition().getY() + ")"
                    : "aucune position";
        }

        board.checkVictory();

    }
/**
 * Exécute le tour du joueur.
 * Si le joueur est une IA, elle évalue ses unités et prend des décisions :
 * déplacement, attaque, fuite vers village ou repos.
 *
 * @param board Référence au plateau graphique pour déclencher des actions visuelles
 */
    public Ordre creerOrdrePourUnite(Unite uniteIA, Unite cibleCommune, BoardPanel board, Set<Hexagone> hexasOccupes) {
        if (!uniteIA.estVivant() || uniteIA.getDeplacementRestant() <= 0 || uniteIA.getPosition() == null) {
            return new OrdreRepos(uniteIA);
        }
        Hexagone destination = chercherCaseProche(uniteIA, cibleCommune);
        if (destination != null && hexasOccupes.contains(destination)) {
            return new OrdreRepos(uniteIA);
        }

        if (uniteIA.estEnFuiteDansVillage() && uniteIA.getPointsVie() >= uniteIA.getPointsVieMax() * 0.5) {

            PlateauDeJeu plateau = uniteIA.getPlateau();
            Hexagone pos = uniteIA.getPosition();
            int x = pos.getX(), y = pos.getY();
            int largeur = plateau.getLargeur(), hauteur = plateau.getHauteur();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx, ny = y + dy;
                    if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) {
                        continue;
                    }

                    Hexagone voisin = plateau.getHexagone(nx, ny);
                    if (!voisin.getTypeTerrain().estVillage() && voisin.getUnite() == null && uniteIA.peutAller(voisin)) {
                        uniteIA.setEnFuiteDansVillage(false);

                        return new OrdreDeplacement(uniteIA, voisin);
                    }
                }
            }
        }
        if (uniteIA.getPointsVie() < uniteIA.getPointsVieMax() * 0.3) {
            Hexagone villageAtteignable = chercherVillageAccessible(uniteIA);
            if (villageAtteignable != null) {
                uniteIA.setEnFuiteDansVillage(true);
                return new OrdreDeplacement(uniteIA, villageAtteignable);
            }

            Hexagone villageProche = chercherVillageProche(uniteIA);
            if (villageProche != null) {
                Hexagone versVillage = chercherCaseProche(uniteIA, villageProche);
                if (versVillage != null) {
                    return new OrdreDeplacement(uniteIA, versVillage);
                }
            }

            Hexagone recul = chercherCaseLoin(uniteIA);
            if (recul != null) {
                return new OrdreDeplacement(uniteIA, recul);
            }
            return new OrdreRepos(uniteIA);

        }

        Unite meilleureCible = null;
        int minPv = Integer.MAX_VALUE;
        for (Joueur autre : getAutresJoueurs()) {
            for (Unite cible : autre.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null) {
                    continue;
                }

                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                Arme arme = uniteIA.getArmes().isEmpty() ? null : uniteIA.getArmes().get(0);

                if (arme != null && distance == 1 && arme.getPortee() == 1) {
                    if (cible.getPointsVie() < minPv) {
                        meilleureCible = cible;
                        minPv = cible.getPointsVie();
                    }
                }
            }
        }

        if (meilleureCible != null) {
            Unite cible = meilleureCible;
            return new Ordre(uniteIA) {
                @Override
                public void executer() {
                    board.lancerCombat(uniteIA, cible);
                }
            };
        }
        if (cibleCommune != null) {
            if (destination != null) {
                return new Ordre(uniteIA) {
                    @Override
                    public void executer() {
                        uniteIA.seDeplacer(destination);
                        for (Joueur autre : getAutresJoueurs()) {
                            for (Unite cible : autre.getUnites()) {
                                if (!cible.estVivant() || cible.getPosition() == null) {
                                    continue;
                                }

                                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                                Arme arme = uniteIA.getArmes().isEmpty() ? null : uniteIA.getArmes().get(0);

                                if (arme != null && distance == 1 && arme.getPortee() == 1) {
                                    board.lancerCombat(uniteIA, cible);
                                    return;
                                }
                            }
                        }
                    }
                };
            }
        }

        return new OrdreRepos(uniteIA);

    }
/**
 * Calcule la distance hexagonale entre deux hexagones.
 * Utilise la conversion en coordonnées cubiques pour une distance exacte sur grille hexagonale.
 *
 * @param a Hexagone de départ
 * @param b Hexagone d’arrivée
 * @return Distance entre les deux hexagones
 */
    private int calculerDistance(Hexagone a, Hexagone b) {
        if (a == null || b == null) {
            return Integer.MAX_VALUE;
        }

        int[] ac = offsetToCube(a.getX(), a.getY());
        int[] bc = offsetToCube(b.getX(), b.getY());

        return Math.max(
                Math.abs(ac[0] - bc[0]),
                Math.max(Math.abs(ac[1] - bc[1]), Math.abs(ac[2] - bc[2]))
        );
    }

    private int[] offsetToCube(int col, int row) {
        int x = col;
        int z = row - (col - (col & 1)) / 2;
        int y = -x - z;
        return new int[]{x, y, z};
    }

    private Hexagone chercherCaseProche(Unite unite, Unite cible) {
        PlateauDeJeu plateau = unite.getPlateau();
        Hexagone depart = unite.getPosition();
        Hexagone objectif = cible.getPosition();
        int largeur = plateau.getLargeur();
        int hauteur = plateau.getHauteur();

        Set<Hexagone> visited = new HashSet<>();
        Queue<Hexagone> queue = new LinkedList<>();
        Map<Hexagone, Hexagone> cameFrom = new HashMap<>();

        queue.add(depart);
        visited.add(depart);

        while (!queue.isEmpty()) {
            Hexagone current = queue.poll();

            if (calculerDistance(current, objectif) <= 1) {
                Hexagone suivant = current;
                while (cameFrom.containsKey(suivant) && cameFrom.get(suivant) != depart) {
                    suivant = cameFrom.get(suivant);
                }
                return suivant;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = current.getX() + dx;
                    int ny = current.getY() + dy;
                    if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) {
                        continue;
                    }

                    Hexagone voisin = plateau.getHexagone(nx, ny);
                    if (visited.contains(voisin)) {
                        continue;
                    }
                    if (voisin.getUnite() != null && voisin != objectif) {
                        continue;
                    }
                    if (voisin.getTypeTerrain().getCoutDeplacement() >= 999) {
                        continue;
                    }

                    visited.add(voisin);
                    cameFrom.put(voisin, current);
                    queue.add(voisin);
                }
            }
        }

        return null;
    }
/**
 * Cherche un village sûr (éloigné des ennemis) pour qu’une unité affaiblie s’y réfugie.
 *
 * @param unite Unité en fuite
 * @return Hexagone village sûr ou null si aucun disponible
 */
    private Hexagone chercherCaseLoin(Unite unite) {
        PlateauDeJeu plateau = unite.getPlateau();
        Hexagone position = unite.getPosition();
        if (position == null) {
            return null;
        }

        int largeur = plateau.getLargeur();
        int hauteur = plateau.getHauteur();
        List<Unite> ennemis = new ArrayList<>();
        for (Joueur j : getAutresJoueurs()) {
            for (Unite u : j.getUnites()) {
                if (u.estVivant() && u.getPosition() != null) {
                    ennemis.add(u);
                }
            }
        }
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Hexagone hex = plateau.getHexagone(x, y);
                if (hex.getUnite() != null) {
                    continue;
                }
                if (!hex.getTypeTerrain().estVillage()) {
                    continue;
                }

                boolean safe = true;
                for (Unite e : ennemis) {
                    int d = calculerDistance(hex, e.getPosition());
                    if (d <= 3) {
                        safe = false;
                        break;
                    }
                }

                if (safe) {
                    return hex;
                }
            }
        }
        double ex = 0, ey = 0;
        for (Unite e : ennemis) {
            ex += e.getPosition().getX();
            ey += e.getPosition().getY();
        }
        ex /= ennemis.size();
        ey /= ennemis.size();

        int dx = position.getX() - (int) ex;
        int dy = position.getY() - (int) ey;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) {
            dist = 1;
        }
        int dirX = (int) Math.round(dx / dist);
        int dirY = (int) Math.round(dy / dist);

        for (int r = 1; r <= unite.getDeplacementRestant(); r++) {
            int nx = position.getX() + dirX * r;
            int ny = position.getY() + dirY * r;

            if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) {
                continue;
            }

            Hexagone hex = plateau.getHexagone(nx, ny);
            if (hex.getUnite() != null) {
                continue;
            }
            if (hex.getTypeTerrain().getCoutDeplacement() >= 999) {
                continue;
            }

            boolean safe = true;
            for (Unite e : ennemis) {
                int d = calculerDistance(hex, e.getPosition());
                if (d <= 3) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                return hex;
            }
        }

        return null;
    }
/**
 * Recherche la meilleure cible à attaquer sur tout le plateau (utilisé pour IA).
 * Sélectionne la cible avec le plus faible nombre de PV.
 *
 * @return Unité ennemie prioritaire, ou null si aucune n’est disponible
 */
    private Unite trouverCibleGlobale() {
        Unite meilleure = null;
        int minScore = Integer.MAX_VALUE;

        for (Joueur autre : getAutresJoueurs()) {
            for (Unite cible : autre.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null) {
                    continue;
                }

                int pv = cible.getPointsVie();
                int score = pv;

                if (score < minScore) {
                    minScore = score;
                    meilleure = cible;
                }
            }
        }

        if (meilleure != null) {
        }

        return meilleure;
    }
/**
 * Cherche un village que l’unité peut atteindre avec ses points de déplacement actuels.
 *
 * @param unite Unité concernée
 * @return Hexagone village atteignable ou null si aucun trouvé
 */
    private Hexagone chercherVillageAccessible(Unite unite) {
        Hexagone meilleure = null;
        int minDistance = Integer.MAX_VALUE;

        for (int x = 0; x < plateau.getLargeur(); x++) {
            for (int y = 0; y < plateau.getHauteur(); y++) {
                Hexagone hex = plateau.getHexagone(x, y);
                if ((hex.getTypeTerrain().estVillage() || hex.getTypeTerrain() == TypeTerrain.REGULAR_TILE)
                        && hex.getUnite() == null) {
                    if (unite.peutAller(hex)) {
                        int dist = distance(unite.getPosition(), hex);
                        if (dist < minDistance) {
                            minDistance = dist;
                            meilleure = hex;
                        }
                    }
                }
            }
        }

        return meilleure;
    }
/**
 * Cherche le village libre le plus proche, sans condition d’accessibilité immédiate.
 *
 * @param unite Unité concernée
 * @return Hexagone village le plus proche ou null
 */
    private Hexagone chercherVillageProche(Unite unite) {
        PlateauDeJeu plateau = unite.getPlateau();
        Hexagone meilleure = null;
        int minDistance = Integer.MAX_VALUE;

        for (int x = 0; x < plateau.getLargeur(); x++) {
            for (int y = 0; y < plateau.getHauteur(); y++) {
                Hexagone hex = plateau.getHexagone(x, y);
                if (hex.getTypeTerrain() == TypeTerrain.REGULAR_TILE && hex.getUnite() == null) {
                    int dist = distance(unite.getPosition(), hex);
                    if (dist < minDistance) {
                        minDistance = dist;
                        meilleure = hex;
                    }
                }
            }
        }

        return meilleure;
    }

    private int distance(Hexagone a, Hexagone b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.abs(dx) + Math.abs(dy);
    }
/**
 * Recherche une case atteignable proche d’un objectif donné.
 * Implémente une recherche en largeur (BFS) pour trouver un chemin.
 *
 * @param unite   Unité qui se déplace
 * @param cible   Cible vers laquelle se rapprocher
 * @return Hexagone à atteindre ou null si aucun chemin trouvé
 */
    private Hexagone chercherCaseProche(Unite unite, Hexagone objectif) {
        PlateauDeJeu plateau = unite.getPlateau();
        Hexagone depart = unite.getPosition();
        int largeur = plateau.getLargeur();
        int hauteur = plateau.getHauteur();

        Set<Hexagone> visited = new HashSet<>();
        Queue<Hexagone> queue = new LinkedList<>();
        Map<Hexagone, Hexagone> cameFrom = new HashMap<>();

        queue.add(depart);
        visited.add(depart);

        while (!queue.isEmpty()) {
            Hexagone current = queue.poll();

            if (calculerDistance(current, objectif) <= 1) {
                Hexagone suivant = current;
                while (cameFrom.containsKey(suivant) && cameFrom.get(suivant) != depart) {
                    suivant = cameFrom.get(suivant);
                }
                return suivant;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = current.getX() + dx;
                    int ny = current.getY() + dy;
                    if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) {
                        continue;
                    }

                    Hexagone voisin = plateau.getHexagone(nx, ny);
                    if (visited.contains(voisin)) {
                        continue;
                    }
                    if (voisin.getUnite() != null && voisin != objectif) {
                        continue;
                    }
                    if (voisin.getTypeTerrain().getCoutDeplacement() >= 999) {
                        continue;
                    }

                    visited.add(voisin);
                    cameFrom.put(voisin, current);
                    queue.add(voisin);
                }
            }
        }

        return null;
    }
}
