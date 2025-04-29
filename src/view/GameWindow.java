package view;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Wargame - Plateau");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        BoardPanel board = new BoardPanel();
        JScrollPane scrollPane = new JScrollPane(board);
        add(scrollPane);
    }
}