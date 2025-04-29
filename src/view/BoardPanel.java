package view;

import model.Hexagone;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class BoardPanel extends JPanel {
    private final int HEX_SIZE = 30;
    private final int HEX_WIDTH = (int) (Math.sqrt(3) * HEX_SIZE);
    private final int HEX_HEIGHT = 2 * HEX_SIZE;
    private PlateauDeJeu plateau;
    private int hoveredCol = -1;
    private int hoveredRow = -1;
    private boolean visionActive = false;

    private Unite uniteSelectionnee = null;
    private Set<Hexagone> accessibles = new HashSet<>();
    private int selX = -1, selY = -1;


    public BoardPanel() {
        this.plateau = new PlateauDeJeu("map/map.txt");

        // Ajout manuel de quelques unités
        plateau.getHexagone(2, 2).setUnite(new Unite("Archer", "resources/archer.png", 1, 10, 3, 3));
        plateau.getHexagone(5, 5).setUnite(new Unite("Soldat", "resources/soldat.png", 2, 12, 4, 2));


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
            Hexagone hex = plateau.getHexagone(hoveredCol, hoveredRow);
            Unite unite = hex.getUnite();

            if (unite != null) {
                uniteSelectionnee = unite;
                selX = hoveredCol;
                selY = hoveredRow;
                visionActive = true;

                accessibles = calculerCasesAccessibles(selX, selY, unite.getDeplacementRestant());

                for (int y = 0; y < plateau.getHauteur(); y++) {
                    for (int x = 0; x < plateau.getLargeur(); x++) {
                        plateau.getHexagone(x, y).setVisible(false);
                    }
                }

                for (Hexagone h : accessibles) {
                    h.setVisible(true);
                }

            } else if (uniteSelectionnee != null && hex.getUnite() == null && accessibles.contains(hex)) {
                // déplacement valide
                int cout = plateau.getCoutDeplacement(hex.getTypeTerrain());
                uniteSelectionnee.reduireDeplacement(cout);

                plateau.getHexagone(selX, selY).setUnite(null);
                hex.setUnite(uniteSelectionnee);

                // mettre à jour
                selX = hoveredCol;
                selY = hoveredRow;

                accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());

                for (int y = 0; y < plateau.getHauteur(); y++) {
                    for (int x = 0; x < plateau.getLargeur(); x++) {
                        plateau.getHexagone(x, y).setVisible(false);
                    }
                }

                for (Hexagone h : accessibles) {
                    h.setVisible(true);
                }

            } else {
                // clic ailleurs → annuler sélection
                uniteSelectionnee = null;
                selX = selY = -1;
                visionActive = false;
                accessibles.clear();

                for (int y = 0; y < plateau.getHauteur(); y++) {
                    for (int x = 0; x < plateau.getLargeur(); x++) {
                        plateau.getHexagone(x, y).setVisible(true);
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

        // Dessin du terrain
        Image terrainImg = plateau.getHexagone(col, row).getTypeTerrain().getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = HEX_HEIGHT;
        g2.drawImage(terrainImg, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);

        // Dessin de l’unité (si visible ou vision désactivée)
        Unite unite = plateau.getHexagone(col, row).getUnite();
        if (unite != null && (!visionActive || plateau.getHexagone(col, row).isVisible())) {
            Image icone = unite.getIcone().getImage();
            int uniteTaille = HEX_SIZE;
            g2.drawImage(icone, centerX - uniteTaille / 2, centerY - uniteTaille / 2, uniteTaille, uniteTaille, null);
        }

        // Dessin du brouillard si activé
        if (visionActive && !plateau.getHexagone(col, row).isVisible()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillPolygon(hex);
        }

        g2.setClip(null);

        // Contour si survolé
        if (col == hoveredCol && row == hoveredRow) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(hex);
        }

        // Contour si unité sélectionnée
        if (col == selX && row == selY) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawPolygon(hex);
        }

        g2.dispose();
        // mettre en surbrillance les cases accessibles
        if (accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 255, 0, 100)); // vert transparent
            g2.fillPolygon(hex);
        }

    }

    private Set<Hexagone> calculerCasesAccessibles(int x, int y, int pointsRestants) {
        Set<Hexagone> accessibles = new HashSet<>();
        Queue<int[]> file = new LinkedList<>();
        Map<String, Integer> dejaVisites = new HashMap<>();

        file.add(new int[]{x, y, pointsRestants});
        dejaVisites.put(x + "," + y, pointsRestants);

        while (!file.isEmpty()) {
            int[] courant = file.poll();
            int cx = courant[0], cy = courant[1], points = courant[2];

            Hexagone hex = plateau.getHexagone(cx, cy);
            accessibles.add(hex);

            // Déplacements dans les 6 directions hexagonales
            int[][] directions = {
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                    {cx % 2 == 0 ? -1 : 1, 1}, {cx % 2 == 0 ? -1 : 1, -1}
            };

            for (int[] dir : directions) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                if (nx < 0 || ny < 0 || nx >= plateau.getLargeur() || ny >= plateau.getHauteur()) continue;

                Hexagone voisin = plateau.getHexagone(nx, ny);
                int cout = plateau.getCoutDeplacement(voisin.getTypeTerrain());

                if (cout <= points && voisin.getUnite() == null) {
                    String key = nx + "," + ny;
                    if (!dejaVisites.containsKey(key) || dejaVisites.get(key) < points - cout) {
                        dejaVisites.put(key, points - cout);
                        file.add(new int[]{nx, ny, points - cout});
                    }
                }
            }
        }

        return accessibles;
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
