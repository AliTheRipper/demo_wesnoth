package view;

import java.awt.Dimension;
import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BoardPanel board = new BoardPanel();
JScrollPane scrollPane = new JScrollPane(board);
scrollPane.setPreferredSize(new Dimension(800, 600));
add(scrollPane);

    }
}
