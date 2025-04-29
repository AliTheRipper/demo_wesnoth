package view;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Wargame - Menu Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("WARGAME", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 32));
        add(titre, BorderLayout.NORTH);

        JPanel boutons = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton nouvellePartie = new JButton("Nouvelle Partie");
        JButton chargerPartie = new JButton("Charger une Partie");
        JButton quitter = new JButton("Quitter");

        boutons.add(nouvellePartie);
        boutons.add(chargerPartie);
        boutons.add(quitter);
        add(boutons, BorderLayout.CENTER);

        // Action : démarrer le jeu
        nouvellePartie.addActionListener(e -> {
            dispose(); // fermer le menu principal
            new GameWindow().setVisible(true); // ouvrir la fenêtre de jeu
        });

        // Action : quitter
        quitter.addActionListener(e -> System.exit(0));
    }
}
