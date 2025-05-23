package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.Queue;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import model.Decoration;
import model.Hexagone;
import model.Joueur;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

/**
 * Panneau principal du plateau de jeu. Gère l’affichage graphique de la carte
 * hexagonale, la sélection des unités, les déplacements, les attaques, le
 * brouillard de guerre, les effets visuels (dégâts, soins, explosions), ainsi
 * que les interactions utilisateur (clics, zoom, survol).
 *
 * Coordonne les événements de jeu en lien avec l’IHM et déclenche les
 * animations appropriées.
 */
public class BoardPanel extends JPanel {

    private final Timer damageTimer;

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

    private final List<Joueur> joueurs;
    private Joueur joueurActif;
    private final List<ShakeEffect> shakeEffects = new ArrayList<>();
    private final Image explosionGif = new ImageIcon("resources/explosion.gif").getImage();

    private final List<GifExplosion> gifExplosions = new ArrayList<>();
    private final Image healIcon = new ImageIcon("resources/heal.png").getImage();
    private final List<HealingEffect> healingEffects = new ArrayList<>();

    private static final int[][] EVEN_Q_DIRS = {
        {+1, 0}, {0, -1}, {-1, -1},
        {-1, 0}, {-1, +1}, {0, +1}
    };

    private static final int[][] ODD_Q_DIRS = {
        {+1, 0}, {+1, -1}, {0, -1},
        {-1, 0}, {0, +1}, {+1, +1}
    };

    private int xDepart = -1;
    private int yDepart = -1;
    private Unite derniereUniteDeplacee = null;
    private int derniereXDepart = -1;
    private int derniereYDepart = -1;

    private double scale = 1.0;
    private final double ZOOM_STEP = 0.1;
    private final double MIN_SCALE = 0.635;
    private final double MAX_SCALE = 2.5;
    private final List<Trace> tracesDeplacement = new ArrayList<>();

    private final Image traceImage = new ImageIcon("resources/step.png").getImage();
    private boolean isNight = false;

    /**
     * Annule le dernier déplacement effectué par une unité (utile pour les
     * retours arrière).
     */
    public void annulerDernierDeplacement() {
        if (derniereUniteDeplacee != null && derniereXDepart != -1 && derniereYDepart != -1) {

            for (int y = 0; y < plateau.getHauteur(); y++) {
                for (int x = 0; x < plateau.getLargeur(); x++) {
                    Hexagone hex = plateau.getHexagone(x, y);
                    if (hex.getUnite() == derniereUniteDeplacee) {
                        hex.setUnite(null);
                        break;
                    }
                }
            }

            plateau.getHexagone(derniereXDepart, derniereYDepart).setUnite(derniereUniteDeplacee);
            derniereUniteDeplacee.resetDeplacement();

            uniteSelectionnee = derniereUniteDeplacee;
            selX = derniereXDepart;
            selY = derniereYDepart;

            accessibles = calculerCasesAccessibles(selX, selY, uniteSelectionnee.getDeplacementRestant());
            setHexVisibility(accessibles);

            infoPanel.majInfos(uniteSelectionnee);
            infoPanel.majDeplacement(uniteSelectionnee.getDeplacementRestant());

            derniereUniteDeplacee = null;
            derniereXDepart = -1;
            derniereYDepart = -1;

            repaint();
            infoPanel.getMiniMapPanel().updateMiniMap();

        }
    }

    private boolean isBridge(Hexagone hex) {
        Decoration decor = hex.getDecoration();
        return decor == Decoration.WOOD_NS
                || decor == Decoration.WOOD_SE
                || decor == Decoration.WOOD_SW
                || decor == Decoration.STONE_BRIDGE_NS;
    }

    /**
     * Gère un clic de souris sur le plateau. Permet de sélectionner une unité,
     * d’attaquer, de déplacer une unité ou de la désélectionner.
     */
    private void handleClick(int mouseX, int mouseY) {

        if (hoveredCol <= 0 || hoveredRow <= 0
                || hoveredCol >= plateau.getLargeur() - 1
                || hoveredRow >= plateau.getHauteur() - 1) {
            return;
        }

        if (hoveredCol >= 0 && hoveredRow >= 0) {
            System.out.println("Hex clicked at: " + (hoveredRow) + "." + (hoveredCol));
            Hexagone hex = plateau.getHexagone(hoveredCol, hoveredRow);
            Unite unite = hex.getUnite();

            if (unite != null && unite.getJoueur() == joueurActif) {
                tracesDeplacement.clear();

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
            } else if (uniteSelectionnee != null && unite != null
                    && unite.getJoueur() != joueurActif
                    && estVoisin(selX, selY, hoveredCol, hoveredRow)) {

                if (!uniteSelectionnee.peutAttaquer()) {
                    InfoPanel.showStyledWarningDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(this),
                            "Cette unité a déjà attaqué ou n'a plus de points de déplacement.",
                            "Attaque impossible"
                    );
                    return;
                }

                FicheCombatDialog dialog = new FicheCombatDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        uniteSelectionnee,
                        unite);
                dialog.setVisible(true);

                if (dialog.getDecision() == FicheCombatDialog.DECISION_ATTAQUER) {
                    int pvAvant = unite.getPointsVie();

                    boolean tuee = uniteSelectionnee.frapper(
                            unite,
                            plateau.getHexagone(hoveredCol, hoveredRow).getTypeTerrain()
                    );

                    int pvApres = unite.getPointsVie();
                    int degatsReels = Math.max(0, pvAvant - pvApres);

                    Rectangle r = createHexagon(hoveredCol, hoveredRow).getBounds();
                    int cx = r.x + r.width / 2;
                    int cy = r.y + r.height / 2;

                    Color couleurDegats = degatsReels >= 10 ? new Color(180, 0, 0)
                            : degatsReels <= 2 ? Color.GRAY
                                    : Color.RED;

                    splash.add(new DamageText(cx, cy, -degatsReels, couleurDegats));
                    shakeEffects.add(new ShakeEffect(cx, cy));

                    playHitSound();

                    if (tuee) {
                        plateau.getHexagone(hoveredCol, hoveredRow).setUnite(null);
                        gifExplosions.add(new GifExplosion(cx, cy));

                        checkVictory();
                    }

                    uniteSelectionnee = null;
                    selX = selY = -1;
                    tracesDeplacement.clear();
                    accessibles.clear();
                    infoPanel.majInfos(null);
                }

                repaint();
                infoPanel.getMiniMapPanel().updateMiniMap();
            } else if (uniteSelectionnee != null && hex.getUnite() == null && accessibles.contains(hex)) {
                int distance = calculerDistanceHex(selX, selY, hoveredCol, hoveredRow);
                uniteSelectionnee.reduireDeplacement(distance);

                plateau.getHexagone(selX, selY).setUnite(null);
                hex.setUnite(uniteSelectionnee);
                uniteSelectionnee.setPosition(hex);

                tracesDeplacement.clear();
                for (Point p : calculerChemin(selX, selY, hoveredCol, hoveredRow)) {
                    tracesDeplacement.add(new Trace(p));
                }

                uniteSelectionnee.setPosition(hex);

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
                tracesDeplacement.clear();
                accessibles.clear();
                setHexVisibility(null);

                infoPanel.majInfos(null);
                infoPanel.majDeplacement(0);
            }

            repaint();
            infoPanel.getMiniMapPanel().updateMiniMap();
        }
    }

    private void setHexVisibility(Set<Hexagone> visibles) {
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                plateau.getHexagone(x, y).setVisible(visibles != null && visibles.contains(plateau.getHexagone(x, y)));
            }
        }
    }

    /**
     * Calcule les coordonnées de l’hexagone survolé par la souris.
     */
    private void updateHoveredHexagon(int rawMouseX, int rawMouseY) {
        int mouseX = (int) (rawMouseX / scale);
        int mouseY = (int) (rawMouseY / scale);

        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);

        int col = mouseX / stepX;
        int row = (col % 2 == 0) ? mouseY / stepY : (mouseY - stepY / 2) / stepY;

        hoveredCol = -1;
        hoveredRow = -1;

        for (int[] c : new int[][]{
            {col, row}, {col - 1, row}, {col + 1, row}, {col, row - 1},
            {col, row + 1}, {col - 1, row - 1}, {col + 1, row - 1}, {col - 1, row + 1}, {col + 1, row + 1}
        }) {
            int cx = c[0], cy = c[1];
            if (cx > 0 && cy > 0 && cx < plateau.getLargeur() - 1 && cy < plateau.getHauteur() - 1) {
                if (createHexagon(cx, cy).contains(mouseX, mouseY)) {
                    hoveredCol = cx;
                    hoveredRow = cy;
                    break;
                }
            }
        }

        tracesDeplacement.clear();
        if (uniteSelectionnee != null
                && hoveredCol >= 0 && hoveredRow >= 0
                && accessibles.contains(plateau.getHexagone(hoveredCol, hoveredRow))) {

            List<Point> chemin = calculerChemin(selX, selY, hoveredCol, hoveredRow);

            for (Point p : chemin) {
                if (accessibles.contains(plateau.getHexagone(p.x, p.y))) {
                    tracesDeplacement.add(new Trace(p));
                }
            }

        }

        infoPanel.majCoordonnees(hoveredCol, hoveredRow);

        repaint();
        infoPanel.getMiniMapPanel().updateMiniMap();
    }

    private Polygon createHexagon(int col, int row) {
        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);

        int offsetX = 25;
        int offsetY = 15;

        int x = col * stepX + offsetX;
        int y = row * stepY + offsetY;
        if (col % 2 != 0) {
            y += stepY / 2;
        }

        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.addPoint((int) (x + HEX_SIZE * Math.cos(angle)), (int) (y + HEX_SIZE * Math.sin(angle)));
        }
        return hex;
    }

    /**
     * Redéfinit le rendu graphique du plateau. Gère les hexagones, les unités,
     * les décorations, les effets visuels et le brouillard de guerre.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cols = plateau.getLargeur();
        int rows = plateau.getHauteur();
        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);
        int offsetX = 25;
        int offsetY = 15;

        Graphics2D g2 = (Graphics2D) g.create();

        g2.scale(scale, scale);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) {
                    y += stepY / 2;
                }
                drawHexAndTerrain(g2, x, y, col, row);
            }
        }

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) {
                    y += stepY / 2;
                }
                drawDecoration(g2, x, y, col, row);
            }
        }

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) {
                    y += stepY / 2;
                }
                drawUnit(g2, x, y, col, row);
            }
        }

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0) {
                    y += stepY / 2;
                }
                drawOverlays(g2, x, y, col, row);
            }
        }

        if (isNight) {
            g2.setColor(new Color(0, 0, 30, 100));
            g2.fillRect(0, 0, (int) (getWidth() / scale), (int) (getHeight() / scale));

        }

        int fogThickness = 30;
        Graphics2D gFog = (Graphics2D) g.create();
        gFog.setColor(new Color(20, 20, 30));

        gFog.fillRect(0, -5, getWidth(), fogThickness);

        gFog.fillRect(0, getHeight() - fogThickness + 17, getWidth(), fogThickness);

        gFog.fillRect(0, 0, fogThickness - 7, getHeight());

        gFog.fillRect(getWidth() - fogThickness + 10, 0, fogThickness, getHeight());

        gFog.dispose();

        g2.dispose();
    }

    private void drawHexAndTerrain(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Polygon hex = createHexagon(col, row);

        TypeTerrain terrainType = plateau.getHexagone(col, row).getTypeTerrain();
        Color fillColor = Color.GRAY;
        try {
            Image terrainImage = terrainType.getIcon().getImage();
            BufferedImage buffered = new BufferedImage(
                    terrainImage.getWidth(null),
                    terrainImage.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D gImg = buffered.createGraphics();
            gImg.drawImage(terrainImage, 0, 0, null);
            gImg.dispose();
            fillColor = new Color(buffered.getRGB(buffered.getWidth() / 2, buffered.getHeight() / 2));
        } catch (Exception e) {

        }

        g2.setColor(fillColor);
        g2.fillPolygon(hex);

        Image terrainImage = terrainType.getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = HEX_HEIGHT;
        g2.drawImage(terrainImage, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);
    }

    private void drawUnit(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Unite u = plateau.getHexagone(col, row).getUnite();
        if (u != null && u.getIcone() != null) {
            Image icon = u.getIcone().getImage();
            int unitSize = HEX_SIZE + 15;
            g2.drawImage(icon, centerX - unitSize / 2, centerY - unitSize / 2, unitSize, unitSize, null);
        }
    }

    private void drawOverlays(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Polygon hex = createHexagon(col, row);

        if (accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 255, 0, 40));
            g2.fillPolygon(hex);

            boolean isBorder = false;
            int[][] dirs = (col % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;
            for (int[] dir : dirs) {

                int nx = col + dir[0];
                int ny = row + dir[1];
                if (nx < 0 || ny < 0 || nx >= plateau.getLargeur() || ny >= plateau.getHauteur()) {
                    isBorder = true;
                    break;
                }

                Hexagone neighbor = plateau.getHexagone(nx, ny);
                if (!accessibles.contains(neighbor)) {
                    isBorder = true;
                    break;
                }
            }

            if (isBorder) {
                g2.setColor(new Color(0, 255, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawPolygon(hex);
            }
        }

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

        if (col == hoveredCol && row == hoveredRow && accessibles.contains(plateau.getHexagone(col, row))) {
            int bonus = plateau.getBonusDefense(plateau.getHexagone(col, row));
            String text = bonus + "%";
            g2.setFont(new Font("Serif", Font.BOLD, 16));
            g2.setColor(new Color(212, 175, 55));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.drawString(text, centerX - textWidth / 2, centerY + textHeight / 2);
        }

        if (visionActive
                && !plateau.getHexagone(col, row).isVisible()
                && !(col == hoveredCol && row == hoveredRow)
                && !accessibles.contains(plateau.getHexagone(col, row))) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillPolygon(hex);
        }

        for (Trace trace : tracesDeplacement) {
            if (trace.position.x == col && trace.position.y == row) {
                int taille = HEX_SIZE / 2;
                g2.drawImage(traceImage,
                        centerX - taille / 2,
                        centerY - taille / 2,
                        taille, taille, null);
            }
        }

        for (DamageText dt : splash) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, dt.alpha));
            g2.setColor(dt.color);
            g2.setFont(getFont().deriveFont(Font.BOLD, 14f));

            Point offset = new Point(0, 0);
            for (ShakeEffect se : shakeEffects) {
                if (se.isActive()) {
                    Point shake = se.getOffset();
                    offset.translate(shake.x, shake.y);
                }
            }

            String txt = (dt.txt != null) ? dt.txt : String.valueOf(dt.dmg);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txt, dt.x - fm.stringWidth(txt) / 2 + offset.x,
                    dt.y + fm.getAscent() / 2 + offset.y);
        }

        g2.setComposite(AlphaComposite.SrcOver);
        for (HealingEffect h : healingEffects) {
            int size = 32;
            int offsetY = h.getOffsetY();
            g2.drawImage(healIcon, h.x - size / 2, h.y - size / 2 + offsetY, size, size, null);
        }

        for (GifExplosion g : gifExplosions) {
            int size = 64;
            g2.drawImage(explosionGif, g.x - size / 2, g.y - size / 2, size, size, null);
        }

    }

    private void drawDecoration(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Decoration decor = plateau.getHexagone(col, row).getDecoration();
        if (decor != Decoration.NONE) {
            Image decorImg = decor.getIcon().getImage();

            int decorWidth = (int) (HEX_SIZE * 1.8);
            int decorHeight = (int) (HEX_SIZE * 1.8);

            Point offset = plateau.getHexagone(col, row).getDecorOffset();
            int dx = centerX + offset.x - decorWidth / 2;
            int dy = centerY + offset.y - decorHeight / 2;

            g2.drawImage(decorImg, dx, dy, decorWidth, decorHeight, null);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);
        int width = stepX * plateau.getLargeur();
        int height = stepY * plateau.getHauteur();
        return new Dimension((int) (width * scale), (int) (height * scale));
    }

    private boolean areAdjacentByLand(Hexagone from, Hexagone to) {

        boolean fromIsLand = from.getTypeTerrain().getCoutDeplacement() < 999;
        boolean toIsLand = to.getTypeTerrain().getCoutDeplacement() < 999;

        return fromIsLand && toIsLand;
    }

    /**
     * Calcule les cases accessibles à une unité donnée à partir de ses points
     * de déplacement restants. Prend en compte les ponts pour traverser l’eau.
     */
    private Set<Hexagone> calculerCasesAccessibles(int startX, int startY, int maxSteps) {
        Set<Hexagone> accessibles = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(new int[]{startX, startY, 0});
        visited.add(startX + "," + startY);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], steps = current[2];

            Hexagone currentHex = plateau.getHexagone(x, y);

            boolean isCurrentWater = currentHex.getTypeTerrain().getCoutDeplacement() >= 999;
            boolean canStandHere = !isCurrentWater || isBridge(currentHex);

            if (steps > maxSteps || !canStandHere) {
                continue;
            }

            accessibles.add(currentHex);

            int[][] dirs = (x % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;

            for (int[] dir : dirs) {
                int nx = x + dir[0], ny = y + dir[1];

                if (nx <= 0 || ny <= 0 || nx >= plateau.getLargeur() - 1 || ny >= plateau.getHauteur() - 1) {
                    continue;
                }

                String key = nx + "," + ny;

                if (visited.contains(key)) {
                    continue;
                }

                if (!visited.contains(key)) {
                    Hexagone neighbor = plateau.getHexagone(nx, ny);
                    int cost = neighbor.getTypeTerrain().getCoutDeplacement();

                    boolean isWater = cost >= 999;
                    boolean bridge = isWater && isBridge(neighbor);
                    boolean pathCrossesWater
                            = isCurrentWater && !isWater && !isBridge(currentHex);

                    if ((neighbor.getUnite() == null || neighbor == plateau.getHexagone(startX, startY))
                            && (!isWater || bridge)
                            && !pathCrossesWater
                            && steps + (bridge ? 1 : cost) <= maxSteps) {

                        visited.add(key);
                        queue.add(new int[]{nx, ny, steps + (bridge ? 1 : cost)});
                    }
                }
            }
        }

        return accessibles;
    }

    private int calculerDistanceHex(int x1, int y1, int x2, int y2) {

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

    private boolean estVoisin(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);

        if (x1 % 2 == 0) {
            return (dx == 1 && (dy == 0 || y2 == y1 - 1)) || (dx == 0 && dy == 1);
        } else {
            return (dx == 1 && (dy == 0 || y2 == y1 + 1)) || (dx == 0 && dy == 1);
        }
    }

    private static class DamageText {

        int x, y, dmg;
        float alpha = 1f;
        String txt = null;

        Color color;

        DamageText(int x, int y, int dmg, Color color) {
            this.x = x;
            this.y = y;
            this.dmg = dmg;
            this.color = color;
        }

        DamageText(int x, int y, String txt, Color color) {
            this.x = x;
            this.y = y;
            this.dmg = 0;
            this.txt = txt;
            this.color = color;
        }

        void tick() {
            y -= 1;
            alpha = Math.max(0f, alpha - 0.025f);
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

    /**
     * Initialise le plateau graphique avec les joueurs, le gestionnaire de
     * partie, les effets visuels et les écouteurs pour les interactions
     * utilisateur (clics, mouvements de souris, zoom, etc).
     *
     * @param infoPanel Panneau d’informations (UI latérale)
     * @param manager PlateauManager contenant les joueurs et le plateau
     */
    public BoardPanel(InfoPanel infoPanel, PlateauManager manager) {
        this.infoPanel = infoPanel;
        this.plateau = manager.plateau;
        this.joueurs = List.of(manager.joueur1, manager.joueur2);
        this.joueurActif = manager.joueurActif;

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                updateHoveredHexagon(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
                checkAutoEndTurn();
            }
        });
        addMouseWheelListener(e -> {
            double newScale = scale;

            if (e.getPreciseWheelRotation() < 0) {
                newScale = Math.min(MAX_SCALE, scale + ZOOM_STEP);
            } else {
                newScale = Math.max(MIN_SCALE, scale - ZOOM_STEP);
            }

            if (newScale != scale) {
                scale = newScale;
                revalidate();
                repaint();
                infoPanel.getMiniMapPanel().updateMiniMap();
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                revalidate();
                repaint();
                infoPanel.getMiniMapPanel().updateMiniMap();

            }
        });

        MouseMotionAdapter scrollAdapter = new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                if (pointerInfo == null) {
                    return;
                }

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
                    if (screenPoint.x < 30) {
                        int newX = Math.max(view.x - 20, 0);
                        viewport.setViewPosition(new Point(newX, view.y));
                    } else if (screenPoint.x > getWidth() - 30) {
                        int newX = Math.min(view.x + 20, getWidth() - view.width);
                        viewport.setViewPosition(new Point(newX, view.y));

                    }

                }
            }
        };
        addMouseMotionListener(scrollAdapter);

        damageTimer = new Timer(30, e -> {
            splash.forEach(DamageText::tick);
            splash.removeIf(DamageText::isDead);
            shakeEffects.removeIf(se -> !se.isActive());
            healingEffects.forEach(HealingEffect::tick);
            healingEffects.removeIf(HealingEffect::isDone);
            gifExplosions.removeIf(GifExplosion::isExpired);
            repaint();
        });

        damageTimer.start();

        PropertyChangeListener tourListener = evt -> SwingUtilities.invokeLater(this::checkAutoEndTurn);

        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null) {
                    u.addPropertyChangeListener(tourListener);

                    u.addPropertyChangeListener(evt -> {
                        if ("healed".equals(evt.getPropertyName())) {
                            int healedAmount = (int) evt.getNewValue();
                            if (u.getPosition() != null) {
                                Point center = getHexCenter(u.getPosition().getX(), u.getPosition().getY());

                                splash.add(new DamageText(center.x, center.y, "+" + healedAmount, new Color(0, 200, 0)));

                                healingEffects.add(new HealingEffect(center.x, center.y));
                            }
                        }
                    });
                }
            }
        }

        Timer traceCleaner = new Timer(100, e -> {
            tracesDeplacement.removeIf(Trace::isExpired);
            repaint();
        });
        traceCleaner.start();

        Timer cycleJourNuit = new Timer(90_000, e -> {
            isNight = !isNight;
            repaint();
        });
        cycleJourNuit.start();

    }

    private Point getHexCenter(int col, int row) {
        int stepX = (int) (HEX_WIDTH * 0.9);
        int stepY = (int) (HEX_HEIGHT * 0.85);

        int offsetX = 0;
        int offsetY = 0;

        int x = col * stepX + offsetX;
        int y = row * stepY + offsetY;

        if (col % 2 != 0) {
            y += stepY / 2;
        }

        return new Point(x, y);
    }

    /**
     * Détecte automatiquement si toutes les unités du joueur ont terminé leur
     * tour, et déclenche le passage au joueur suivant si nécessaire.
     */
    private void checkAutoEndTurn() {
        if (shouldEndTurn()) {
            passerAuTourSuivant();
        }
    }

    private boolean shouldEndTurn() {
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.getJoueur() == joueurActif) {
                    if (u.getDeplacementRestant() > 0 || !u.aAttaqueCeTour()) {
                        return false;
                    }

                }
            }
        }
        return true;
    }

    /**
     * Passe au tour du joueur suivant, réinitialise les états des unités, met à
     * jour les informations affichées et déclenche l'effet visuel de
     * transition.
     */
    public void passerAuTourSuivant() {
        tracesDeplacement.clear();
        if (uniteSelectionnee != null
                && hoveredCol >= 0 && hoveredRow >= 0
                && accessibles.contains(plateau.getHexagone(hoveredCol, hoveredRow))) {

            List<Point> chemin = calculerChemin(selX, selY, hoveredCol, hoveredRow);

            tracesDeplacement.clear();
            for (Point p : chemin) {
                tracesDeplacement.add(new Trace(p));
            }
        }
        joueurActif = (joueurActif == joueurs.get(0)) ? joueurs.get(1) : joueurs.get(0);

        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.getJoueur() == joueurActif) {
                    u.seReposer();
                    u.resetDeplacement();
                    u.setAAttaqueCeTour(false);
                }

            }
        }

        uniteSelectionnee = null;
        selX = selY = -1;
        accessibles.clear();
        visionActive = false;

        infoPanel.majJoueurActif(joueurActif);
        infoPanel.majInfos(null);
        infoPanel.majDeplacement(0);
        repaint();
        infoPanel.getMiniMapPanel().updateMiniMap();
        InfoPanel.showStyledTurnDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                joueurActif.getNom()
        );

    }

    /**
     * Applique un zoom centré sur une position de la souris. Permet une
     * navigation fluide sur le plateau.
     */
    public void zoomAt(Point mouseInComponent, boolean zoomIn) {
        double oldScale = scale;
        double newScale = zoomIn
                ? Math.min(MAX_SCALE, scale + ZOOM_STEP)
                : Math.max(MIN_SCALE, scale - ZOOM_STEP);

        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);

        if (scrollPane != null && !zoomIn) {
            Dimension scaledSize = new Dimension(
                    (int) (getBaseWidth() * newScale),
                    (int) (getBaseHeight() * newScale)
            );
            Dimension viewSize = scrollPane.getViewport().getExtentSize();

            if (scaledSize.width < viewSize.width || scaledSize.height < viewSize.height) {
                return;
            }
        }

        scale = newScale;

        double zoomFactor = scale / oldScale;

        if (scrollPane != null) {
            JViewport viewport = scrollPane.getViewport();

            if (mouseInComponent == null) {
                mouseInComponent = new Point(viewport.getWidth() / 2, viewport.getHeight() / 2);
            }

            Point viewPos = viewport.getViewPosition();
            int mouseMapX = mouseInComponent.x + viewPos.x;
            int mouseMapY = mouseInComponent.y + viewPos.y;

            int newViewX = (int) ((mouseMapX * zoomFactor) - mouseInComponent.x);
            int newViewY = (int) ((mouseMapY * zoomFactor) - mouseInComponent.y);

            revalidate();
            repaint();
            infoPanel.getMiniMapPanel().updateMiniMap();

            SwingUtilities.invokeLater(() -> {
                viewport.setViewPosition(new Point(newViewX, newViewY));
            });
        }
    }

    private int getBaseWidth() {
        return (int) (1.5 * HEX_SIZE) * plateau.getLargeur();
    }

    private int getBaseHeight() {
        return (int) (Math.sqrt(3) * HEX_SIZE) * plateau.getHauteur();
    }

    public double getScale() {
        return this.scale;
    }

    /**
     * Calcule et retourne le chemin le plus court entre deux points sur le
     * plateau (utilisé pour afficher les traces de déplacement).
     */
    private List<Point> calculerChemin(int x1, int y1, int x2, int y2) {
        Queue<Point> queue = new LinkedList<>();
        Map<String, Point> cameFrom = new HashMap<>();
        Map<String, Integer> costSoFar = new HashMap<>();

        Point start = new Point(x1, y1);
        Point end = new Point(x2, y2);

        queue.add(start);
        cameFrom.put(x1 + "," + y1, null);
        costSoFar.put(x1 + "," + y1, 0);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            int[][] dirs = (current.x % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;
            for (int[] dir : dirs) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                String key = nx + "," + ny;

                if (nx <= 0 || ny <= 0 || nx >= plateau.getLargeur() - 1 || ny >= plateau.getHauteur() - 1) {
                    continue;
                }

                Hexagone neighbor = plateau.getHexagone(nx, ny);
                if (!accessibles.contains(neighbor)) {
                    continue;
                }

                int newCost = costSoFar.get(current.x + "," + current.y) + neighbor.getTypeTerrain().getCoutDeplacement();
                if (!costSoFar.containsKey(key) || newCost < costSoFar.get(key)) {
                    costSoFar.put(key, newCost);
                    queue.add(new Point(nx, ny));
                    cameFrom.put(key, current);
                }
            }
        }

        List<Point> path = new ArrayList<>();
        Point current = end;
        while (current != null && cameFrom.containsKey(current.x + "," + current.y)) {
            path.add(0, current);
            current = cameFrom.get(current.x + "," + current.y);
        }

        return path;
    }

    private static class Trace {

        Point position;
        long timestamp;

        Trace(Point position) {
            this.position = position;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 1000;
        }
    }

    /**
     * Affiche une boîte de dialogue de victoire si un joueur n’a plus d’unités
     * vivantes.
     */
    public void checkVictory() {
        int countJ1 = 0, countJ2 = 0;

        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.estVivant()) {
                    if (u.getJoueur() == joueurs.get(0)) {
                        countJ1++;
                    } else if (u.getJoueur() == joueurs.get(1)) {
                        countJ2++;
                    }
                }
            }
        }

        if (countJ1 == 0) {
            Joueur gagnant = joueurs.get(1);
            showVictoryDialog(gagnant.getNom(), gagnant.estIA());
        } else if (countJ2 == 0) {
            Joueur gagnant = joueurs.get(0);
            showVictoryDialog(gagnant.getNom(), gagnant.estIA());
        }
    }

    private void showVictoryDialog(String winnerName, boolean isIA) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog((JFrame) parentWindow, "Fin de partie", true);
        dialog.setUndecorated(true);
        dialog.setSize(720, 130);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(InfoPanel.BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        String raw = isIA
                ? "Defaite... L’IA a gagne cette partie."
                : "Felicitations " + winnerName + ", vous avez remporte la victoire !";

        String message = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(Font.BOLD, 16f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        buttonsPanel.setBackground(InfoPanel.BACKGROUND);

        JButton replay = InfoPanel.createStyledButton("Nouvelle partie");
        JButton menu = InfoPanel.createStyledButton("Menu principal");

        buttonsPanel.add(replay);
        buttonsPanel.add(menu);

        content.add(label);
        content.add(buttonsPanel);

        dialog.setContentPane(content);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

        replay.addActionListener(e -> {
            dialog.dispose();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }

            GameWindow newGame = new GameWindow(new MainMenu(), PlateauManager.initialiserNouvellePartie("Joueur 1", "Joueur 2", false));
            JFrame frame = new JFrame("Nouvelle Partie");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(newGame);
            frame.pack();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });

        menu.addActionListener(e -> {
            dialog.dispose();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            new MainMenu().showMainMenu();
        });

        dialog.setVisible(true);
    }

    private static class ShakeEffect {

        int x, y;
        int amplitude = 6;
        int duration = 10;
        int ticks = 0;

        ShakeEffect(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isActive() {
            return ticks < duration;
        }

        Point getOffset() {
            ticks++;
            int dx = (int) (Math.random() * amplitude * 2) - amplitude;
            int dy = (int) (Math.random() * amplitude * 2) - amplitude;
            return new Point(dx, dy);
        }
    }

    private static class GifExplosion {

        int x, y;
        long startTime;
        long durationMillis = 1000;

        GifExplosion(int x, int y) {
            this.x = x;
            this.y = y;
            this.startTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - startTime > durationMillis;
        }
    }

    private static class HealingEffect {

        int x, y;
        int ticks = 0;

        HealingEffect(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void tick() {
            ticks++;
        }

        boolean isDone() {
            return ticks > 45;
        }

        int getOffsetY() {
            return 0;
        }
    }

    /**
     * Lance un combat immédiat entre deux unités. Joue l’animation de dégâts et
     * retire l’unité cible du plateau si elle meurt.
     */
    public void lancerCombat(Unite attaquant, Unite cible) {
        if (attaquant == null || cible == null) {
            return;
        }
        Hexagone cibleHex = cible.getPosition();
        if (cibleHex == null) {
            return;
        }

        boolean tuee = attaquant.frapper(cible, cibleHex.getTypeTerrain());

        Polygon poly = createHexagon(cibleHex.getX(), cibleHex.getY());
        Rectangle r = poly.getBounds();
        int cx = r.x + r.width / 2;
        int cy = r.y + r.height / 2;

        int dmg = attaquant.getArmes().get(0).getDegats();
        Color couleurDegats = dmg >= 10 ? new Color(180, 0, 0)
                : dmg <= 2 ? Color.GRAY : Color.RED;

        splash.add(new DamageText(cx, cy, -dmg, couleurDegats));

        playHitSound();

        if (tuee) {
            cibleHex.setUnite(null);
        }

        repaint();
    }

    public Joueur getJoueurActif() {
        return joueurActif;
    }
}
