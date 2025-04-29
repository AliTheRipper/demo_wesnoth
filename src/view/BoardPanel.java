package view;

import model.PlateauDeJeu;
import model.Hexagone;
import model.TypeTerrain;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final int HEX_SIZE = 30;
    private final int HEX_WIDTH = (int) (Math.sqrt(3) * HEX_SIZE);
    private final int HEX_HEIGHT = 2 * HEX_SIZE;
    private PlateauDeJeu plateau;
    private int hoveredCol = -1;
    private int hoveredRow = -1;
    private boolean visionActive = false;

    public BoardPanel() {
        this.plateau = new PlateauDeJeu("map/map.txt");

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                updateHoveredHexagon(e.getX(), e.getY());
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    private void handleClick(int mouseX, int mouseY) {
        if (hoveredCol >= 0 && hoveredRow >= 0) {
            visionActive = true;

            for (int y = 0; y < plateau.getHauteur(); y++) {
                for (int x = 0; x < plateau.getLargeur(); x++) {
                    plateau.getHexagone(x, y).setVisible(false);
                }
            }

            int rayon = 2;
            for (int dy = -rayon; dy <= rayon; dy++) {
                for (int dx = -rayon; dx <= rayon; dx++) {
                    int nx = hoveredCol + dx;
                    int ny = hoveredRow + dy;
                    if (nx >= 0 && ny >= 0 && nx < plateau.getLargeur() && ny < plateau.getHauteur()) {
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance <= rayon) {
                            plateau.getHexagone(nx, ny).setVisible(true);
                        }
                    }
                }
            }

            repaint();
        }
    }

    private void updateHoveredHexagon(int mouseX, int mouseY) {
        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);

        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();

        int offsetX = (getWidth() - stepX * cols) / 2;
        int offsetY = (getHeight() - stepY * rows) / 2;

        int adjustedX = mouseX - offsetX;
        int adjustedY = mouseY - offsetY;

        int col = adjustedX / stepX;
        int row = (col % 2 == 0) ? adjustedY / stepY : (adjustedY - stepY / 2) / stepY;

        int[][] candidates = {
                {col, row}, {col - 1, row}, {col + 1, row},
                {col, row - 1}, {col, row + 1},
                {col - 1, row - 1}, {col + 1, row - 1},
                {col - 1, row + 1}, {col + 1, row + 1}
        };

        hoveredCol = -1;
        hoveredRow = -1;

        for (int[] candidate : candidates) {
            int cCol = candidate[0];
            int cRow = candidate[1];
            if (cCol >= 0 && cRow >= 0 && cCol < cols && cRow < rows) {
                Polygon hex = createHexagon(cCol, cRow);
                if (hex.contains(mouseX, mouseY)) {
                    hoveredCol = cCol;
                    hoveredRow = cRow;
                    break;
                }
            }
        }

        repaint();
    }

    private Polygon createHexagon(int col, int row) {
        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);

        int offsetX = (getWidth() - stepX * plateau.getLargeur()) / 2;
        int offsetY = (getHeight() - stepY * plateau.getHauteur()) / 2;

        int x = col * stepX + offsetX;
        int y = row * stepY + offsetY;

        if (col % 2 != 0) {
            y += stepY / 2;
        }

        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int px = (int) (x + HEX_SIZE * Math.cos(angle));
            int py = (int) (y + HEX_SIZE * Math.sin(angle));
            hex.addPoint(px, py);
        }

        return hex;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();

        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);

        int offsetX = (getWidth() - stepX * cols) / 2;
        int offsetY = (getHeight() - stepY * rows) / 2;

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) {
                    y += stepY / 2;
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

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(hex);

        Image img = plateau.getHexagone(col, row).getTypeTerrain().getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = (int) (HEX_HEIGHT);
        g2.drawImage(img, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);

        if (visionActive && !plateau.getHexagone(col, row).isVisible()) {
            g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent
            g2.fillPolygon(hex);
        }

        g2.setClip(null);

        if (col == hoveredCol && row == hoveredRow) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(hex);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);
        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();
        int width = stepX * cols + HEX_SIZE;
        int height = stepY * rows + HEX_SIZE;
        return new Dimension(width, height);
    }
}