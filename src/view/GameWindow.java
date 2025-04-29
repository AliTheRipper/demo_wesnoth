package view;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // ➕ Demander les noms des joueurs
        StartDialog dialog = new StartDialog(this);
        dialog.setVisible(true);
        String nomJoueur1 = dialog.getJoueur1();
        String nomJoueur2 = dialog.getJoueur2();

        // ✅ Créer les composants avec les noms
        InfoPanel infoPanel = new InfoPanel(nomJoueur1, nomJoueur2);
        infoPanel.setPreferredSize(new Dimension(240, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(true);

        BoardPanel board = new BoardPanel(infoPanel, nomJoueur1, nomJoueur2);
        board.setPreferredSize(new Dimension(1400, 800)); // ou dynamique

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        // ✅ Séparation avec JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(1.0); // scrollPane prend l’espace principal

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Fixer la position du panneau de droite une fois visible
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(getWidth() - 210));

        // Actions
        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> board.annulerDernierDeplacement());

        setVisible(true);
    }
}
