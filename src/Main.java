
import view.GameWindow;

import javax.swing.*;

/**
 * Point d'entrée principal de l'application. Lance l'interface graphique du jeu
 * via la classe GameWindow.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow().setVisible(true);
        });
    }
}
