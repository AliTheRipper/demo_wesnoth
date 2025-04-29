package view;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        BoardPanel board = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(board);
        add(scrollPane);

        // === Barre de menu ===
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem quitter = new JMenuItem("Retour au Menu Principal");

        quitter.addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });
        menuFichier.add(quitter);

        JMenu menuAide = new JMenu("Aide");
        JMenuItem regles = new JMenuItem("Règles du jeu");
        JMenuItem apropos = new JMenuItem("À propos");

        regles.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "But du jeu : détruire les unités ennemies ou survivre jusqu'au dernier tour.\n" +
                        "Chaque unité a des caractéristiques propres (attaque, défense, déplacement).\n" +
                        "Clique sur la carte pour afficher la zone de vision."));
        apropos.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Wargame Java v1.0\nRéalisé par Ilias H."));

        menuAide.add(regles);
        menuAide.add(apropos);

        menuBar.add(menuFichier);
        menuBar.add(menuAide);

        setJMenuBar(menuBar);
    }
}
