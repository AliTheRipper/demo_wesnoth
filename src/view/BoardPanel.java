package view;

import model.Hexagone;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;
import javax.swing.Timer;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Queue;

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
    private int joueurActif = 1;
    private InfoPanel infoPanel;

    private int xDepart = -1;
    private int yDepart = -1;
    private Unite derniereUniteDeplacee = null;
    private int derniereXDepart = -1;
    private int derniereYDepart = -1;
    
    private final Image backgroundImage = new ImageIcon("resources/plaine.png").getImage();

    public void annulerDernierDeplacement() {
        if (derniereUniteDeplacee != null && derniereXDepart != -1 && derniereYDepart != -1) {
            // Trouve sa position actuelle
            for (int y = 0; y < plateau.getHauteur(); y++) {
                for (int x = 0; x < plateau.getLargeur(); x++) {
                    Hexagone hex = plateau.getHexagone(x, y);
                    if (hex.getUnite() == derniereUniteDeplacee) {
                        hex.setUnite(null); // on enlève de là
                        break;
                    }
                }
            }
    
            // On la remet à sa position de départ
            plateau.getHexagone(derniereXDepart, derniereYDepart).setUnite(derniereUniteDeplacee);
            derniereUniteDeplacee.resetDeplacement();
    
            // Mise à jour affichage
            uniteSelectionnee = derniereUniteDeplacee;
            selX = derniereXDepart;
            selY = derniereYDepart;
    
            accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());
            setHexVisibility(accessibles);
    
            infoPanel.majInfos(uniteSelectionnee);
            infoPanel.majDeplacement(uniteSelectionnee.getDeplacementRestant());
    
            // Réinitialise pour éviter d’annuler plusieurs fois
            derniereUniteDeplacee = null;
            derniereXDepart = -1;
            derniereYDepart = -1;
    
            repaint();
        }
    }
    

    public BoardPanel(InfoPanel infoPanel, String joueur1, String joueur2) {
        this.infoPanel = infoPanel;
        this.plateau = new PlateauDeJeu("map/map.txt");
        placerUnitesParJoueur();

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                updateHoveredHexagon(e.getX(), e.getY());
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                revalidate();
                repaint();
            }
        });
    }

    private void placerUnitesParJoueur() {
        ajouterUnite("Archer", "resources/archer.png", 1, 1, 1);
        ajouterUnite("Soldat", "resources/soldat.png", 1, 2, 2);
        ajouterUnite("Cavalier", "resources/cavalier.png", 1, 3, 1);

        int h = plateau.getHauteur();
        int l = plateau.getLargeur();
        ajouterUnite("Archer", "resources/archer.png", 2, l - 2, h - 2);
        ajouterUnite("Soldat", "resources/soldat.png", 2, l - 3, h - 3);
        ajouterUnite("Cavalier", "resources/cavalier.png", 2, l - 4, h - 2);
    }

    private void ajouterUnite(String nom, String image, int joueur, int x, int y) {
        Unite u = new Unite(nom, image, joueur, 10, 3, 5);
        plateau.getHexagone(x, y).setUnite(u);
    }

    private void handleClick(int mouseX, int mouseY) {
        if (hoveredCol <= 0 || hoveredRow <= 0 ||
    hoveredCol >= plateau.getLargeur() - 1 ||
    hoveredRow >= plateau.getHauteur() - 1) {
    return; // Ignore clicks on border cells
}

        if (hoveredCol >= 0 && hoveredRow >= 0) {
            Hexagone hex = plateau.getHexagone(hoveredCol, hoveredRow);
            Unite unite = hex.getUnite();

            if (unite != null && unite.getJoueur() == joueurActif) {
                uniteSelectionnee = unite;
                selX = hoveredCol;
                selY = hoveredRow;
                xDepart = selX;
                yDepart = selY;
                visionActive = true;

                accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());

                setHexVisibility(accessibles);

                infoPanel.majInfos(uniteSelectionnee);
                infoPanel.majDeplacement(uniteSelectionnee.getDeplacementRestant());

            } else if (uniteSelectionnee != null && hex.getUnite() == null && accessibles.contains(hex)) {
                int distance = calculerDistanceHex(selX, selY, hoveredCol, hoveredRow);
                uniteSelectionnee.reduireDeplacement(distance);
                  // movement by number of steps


                  plateau.getHexagone(selX, selY).setUnite(null);
                  hex.setUnite(uniteSelectionnee);
                  
                  // Mémorise pour annuler plus tard
                  derniereUniteDeplacee = uniteSelectionnee;
                  derniereXDepart = xDepart;
                  derniereYDepart = yDepart;
                  

                selX = hoveredCol;
                selY = hoveredRow;

                accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());
                setHexVisibility(accessibles);

                infoPanel.majInfos(uniteSelectionnee);
                infoPanel.majDeplacement(uniteSelectionnee.getDeplacementRestant());

            } else {
                uniteSelectionnee = null;
                selX = selY = -1;
                visionActive = false;
                accessibles.clear();
                setHexVisibility(null);

                infoPanel.majInfos(null);
                infoPanel.majDeplacement(0);
            }

            repaint();
        }
    }

    private void setHexVisibility(Set<Hexagone> visibles) {
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                plateau.getHexagone(x, y).setVisible(visibles != null && visibles.contains(plateau.getHexagone(x, y)));
            }
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
    
        hoveredCol = -1;
        hoveredRow = -1;
    
        for (int[] c : new int[][]{
                {col, row}, {col - 1, row}, {col + 1, row}, {col, row - 1},
                {col, row + 1}, {col - 1, row - 1}, {col + 1, row - 1}, {col - 1, row + 1}, {col + 1, row + 1}
        }) {
            int cx = c[0], cy = c[1];
            if (cx >= 1 && cy >= 1 && cx < cols - 1 && cy < rows - 1) {
                if (createHexagon(cx, cy).contains(mouseX, mouseY)) {
                    hoveredCol = cx;
                    hoveredRow = cy;
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
        if (col % 2 != 0) y += stepY / 2;

        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.addPoint((int) (x + HEX_SIZE * Math.cos(angle)), (int) (y + HEX_SIZE * Math.sin(angle)));
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

        Graphics2D g2 = (Graphics2D) g.create();

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) y += stepY / 2;
                drawHexagon(g2, x, y, col, row);
            }
        }

        g2.dispose();
    }

    private void drawHexagon(Graphics g, int centerX, int centerY, int col, int row) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.addPoint((int) (centerX + HEX_SIZE * Math.cos(angle)), (int) (centerY + HEX_SIZE * Math.sin(angle)));
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(hex);

        Image terrain = plateau.getHexagone(col, row).getTypeTerrain().getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
int imgHeight = HEX_HEIGHT;
g2.drawImage(terrain, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);


        Unite u = plateau.getHexagone(col, row).getUnite();
        if (u != null) {
            Image icon = u.getIcone().getImage();
            g2.drawImage(icon, centerX - HEX_SIZE / 2, centerY - HEX_SIZE / 2, HEX_SIZE, HEX_SIZE, null);
        }
        


        g2.setClip(null);
        if (col == hoveredCol && row == hoveredRow) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(hex);
        }

        if (col == selX && row == selY) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawPolygon(hex);
        }

        if (accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 255, 0, 100));
            g2.fillPolygon(hex);
        }
        if (visionActive && !plateau.getHexagone(col, row).isVisible()) {
            g2.setColor(new Color(0, 0, 0, 100)); // brouillard semi-transparent
            g2.fillPolygon(hex);
        }
        
        g2.dispose();
    }

    public void passerAuTourSuivant() {
        joueurActif = (joueurActif == 1) ? 2 : 1;

        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.getJoueur() == joueurActif) {
                    u.resetDeplacement();
                }
            }
        }

        uniteSelectionnee = null;
        selX = selY = -1;
        accessibles.clear();
        visionActive = false;

        setHexVisibility(null);
        infoPanel.majInfos(null);
        infoPanel.majDeplacement(0);
        infoPanel.majJoueurActif(joueurActif);

        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);
        int width = stepX * plateau.getLargeur();
        int height = stepY * plateau.getHauteur() + (HEX_SIZE / 2) - 80;
        return new Dimension(width, height);
    }
    


    private Set<Hexagone> calculerCasesAccessibles(int startX, int startY, int maxSteps) {
        Set<Hexagone> accessibles = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
    
        queue.add(new int[]{startX, startY, 0});
        visited.add(startX + "," + startY);
    
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], steps = current[2];
    
            // Skip border tiles completely
            if (x <= 0 || y <= 0 || x >= plateau.getLargeur() - 1 || y >= plateau.getHauteur() - 1)
                continue;
    
            if (steps > maxSteps) continue;
    
            Hexagone hex = plateau.getHexagone(x, y);
            accessibles.add(hex);
    
            int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {x % 2 == 0 ? -1 : 1, 1}, {x % 2 == 0 ? -1 : 1, -1}
            };
    
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
    
                // Skip border tiles as neighbors too
                if (nx <= 0 || ny <= 0 || nx >= plateau.getLargeur() - 1 || ny >= plateau.getHauteur() - 1)
                    continue;
    
                String key = nx + "," + ny;
                if (!visited.contains(key)) {
                    Hexagone voisin = plateau.getHexagone(nx, ny);
                    if (voisin.getUnite() == null) {
                        visited.add(key);
                        queue.add(new int[]{nx, ny, steps + 1});
                    }
                }
            }
        }
    
        return accessibles;
    }
    
    
        
    private int calculerDistanceHex(int x1, int y1, int x2, int y2) {
        // Conversion des coordonnées offset en coordonnées cubes
        int[] cube1 = offsetToCube(x1, y1);
        int[] cube2 = offsetToCube(x2, y2);
    
        return Math.max(Math.abs(cube1[0] - cube2[0]),
               Math.max(Math.abs(cube1[1] - cube2[1]),
                        Math.abs(cube1[2] - cube2[2])));
    }
    
    private int[] offsetToCube(int col, int row) {
        int x = col;
        int z = row - (col - (col & 1)) / 2;
        int y = -x - z;
        return new int[]{x, y, z};
    }
    
    public BoardPanel(InfoPanel infoPanel, PlateauManager manager) {
        this.infoPanel = infoPanel;
        this.plateau = manager.plateau;
        this.joueurActif = manager.joueurActif;

    
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                updateHoveredHexagon(e.getX(), e.getY());
            }
        });
    
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                revalidate();
                repaint();
            }
        });
        MouseMotionAdapter scrollAdapter = new MouseMotionAdapter() {
    public void mouseMoved(MouseEvent e) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) return;

        Point screenPoint = pointerInfo.getLocation();
        SwingUtilities.convertPointFromScreen(screenPoint, BoardPanel.this);

        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, BoardPanel.this);
        if (scrollPane != null) {
            JViewport viewport = scrollPane.getViewport();
            Rectangle view = viewport.getViewRect();

            if (screenPoint.y < 30) {
                int newY = Math.max(view.y - 20, 0);
                viewport.setViewPosition(new Point(view.x, newY));
            } else if (screenPoint.y > getHeight() - 30) {
                int newY = Math.min(view.y + 20, getHeight() - view.height);
                viewport.setViewPosition(new Point(view.x, newY));
            }
        }
    }
};
addMouseMotionListener(scrollAdapter);

    }
    
}
