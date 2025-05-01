package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        InfoPanel infoPanel = new InfoPanel(manager.nomJoueur1, manager.nomJoueur2);
        infoPanel.setPreferredSize(new Dimension(250, 0));
        
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
                    frame.getContentPane().removeAll();
                    MainMenu menuPanel = new MainMenu(); 
                    frame.setContentPane(menuPanel);     
                    frame.revalidate();
                    frame.repaint();
                }
                
            }
        });
        
        
        infoPanel.getFinTourButton().addActionListener(e -> board.passerAuTourSuivant());
        infoPanel.getAnnulerMouvementButton().addActionListener(e -> board.annulerDernierDeplacement());

        infoPanel.getSauvegarderButton().addActionListener(e -> {
            String nom = InfoPanel.showCustomInputDialog(infoPanel);
            if (nom != null && !nom.isEmpty()) {
                // → Call your save logic here with `nom`
                System.out.println("Nom de la sauvegarde: " + nom);
            }
        });
        

        add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            int infoWidth = infoPanel.getPreferredSize().width;
            splitPane.setDividerLocation(getWidth() - infoWidth);
        });
    }
    
}
