package model;

import java.util.*;

public class MainTest {
    public static void main(String[] args) {
        // Joueurs
        Joueur alice = new Joueur("Alice", false, "bleu");
        Joueur bob = new Joueur("Bob", false, "rouge");

        // Armes
        Arme epee = new Arme("Épée", 1, 5, false);
        Arme hache = new Arme("Hache", 1, 7, false);
        Arme arc = new Arme("Arc", 2, 6, true);
        Arme baton = new Arme("Bâton", 1, 5, false);
        Arme lance = new Arme("Lance", 1, 8, false);

        // Groupes d’armes
        List<Arme> armesInfanterie = List.of(epee);
        List<Arme> armesLourde = List.of(hache);
        List<Arme> armesCavalerie = List.of(lance);
        List<Arme> armesMage = List.of(baton);
        List<Arme> armesArcher = List.of(arc);

        // Unités de chaque joueur
        Unite infanterie = new Unite("Infanterie", TypeUnite.INFANTERIE, alice, armesInfanterie);
        Unite lourde = new Unite("Inf. Lourde", TypeUnite.INFANTERIE_LOURDE, bob, armesLourde);
        Unite cavalier = new Unite("Cavalier", TypeUnite.CAVALERIE, alice, armesCavalerie);
        Unite mage = new Unite("Mage", TypeUnite.MAGE, bob, armesMage);
        Unite archer = new Unite("Archer", TypeUnite.ARCHER, alice, armesArcher);

        // Terrains
        Hexagone forteresse = new Hexagone(0, 0, TypeTerrain.FORTERESSE);
        Hexagone colline = new Hexagone(1, 0, TypeTerrain.COLLINE);
        Hexagone montagne = new Hexagone(2, 0, TypeTerrain.MONTAGNE);
        Hexagone village = new Hexagone(3, 0, TypeTerrain.VILLAGE);

        // Positionnement
        infanterie.setPosition(forteresse);
        lourde.setPosition(forteresse);
        cavalier.setPosition(colline);
        mage.setPosition(montagne);
        archer.setPosition(village);

        System.out.println("\n--- Début du combat ---");

        // Tour de Alice
        System.out.println("\n> Tour de Alice");
        System.out.println("PV Mage avant : " + mage.getPointsVie());
        archer.attaquer(mage, mage.getPosition().getTypeTerrain());
        System.out.println("PV Mage après : " + mage.getPointsVie());

        System.out.println("PV Lourde avant : " + lourde.getPointsVie());
        infanterie.attaquer(lourde, lourde.getPosition().getTypeTerrain());
        System.out.println("PV Lourde après : " + lourde.getPointsVie());

        // Tour de Bob
        System.out.println("\n> Tour de Bob");
        System.out.println("PV Infanterie avant : " + infanterie.getPointsVie());
        lourde.attaquer(infanterie, infanterie.getPosition().getTypeTerrain());
        System.out.println("PV Infanterie après : " + infanterie.getPointsVie());

        System.out.println("PV Cavalier avant : " + cavalier.getPointsVie());
        mage.attaquer(cavalier, cavalier.getPosition().getTypeTerrain());
        System.out.println("PV Cavalier après : " + cavalier.getPointsVie());

        System.out.println("\n--- Fin du tour ---");
    }
}
