package wargame.ui;

import javax.swing.*;
import java.awt.*;
import wargame.model.Board;

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

        // Affichage de la fenêtre
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
