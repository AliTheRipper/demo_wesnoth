package view;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setPreferredSize(new Dimension(260, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(true);

        BoardPanel board = new BoardPanel(infoPanel);

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        // ✅ Utilise JSplitPane pour bien séparer les deux zones
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setDividerSize(0); // pas de bord visible
        splitPane.setEnabled(false); // l'utilisateur ne peut pas le déplacer
        splitPane.setResizeWeight(1.0); // toute la taille dispo va au scroll
        splitPane.setDividerLocation(-1); // sera forcé ensuite dans pack()

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Déclenche les listeners après affichage
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(getWidth() - 320));

        setVisible(true);

        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> board.annulerDernierDeplacement());
    }
}
