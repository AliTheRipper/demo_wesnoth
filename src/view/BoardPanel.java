

package view;

import model.PlateauDeJeu;
import model.Hexagone;
import model.TypeTerrain;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private final int HEX_SIZE = 30;
    private final int HEX_WIDTH = 2 * HEX_SIZE;
    private final int HEX_HEIGHT = (int) (Math.sqrt(3) * HEX_SIZE);
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        int stepX = (int) (HEX_WIDTH * 0.75);
        int stepY = HEX_HEIGHT;
    
        int cols = 20;
        int rows = 20;
    
        int totalWidth = stepX * cols;
        int totalHeight = stepY * rows;
    
        int offsetX = (getWidth() - totalWidth) / 2;
        int offsetY = (getHeight() - totalHeight) / 2;
    
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
    
                if (col % 2 != 0) {
                    y += stepY / 2;
                }
    
                drawHexagon(g, x, y);
            }
        }
    }
    
    
    private void drawHexagon(Graphics g, int centerX, int centerY) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int px = (int) (centerX + HEX_SIZE * Math.cos(angle));
            int py = (int) (centerY + HEX_SIZE * Math.sin(angle));
            hex.addPoint(px, py);
        }
        g.drawPolygon(hex);
    }
    @Override
public Dimension getPreferredSize() {
    int stepX = (int) (HEX_WIDTH * 0.75);
    int stepY = HEX_HEIGHT;
    int cols = 20;
    int rows = 20;
    int width = stepX * cols + HEX_SIZE;
    int height = stepY * rows + HEX_SIZE;
    return new Dimension(width, height);
}

}
