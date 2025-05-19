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
    public Joueur joueurActif; // ← ❶ type Joueur (plus int)

    public static PlateauManager initialiserNouvellePartie() {
        PlateauManager m = new PlateauManager();

        m.joueur1 = new Joueur("Humain 1", false, "#ff0000");
        m.joueur2 = new Joueur("Humain 2", false, "#0000ff");

        m.plateau = new PlateauDeJeu("map/map.txt", "map/decor.txt");

// Ajoute cette ligne pour associer les joueurs au plateau
m.plateau.setJoueur1(m.joueur1);
m.plateau.setJoueur2(m.joueur2);

        placerUnitesParJoueur(m.plateau, m.joueur1, m.joueur2);

        m.joueurActif = m.joueur1; // ← ❷ premier joueur actif
        return m;
    }

    public static void sauvegarderDansFichier(PlateauManager data, String nom) {
        File folder = new File("sauvegardes");
        if (!folder.exists())
            folder.mkdirs();

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

        // 🔧 FIX: restore icons manually
        for (int y = 0; y < loaded.plateau.getHauteur(); y++) {
            for (int x = 0; x < loaded.plateau.getLargeur(); x++) {
                Unite u = loaded.plateau.getHexagone(x, y).getUnite();
                if (u != null) {
                    u.reinitialiserIcone();  // You need to add this method in Unite.java
                }
            }
        }

        return loaded;

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        return null;
    }
}


    private static void placerUnitesParJoueur(PlateauDeJeu plat,
            Joueur j1, Joueur j2) {
        /* Joueur 1 */
        plat.getHexagone(21, 2).setUnite(nouvelleUnite("Mage", "resources/mage.png", j1));
        plat.getHexagone(21, 4).setUnite(nouvelleUnite("Soldat", "resources/soldat.png", j1));
        plat.getHexagone(21, 4).setUnite(nouvelleUnite("Cavalier", "resources/cavalier.png", j1));
        plat.getHexagone(22, 3).setUnite(nouvelleUnite("Fantassin", "resources/fantassin.png", j1));
        plat.getHexagone(22, 4).setUnite(nouvelleUnite("Voleur", "resources/voleur.png", j1));
        plat.getHexagone(22, 5).setUnite(nouvelleUnite("Archer", "resources/archer.png", j1));

        /* Joueur 2 – exemple en miroir */
        int h = plat.getHauteur(), l = plat.getLargeur();
        plat.getHexagone(25, 2).setUnite(nouvelleUnite("Mage", "resources/mage.png", j2));
        plat.getHexagone(25, 3).setUnite(nouvelleUnite("Soldat", "resources/soldat.png", j2));
        plat.getHexagone(25, 4).setUnite(nouvelleUnite("Cavalier", "resources/cavalier.png", j2));
        plat.getHexagone(26, 4).setUnite(nouvelleUnite("Fantassin", "resources/fantassin.png", j2));
        plat.getHexagone(26,3).setUnite(nouvelleUnite("Voleur", "resources/voleur.png", j2));
        plat.getHexagone(26, 2).setUnite(nouvelleUnite("Archer", "resources/archer.png", j2));

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
}
