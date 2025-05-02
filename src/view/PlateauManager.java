package view;

import model.*;

import java.io.*;
import java.util.Arrays;

public class PlateauManager implements Serializable {
    public PlateauDeJeu plateau;
    public String nomJoueur1;
    public String nomJoueur2;
    public int joueurActif = 1;

    public static PlateauManager initialiserNouvellePartie() {
        PlateauManager m = new PlateauManager();
        m.plateau = new PlateauDeJeu("map/map.txt");
        placerUnitesParJoueur(m.plateau);
        return m;
    }

    public static void sauvegarderDansFichier(PlateauManager data, String nom) {
        File folder = new File("sauvegardes");
        if (!folder.exists()) folder.mkdirs();

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
            return (PlateauManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void placerUnitesParJoueur(PlateauDeJeu plateau) {
        // Joueur 1
        Unite archer1 = new Unite("Archer", "resources/archer.png", 1, 14, 3, 5);
        archer1.ajouterArme(new Arme("Arc long", "distance", 7, 3, 60));
        archer1.ajouterArme(new Arme("Poignard", "mêlée", 4, 2, 70));
        plateau.getHexagone(1, 1).setUnite(archer1);

        Unite soldat1 = new Unite("Soldat", "resources/soldat.png", 1, 16, 4, 4);
        soldat1.ajouterArme(new Arme("Épée", "mêlée", 8, 2, 65));
        plateau.getHexagone(2, 1).setUnite(soldat1);

        Unite cavalier1 = new Unite("Cavalier", "resources/cavalier.png", 1, 18, 5, 6);
        cavalier1.ajouterArme(new Arme("Lance", "mêlée", 9, 2, 70));
        plateau.getHexagone(3, 1).setUnite(cavalier1);

        Unite mage1 = new Unite("Mage", "resources/mage.png", 1, 12, 6, 3);
        mage1.ajouterArme(new Arme("Éclair magique", "distance", 10, 2, 60));
        plateau.getHexagone(4, 2).setUnite(mage1);

        Unite voleur1 = new Unite("Voleur", "resources/voleur.png", 1, 10, 3, 5);
        voleur1.ajouterArme(new Arme("Dague", "mêlée", 6, 3, 75));
        plateau.getHexagone(5, 2).setUnite(voleur1);

        Unite fantassin1 = new Unite("Fantassin", "resources/fantassin.png", 1, 15, 3, 4);
        fantassin1.ajouterArme(new Arme("Hache", "mêlée", 7, 2, 65));
        plateau.getHexagone(6, 1).setUnite(fantassin1);

        // Joueur 2
        int h = plateau.getHauteur();
        int l = plateau.getLargeur();

        Unite archer2 = new Unite("Archer", "resources/archer.png", 2, 14, 3, 5);
        archer2.ajouterArme(new Arme("Arc long", "distance", 7, 3, 60));
        archer2.ajouterArme(new Arme("Poignard", "mêlée", 4, 2, 70));
        plateau.getHexagone(l - 2, h - 2).setUnite(archer2);

        Unite soldat2 = new Unite("Soldat", "resources/soldat.png", 2, 16, 4, 4);
        soldat2.ajouterArme(new Arme("Épée", "mêlée", 8, 2, 65));
        plateau.getHexagone(l - 3, h - 2).setUnite(soldat2);

        Unite cavalier2 = new Unite("Cavalier", "resources/cavalier.png", 2, 18, 5, 6);
        cavalier2.ajouterArme(new Arme("Lance", "mêlée", 9, 2, 70));
        plateau.getHexagone(l - 4, h - 2).setUnite(cavalier2);

        Unite mage2 = new Unite("Mage", "resources/mage.png", 2, 12, 6, 3);
        mage2.ajouterArme(new Arme("Éclair magique", "distance", 10, 2, 60));
        plateau.getHexagone(l - 5, h - 3).setUnite(mage2);

        Unite voleur2 = new Unite("Voleur", "resources/voleur.png", 2, 10, 3, 5);
        voleur2.ajouterArme(new Arme("Dague", "mêlée", 6, 3, 75));
        plateau.getHexagone(l - 6, h - 3).setUnite(voleur2);

        Unite fantassin2 = new Unite("Fantassin", "resources/fantassin.png", 2, 15, 3, 4);
        fantassin2.ajouterArme(new Arme("Hache", "mêlée", 7, 2, 65));
        plateau.getHexagone(l - 7, h - 2).setUnite(fantassin2);
    }

}
