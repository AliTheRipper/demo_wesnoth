package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Classe principale représentant la fenêtre de jeu. Gère l’interface
 * utilisateur, le plateau graphique (BoardPanel), les informations affichées
 * (InfoPanel), ainsi que les interactions telles que le zoom, la sauvegarde, la
 * fin de tour ou le retour au menu.
 */
public class GameWindow extends JPanel {

    private MainMenu menu;
    private PlateauManager manager;
    private BoardPanel boardPanel;
    private InfoPanel infoPanel;

    /**
     * Constructeur pour lancer une nouvelle partie à partir d’un PlateauManager
     * donné.
     *
     * @param menu Référence au menu principal
     * @param manager Gestionnaire de plateau contenant l’état initial du jeu
     */
    public GameWindow(MainMenu menu, PlateauManager manager) {
        this.menu = menu;
        this.manager = manager;
        lancerUIAvec(manager);
    }

    /**
     * Constructeur pour charger une partie à partir d’un nom de sauvegarde.
     *
     * @param menu Référence au menu principal
     * @param nomSauvegarde Nom du fichier de sauvegarde à charger
     */
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

    /**
     * Initialise l’interface utilisateur avec le plateau de jeu et les
     * panneaux.
     *
     * @param manager Gestionnaire du plateau à afficher
     */
    private void lancerUIAvec(PlateauManager manager) {
        setLayout(new BorderLayout());

        infoPanel = new InfoPanel(manager.joueur1.getNom(), manager.joueur2.getNom(), manager.plateau, MainMenu.gothicFont);

        boardPanel = new BoardPanel(infoPanel, manager);
        infoPanel.getMiniMapPanel().setBoardPanel(boardPanel);

        configurerUI();
        configurerBoutons();
        infoPanel.majJoueurActif(manager.joueurActif);

    }

    /**
     * Configure la disposition des composants UI, attache les écouteurs aux
     * boutons, et centre la vue sur le plateau.
     */
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
                Timer t = new Timer(500, ev -> {
                    boardPanel.getJoueurActif().jouerTour(boardPanel);
                    boardPanel.passerAuTourSuivant();
                });
                t.setRepeats(false);
                t.start();
            }
        });

        infoPanel.getAnnulerMouvementButton().addActionListener(e -> boardPanel.annulerDernierDeplacement());

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

    /**
     * Configure les actions des boutons de zoom, annulation, et sauvegarde dans
     * le panneau latéral.
     */
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

    /**
     * Constructeur par défaut pour démarrer une partie classique avec deux
     * joueurs.
     */
    public GameWindow() {
        this(new MainMenu(), PlateauManager.initialiserNouvellePartie("Joueur 1", "Joueur 2", false));

    }

    /**
     * Rafraîchit l’interface utilisateur pour refléter l’état actuel du jeu
     * (joueur actif, plateau).
     */
    public void refreshUI() {
        infoPanel.majJoueurActif(manager.joueurActif);
        boardPanel.repaint();
    }
}
