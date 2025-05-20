package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;
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
    
        for (Unite unite : unites) {
            String pos = (unite.getPosition() != null)
                ? "(" + unite.getPosition().getX() + "," + unite.getPosition().getY() + ")"
                : "aucune position";
            System.out.println("   - " + unite.getNom() + " à " + pos);
        }
    
        if (estIA) {
            Unite cibleCommune = trouverCibleGlobale();
    
            for (Unite unite : unites) {
                if (unite.estVivant() && unite.getPosition() != null && unite.getDeplacementRestant() > 0) {
                    Ordre ordre = creerOrdrePourUnite(unite, cibleCommune, board);
                    System.out.println("IA crée l'ordre : " + ordre.getClass().getSimpleName());
                    
                    ordre.executer();
                    board.repaint();
    
                    System.out.println("Ordre exécuté pour " + unite.getNom());
                }
            }
        }
    }
    
    //IA
    public Ordre creerOrdrePourUnite(Unite uniteIA, Unite cibleCommune, BoardPanel board) {
        if (!uniteIA.estVivant() || uniteIA.getDeplacementRestant() <= 0 || uniteIA.getPosition() == null)
            return new OrdreRepos(uniteIA);
    
        // Fuite si faible
        if (uniteIA.getPointsVie() < uniteIA.getPointsVieMax() * 0.3) {
            Hexagone recul = chercherCaseLoin(uniteIA);
            if (recul != null) {
                System.out.println("😰 " + uniteIA.getNom() + " est faible et fuit vers " + recul.getX() + "," + recul.getY());
                return new Ordre(uniteIA) {
                    @Override
                    public void executer() {
                        int x = recul.getX();
                        int y = recul.getY();
                        PlateauDeJeu plateau = uniteIA.getPlateau();
                        int largeur = plateau.getLargeur();
                        int hauteur = plateau.getHauteur();
                
                        if (x < 0 || y < 0 || x >= largeur || y >= hauteur) {
                            System.out.println("❌ Fuite échouée : case hors carte. " + uniteIA.getNom() + " se repose à la place.");
                            uniteIA.seReposer();
                        } else {
                            uniteIA.seDeplacer(recul);
                        }
                    }
                };
                
            } else {
                System.out.println("😩 " + uniteIA.getNom() + " est faible mais ne peut pas fuir. Elle se repose.");
                return new OrdreRepos(uniteIA);
            }
        }
    
        // Attaque si une cible à portée
        Unite meilleureCible = null;
        int minPv = Integer.MAX_VALUE;
        for (Joueur autre : getAutresJoueurs()) {
            for (Unite cible : autre.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null) continue;
    
                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                Arme arme = uniteIA.getArmes().isEmpty() ? null : uniteIA.getArmes().get(0);
    
                if (arme != null && distance <= arme.getPortee()) {
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
            Hexagone destination = chercherCaseProche(uniteIA, cibleCommune);
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
    
                                if (arme != null && distance <= arme.getPortee()) {
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
    
        return new OrdreRepos(uniteIA);
    }
    
    private Unite trouverMeilleureCible(Unite uniteIA) {
        Unite meilleure = null;
        int minScore = Integer.MAX_VALUE;
    
        for (Joueur autre : getAutresJoueurs()) {
            for (Unite cible : autre.getUnites()) {
                if (!cible.estVivant() || cible.getPosition() == null)
                    continue;
    
                int distance = calculerDistance(uniteIA.getPosition(), cible.getPosition());
                int pv = cible.getPointsVie();
    
                // Score = distance + moitié des PV (plus bas = meilleure cible)
                int score = distance + (pv / 2);
    
                if (score < minScore) {
                    minScore = score;
                    meilleure = cible;
                }
            }
        }
    
        if (meilleure != null) {
            System.out.println("🎯 Cible stratégique : " + meilleure.getNom() + " (score " + minScore + ")");
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

    // Recherche autour de la direction de fuite
    for (int r = 1; r <= unite.getDeplacementRestant(); r++) {
        int nx = position.getX() + dirX * r;
        int ny = position.getY() + dirY * r;

        if (nx < 1 || ny < 1 || nx >= largeur - 1 || ny >= hauteur - 1) continue;

        Hexagone hex = plateau.getHexagone(nx, ny);
        if (hex.getUnite() != null) continue;
        if (hex.getTypeTerrain().getCoutDeplacement() >= 999) continue;

        // Doit être à distance raisonnable des ennemis
        boolean safe = true;
        for (Unite e : ennemis) {
            int d = calculerDistance(hex, e.getPosition());
            if (d <= 3) {
                safe = false;
                break;
            }
        }
        if (safe) return hex;
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


}
