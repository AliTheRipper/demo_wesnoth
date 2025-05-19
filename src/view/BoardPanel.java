package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Queue;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer; // pour lire le son
import model.Decoration;
import model.Hexagone;
import model.Joueur;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

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

    private final List<Joueur> joueurs; // injected from your controller
    private Joueur joueurActif; // instead of int joueurActif = 1;
    private final List<ShakeEffect> shakeEffects = new ArrayList<>();
    private final Image explosionGif = new ImageIcon("resources/explosion.gif").getImage();

    private final List<GifExplosion> gifExplosions = new ArrayList<>();




    // Directions for hex grid neighbors (even-q layout)
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
private int offsetX = HEX_SIZE;
private int offsetY = HEX_SIZE;

    //private final Image backgroundImage = new ImageIcon("resources/plaine.png").getImage();
    private double scale = 1.0;
    private final double ZOOM_STEP = 0.1;
    private final double MIN_SCALE = 0.635;
    private final double MAX_SCALE = 2.5;
    private final List<Trace> tracesDeplacement = new ArrayList<>();

    private final Image traceImage = new ImageIcon("resources/step.png").getImage(); // chemin vers l’image
    private boolean isNight = false;





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
            infoPanel.getMiniMapPanel().updateMiniMap();

        }
    }

    private void handleClick(int rawMouseX, int rawMouseY) {
        int mouseX = (int)(rawMouseX / scale);
        int mouseY = (int)(rawMouseY / scale);

        if (hoveredCol <= 0 || hoveredRow <= 0 ||
                hoveredCol >= plateau.getLargeur() - 1 ||
                hoveredRow >= plateau.getHauteur() - 1) {
            return;
        }

        if (hoveredCol >= 0 && hoveredRow >= 0) {
            System.out.println("Hex clicked at: " + (hoveredRow) + "." + (hoveredCol));
            Hexagone hex = plateau.getHexagone(hoveredCol, hoveredRow);
            Unite unite = hex.getUnite();

            // Cas 1 : Sélection d'une unité alliée
            if (unite != null && unite.getJoueur() == joueurActif) {
                tracesDeplacement.clear(); // Nettoyer les anciennes traces

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
            }

            // Cas 2 : Attaque ennemi voisin
            else if (uniteSelectionnee != null && unite != null
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

                    Color couleurDegats = degatsReels >= 10 ? new Color(180, 0, 0) : // gros coup
                            degatsReels <= 2 ? Color.GRAY :
                                    Color.RED;

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
            }

            // Cas 3 : Déplacement sur une case accessible
            else if (uniteSelectionnee != null && hex.getUnite() == null && accessibles.contains(hex)) {
                int distance = calculerDistanceHex(selX, selY, hoveredCol, hoveredRow);
                uniteSelectionnee.reduireDeplacement(distance);

                plateau.getHexagone(selX, selY).setUnite(null);
                hex.setUnite(uniteSelectionnee);

                tracesDeplacement.clear();
                for (Point p : calculerChemin(selX, selY, hoveredCol, hoveredRow)) {
                    tracesDeplacement.add(new Trace(p));
                }

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

    private void updateHoveredHexagon(int rawMouseX, int rawMouseY) {
    int mouseX = (int)(rawMouseX / scale);
    int mouseY = (int)(rawMouseY / scale);

    int stepX = (int) (1.5 * HEX_SIZE);
    int stepY = (int) (Math.sqrt(3) * HEX_SIZE);

    int col = mouseX / stepX;
    int row = (col % 2 == 0) ? mouseY / stepY : (mouseY - stepY / 2) / stepY;

    hoveredCol = -1;
    hoveredRow = -1;

    for (int[] c : new int[][] {
            { col, row }, { col - 1, row }, { col + 1, row }, { col, row - 1 },
            { col, row + 1 }, { col - 1, row - 1 }, { col + 1, row - 1 }, { col - 1, row + 1 }, { col + 1, row + 1 }
    }) {
        int cx = c[0], cy = c[1];
        if (cx > 0 && cy > 0 && cx < plateau.getLargeur() - 1 && cy < plateau.getHauteur() - 1){
            if (createHexagon(cx, cy).contains(mouseX, mouseY)) {
                hoveredCol = cx;
                hoveredRow = cy;
                break;
            }
        }
    }

    tracesDeplacement.clear();
    if (uniteSelectionnee != null &&
        hoveredCol >= 0 && hoveredRow >= 0 &&
        accessibles.contains(plateau.getHexagone(hoveredCol, hoveredRow))) {

        List<Point> chemin = calculerChemin(selX, selY, hoveredCol, hoveredRow);

        for (Point p : chemin) {
    if (accessibles.contains(plateau.getHexagone(p.x, p.y))) {
        tracesDeplacement.add(new Trace(p));
    }
}

    }

    repaint();
    infoPanel.getMiniMapPanel().updateMiniMap();
}



    private Polygon createHexagon(int col, int row) {
        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);

        int offsetX = 25; // Alignement top-left
        int offsetY = 15; // Alignement top-left

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
        int stepX = (int) (1.5 * HEX_SIZE);
        int stepY = (int) (Math.sqrt(3) * HEX_SIZE);
        int offsetX = 25; // Alignement top-left
        int offsetY = 15; // Alignement top-left

        Graphics2D g2 = (Graphics2D) g.create();
        //////////////////////////////
        g2.scale(scale, scale);
        ///////////////////////////////
        // Active un rendu lisse
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // PASS 1: Dessiner terrains et unités
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawTerrainAndUnit(g2, x, y, col, row);
            }
        }

        // PASS 2: Dessiner décorations
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawDecoration(g2, x, y, col, row);
            }
        }

        // PASS 3: Dessiner surlignages et overlays
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * stepX + offsetX;
                int y = row * stepY + offsetY;
                if (col % 2 != 0)
                    y += stepY / 2;
                drawOverlays(g2, x, y, col, row);
            }
        }
        if (isNight) {
            g2.setColor(new Color(0, 0, 30, 100)); // bleu nuit semi-transparent
            g2.fillRect(0, 0, (int)(getWidth() / scale), (int)(getHeight() / scale));

        }


// --- Draw border fog ---
int fogThickness = 30;
Graphics2D gFog = (Graphics2D) g.create();
gFog.setColor(new Color(20, 20, 30)); // semi-transparent black

// Top
gFog.fillRect(0, -5, getWidth(), fogThickness);
// Bottom
gFog.fillRect(0, getHeight() - fogThickness + 17, getWidth(), fogThickness);
// Left
gFog.fillRect(0, 0, fogThickness - 7, getHeight());
// Right
gFog.fillRect(getWidth() - fogThickness + 10, 0, fogThickness, getHeight());

gFog.dispose();

        g2.dispose();
    }

    private void drawTerrainAndUnit(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Polygon hex = createHexagon(col, row);

        // Remplissage du polygone avec une couleur proche du terrain
        TypeTerrain terrainType = plateau.getHexagone(col, row).getTypeTerrain();
        Color fillColor = Color.GRAY; // Par défaut
        try {
            Image terrainImage = terrainType.getIcon().getImage();
            BufferedImage buffered = new BufferedImage(
                    terrainImage.getWidth(null),
                    terrainImage.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D gImg = buffered.createGraphics();
            gImg.drawImage(terrainImage, 0, 0, null);
            gImg.dispose();
            fillColor = new Color(buffered.getRGB(buffered.getWidth()/2, buffered.getHeight()/2));
        } catch (Exception e) {
            // ignore
        }
        g2.setColor(fillColor);
        g2.fillPolygon(hex);

        // Dessin de l’image terrain sans clipping
        Image terrainImage = terrainType.getIcon().getImage();
        int imgWidth = (int) (HEX_WIDTH * 1.2);
        int imgHeight = HEX_HEIGHT;
        g2.drawImage(terrainImage, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, null);

        // Dessin de l’unité
      Unite u = plateau.getHexagone(col, row).getUnite();
if (u != null && u.getIcone() != null) {
    Image icon = u.getIcone().getImage();
    g2.drawImage(icon, centerX - HEX_SIZE / 2, centerY - HEX_SIZE / 2, HEX_SIZE, HEX_SIZE, null);
}

    }


 private void drawOverlays(Graphics2D g2, int centerX, int centerY, int col, int row) {
    Polygon hex = createHexagon(col, row);

    // 1. Fill accessible
        if (accessibles.contains(plateau.getHexagone(col, row))) {
    g2.setColor(new Color(0, 255, 0, 40)); // soft green inside
    g2.fillPolygon(hex);

    boolean isBorder = false;
    int[][] dirs = (col % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;
for (int[] dir : dirs) {

        int nx = col + dir[0];
        int ny = row + dir[1];
        if (nx < 0 || ny < 0 || nx >= plateau.getLargeur() || ny >= plateau.getHauteur()) {
            isBorder = true; break;
        }

        Hexagone neighbor = plateau.getHexagone(nx, ny);
        if (!accessibles.contains(neighbor)) {
            isBorder = true;
            break;
        }
    }

    if (isBorder) {
        g2.setColor(new Color(0, 255, 0)); // consistent strong green
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(hex);
    }
}



    // 3. Outline survol
    if (col == hoveredCol && row == hoveredRow) {
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(hex);
    }

    // 4. Selection
    if (col == selX && row == selY) {
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(3));
        g2.drawPolygon(hex);
    }

    // 5. Bonus defense
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

    // 6. Fog of war
    if (visionActive 
        && !plateau.getHexagone(col, row).isVisible()
        && !(col == hoveredCol && row == hoveredRow)
        && !accessibles.contains(plateau.getHexagone(col, row))) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillPolygon(hex);
    }

    // 7. Traces
    for (Trace trace : tracesDeplacement) {
        if (trace.position.x == col && trace.position.y == row) {
            int taille = HEX_SIZE / 2;
            g2.drawImage(traceImage,
                    centerX - taille / 2,
                    centerY - taille / 2,
                    taille, taille, null);
        }
    }

    // 8. Damage splash
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

         String txt = String.valueOf(dt.dmg);
         FontMetrics fm = g2.getFontMetrics();
         g2.drawString(txt, dt.x - fm.stringWidth(txt) / 2 + offset.x,
                 dt.y + fm.getAscent() / 2 + offset.y);
     }

     g2.setComposite(AlphaComposite.SrcOver);
     for (GifExplosion g : gifExplosions) {
         int size = 64;
         g2.drawImage(explosionGif, g.x - size / 2, g.y - size / 2, size, size, null);
     }



 }


    private void drawDecoration(Graphics2D g2, int centerX, int centerY, int col, int row) {
        Decoration decor = plateau.getHexagone(col, row).getDecoration();
        if (decor != Decoration.NONE) {
            Image decorImg = decor.getIcon().getImage();

            int decorWidth = HEX_SIZE * 2; // ajuster si nécessaire
            int decorHeight = HEX_SIZE * 2;

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
        if (steps > maxSteps || currentHex.getTypeTerrain().getCoutDeplacement() >= 999) continue;

        accessibles.add(currentHex);

        int[][] dirs = (x % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;


        for (int[] dir : dirs) {
            int nx = x + dir[0], ny = y + dir[1];
            String key = nx + "," + ny;

            if (nx <= 0 || ny <= 0 || nx >= plateau.getLargeur() - 1 || ny >= plateau.getHauteur() - 1)
                continue;

            if (!visited.contains(key)) {
                Hexagone neighbor = plateau.getHexagone(nx, ny);
                int cost = neighbor.getTypeTerrain().getCoutDeplacement();

                // Only walk through if it's empty or you're standing on it
                if (neighbor.getUnite() == null && steps + cost <= maxSteps) {
                    visited.add(key);
                    queue.add(new int[]{nx, ny, steps + cost});
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
        Color color;

        DamageText(int x, int y, int dmg, Color color) {
            this.x = x;
            this.y = y;
            this.dmg = dmg;
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

    public BoardPanel(InfoPanel infoPanel, PlateauManager manager) {
        this.infoPanel = infoPanel;
        this.plateau = manager.plateau;
        this.joueurs = List.of(manager.joueur1, manager.joueur2);
        this.joueurActif = manager.joueurActif;

        // Configuration des écouteurs de souris
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

        // Configuration du défilement au bord (auto scroll)
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

        // Timer pour les animations de dégâts
        damageTimer = new Timer(30, e -> {
            splash.forEach(DamageText::tick);
            splash.removeIf(DamageText::isDead);
            shakeEffects.removeIf(se -> !se.isActive());
            //explosions.forEach(Explosion::tick);
            //explosions.removeIf(Explosion::isDead);
            gifExplosions.removeIf(GifExplosion::isExpired); // ✅ nettoyage des gifs
            repaint();
        });



        damageTimer.start();

        // Configuration du passage automatique de tour
        PropertyChangeListener tourListener = evt -> SwingUtilities.invokeLater(this::checkAutoEndTurn);

        // Ajout des écouteurs à toutes les unités
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null) {
                    u.addPropertyChangeListener(tourListener);
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
                    if (u.getDeplacementRestant() > 0 || (!u.aAttaqueCeTour() && u.peutAttaquer())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void passerAuTourSuivant() {
tracesDeplacement.clear();
if (uniteSelectionnee != null &&
    hoveredCol >= 0 && hoveredRow >= 0 &&
    accessibles.contains(plateau.getHexagone(hoveredCol, hoveredRow))) {

    List<Point> chemin = calculerChemin(selX, selY, hoveredCol, hoveredRow);

    tracesDeplacement.clear();
for (Point p : chemin) {
    tracesDeplacement.add(new Trace(p));
}

}

        joueurActif = (joueurActif == joueurs.get(0)) ? joueurs.get(1) : joueurs.get(0);

        // Réinitialisation des unités du nouveau joueur
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                Unite u = plateau.getHexagone(x, y).getUnite();
                if (u != null && u.getJoueur() == joueurActif) {
                    if (u.getJoueur() == joueurActif) {
                        u.seReposer(); // récupère les PV si sur village
                    }
                    
                }
            }
        }

        // Nettoyage de l'interface
        uniteSelectionnee = null;
        selX = selY = -1;
        accessibles.clear();
        visionActive = false;

        // Mise à jour de l'affichage
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
            return; // Prevent zooming out beyond the visible area
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
    private List<Point> calculerChemin(int x1, int y1, int x2, int y2) {
    Queue<Point> queue = new LinkedList<>();
    Map<String, Point> cameFrom = new HashMap<>();
    Map<String, Integer> costSoFar = new HashMap<>();
    Set<String> visited = new HashSet<>();

    Point start = new Point(x1, y1);
    Point end = new Point(x2, y2);

    queue.add(start);
    cameFrom.put(x1 + "," + y1, null);
    costSoFar.put(x1 + "," + y1, 0);

    while (!queue.isEmpty()) {
        Point current = queue.poll();

        if (current.equals(end)) break;

        int[][] dirs = (current.x % 2 == 0) ? EVEN_Q_DIRS : ODD_Q_DIRS;
        for (int[] dir : dirs) {
            int nx = current.x + dir[0];
            int ny = current.y + dir[1];
            String key = nx + "," + ny;

            if (nx <= 0 || ny <= 0 || nx >= plateau.getLargeur() - 1 || ny >= plateau.getHauteur() - 1)
                continue;

            Hexagone neighbor = plateau.getHexagone(nx, ny);
            if (!accessibles.contains(neighbor)) continue;

            int newCost = costSoFar.get(current.x + "," + current.y) + neighbor.getTypeTerrain().getCoutDeplacement();
            if (!costSoFar.containsKey(key) || newCost < costSoFar.get(key)) {
                costSoFar.put(key, newCost);
                queue.add(new Point(nx, ny));
                cameFrom.put(key, current);
            }
        }
    }

    // Reconstruct path
    List<Point> path = new ArrayList<>();
    Point current = end;
    while (current != null && cameFrom.containsKey(current.x + "," + current.y)) {
        path.add(0, current);
        current = cameFrom.get(current.x + "," + current.y);
    }

    return path;
}


    /////////////////////////////////////////////CLASE TRACE/////////////////////////////
    private static class Trace {
        Point position;
        long timestamp; // en millisecondes

        Trace(Point position) {
            this.position = position;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 1000; // 3 secondes
        }
    }
private void checkVictory() {
    int countJ1 = 0, countJ2 = 0;

    for (int y = 0; y < plateau.getHauteur(); y++) {
        for (int x = 0; x < plateau.getLargeur(); x++) {
            Unite u = plateau.getHexagone(x, y).getUnite();
            if (u != null && u.getPointsVie() > 0) {
                if (u.getJoueur() == joueurs.get(0)) countJ1++;
                else if (u.getJoueur() == joueurs.get(1)) countJ2++;
            }
        }
    }

    if (countJ1 == 0) {
        showVictoryDialog(joueurs.get(1).getNom());
    } else if (countJ2 == 0) {
        showVictoryDialog(joueurs.get(0).getNom());
    }
}

private void showVictoryDialog(String winnerName) {
    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Victoire", true);
    dialog.setUndecorated(true);
    dialog.setSize(500, 180);
    Window gameWindow = SwingUtilities.getWindowAncestor(this);
dialog.setLocationRelativeTo(gameWindow);

    dialog.setLayout(new BorderLayout());

    JPanel content = new JPanel();
    content.setBackground(InfoPanel.BACKGROUND);
    content.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    JLabel label = new JLabel("Felicitations " + winnerName + ", vous avez gagne !", SwingConstants.CENTER);
    label.setForeground(Color.WHITE);
    label.setFont(InfoPanel.gothic.deriveFont(Font.BOLD, 18f));
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

    JPanel btns = new JPanel(new FlowLayout());
    btns.setBackground(InfoPanel.BACKGROUND);

    JButton replay = InfoPanel.createStyledButton("Nouvelle partie");
    JButton menu = InfoPanel.createStyledButton("Menu principal");

    btns.add(replay);
    btns.add(menu);

    content.add(label);
    content.add(btns);
    dialog.setContentPane(content);

    replay.addActionListener(e -> {
        dialog.dispose();
        GameWindow newGame = new GameWindow(new MainMenu(), PlateauManager.initialiserNouvellePartie());
        JFrame frame = new JFrame("Nouvelle Partie");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(newGame);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    });

    menu.addActionListener(e -> {
        dialog.dispose();
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
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
            int dx = (int)(Math.random() * amplitude * 2) - amplitude;
            int dy = (int)(Math.random() * amplitude * 2) - amplitude;
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





}

