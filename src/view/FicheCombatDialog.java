package view;

import model.Arme;
import model.Unite;

import javax.swing.*;
import java.awt.*;

public class FicheCombatDialog extends JDialog {
    public FicheCombatDialog(JFrame parent, Unite attaquant, Unite defenseur) {
        super(parent, "Combat", true);
        setLayout(new BorderLayout());
        setSize(600, 300);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel(new GridLayout(2, 2));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === Partie Attaquant ===
        JPanel attaquantPanel = new JPanel();
        attaquantPanel.setLayout(new BoxLayout(attaquantPanel, BoxLayout.Y_AXIS));
        attaquantPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Image attaquant
        ImageIcon iconeAttaquant = attaquant.getIcone();
        if (iconeAttaquant != null) {
            JLabel imageLabel = new JLabel(new ImageIcon(
                    iconeAttaquant.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)
            ));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            attaquantPanel.add(imageLabel);
        }

        // Texte attaquant
        JLabel attaquantLabel = new JLabel("<html><h3>" + attaquant.getNom() + "</h3><br/>PV : " + attaquant.getPointsVie() + "</html>");
        attaquantLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        attaquantPanel.add(attaquantLabel);

        content.add(attaquantPanel);

        // === Partie Défenseur ===
        JPanel defenseurPanel = new JPanel();
        defenseurPanel.setLayout(new BoxLayout(defenseurPanel, BoxLayout.Y_AXIS));
        defenseurPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Image défenseur
        ImageIcon iconeDefenseur = defenseur.getIcone();
        if (iconeDefenseur != null) {
            JLabel imageLabel = new JLabel(new ImageIcon(
                    iconeDefenseur.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)
            ));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            defenseurPanel.add(imageLabel);
        }

        // Texte défenseur
        JLabel defenseurLabel = new JLabel("<html><h3>" + defenseur.getNom() + "</h3><br/>PV : " + defenseur.getPointsVie() + "</html>");
        defenseurLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        defenseurPanel.add(defenseurLabel);

        content.add(defenseurPanel);

        // === Armes Attaquant ===
        StringBuilder aWeapons = new StringBuilder("<html>");
        for (Arme arme : attaquant.getArmes()) {
            aWeapons.append("→ ").append(arme.getNom()).append(": ").append(arme.getDescription()).append("<br/>");
        }
        aWeapons.append("</html>");
        content.add(new JLabel(aWeapons.toString()));

        // === Armes Défenseur ===
        StringBuilder dWeapons = new StringBuilder("<html>");
        for (Arme arme : defenseur.getArmes()) {
            dWeapons.append("→ ").append(arme.getNom()).append(": ").append(arme.getDescription()).append("<br/>");
        }
        dWeapons.append("</html>");
        content.add(new JLabel(dWeapons.toString()));

        // === Boutons ===
        JPanel buttons = new JPanel();
        JButton btnAttaquer = new JButton("Attaquer");
        JButton btnAnnuler = new JButton("Annuler");

        buttons.add(btnAttaquer);
        buttons.add(btnAnnuler);

        btnAttaquer.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        btnAnnuler.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        add(content, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}
