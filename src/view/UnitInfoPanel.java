package view;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau affichant les informations d'une unité sélectionnée dans le menu
 * principal. Utilisé pour présenter les descriptions des différentes unités
 * jouables.
 */
public class UnitInfoPanel extends JPanel {

    /**
     * Initialise le panneau avec un fond personnalisé et une bordure stylisée.
     */
    public UnitInfoPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(20, 20, 30));
        setPreferredSize(new Dimension(500, 100));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    /**
     * Dessine un fond semi-transparent noir pour renforcer la lisibilité des
     * informations affichées.
     *
     * @param g l'objet Graphics utilisé pour le rendu
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
