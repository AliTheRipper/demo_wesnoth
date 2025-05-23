package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import model.*;

/**
 * MapEditor is a Swing-based tool allowing users to create, edit, save, and
 * load custom hexagonal terrain maps. It supports terrain selection,
 * interactive painting, viewport dragging, and serialization of the grid.
 */
public class MapEditor extends JFrame {

    private Hexagone[][] grid;
    private TypeTerrain selectedTerrain = TypeTerrain.GREEN;

    private JPanel mapPanel;
    private int hexSize = 40;
    private int panelPadding = 50;

    private int mapWidth;
    private int mapHeight;
    private int panelPaddingX = 50;
    private int panelPaddingY = 0;
    private Point lastDragPoint;

    /**
     * Initializes the map editor window, sets up map dimensions and UI
     * components.
     */
    public MapEditor() {
        setTitle("Éditeur de Map");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        calculateMapSize();
        initializeGrid();
        setupUI();
    }

    /**
     * Calculates the default width and height of the hex map based on screen
     * dimensions and hex size.
     */
    private void calculateMapSize() {

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int hexWidth = (int) (hexSize * 1.5);
        int hexHeight = (int) (Math.sqrt(3) * hexSize);

        mapWidth = (screen.width - 2 * panelPadding) / hexWidth;
        mapHeight = (screen.height - 2 * panelPadding) / (hexHeight * 3 / 4);
        mapWidth = 50;
        mapHeight = 50;
    }

    /**
     * Initializes the 2D hex grid with default terrain (GREEN).
     */
    private void initializeGrid() {
        grid = new Hexagone[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                grid[x][y] = new Hexagone(x, y, TypeTerrain.GREEN);

            }
        }
    }

    /**
     * Constructs and arranges the UI components including terrain buttons, map
     * panel, and control buttons.
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }

            @Override
            public Dimension getPreferredSize() {
                int hexWidth = (int) (1.5 * hexSize);
                double hexHeight = Math.sqrt(3) * hexSize;
                int width = mapWidth * hexWidth + 2 * panelPaddingX;
                int height = (int) (mapHeight * (hexHeight * 0.75)) + 2 * panelPaddingY;
                return new Dimension(width, height);
            }

        };
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = SwingUtilities.convertPoint(mapPanel, e.getPoint(), mapPanel.getParent());
            }
        });

        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint == null) {
                    return;
                }

                JViewport viewport = (JViewport) mapPanel.getParent();
                Point currentPoint = SwingUtilities.convertPoint(mapPanel, e.getPoint(), mapPanel.getParent());

                int dx = lastDragPoint.x - currentPoint.x;
                int dy = lastDragPoint.y - currentPoint.y;

                Point viewPos = viewport.getViewPosition();
                viewPos.translate(dx, dy);

                int maxX = mapPanel.getWidth() - viewport.getWidth();
                int maxY = mapPanel.getHeight() - viewport.getHeight();
                viewPos.x = Math.max(0, Math.min(viewPos.x, maxX));
                viewPos.y = Math.max(0, Math.min(viewPos.y, maxY));

                viewport.setViewPosition(viewPos);
                lastDragPoint = currentPoint;
            }
        });

        mapPanel.setBackground(new Color(240, 240, 240));
        mapPanel.addMouseListener(new MapMouseListener());

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Terrains"));

        ButtonGroup terrainGroup = new ButtonGroup();
        for (TypeTerrain terrain : TypeTerrain.values()) {
            JToggleButton btn = new JToggleButton(terrain.name(), terrain.getIcon());
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.addActionListener(e -> selectedTerrain = terrain);
            terrainGroup.add(btn);
            toolsPanel.add(btn);

            if (terrain == TypeTerrain.GREEN) {
                btn.setSelected(true);
            }

        }

        JButton saveBtn = new JButton("Sauvegarder");
        saveBtn.addActionListener(e -> saveMap());

        JButton loadBtn = new JButton("Charger");
        loadBtn.addActionListener(e -> loadMap());

        JButton clearBtn = new JButton("Nouvelle Map");
        clearBtn.addActionListener(e -> {
            initializeGrid();
            mapPanel.repaint();
        });

        JPanel controls = new JPanel(new GridLayout(3, 1, 5, 5));
        controls.add(saveBtn);
        controls.add(loadBtn);
        controls.add(clearBtn);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(toolsPanel, BorderLayout.CENTER);
        rightPanel.add(controls, BorderLayout.SOUTH);

        mainPanel.add(new JScrollPane(mapPanel), BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    /**
     * Renders the entire hex grid using the selected terrain images. Called
     * during repainting of the mapPanel.
     */
    private void drawMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double hexHeight = Math.sqrt(3) * hexSize;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int xPos = panelPaddingX + (int) (x * 1.5 * hexSize);
                int yPos = panelPaddingY + (int) (y * hexHeight);
                if (x % 2 == 1) {
                    yPos += hexHeight / 2;
                }

                drawHexagon(g2d, xPos, yPos, grid[x][y].getTypeTerrain());
            }
        }
    }

    /**
     * Draws a single hex tile at the specified screen coordinates using the
     * terrain's image.
     */
    private void drawHexagon(Graphics2D g, int x, int y, TypeTerrain terrain) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int xi = (int) (x + hexSize * Math.cos(angle));
            int yi = (int) (y + hexSize * Math.sin(angle));
            hex.addPoint(xi, yi);
        }

        g.drawImage(terrain.getIcon().getImage(),
                x - hexSize, y - hexSize,
                hexSize * 2, hexSize * 2, null);
        g.setColor(Color.BLACK);

    }

    /**
     * Computes the pixel center of a hexagon in the grid based on its grid
     * coordinates.
     */
    private Point getHexCenter(int x, int y) {
        double hexHeight = Math.sqrt(3) * hexSize;
        int xPos = (int) (x * 1.5 * hexSize);
        int yPos = (int) (y * hexHeight + ((x % 2) * hexHeight / 2));
        return new Point(xPos + hexSize, yPos + hexSize);
    }

    /**
     * Opens a file dialog to save the current grid state to a .map file via
     * serialization.
     */
    private void saveMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("maps"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(grid);
                JOptionPane.showMessageDialog(this, "Map sauvegardée !");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    /**
     * Loads a previously saved hex grid from a .map file and repaints the map.
     */
    private void loadMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("maps"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                grid = (Hexagone[][]) ois.readObject();
                mapWidth = grid.length;
                mapHeight = grid[0].length;
                mapPanel.repaint();
                JOptionPane.showMessageDialog(this, "Map chargée !");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    /**
     * Handles mouse clicks on the map to paint the selected terrain on clicked
     * hex cells.
     */
    private class MapMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            Point mouse = e.getPoint();
            JViewport viewport = (JViewport) mapPanel.getParent();
            Point viewPos = viewport.getViewPosition();
            mouse.translate(viewPos.x, viewPos.y);

            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {
                    Point center = getHexCenter(x, y);
                    Polygon hex = new Polygon();
                    for (int i = 0; i < 6; i++) {
                        double angle = Math.toRadians(60 * i);
                        int hx = (int) (center.x + hexSize * Math.cos(angle));
                        int hy = (int) (center.y + hexSize * Math.sin(angle));
                        hex.addPoint(hx, hy);
                    }

                    if (hex.contains(mouse)) {
                        grid[x][y] = new Hexagone(x, y, selectedTerrain);
                        mapPanel.repaint();
                        return;
                    }
                }
            }
        }
    }
}
