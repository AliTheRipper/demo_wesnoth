package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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

        infoPanel = new InfoPanel(manager.joueur1.getNom(), manager.joueur2.getNom(), manager.plateau, MainMenu.gothicFont);

        boardPanel = new BoardPanel(infoPanel, manager);
        infoPanel.getMiniMapPanel().setBoardPanel(boardPanel);

        configurerUI();
        configurerBoutons();
        infoPanel.majJoueurActif(manager.joueurActif);

    }

    private void configurerUI() {

        infoPanel.setPreferredSize(new Dimension(400, 0));
        infoPanel.setBackground(new Color(20, 20, 30));
        boardPanel.setPreferredSize(new Dimension(1400, 800));

        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(Color.BLACK);
        boardContainer.add(boardPanel);

        JScrollPane scrollPane = new JScrollPane(boardContainer);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        configurerDeplacementAutomatique(scrollPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setComponentZOrder(infoPanel, 0);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(1.0);
        infoPanel.getFinPartieButton().addActionListener(e -> {
            boolean confirmed = InfoPanel.showStyledConfirmDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            if (confirmed) {
                Container parent = getParent();
                while (parent != null && !(parent instanceof JFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JFrame) {
                    JFrame frame = (JFrame) parent;
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w != null) {
                        w.dispose();
                    }
                    new MainMenu();
                }

            }
        });

        infoPanel.getFinTourButton().addActionListener(e -> {
            boardPanel.passerAuTourSuivant();

            if (boardPanel.getJoueurActif().estIA()) {
                System.out.println("🤖 L’IA réfléchit...");

                Timer t = new Timer(500, ev -> {
                    boardPanel.getJoueurActif().jouerTour(boardPanel);
                    boardPanel.passerAuTourSuivant();
                });
                t.setRepeats(false);
                t.start();
            }
        });

        infoPanel.getAnnulerMouvementButton().addActionListener(e -> boardPanel.annulerDernierDeplacement());

        infoPanel.getSauvegarderButton().addActionListener(e -> {

            String nom = InfoPanel.showCustomInputDialog(this);
            if (nom != null && !nom.isEmpty()) {
                PlateauManager.sauvegarderDansFichier(manager, nom);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès !");
            }
        });

        add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            JViewport viewport = scrollPane.getViewport();
            Dimension viewSize = viewport.getExtentSize();
            Dimension boardSize = boardPanel.getPreferredSize();

            int centerX = (boardSize.width - viewSize.width) / 2;
            int centerY = (boardSize.height - viewSize.height) / 2;

            viewport.setViewPosition(new Point(centerX, centerY));
        });

        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(getWidth() - infoPanel.getPreferredSize().width);
        });
    }

    private void configurerDeplacementAutomatique(JScrollPane scrollPane) {
        Timer autoScrollTimer = new Timer(30, e -> {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo != null) {
                Point p = pointerInfo.getLocation();

                SwingUtilities.convertPointFromScreen(p, this);

                if (infoPanel.getBounds().contains(p)) {
                    return;
                }

                SwingUtilities.convertPointFromScreen(p, scrollPane);

                JViewport viewport = scrollPane.getViewport();
                Rectangle view = viewport.getViewRect();

                int maxX = boardPanel.getWidth() - view.width;
                int maxY = boardPanel.getHeight() - view.height;

                int newX = view.x;
                int newY = view.y;

                if (p.x < 30) {
                    newX = Math.max(view.x - 20, 0);
                } else if (p.x > scrollPane.getWidth() - 30) {
                    newX = Math.min(view.x + 20, maxX);
                }

                if (p.y < 30) {
                    newY = Math.max(view.y - 20, 0);
                } else if (p.y > scrollPane.getHeight() - 30) {
                    newY = Math.min(view.y + 20, maxY);
                }

                viewport.setViewPosition(new Point(newX, newY));
            }
        });
        autoScrollTimer.start();
    }

    private void configurerBoutons() {

        infoPanel.getAnnulerMouvementButton().addActionListener(e -> boardPanel.annulerDernierDeplacement());

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
        if (w != null) {
            w.dispose();
        }
    }

    private void sauvegarderPartie() {
        String nom = InfoPanel.showCustomInputDialog(this);
        if (nom != null && !nom.isEmpty()) {
            PlateauManager.sauvegarderDansFichier(manager, nom);
            InfoPanel.showStyledInfoDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Partie sauvegardee avec succes !",
                    "Sauvegarde"
            );

        }
    }

    private void ouvrirEditeurMap() {
        if (mapEditor == null || !mapEditor.isVisible()) {
            mapEditor = new MapEditor();
            mapEditor.setLocationRelativeTo(this);
            mapEditor.setVisible(true);

            mapEditor.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {

                    boardPanel.repaint();
                }
            });
        }
    }

    public GameWindow() {
        this(new MainMenu(), PlateauManager.initialiserNouvellePartie("Joueur 1", "Joueur 2", false));

    }

    public void refreshUI() {
        infoPanel.majJoueurActif(manager.joueurActif);
        boardPanel.repaint();
    }
}
