package model;

import java.util.*;

/**
 * Classe principale pour tester le modèle de jeu. Crée des joueurs, des armes,
 * des unités et les place sur un plateau de jeu.
 */
public class MainTest {

    public static void main(String[] args) {

        Joueur alice = new Joueur("Alice", false, "bleu");
        Joueur bob = new Joueur("Bob", false, "rouge");

        Arme epee = new Arme("Épée", "mêlée", 5, 1, 80);
        Arme hache = new Arme("Hache", "mêlée", 7, 1, 75);
        Arme arc = new Arme("Arc", "distance", 6, 1, 85);
        Arme baton = new Arme("Bâton", "mêlée", 5, 1, 70);
        Arme lance = new Arme("Lance", "mêlée", 8, 1, 80);

        List<Arme> armesInfanterie = List.of(epee);
        List<Arme> armesLourde = List.of(hache);
        List<Arme> armesCavalerie = List.of(lance);
        List<Arme> armesMage = List.of(baton);
        List<Arme> armesArcher = List.of(arc);

        Unite cavalier = new Unite("Cavalier", TypeUnite.CAVALERIE, alice, armesCavalerie);
        Unite mage = new Unite("Mage", TypeUnite.MAGE, bob, armesMage);
        Unite archer = new Unite("Archer", TypeUnite.ARCHER, alice, armesArcher);

        Hexagone colline = new Hexagone(1, 0, TypeTerrain.REGULAR);
        Hexagone montagne = new Hexagone(2, 0, TypeTerrain.BASIC);
        Hexagone village = new Hexagone(3, 0, TypeTerrain.REGULAR_TILE);

        cavalier.setPosition(colline);
        mage.setPosition(montagne);
        archer.setPosition(village);
    }
}
