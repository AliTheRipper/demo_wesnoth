package model;

import java.util.*;

public class MainTest {
    public static void main(String[] args) {
        // Joueurs
        Joueur alice = new Joueur("Alice", false, "bleu");
        Joueur bob = new Joueur("Bob", false, "rouge");

        // Armes (utilise le constructeur avec type, degats, coups, précision)
        Arme epee = new Arme("Épée", "mêlée", 5, 1, 80);
        Arme hache = new Arme("Hache", "mêlée", 7, 1, 75);
        Arme arc = new Arme("Arc", "distance", 6, 1, 85);
        Arme baton = new Arme("Bâton", "mêlée", 5, 1, 70);
        Arme lance = new Arme("Lance", "mêlée", 8, 1, 80);

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

        Hexagone colline = new Hexagone(1, 0, TypeTerrain.REGULAR);
Hexagone montagne = new Hexagone(2, 0, TypeTerrain.BASIC);
Hexagone village = new Hexagone(3, 0, TypeTerrain.REGULAR_TILE);


        // Positionnement
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
        System.out.println("PV Infanterie apres : " + infanterie.getPointsVie());

        System.out.println("PV Cavalier avant : " + cavalier.getPointsVie());
        mage.attaquer(cavalier, cavalier.getPosition().getTypeTerrain());
        System.out.println("PV Cavalier apres : " + cavalier.getPointsVie());

        System.out.println("\n--- Fin du tour ---");
    }
}
