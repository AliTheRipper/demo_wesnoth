package view;

import java.awt.*;
import javax.swing.*;

public class GameWindow extends JPanel {
    private PlateauManager manager;

    public GameWindow(MainMenu menu, PlateauManager manager) {
        this.manager = manager;
        lancerUIAvec(manager, menu);
    }

    public GameWindow(MainMenu menu, String nomSauvegarde) {
        this.manager = PlateauManager.chargerDepuisFichier(nomSauvegarde);
        if (manager != null) {
            lancerUIAvec(manager, menu);
        } else {
            JOptionPane.showMessageDialog(menu, "Erreur : impossible de charger la sauvegarde.");
            menu.showMainMenu(); // Re-show menu
        }
    }

    private void lancerUIAvec(PlateauManager manager, MainMenu menu) {
        setLayout(new BorderLayout());

        // Utilisez simplement le paramètre 'manager' sans le redéclarer
        // PlateauManager manager = new PlateauManager(); // Cette ligne doit être
        // supprimée.

        InfoPanel infoPanel = new InfoPanel("Joueur1", "Joueur2", manager.plateau);

        infoPanel.setPreferredSize(new Dimension(200, 0));

        infoPanel.setOpaque(true);
        infoPanel.setBackground(new Color(20, 20, 30)); // même fond que les autres parties

        BoardPanel board = new BoardPanel(infoPanel, manager);
        board.setPreferredSize(new Dimension(1400, 800));

        JScrollPane scrollPane = new JScrollPane(board);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setWheelScrollingEnabled(false); // optional, prevents accidental wheel scroll

        scrollPane.setBorder(null);
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
                    int maxY = board.getHeight() - view.height;
                    viewport.setViewPosition(new Point(view.x, Math.min(view.y + 20, maxY)));
                }
            }
        });
        autoScrollTimer.start();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoPanel);
        splitPane.setComponentZOrder(infoPanel, 0); // Force infoPanel devant
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
                    if (w != null)
                        w.dispose(); // Close the current GameWindow frame
                    new MainMenu();
                }

            }
        });

        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> board.annulerDernierDeplacement());

        infoPanel.getSauvegarderButton().addActionListener(e -> {
            // Parent should be the GameWindow (this), not infoPanel, to center the dialog
            // correctly
            String nom = InfoPanel.showCustomInputDialog(this);
            if (nom != null && !nom.isEmpty()) {
                PlateauManager.sauvegarderDansFichier(manager, nom);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès !");
            }
        });

        add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            int infoWidth = infoPanel.getPreferredSize().width;
            splitPane.setDividerLocation(getWidth() - infoWidth);
        });
    }

    // juste à l’intérieur de la classe GameWindow
    public GameWindow() {
        this(new MainMenu(), PlateauManager.initialiserNouvellePartie());
    }

}
