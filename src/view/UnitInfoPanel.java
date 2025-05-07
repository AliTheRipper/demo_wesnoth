package view;

import javax.swing.*;
import java.awt.*;

public class UnitInfoPanel extends JPanel {
    public UnitInfoPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(20, 20, 30)); // unified dark background
        setPreferredSize(new Dimension(500, 100));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 180)); // semi-transparent black background
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g); // paint children
    }
}
