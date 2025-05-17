package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MiniMapPanel extends JPanel {
    private PlateauDeJeu plateau;
    private BoardPanel boardPanel;
    private Map<TypeTerrain, Image> terrainIcons = new HashMap<>();

    public MiniMapPanel(PlateauDeJeu plateau, BoardPanel boardPanel) {
        this.plateau = plateau;
        this.boardPanel = boardPanel;
        setPreferredSize(new Dimension(120, 120));
        setBackground(Color.DARK_GRAY);

        for (TypeTerrain t : TypeTerrain.values()) {
            Image img = t.getIcon().getImage();
            Image scaled = img.getScaledInstance(4, 4, Image.SCALE_SMOOTH);
            terrainIcons.put(t, scaled);
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
                Color color = mapColorFromTerrain(type);

                g2.setColor(color);
                g2.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
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
            case PLAINE       -> new Color(144, 238, 144); // vert clair
            case FORET        -> new Color(34, 139, 34);   // vert foncé
            case COLLINE      -> new Color(189, 183, 107); // doré/ocre
            case MONTAGNE     -> new Color(105, 105, 105); // gris foncé
            case VILLAGE      -> new Color(222, 184, 135); // beige / village
            case CHATEAU      -> new Color(169, 169, 169); // gris moyen
            case FUNGUS       -> new Color(148, 0, 211);   // violet foncé
            case EAU_PROFONDE -> new Color(30, 144, 255);  // bleu profond
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
