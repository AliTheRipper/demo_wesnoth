package view;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class PlateauManager implements Serializable {

    public PlateauDeJeu plateau;
    public String nomJoueur1;
    public String nomJoueur2;
    public Joueur joueur1;
    public Joueur joueur2;
    public Joueur joueurActif;

    public static PlateauManager initialiserNouvellePartie(String nom1, String nom2, boolean joueur2IA) {
        PlateauManager m = new PlateauManager();

        m.joueur1 = new Joueur(nom1, false, "#ff0000");
        m.joueur2 = new Joueur(nom2, joueur2IA, "#0000ff");

        m.plateau = new PlateauDeJeu("map/map.txt", "map/decor.txt");

        m.plateau.setJoueur1(m.joueur1);
        m.plateau.setJoueur2(m.joueur2);

        placerUnitesParJoueur(m.plateau, m.joueur1, m.joueur2);

        m.joueurActif = m.joueur1;

        if (m.joueurActif.estIA()) {

            m.joueurActif = m.joueur1;
        }

        List<Joueur> tousLesJoueurs = List.of(m.joueur1, m.joueur2);
        m.joueur1.setTousLesJoueurs(tousLesJoueurs);
        m.joueur2.setTousLesJoueurs(tousLesJoueurs);

        return m;
    }

    public static void sauvegarderDansFichier(PlateauManager data, String nom) {
        File folder = new File("sauvegardes");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("sauvegardes/" + nom + ".save"))) {
            oos.writeObject(data);
            System.out.println("Partie sauvegardée sous : " + nom);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static PlateauManager chargerDepuisFichier(String nom) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("sauvegardes/" + nom + ".save"))) {
            PlateauManager loaded = (PlateauManager) ois.readObject();

            for (int y = 0; y < loaded.plateau.getHauteur(); y++) {
                for (int x = 0; x < loaded.plateau.getLargeur(); x++) {
                    Unite u = loaded.plateau.getHexagone(x, y).getUnite();
                    if (u != null) {
                        u.reinitialiserIcone();
                        u.setPosition(loaded.plateau.getHexagone(x, y));
                    }
                }
            }

            return loaded;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void placerUnitesParJoueur(PlateauDeJeu plat, Joueur j1, Joueur j2) {

        Unite u1j1 = nouvelleUnite("Mage", "resources/mage.png", j1);
        Hexagone h1j1 = plat.getHexagone(33, 27);
        h1j1.setUnite(u1j1);
        u1j1.setPosition(h1j1);
        j1.ajouterUnite(u1j1);

        Unite u2j1 = nouvelleUnite("Soldat", "resources/soldat.png", j1);
        Hexagone h2j1 = plat.getHexagone(34, 27);
        h2j1.setUnite(u2j1);
        u2j1.setPosition(h2j1);
        j1.ajouterUnite(u2j1);

        Unite u3j1 = nouvelleUnite("Cavalier", "resources/cavalier.png", j1);
        Hexagone h3j1 = plat.getHexagone(34, 28);
        h3j1.setUnite(u3j1);
        u3j1.setPosition(h3j1);
        j1.ajouterUnite(u3j1);

        Unite u4j1 = nouvelleUnite("Fantassin", "resources/fantassin.png", j1);
        Hexagone h4j1 = plat.getHexagone(35, 27);
        h4j1.setUnite(u4j1);
        u4j1.setPosition(h4j1);
        j1.ajouterUnite(u4j1);

        Unite u5j1 = nouvelleUnite("Voleur", "resources/voleur.png", j1);
        Hexagone h5j1 = plat.getHexagone(35, 28);
        h5j1.setUnite(u5j1);
        u5j1.setPosition(h5j1);
        j1.ajouterUnite(u5j1);

        Unite u6j1 = nouvelleUnite("Archer", "resources/archer.png", j1);
        Hexagone h6j1 = plat.getHexagone(36, 28);
        h6j1.setUnite(u6j1);
        u6j1.setPosition(h6j1);
        j1.ajouterUnite(u6j1);

        Unite u1j2 = nouvelleUnite("Mage", "resources/mage.png", j2);
        Hexagone h1j2 = plat.getHexagone(5, 20);
        h1j2.setUnite(u1j2);
        u1j2.setPosition(h1j2);
        j2.ajouterUnite(u1j2);

        Unite u2j2 = nouvelleUnite("Soldat", "resources/soldat.png", j2);
        Hexagone h2j2 = plat.getHexagone(6, 20);
        h2j2.setUnite(u2j2);
        u2j2.setPosition(h2j2);
        j2.ajouterUnite(u2j2);

        Unite u3j2 = nouvelleUnite("Cavalier", "resources/cavalier.png", j2);
        Hexagone h3j2 = plat.getHexagone(6, 21);
        h3j2.setUnite(u3j2);
        u3j2.setPosition(h3j2);
        j2.ajouterUnite(u3j2);

        Unite u4j2 = nouvelleUnite("Fantassin", "resources/fantassin.png", j2);
        Hexagone h4j2 = plat.getHexagone(7, 20);
        h4j2.setUnite(u4j2);
        u4j2.setPosition(h4j2);
        j2.ajouterUnite(u4j2);

        Unite u5j2 = nouvelleUnite("Voleur", "resources/voleur.png", j2);
        Hexagone h5j2 = plat.getHexagone(7, 21);
        h5j2.setUnite(u5j2);
        u5j2.setPosition(h5j2);
        j2.ajouterUnite(u5j2);

        Unite u6j2 = nouvelleUnite("Archer", "resources/archer.png", j2);
        Hexagone h6j2 = plat.getHexagone(8, 21);
        h6j2.setUnite(u6j2);
        u6j2.setPosition(h6j2);
        j2.ajouterUnite(u6j2);
    }

    private static Unite nouvelleUnite(String nom, String img, Joueur owner) {
        int pv = 30;
        int att = 5;
        int dep = 5;
        List<Arme> armes = new ArrayList<>();

        switch (nom) {
            case "Mage" -> {
                pv = 24;
                att = 7;
                dep = 5;
                armes.add(new Arme("Eclair magique", 2, 10, true));
            }
            case "Fantassin" -> {
                pv = 38;
                att = 11;
                dep = 4;
                armes.add(new Arme("Hache", 1, 7, false));
            }
            case "Voleur" -> {
                pv = 24;
                att = 6;
                dep = 6;
                armes.add(new Arme("Dague", 1, 6, false));
            }
            case "Cavalier" -> {
                pv = 38;
                att = 9;
                dep = 8;
                armes.add(new Arme("Lance", 1, 9, false));
            }
            case "Archer" -> {
                pv = 33;
                att = 6;
                dep = 5;
                armes.add(new Arme("Arc long", 3, 7, true));
                armes.add(new Arme("Poignard", 1, 4, false));
            }
            case "Soldat" -> {
                pv = 35;
                att = 8;
                dep = 5;
                armes.add(new Arme("Epee", 1, 8, false));
            }
        }

        Unite u = new Unite(nom, img, owner, pv, att, dep);
        armes.forEach(u::ajouterArme);
        return u;
    }

    public void passerAuJoueurSuivant(BoardPanel board) {
        joueurActif = (joueurActif == joueur1) ? joueur2 : joueur1;

        if (joueurActif.estIA()) {
            joueurActif.jouerTour(board);
        }
    }

}
