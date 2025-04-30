package view;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private PlateauManager manager;

    public GameWindow(MainMenu menu) {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Afficher la boîte pour saisir les noms
        StartDialog dialog = new StartDialog(this);
        dialog.setVisible(true);

        String nomJoueur1 = dialog.getJoueur1();
        String nomJoueur2 = dialog.getJoueur2();

        if (nomJoueur1 == null || nomJoueur2 == null) {
            dispose();
            menu.setVisible(true);
            return;
        }

        manager = PlateauManager.initialiserNouvellePartie();
        manager.nomJoueur1 = nomJoueur1;
        manager.nomJoueur2 = nomJoueur2;

        lancerUIAvec(manager, menu);
    }

    public GameWindow(MainMenu menu, String nomSauvegarde) {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        manager = PlateauManager.chargerDepuisFichier(nomSauvegarde);
        lancerUIAvec(manager, menu);
    }

    private void lancerUIAvec(PlateauManager manager, MainMenu menu) {
        InfoPanel infoPanel = new InfoPanel(manager.nomJoueur1, manager.nomJoueur2);
        infoPanel.setPreferredSize(new Dimension(250, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(true);

        BoardPanel board = new BoardPanel(infoPanel, manager);
        board.setPreferredSize(new Dimension(1400, 800));

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(1.0);

        infoPanel.getFinPartieButton().addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir terminer la partie ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                MainMenu.clearCurrentGame();
                new MainMenu().setVisible(true);
            }
        });

        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> board.annulerDernierDeplacement());

        infoPanel.getSauvegarderButton().addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(this, "Nom de la sauvegarde :");
            if (nom != null && !nom.isEmpty()) {
                PlateauManager.sauvegarderDansFichier(manager, nom);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée !");
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> {
            int infoWidth = infoPanel.getPreferredSize().width;
            splitPane.setDividerLocation(getWidth() - infoWidth);
        });

        setVisible(true);
    }
}
