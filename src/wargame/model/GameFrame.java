package wargame.ui;

import javax.swing.*;
import java.awt.*;
import wargame.model.Board;
import wargame.model.Hex;
import wargame.model.TerrainType;
import wargame.model.Unit;

/**
 * Fenêtre principale et affichage du plateau.
 */
public class GameFrame extends JFrame {
    private final Board board;
    private final JButton[][] buttons;
    private final JPanel boardPanel;

    public GameFrame() {
        // Initialisation du modèle
        board = new Board(10, 10);
        // Configuration de la fenêtre
        setTitle("Wargame 2025");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Construction du panneau du plateau
        boardPanel = new JPanel(new GridLayout(board.getWidth(), board.getHeight()));
        buttons = new JButton[board.getWidth()][board.getHeight()];
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                JButton btn = new JButton();
                buttons[x][y] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Rafraîchissement initial de la vue
        refreshBoard();

        // Affichage de la fenêtre
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    /**
     * Met à jour l'affichage des boutons selon l'état du modèle.
     */
    private void refreshBoard() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Hex hex = board.getHex(x, y);
                JButton btn = buttons[x][y];
                // Couleur de fond selon le terrain
                btn.setBackground(getColor(hex.getTerrain()));
                // Texte avec nom de l'unité, si présente
                btn.setText(hex.getUnit() != null ? hex.getUnit().getName() : "");
            }
        }
    }

    private Color getColor(TerrainType terrain) {
        return switch (terrain) {
            case PLAINS -> Color.GREEN;
            case FOREST -> new Color(34, 139, 34);
            case MOUNTAIN -> Color.GRAY;
            case HILL -> new Color(139, 69, 19);
            case RIVER -> Color.BLUE;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
