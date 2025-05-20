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


public class Joueur implements Serializable {
    private String nom;
    private boolean estIA;
    private List<Unite> unites;
    private String couleur;

    //IA
    private List<Joueur> tousLesJoueurs;
    public void setTousLesJoueurs(List<Joueur> joueurs) {
        this.tousLesJoueurs = joueurs;
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
    

    private PlateauDeJeu plateau;

    public void setPlateau(PlateauDeJeu plateau) {
        this.plateau = plateau;
    }   

    public PlateauDeJeu getPlateau() {
        return plateau;
    }

    

    public Joueur(String nom, boolean estIA, String couleur) {
        this.nom = nom;
        this.estIA = estIA;
        this.couleur = couleur;
        this.unites = new ArrayList<>();
    }

    public List<Unite> getUnites() {
        return unites;
    }

    public void ajouterUnite(Unite u) {
        unites.add(u);
    }

    public String getNom() {
        return nom;
    }

    public boolean estIA() {
        return estIA;
    }
// In Joueur.java
public void setNom(String nom) {
    this.nom = nom;
}

    public String getCouleur() {
        return couleur;
    }

    //IA
    // Méthode appelée pour exécuter un tour complet (à implémenter selon ton contrôleur)
    public void jouerTour(BoardPanel board) {
        System.out.println(">> IA joue son tour (" + nom + ")");
    Set<Hexagone> hexasOccupes = new HashSet<>();
         Unite cibleCommune = null;
    if (estIA) {
        cibleCommune = trouverCibleGlobale(); // ✅ defined in correct scope
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
            System.out.println("   - " + unite.getNom() + " à " + pos);
        }
    

    }
    
    //IA
    public Ordre creerOrdrePourUnite(Unite uniteIA, Unite cibleCommune, BoardPanel board, Set<Hexagone> hexasOccupes)
 {
        if (!uniteIA.estVivant() || uniteIA.getDeplacementRestant() <= 0 || uniteIA.getPosition() == null)
            return new OrdreRepos(uniteIA);
        // ✅ Si l'unité est dans un village après une fuite et a récupéré ≥ 50% PV, elle quitte le village
Hexagone destination = chercherCaseProche(uniteIA, cibleCommune);
if (destination != null && hexasOccupes.contains(destination)) {
    return new OrdreRepos(uniteIA); // skip to avoid crowding
}


        if (uniteIA.estEnFuiteDansVillage() && uniteIA.getPointsVie() >= uniteIA.getPointsVieMax() * 0.5) {

            PlateauDeJeu plateau = uniteIA.getPlateau();
            Hexagone pos = uniteIA.getPosition();
            int x = pos.getX(), y = pos.getY();
            int largeur = plateau.getLargeur(), hauteur = plateau.getHauteur();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx, ny = y + dy;
                    if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) continue;

                    Hexagone voisin = plateau.getHexagone(nx, ny);
                    if (!voisin.getTypeTerrain().estVillage() && voisin.getUnite() == null && uniteIA.peutAller(voisin)) {

                        System.out.println("✅ " + uniteIA.getNom() + " quitte le village (PV ≥ 50%)");
                        uniteIA.setEnFuiteDansVillage(false);  // on désactive l'état de fuite
                        
                        return new OrdreDeplacement(uniteIA, voisin);
                    }
                }
            }
        }


    
        // Fuite si faible
        if (uniteIA.getPointsVie() < uniteIA.getPointsVieMax() * 0.3) {
            Hexagone villageAtteignable = chercherVillageAccessible(uniteIA);
            if (villageAtteignable != null) {
                System.out.println("🏘️ Village atteignable trouvé pour fuir : " + villageAtteignable.getX() + "," + villageAtteignable.getY());
                uniteIA.setEnFuiteDansVillage(true); 
                return new OrdreDeplacement(uniteIA, villageAtteignable);
            }
            
            
            Hexagone villageProche = chercherVillageProche(uniteIA);
            if (villageProche != null) {
                Hexagone versVillage = chercherCaseProche(uniteIA, villageProche);
                if (versVillage != null) {
                    System.out.println("🏃 Se rapproche d’un village vers : " + versVillage.getX() + "," + versVillage.getY());
                    return new OrdreDeplacement(uniteIA, versVillage);
                }
            }
            
            Hexagone recul = chercherCaseLoin(uniteIA);
            if (recul != null) {
                System.out.println("😰 Fuite standard vers : " + recul.getX() + "," + recul.getY());
                return new OrdreDeplacement(uniteIA, recul);
            }
            
            System.out.println("😩 Aucune case trouvée, se repose.");
            return new OrdreRepos(uniteIA);
            
        }
    
        // Attaque si une cible à portée
        Unite meilleureCible = null;
        int minPv = Integer.MAX_VALUE;
        for (Joueur autre : getAutresJoueurs()) {
            for (Unite cible : autre.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null) continue;
    
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
            System.out.println("⚔️ IA attaque la cible la plus faible : " + meilleureCible.getNom() + " (" + minPv + " PV)");
            Unite cible = meilleureCible;
            return new Ordre(uniteIA) {
                @Override
                public void executer() {
                    board.lancerCombat(uniteIA, cible);
                }
            };
        }
    
        // Sinon, on avance vers la cible commune
        if (cibleCommune != null) {
            if (destination != null) {
                return new Ordre(uniteIA) {
                    @Override
                    public void executer() {
                        uniteIA.seDeplacer(destination);
    
                        // Attaque après déplacement
                        for (Joueur autre : getAutresJoueurs()) {
                            for (Unite cible : autre.getUnites()) {
                                if (!cible.estVivant() || cible.getPosition() == null) continue;
    
                                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                                Arme arme = uniteIA.getArmes().isEmpty() ? null : uniteIA.getArmes().get(0);
    
                                if (arme != null && distance == 1 && arme.getPortee() == 1)

 {
                                    System.out.println("⚔️ IA attaque après déplacement !");
                                    board.lancerCombat(uniteIA, cible);
                                    return;
                                }
                            }
                        }
                    }
                };
            }
        }
    
        System.out.println("💤 IA ne trouve aucune action utile, repose " + uniteIA.getNom());
        return new OrdreRepos(uniteIA);

    }
    
    private Unite trouverMeilleureCible(Unite uniteIA) {
        Unite meilleure = null;
        int meilleurScore = Integer.MAX_VALUE;
    
        for (Joueur adversaire : getAutresJoueurs()) {
            for (Unite cible : adversaire.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null)
                    continue;
    
                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                int pv = cible.getPointsVie();
    
                // calculer nombre d’alliés proches autour de la cible
                int alliesProches = 0;
                for (Unite autre : adversaire.getUnites()) {
                    if (autre != cible && autre.estVivant() && autre.getPosition() != null) {
                        if (calculerDistance(autre.getPosition(), cible.getPosition()) <= 2) {
                            alliesProches++;
                        }
                    }
                }
    
                // Score : privilégie les isolés (-bonus) et les faibles (-PV)
                int score = distance + (pv / 2) + (alliesProches * 10); // chaque allié proche augmente le score
    
                if (score < meilleurScore) {
                    meilleurScore = score;
                    meilleure = cible;
                }
            }
        }
    
        if (meilleure != null) {
            System.out.println("🎯 Cible stratégique choisie : " + meilleure.getNom() +
                " (score = " + meilleurScore + ", PV = " + meilleure.getPointsVie() + ")");
        } else {
            System.out.println("🚫 Aucune cible valable trouvée pour " + uniteIA.getNom());
        }
    
        return meilleure;
    }
    
    private int calculerDistance(Hexagone a, Hexagone b) {
        if (a == null || b == null) return Integer.MAX_VALUE; 
    
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
        return new int[] { x, y, z };
    }
    
    
    private Hexagone chercherCaseProche(Unite unite, Unite cible) {
    PlateauDeJeu plateau = unite.getPlateau();
    Hexagone depart = unite.getPosition();
    Hexagone objectif = cible.getPosition();

    int maxDep = unite.getDeplacementRestant();
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
            // On est adjacent à la cible
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
                if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) continue;

                Hexagone voisin = plateau.getHexagone(nx, ny);
                if (visited.contains(voisin)) continue;
                if (voisin.getUnite() != null && voisin != objectif) continue;
                if (voisin.getTypeTerrain().getCoutDeplacement() >= 999) continue;

                visited.add(voisin);
                cameFrom.put(voisin, current);
                queue.add(voisin);
            }
        }
    }
    

    return null;
}
private Hexagone chercherCaseLoin(Unite unite) {
    PlateauDeJeu plateau = unite.getPlateau();
    Hexagone position = unite.getPosition();
    if (position == null) return null;

    int largeur = plateau.getLargeur();
    int hauteur = plateau.getHauteur();

    // Liste des ennemis
    List<Unite> ennemis = new ArrayList<>();
    for (Joueur j : getAutresJoueurs()) {
        for (Unite u : j.getUnites()) {
            if (u.estVivant() && u.getPosition() != null)
                ennemis.add(u);
        }
    }

    // Étape 1 : chercher un village sûr d'abord
    for (int y = 0; y < hauteur; y++) {
        for (int x = 0; x < largeur; x++) {
            Hexagone hex = plateau.getHexagone(x, y);
            System.out.println("🧱 Case testée : " + x + "," + y + " → " + hex.getTypeTerrain() + " | village ? " + hex.getTypeTerrain().estVillage());
            if (hex.getUnite() != null) continue;
            if (!hex.getTypeTerrain().estVillage()) continue;

            boolean safe = true;
            for (Unite e : ennemis) {
                int d = calculerDistance(hex, e.getPosition());
                if (d <= 3) {
                    safe = false;
                    break;
                }
            }

            if (safe) {
                System.out.println("🏘️ Village sûr trouvé pour fuir : " + x + "," + y);
                return hex;
            }
        }
    }

    // Étape 2 : sinon, continuer à fuir loin comme avant
    // Calcule du barycentre des ennemis
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
    if (dist == 0) dist = 1; // évite division par 0
    int dirX = (int) Math.round(dx / dist);
    int dirY = (int) Math.round(dy / dist);

    for (int r = 1; r <= unite.getDeplacementRestant(); r++) {
        int nx = position.getX() + dirX * r;
        int ny = position.getY() + dirY * r;

        if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) continue;

        Hexagone hex = plateau.getHexagone(nx, ny);
        if (hex.getUnite() != null) continue;
        if (hex.getTypeTerrain().getCoutDeplacement() >= 999) continue;

        boolean safe = true;
        for (Unite e : ennemis) {
            int d = calculerDistance(hex, e.getPosition());
            if (d <= 3) {
                safe = false;
                break;
            }
        }
        if (safe) {
            System.out.println("🏃 Fuite classique vers : " + nx + "," + ny);
            return hex;
        }
    }

    return null;
}

private Unite trouverCibleGlobale() {
    Unite meilleure = null;
    int minScore = Integer.MAX_VALUE;

    for (Joueur autre : getAutresJoueurs()) {
        for (Unite cible : autre.getUnites()) {
            if (!cible.estVivant() || cible.getPosition() == null) continue;

            int pv = cible.getPointsVie();
            int score = pv; // Simple : plus faible en PV

            if (score < minScore) {
                minScore = score;
                meilleure = cible;
            }
        }
    }

    if (meilleure != null) {
        System.out.println("🎯 Cible commune du tour IA : " + meilleure.getNom() + " (" + meilleure.getPointsVie() + " PV)");
    }

    return meilleure;
}

private Hexagone chercherVillageAccessible(Unite unite) {
    PlateauDeJeu plateau = unite.getPlateau();
    Hexagone meilleure = null;
    int minDistance = Integer.MAX_VALUE;

    for (int x = 0; x < plateau.getLargeur(); x++) {
        for (int y = 0; y < plateau.getHauteur(); y++) {
            Hexagone hex = plateau.getHexagone(x, y);
            if ((hex.getTypeTerrain().estVillage() || hex.getTypeTerrain() == TypeTerrain.REGULAR_TILE)
    && hex.getUnite() == null)
{
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

private Hexagone chercherCaseProche(Unite unite, Hexagone objectif) {
    PlateauDeJeu plateau = unite.getPlateau();
    Hexagone depart = unite.getPosition();

    int maxDep = unite.getDeplacementRestant();
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
            // On est adjacent à l’objectif
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
                if (nx < 0 || ny < 0 || nx >= largeur || ny >= hauteur) continue;

                Hexagone voisin = plateau.getHexagone(nx, ny);
                if (visited.contains(voisin)) continue;
                if (voisin.getUnite() != null && voisin != objectif) continue;
                if (voisin.getTypeTerrain().getCoutDeplacement() >= 999) continue;

                visited.add(voisin);
                cameFrom.put(voisin, current);
                queue.add(voisin);
            }
        }
    }

    return null;
}



}
