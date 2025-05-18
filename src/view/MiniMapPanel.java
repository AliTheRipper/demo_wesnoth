package view;

import java.awt.*;
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
        setPreferredSize(new Dimension(120, 120));
        setBackground(Color.DARK_GRAY);

        terrainIcons = new HashMap<>();
        for (TypeTerrain type : TypeTerrain.values()) {
            Image original = type.getIcon().getImage();
            Image scaled = original.getScaledInstance(4, 4, Image.SCALE_SMOOTH);
            terrainIcons.put(type, scaled);
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (plateau == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();

        // Taille d’un hexagone réduit (1 pixel = 1 terrain par exemple)
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                TypeTerrain type = plateau.getHexagone(x, y).getTypeTerrain();
                Image icon = terrainIcons.get(type);

                if (icon != null) {
                    g2.drawImage(icon, x * cellWidth, y * cellHeight, cellWidth, cellHeight, null);
                } else {
                    g2.setColor(mapColorFromTerrain(type)); // fallback color
                    g2.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                }
            }
        }


        // Optionnel : Afficher la "vue actuelle" de l’écran principal
        if (boardPanel != null && boardPanel.getParent() instanceof JViewport viewport) {
            Rectangle view = viewport.getViewRect();

            double zoom = boardPanel.getScale();
            int mapWidth = boardPanel.getWidth();
            int mapHeight = boardPanel.getHeight();

            int miniX = (int) ((view.x / (double) mapWidth) * getWidth());
            int miniY = (int) ((view.y / (double) mapHeight) * getHeight());
            int miniW = (int) ((view.width / (double) mapWidth) * getWidth());
            int miniH = (int) ((view.height / (double) mapHeight) * getHeight());

            g2.setColor(Color.YELLOW);
            g2.drawRect(miniX, miniY, miniW, miniH);
        }
    }

private Color mapColorFromTerrain(TypeTerrain type) {
    return switch (type) {
        case GREEN        -> new Color(144, 238, 144); // ancien PLAINE
        case LEAF         -> new Color(34, 139, 34);   // ancien FORET
        case REGULAR      -> new Color(189, 183, 107); // ancien COLLINE
        case BASIC        -> new Color(105, 105, 105); // ancien MONTAGNE
        case REGULAR_TILE -> new Color(222, 184, 135); // ancien VILLAGE
        case RUINED_KEEP  -> new Color(169, 169, 169); // ancien CHATEAU
        case OCEAN        -> new Color(30, 144, 255);  // ancien EAU_1
        case SUNKEN_RUIN  -> new Color(0, 105, 180);   // ancien EAU_2
        default           -> Color.GRAY;
    };
}


    public void setBoardPanel(BoardPanel bp) {
        this.boardPanel = bp;
    }


    public void updateMiniMap() {
        repaint();
    }

}
