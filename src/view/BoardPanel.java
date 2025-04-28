package view;

import model.PlateauDeJeu;
import model.Hexagone;
import model.TypeTerrain;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private final int HEX_SIZE = 30;
    private final int HEX_WIDTH = (int) (Math.sqrt(3) * HEX_SIZE); // √3 * rayon
    private final int HEX_HEIGHT = 2 * HEX_SIZE; // 2 * rayon (hauteur totale)
    private PlateauDeJeu plateau;

    public BoardPanel() {
        this.plateau = new PlateauDeJeu("map/map.txt"); // Corrigé : pas besoin de /map/
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int cols = plateau.getLargeur();
    int rows = plateau.getHauteur();

    int stepX = (int) (HEX_WIDTH * 0.90);  // 75% de la largeur
    int stepY = (int) (HEX_HEIGHT * 0.85); // **!!! 75% de la hauteur !!!**

    int totalWidth = stepX * cols;
    int totalHeight = stepY * rows;

    int offsetX = (getWidth() - totalWidth) / 2;
    int offsetY = (getHeight() - totalHeight) / 2;

    for (int col = 0; col < cols; col++) {
        for (int row = 0; row < rows; row++) {
            int x = col * stepX + offsetX;
            int y = row * stepY + offsetY;

            if (col % 2 != 0) {
                y += stepY / 2; // décalage pour colonnes impaires
            }

            drawHexagon(g, x, y, col, row);
        }
    }
}


    private void drawHexagon(Graphics g, int centerX, int centerY, int col, int row) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int px = (int) (centerX + HEX_SIZE * Math.cos(angle));
            int py = (int) (centerY + HEX_SIZE * Math.sin(angle));
            hex.addPoint(px, py);
        }
    
        TypeTerrain terrain = plateau.getHexagone(col, row).getTypeTerrain();
        Image img = terrain.getIcon().getImage();
    
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(hex);
    
        // IMPORTANT : Réduire encore plus l'image pour qu'elle ne dépasse pas
        int imgWidth = (int) (HEX_WIDTH * 0.6);   // 60% de la largeur de l'hexagone
        int imgHeight = (int) (HEX_HEIGHT * 0.6); // 60% de la hauteur
    
        g2.drawImage(img, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);
    
        g2.setClip(null);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(hex);
    
        g2.dispose();
    }
    
    

    @Override
    public Dimension getPreferredSize() {
        int stepX = (int) (HEX_WIDTH * 0.75);
        int stepY = HEX_HEIGHT * 3 / 4;
        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();
        int width = stepX * cols + HEX_SIZE;
        int height = stepY * rows + HEX_SIZE;
        return new Dimension(width, height);
    }
}
