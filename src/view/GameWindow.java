package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.*;

public class GameWindow extends JPanel {
    private PlateauManager manager;
    private BoardPanel boardPanel;
    private InfoPanel infoPanel;
    private MainMenu menu;
    private MapEditor mapEditor;

    public GameWindow(MainMenu menu, PlateauManager manager) {
        this.menu = menu;
        this.manager = manager;
        lancerUIAvec(manager);
    }

    public GameWindow(MainMenu menu, String nomSauvegarde) {
        this.menu = menu;
        this.manager = PlateauManager.chargerDepuisFichier(nomSauvegarde);
        if (manager != null) {
            lancerUIAvec(manager);
        } else {
            JOptionPane.showMessageDialog(menu, "Erreur : impossible de charger la sauvegarde.");
            menu.showMainMenu();
        }
    }

    private void lancerUIAvec(PlateauManager manager) {
        setLayout(new BorderLayout());

        // Initialisation des composants
        infoPanel = new InfoPanel(manager.joueur1.getNom(), manager.joueur2.getNom(), manager.plateau);
        boardPanel = new BoardPanel(infoPanel, manager);
        infoPanel.getMiniMapPanel().setBoardPanel(boardPanel);

        // Configuration de l'UI
        configurerUI();
        configurerBoutons();
    }

    private void configurerUI() {
        // Style des panels
        infoPanel.setPreferredSize(new Dimension(400, 0));
        infoPanel.setBackground(new Color(20, 20, 30));
        boardPanel.setPreferredSize(new Dimension(1400, 800));

        // ScrollPane avec défilement automatique
        JScrollPane scrollPane = new JScrollPane(boardPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Configuration du défilement automatique
        configurerDeplacementAutomatique(scrollPane);

        // SplitPane principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setComponentZOrder(infoPanel, 0);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(1.0);

        add(splitPane, BorderLayout.CENTER);

        // Ajustement final
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(getWidth() - infoPanel.getPreferredSize().width);
        });
    }

    private void configurerDeplacementAutomatique(JScrollPane scrollPane) {
        Timer autoScrollTimer = new Timer(30, e -> {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo != null) {
                Point p = pointerInfo.getLocation();
                SwingUtilities.convertPointFromScreen(p, scrollPane);
                JViewport viewport = scrollPane.getViewport();
                Rectangle view = viewport.getViewRect();

                if (p.y < 30) {
                    viewport.setViewPosition(new Point(view.x, Math.max(view.y - 20, 0)));
                } else if (p.y > scrollPane.getHeight() - 30) {
                    int maxY = boardPanel.getHeight() - view.height;
                    viewport.setViewPosition(new Point(view.x, Math.min(view.y + 20, maxY)));
                }
            }
        });
        autoScrollTimer.start();
    }

    private void configurerBoutons() {
        // Bouton Fin de Partie
        infoPanel.getFinPartieButton().addActionListener(e -> {
            if (InfoPanel.showStyledConfirmDialog((JFrame)SwingUtilities.getWindowAncestor(this))) {
                retourAuMenu();
            }
        });

        // Bouton Fin de Tour
        infoPanel.getFinTourButton().addActionListener(e -> boardPanel.passerAuTourSuivant());

        // Bouton Annuler Mouvement
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> boardPanel.annulerDernierDeplacement());

        // Bouton Sauvegarder
        infoPanel.getSauvegarderButton().addActionListener(e -> sauvegarderPartie());

        infoPanel.getZoomInButton().addActionListener(e -> {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouse, boardPanel);
            boardPanel.zoomAt(mouse, true);
        });

        infoPanel.getZoomOutButton().addActionListener(e -> {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouse, boardPanel);
            boardPanel.zoomAt(mouse, false);
        });

    }

    private void retourAuMenu() {
        menu.showMainMenu();
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    private void sauvegarderPartie() {
        String nom = InfoPanel.showCustomInputDialog(this);
        if (nom != null && !nom.isEmpty()) {
            PlateauManager.sauvegarderDansFichier(manager, nom);
            JOptionPane.showMessageDialog(this,
                    "Partie sauvegardée avec succès !",
                    "Sauvegarde",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void ouvrirEditeurMap() {
        if (mapEditor == null || !mapEditor.isVisible()) {
            mapEditor = new MapEditor();
            mapEditor.setLocationRelativeTo(this);
            mapEditor.setVisible(true);

            // Fermer l'éditeur quand la partie se termine
            mapEditor.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // Recharger la map si nécessaire
                    boardPanel.repaint();
                }
            });
        }
    }

    // Constructeur par défaut
    public GameWindow() {
        this(new MainMenu(), PlateauManager.initialiserNouvellePartie());
    }

    // Méthode pour rafraîchir l'interface
    public void refreshUI() {
        infoPanel.majJoueurActif(manager.joueurActif);
        boardPanel.repaint();
    }
}