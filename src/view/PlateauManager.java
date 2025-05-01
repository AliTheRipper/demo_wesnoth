package view;

import model.*;

import java.io.*;

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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("sauvegardes/" + nom + ".save"))) {
            oos.writeObject(data);
        } catch (IOException e) {
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
        // joueur 1
        plateau.getHexagone(1, 1).setUnite(new Unite("Archer", "resources/archer.png", 1, 10, 3, 3));
        plateau.getHexagone(2, 2).setUnite(new Unite("Soldat", "resources/soldat.png", 1, 10, 3, 3));
        plateau.getHexagone(3, 1).setUnite(new Unite("Cavalier", "resources/cavalier.png", 1, 10, 3, 3));
    
        // joueur 2
        int h = plateau.getHauteur();
        int l = plateau.getLargeur();
        plateau.getHexagone(l - 2, h - 2).setUnite(new Unite("Archer", "resources/archer.png", 2, 10, 3, 3));
        plateau.getHexagone(l - 3, h - 3).setUnite(new Unite("Soldat", "resources/soldat.png", 2, 10, 3, 3));
        plateau.getHexagone(l - 4, h - 2).setUnite(new Unite("Cavalier", "resources/cavalier.png", 2, 10, 3, 3));
    }
    
}

