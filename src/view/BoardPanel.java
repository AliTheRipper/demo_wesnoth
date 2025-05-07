
package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Queue;
import javax.sound.sampled.*; // pour lire le son

import javax.swing.*;
import model.Decoration;
import model.Hexagone;
import model.Joueur;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

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
    private InfoPanel infoPanel;
    private final java.util.List<DamageText> splash = new ArrayList<>();

    private final List<Joueur> joueurs; // injected from your controller
    private Joueur joueurActif; // instead of int joueurActif = 1;

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

    public BoardPanel(InfoPanel info, String nom1, String nom2) {
        this.infoPanel = info;
        this.plateau = new PlateauDeJeu("map/map.txt", "map/decor.txt");
        Joueur j1 = new Joueur(nom1, false, "#ff0000");
        Joueur j2 = new Joueur(nom2, false, "#0000ff");
        joueurs = List.of(j1, j2);
        joueurActif = j1;
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
        new javax.swing.Timer(30, e -> { // 30 ms ≈ 33 FPS
            splash.forEach(DamageText::tick); // monte & fait baisser alpha
            splash.removeIf(DamageText::isDead); // <= même nom que la méthode
            repaint();
        }).start();

    }

    private void placerUnitesParJoueur() {
        // Joueur 1 (en haut à gauche)
        ajouterUnite("Archer", "resources/archer.png", 1, 1, 1);
        ajouterUnite("Soldat", "resources/soldat.png", 1, 2, 2);
        ajouterUnite("Cavalier", "resources/cavalier.png", 1, 3, 1);
        ajouterUnite("Mage", "resources/mage.png", 1, 4, 2);
        ajouterUnite("Fantassin", "resources/fantassin.png", 1, 1, 3);
        ajouterUnite("Voleur", "resources/voleur.png", 1, 2, 4);

        // Joueur 2 (en bas à droite)
        int h = plateau.getHauteur();
        int l = plateau.getLargeur();

        ajouterUnite("Archer", "resources/archer.png", 2, l - 2, h - 2);
        ajouterUnite("Soldat", "resources/soldat.png", 2, l - 3, h - 3);
        ajouterUnite("Cavalier", "resources/cavalier.png", 2, l - 4, h - 2);
        ajouterUnite("Mage", "resources/mage.png", 2, l - 5, h - 3);
        ajouterUnite("Fantassin", "resources/fantassin.png", 2, l - 2, h - 4);
        ajouterUnite("Voleur", "resources/voleur.png", 2, l - 3, h - 5);
    }

    // ───── REMPLACEZ ENTIEREMENT la méthode ─────
    private void ajouterUnite(String nom, String imagePath,
            int numJoueur, int x, int y) {

        int pv = 30, att = 5, dep = 5;

        switch (nom) { // ajustement des stats
            case "Mage" -> {
                pv = 24;
                att = 7;
                dep = 5;
            }
            case "Fantassin" -> {
                pv = 38;
                att = 11;
                dep = 4;
            }
            case "Voleur" -> {
                pv = 24;
                att = 6;
                dep = 6;
            }
            case "Cavalier" -> {
                pv = 38;
                att = 9;
                dep = 8;
            }
            case "Archer" -> {
                pv = 33;
                att = 6;
                dep = 5;
            }
            case "Soldat" -> {
                pv = 35;
                att = 8;
                dep = 5;
            }
        }

        Joueur owner = joueurs.get(numJoueur - 1); // 1→0, 2→1
        Unite u = new Unite(nom, imagePath, owner, pv, att, dep);
        plateau.getHexagone(x, y).setUnite(u);
    }

    private void handleClick(int mouseX, int mouseY) {
        if (hoveredCol <= 0 || hoveredRow <= 0 ||
                hoveredCol >= plateau.getLargeur() - 1 ||
                hoveredRow >= plateau.getHauteur() - 1) {
            return; // Ignore les cases en bordure
        }

        if (hoveredCol >= 0 && hoveredRow >= 0) {
            Hexagone hex = plateau.getHexagone(hoveredCol, hoveredRow);
            Unite unite = hex.getUnite();

            // Cas 1 : Sélection d'une unité alliée
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

                // 👉 Affichage fiche d'unité (avec stats, armes...)
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

            }

            // Cas 2 : Unité ennemie voisine → Afficher fiche de combat
            else if (uniteSelectionnee != null && unite != null
                    && unite.getJoueur() != joueurActif
                    && estVoisin(selX, selY, hoveredCol, hoveredRow)) {

                boolean tuee = uniteSelectionnee.frapper(
                        unite,
                        plateau.getHexagone(hoveredCol, hoveredRow).getTypeTerrain());

                Polygon poly = createHexagon(hoveredCol, hoveredRow);
                Rectangle r = poly.getBounds();
                int cx = r.x + r.width / 2;
                int cy = r.y + r.height / 2;
                splash.add(new DamageText(cx, cy, -uniteSelectionnee.getArmes().get(0).getDegats()));

                playHitSound();

                /* ► Si la cible meurt, on vide la case et on annule la sélection */
                if (tuee) {
                    plateau.getHexagone(hoveredCol, hoveredRow).setUnite(null);
                    uniteSelectionnee = null;
                    infoPanel.majInfos(null);
                } else {
                    infoPanel.majInfos(unite); // cible encore vivante
                }

            }

            // Cas 3 : Déplacement vers une case accessible
            else if (uniteSelectionnee != null && hex.getUnite() == null && accessibles.contains(hex)) {
                int distance = calculerDistanceHex(selX, selY, hoveredCol, hoveredRow);
                uniteSelectionnee.reduireDeplacement(distance);

                plateau.getHexagone(selX, selY).setUnite(null);
                hex.setUnite(uniteSelectionnee);

                derniereUniteDeplacee = uniteSelectionnee;
                derniereXDepart = xDepart;
                derniereYDepart = yDepart;

                selX = hoveredCol;
                selY = hoveredRow;

                accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());
                setHexVisibility(accessibles);

                infoPanel.majInfos(uniteSelectionnee);
                infoPanel.majDeplacement(uniteSelectionnee.getDeplacementRestant());
            }

            // Cas 4 : Clic ailleurs = désélection
            else {
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

        for (int[] c : new int[][] {
                { col, row }, { col - 1, row }, { col + 1, row }, { col, row - 1 },
                { col, row + 1 }, { col - 1, row - 1 }, { col + 1, row - 1 }, { col - 1, row + 1 }, { col + 1, row + 1 }
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
        if (col % 2 != 0)
            y += stepY / 2;

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

        // PASS 1: Draw terrain and units (but no decoration, no overlays)
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawTerrainAndUnit(g2, x, y, col, row);
            }
        }

        // PASS 2: Draw decorations on top of terrain and units
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawDecoration(g2, x, y, col, row);
            }
        }

        // PASS 3: Draw overlays (highlight, selection, fog...) on top of everything
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawOverlays(g2, x, y, col, row);
            }
        }

        g2.dispose();
    }

    private void drawTerrainAndUnit(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Polygon hex = createHexagon(col, row);
        g2.setClip(hex);

        // 1. Terrain
        TypeTerrain terrainType = plateau.getHexagone(col, row).getTypeTerrain();
        Image terrainImage = terrainType.getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = HEX_HEIGHT;
        g2.drawImage(terrainImage, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);

        // 2. Unit
        Unite u = plateau.getHexagone(col, row).getUnite();
        if (u != null) {
            Image icon = u.getIcone().getImage();
            g2.drawImage(icon, centerX - HEX_SIZE / 2, centerY - HEX_SIZE / 2, HEX_SIZE, HEX_SIZE, null);
        }

        g2.setClip(null);
    }

    private void drawOverlays(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Polygon hex = createHexagon(col, row);

        // 1. Accessible overlay
        if (accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 255, 0, 100));
            g2.fillPolygon(hex);
        }

        // 2. Selection and hover outlines
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

        // 3. Defense bonus text
        if (col == hoveredCol && row == hoveredRow && accessibles.contains(plateau.getHexagone(col, row))) {
            int bonus = plateau.getHexagone(col, row).getTypeTerrain().getBonusDefense();
            String text = bonus + "%";
            g2.setFont(new Font("Serif", Font.BOLD, 16));
            g2.setColor(new Color(212, 175, 55));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.drawString(text, centerX - textWidth / 2, centerY + textHeight / 2);
        }

        // 4. Fog
        if (visionActive && !plateau.getHexagone(col, row).isVisible()) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillPolygon(hex);
        }
        //
        for (DamageText dt : splash) {
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, dt.alpha));
            g2.setColor(Color.RED);
            g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
            String txt = String.valueOf(dt.dmg);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txt, dt.x - fm.stringWidth(txt) / 2,
                    dt.y + fm.getAscent() / 2);
        }
        g2.setComposite(AlphaComposite.SrcOver); // réinitialise l’opacité

    }

    private void drawHexagon(Graphics g, int centerX, int centerY, int col, int row) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.addPoint((int) (centerX + HEX_SIZE * Math.cos(angle)), (int) (centerY + HEX_SIZE * Math.sin(angle)));
        }

        Graphics2D g2 = (Graphics2D) g.create();

        // 1. Terrain
        g2.setClip(hex);
        TypeTerrain terrainType = plateau.getHexagone(col, row).getTypeTerrain();
        Image terrainImage = terrainType.getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = HEX_HEIGHT;
        g2.drawImage(terrainImage, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);

        // 2. Unit
        Unite u = plateau.getHexagone(col, row).getUnite();
        if (u != null) {
            Image icon = u.getIcone().getImage();
            g2.drawImage(icon, centerX - HEX_SIZE / 2, centerY - HEX_SIZE / 2, HEX_SIZE, HEX_SIZE, null);
        }

        g2.setClip(null);

        // 3. Decoration (always drawn on top)

        // 4. Accessible overlay
        if (accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 255, 0, 100));
            g2.fillPolygon(hex);
        }

        // 5. Selection and hover outlines
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

        // 6. Defense bonus
        if (col == hoveredCol && row == hoveredRow && accessibles.contains(plateau.getHexagone(col, row))) {
            int bonus = terrainType.getBonusDefense();
            String text = bonus + "%";
            g2.setFont(new Font("Serif", Font.BOLD, 16));
            g2.setColor(new Color(212, 175, 55));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.drawString(text, centerX - textWidth / 2, centerY + textHeight / 2);
        }
        // 7. Fog (drawn last)
        if (visionActive && !plateau.getHexagone(col, row).isVisible()) {

            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillPolygon(hex);
        }

        g2.dispose();
    }

    public void passerAuTourSuivant() {
        joueurActif = (joueurActif == joueurs.get(0)) ? joueurs.get(1)
                : joueurs.get(0);

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

        queue.add(new int[] { startX, startY, 0 });
        visited.add(startX + "," + startY);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], steps = current[2];

            // Skip border tiles completely
            if (x <= 0 || y <= 0 || x >= plateau.getLargeur() - 1 || y >= plateau.getHauteur() - 1)
                continue;

            if (steps > maxSteps)
                continue;

            Hexagone hex = plateau.getHexagone(x, y);
            accessibles.add(hex);

            int[][] directions = {
                    { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                    { x % 2 == 0 ? -1 : 1, 1 }, { x % 2 == 0 ? -1 : 1, -1 }
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
                    int cout = voisin.getTypeTerrain().getCoutDeplacement();

                    if (voisin.getUnite() == null && steps + cout <= maxSteps && cout < 999) {
                        visited.add(key);
                        queue.add(new int[] { nx, ny, steps + cout });
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
        return new int[] { x, y, z };
    }

    public BoardPanel(InfoPanel infoPanel, PlateauManager manager) {
        this.infoPanel = infoPanel;
        this.plateau = manager.plateau;
        this.joueurs = List.of(manager.joueur1, manager.joueur2); // ← ❸
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
                if (pointerInfo == null)
                    return;

                Point screenPoint = pointerInfo.getLocation();
                SwingUtilities.convertPointFromScreen(screenPoint, BoardPanel.this);

                JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class,
                        BoardPanel.this);
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

    private boolean estVoisin(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);

        if (x1 % 2 == 0) {
            return (dx == 1 && (dy == 0 || y2 == y1 - 1)) || (dx == 0 && dy == 1);
        } else {
            return (dx == 1 && (dy == 0 || y2 == y1 + 1)) || (dx == 0 && dy == 1);
        }
    }

    private void drawDecoration(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Decoration decor = plateau.getHexagone(col, row).getDecoration();
        if (decor != Decoration.NONE) {
            Image decorImg = decor.getIcon().getImage();

            int decorWidth = HEX_SIZE * 2; // make it larger if needed
            int decorHeight = HEX_SIZE * 2;

            Point offset = plateau.getHexagone(col, row).getDecorOffset();
            int dx = centerX + offset.x - decorWidth / 2;
            int dy = centerY + offset.y - decorHeight / 2;

            g2.drawImage(decorImg, dx, dy, decorWidth, decorHeight, null);
        }
    }

    private static class DamageText {
        int x, y, dmg;
        float alpha = 1f; // 1 = opaque, 0 = invisible

        DamageText(int x, int y, int dmg) {
            this.x = x;
            this.y = y;
            this.dmg = dmg;
        }

        void tick() { // appelé toutes les 30 ms
            y -= 1; // remonte
            alpha -= 0.02f; // 0.03 × (1000/30) ≈ 1 s de vie
        }

        boolean isDead() {
            return alpha <= 0f;
        }
    }

    private void playHitSound() {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File("resources/sounds/hit.wav"))) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception ex) {
            System.err.println("Son impossible à jouer : " + ex.getMessage());
        }
    }

}
