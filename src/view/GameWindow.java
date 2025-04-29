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
        infoPanel.setPreferredSize(new Dimension(240, 0)); // Largeur fixe
        infoPanel.setBackground(new Color(220, 220, 220));
        infoPanel.setOpaque(true);


        BoardPanel board = new BoardPanel(infoPanel);

        // Fixe la taille minimale du plateau pour qu’il ne déborde jamais
        board.setPreferredSize(new Dimension(1400, 800)); // Ajuste si besoin

        // On met juste le BoardPanel dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Layout principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);   // Plateau au centre
        contentPanel.add(infoPanel, BorderLayout.EAST);      // Barre à droite

        setContentPane(contentPanel);
        pack();
        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());

    }
}
