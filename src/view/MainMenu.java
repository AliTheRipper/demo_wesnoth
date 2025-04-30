package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainMenu extends JFrame {
    private static GameWindow currentGame = null;

    public MainMenu() {
        setTitle("Wargame - Menu Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("WARGAME", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 32));
        add(titre, BorderLayout.NORTH);

        JPanel boutons = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton nouvellePartie = new JButton("Commencer une Partie");
        JButton continuerPartie = new JButton("Continuer Partie");
        JButton voirSauvegardes = new JButton("Parties Sauvegardées");
        JButton apropos = new JButton("À propos du jeu");
        JButton quitter = new JButton("Quitter");

        boutons.add(nouvellePartie);

        if (currentGame != null) {
            boutons.add(continuerPartie);
        }

        boutons.add(voirSauvegardes);
        boutons.add(apropos);
        boutons.add(quitter);

        add(boutons, BorderLayout.CENTER);

        nouvellePartie.addActionListener(e -> {
            setVisible(false); // cache le menu sans le fermer
            currentGame = new GameWindow(this);
        });

        continuerPartie.addActionListener(e -> {
            setVisible(false);
            if (currentGame != null) {
                currentGame.setVisible(true);
            }
        });

        voirSauvegardes.addActionListener(e -> {
            File folder = new File("sauvegardes");
            if (!folder.exists()) folder.mkdirs();

            File[] saves = folder.listFiles((dir, name) -> name.endsWith(".save"));

            if (saves == null || saves.length == 0) {
                JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvée.");
                return;
            }

            String[] noms = new String[saves.length];
            for (int i = 0; i < saves.length; i++) {
                noms[i] = saves[i].getName().replace(".save", "");
            }

            String selection = (String) JOptionPane.showInputDialog(
                    this, "Choisir une sauvegarde :", "Chargement",
                    JOptionPane.PLAIN_MESSAGE, null, noms, noms[0]);

            if (selection != null) {
                setVisible(false);
                currentGame = new GameWindow(this, selection);
            }
        });

        apropos.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Jeu de stratégie au tour par tour. Créé par McInflame."));

        quitter.addActionListener(e -> System.exit(0));
    }

    public static void clearCurrentGame() {
        currentGame = null;
    }
}
