package view;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // ➕ Saisie des noms au démarrage
        StartDialog dialog = new StartDialog(this);
        dialog.setVisible(true);

        String nomJoueur1 = dialog.getJoueur1();
        String nomJoueur2 = dialog.getJoueur2();

        // ✅ Création des composants avec noms
        InfoPanel infoPanel = new InfoPanel(nomJoueur1, nomJoueur2);
        infoPanel.setPreferredSize(new Dimension(240, 0));
        infoPanel.setBackground(new Color(220, 220, 220));
        infoPanel.setOpaque(true);

        BoardPanel board = new BoardPanel(infoPanel, nomJoueur1, nomJoueur2);
        board.setPreferredSize(new Dimension(1400, 800)); // Ajustable selon taille map

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ➕ Agencement
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.EAST);

        setContentPane(contentPanel);
        pack();

        // 🔁 Bouton Fin du tour
        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
    }
}
