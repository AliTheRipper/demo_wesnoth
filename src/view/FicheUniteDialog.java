package view;

import model.Arme;
import model.Unite;

import javax.swing.*;
import java.awt.*;

public class FicheUniteDialog extends JDialog {
    public FicheUniteDialog(JFrame parent, Unite u) {
        super(parent, u.getNom(), true);
        setSize(350, 300);
        setLocationRelativeTo(parent);
        setUndecorated(false);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        content.add(new JLabel("Nom : " + u.getNom()));
        content.add(new JLabel("Joueur : " + u.getJoueur()));
        content.add(new JLabel("Points de vie : " + u.getPointsVie()));
        content.add(new JLabel("Attaque : " + u.getAttaque()));
        content.add(new JLabel("Déplacement : " + u.getDeplacementRestant()));
        content.add(new JLabel(""));

        content.add(new JLabel("Armes :"));
        for (Arme a : u.getArmes()) {
            content.add(new JLabel("  - " + a.toString()));
        }

        JButton fermer = new JButton("Fermer");
        fermer.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(fermer);

        add(content, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
