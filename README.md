# Empire in War – Wargame Tactique en Java

**Empire in War** est un jeu de stratégie tactique au tour par tour développé en Java avec le framework Swing. Ce projet simule un champ de bataille où les joueurs peuvent contrôler des unités, les déplacer sur une carte hexagonale, attaquer des ennemis, exploiter les avantages du terrain et affronter une IA. Le jeu est conçu pour être modulaire, visuellement immersif et facilement extensible.

## Présentation

Ce jeu vise à offrir une expérience tactique immersive sur une grille hexagonale. Il a été conçu dans le cadre d’un projet étudiant afin d’explorer les principes de la programmation orientée objet, la logique d’intelligence artificielle et la conception d’interfaces graphiques en Java.

### Fonctionnalités principales

- Système de combat tactique au tour par tour (Joueur contre Joueur ou Joueur contre IA)
- Plusieurs types d’unités aux statistiques et armes uniques (Archer, Mage, Cavalier, etc.)
- Mécanismes liés au terrain (bonus de défense, récupération de PV dans les villages)
- Interface interactive avec des dialogues personnalisés et des retours visuels
- Éditeur de carte intégré pour créer et modifier des cartes personnalisées
- Fonctionnalité de sauvegarde et chargement de parties
- Mini-carte pour visualiser le terrain et suivre le point de vue
- Comportement IA intégré pour les parties solo
- Intégration d’une musique de fond

## Technologies utilisées

- Java (version 17+)
- Swing pour le développement de l’interface graphique
- Conception orientée objet (POO)
- I/O fichiers pour la gestion des sauvegardes et des cartes
- Sérialisation des objets
- Polices personnalisées et thème graphique

## Structure du projet

```
demo_wesnoth/
├── map/   
├── sauvegardes/ 
├── resources/      # Ressources : polices, icônes, cartes, 
├── bin/  
├── src/      # Ressources : polices, icônes, cartes, 
│   ├── model/      # Logique du jeu : unités, terrain, 
│   ├── view/       # Interface graphique et panneaux de jeu
│   └── Main.java       # Point d’entrée de l’application
└── EmpireInWar.jar      # Fichier exécutable compilé
```

## Lancer le projet

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/AliTheRipper/demo_wesnoth.git
   ```

2. Exécutez le fichier `Main.java` pour lancer le jeu.

## Éditeur de cartes

Un éditeur de carte intégré permet à l’utilisateur de :
- Sélectionner différents types de tuiles (herbe, forêt, village, océan, etc.)
- Sauvegarder des cartes personnalisées
- Charger et modifier des cartes existantes

## Compiler et générer le fichier `.jar`

Double-cliquez sur le fichier nommé EmpireInWar.jar pour lancer le jeu.

## Comportement de l’IA

L’intelligence artificielle est conçue pour :
- Attaquer les unités ennemies proches et vulnérables
- Reculer ou se soigner en cas de faibles PV
- Capturer les villages pour restaurer sa santé

Cette logique est extensible et peut être enrichie avec des stratégies plus complexes.

## Système de sauvegarde

Les états de jeu sont enregistrés dans des fichiers `.save` dans le dossier `sauvegardes/`. Les joueurs peuvent nommer leur sauvegarde et reprendre leur progression ultérieurement.

## Contributeurs

Ce projet a été développé en groupe dans un cadre académique par :
- Khettou Hajar  
- Halmi Ilias  
- Hijazi Yahya  
- Shel Hazem  
- Zifouti Ali  

## Améliorations futures

- IA plus avancée avec une prise de décision tactique améliorée
- Mode multijoueur en réseau
- Animations visuelles pour les déplacements et attaques
- Mode campagne avec narration
- Tutoriel interactif et panneau d’aide intégré

## Licence

Ce projet a été réalisé dans un cadre pédagogique et est libre d’utilisation à des fins éducatives ou de démonstration.
