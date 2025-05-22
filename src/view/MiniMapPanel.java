package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import model.*;

public class MiniMapPanel extends JPanel {

    private PlateauDeJeu plateau;
    private BoardPanel boardPanel;
    private Map<TypeTerrain, Image> terrainIcons = new HashMap<>();

    public MiniMapPanel(PlateauDeJeu plateau, BoardPanel boardPanel) {
        this.plateau = plateau;
        this.boardPanel = boardPanel;

        setPreferredSize(new Dimension(120, 200));
        setBackground(new Color(20, 20, 30));
        setOpaque(true);
        setBorder(null);

        for (TypeTerrain type : TypeTerrain.values()) {
            ImageIcon icon = type.getIcon();
            Image original = icon.getImage();

            if (original instanceof BufferedImage img) {
                int cropSize = Math.min(img.getWidth(), img.getHeight()) / 2;
                int startX = (img.getWidth() - cropSize) / 2;
                int startY = (img.getHeight() - cropSize) / 2;

                BufferedImage squareCrop = img.getSubimage(startX, startY, cropSize, cropSize);
                Image scaled = squareCrop.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
                terrainIcons.put(type, scaled);

            } else {

                terrainIcons.put(type, original.getScaledInstance(10, 10, Image.SCALE_SMOOTH));
            }
        }
        new Timer(30, e -> repaint()).start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (plateau == null || boardPanel == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();

        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;
        int offsetX = (getWidth() - (cellWidth * cols)) / 2;
        int offsetY = 30 + (getHeight() - (cellHeight * rows) - 10) / 2;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TypeTerrain type = plateau.getHexagone(x, y).getTypeTerrain();
                Image img = terrainIcons.get(type);

                int drawX = offsetX + x * cellWidth;
                int drawY = offsetY + y * cellHeight;

                if (img != null) {
                    g2.drawImage(img, drawX, drawY, cellWidth, cellHeight, null);
                } else {
                    g2.setColor(mapColorFromTerrain(type));
                    g2.fillRect(drawX, drawY, cellWidth, cellHeight);
                }
            }
        }

        Container parent = boardPanel.getParent();
        while (parent != null && !(parent instanceof JViewport)) {
            parent = parent.getParent();
        }

        if (parent instanceof JViewport viewport) {
            Rectangle view = viewport.getViewRect();
            int mapWidth = boardPanel.getWidth();
            int mapHeight = boardPanel.getHeight();

            double ratioX = (double) cellWidth * cols / mapWidth;
            double ratioY = (double) cellHeight * rows / mapHeight;

            int miniX = (int) (view.x * ratioX) + offsetX;
            int miniY = (int) (view.y * ratioY) + offsetY;
            int miniW = (int) (view.width * ratioX);
            int miniH = (int) (view.height * ratioY);

            g2.setColor(new Color(212, 175, 55));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(miniX, miniY, miniW, miniH);
        }
    }

    private Color mapColorFromTerrain(TypeTerrain type) {
        return switch (type) {
            case GREEN ->
                new Color(144, 238, 144);
            case LEAF ->
                new Color(34, 139, 34);
            case REGULAR ->
                new Color(189, 183, 107);
            case BASIC ->
                new Color(105, 105, 105);
            case REGULAR_TILE ->
                new Color(222, 184, 135);
            case RUINED_KEEP ->
                new Color(169, 169, 169);
            case OCEAN ->
                new Color(30, 144, 255);
            case SUNKEN_RUIN ->
                new Color(0, 105, 180);
            default ->
                Color.GRAY;
        };
    }

    public void setBoardPanel(BoardPanel bp) {
        this.boardPanel = bp;
    }

    public void updateMiniMap() {
        repaint();
    }
}
