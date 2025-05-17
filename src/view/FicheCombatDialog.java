package view;

import model.Arme;
import model.Unite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FicheCombatDialog extends JDialog {
    public static final int DECISION_ANNULER = 0;
    public static final int DECISION_ATTAQUER = 1;

    private int decision = DECISION_ANNULER;

    public FicheCombatDialog(JFrame parent, Unite attaquant, Unite defenseur) {
        super(parent, "Combat", true);
        setLayout(new BorderLayout());
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel(new GridLayout(2, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === Partie Attaquant ===
        JPanel attaquantPanel = createUnitePanel(attaquant, "Attaquant");
        content.add(attaquantPanel);

        // === Partie Défenseur ===
        JPanel defenseurPanel = createUnitePanel(defenseur, "Défenseur");
        content.add(defenseurPanel);

        // === Statistiques de combat ===
        JPanel statsPanel = new JPanel(new GridLayout(0, 1));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Prévision de combat"));

        // Calcul des dégâts estimés
        int degatsEstimes = attaquant.getAttaque() - defenseur.getDefense();
        degatsEstimes = Math.max(1, degatsEstimes); // Au moins 1 dégât

        statsPanel.add(new JLabel("Dégâts estimés: " + degatsEstimes));
        statsPanel.add(new JLabel("PV restants estimés: " + Math.max(0, defenseur.getPointsVie() - degatsEstimes)));

        content.add(statsPanel);

        // === Armes ===
        JPanel armesPanel = new JPanel(new GridLayout(0, 1));
        armesPanel.setBorder(BorderFactory.createTitledBorder("Armes disponibles"));

        for (Arme arme : attaquant.getArmes()) {
            armesPanel.add(new JLabel(arme.getNom() + ": " + arme.getDegats() + " dégâts"));
        }

        content.add(armesPanel);

        // === Boutons ===
        JPanel buttons = new JPanel();
        JButton btnAttaquer = new JButton("Attaquer");
        JButton btnAnnuler = new JButton("Annuler");

        btnAttaquer.addActionListener(e -> {
            decision = DECISION_ATTAQUER;
            dispose();
        });

        btnAnnuler.addActionListener(e -> {
            decision = DECISION_ANNULER;
            dispose();
        });

        buttons.add(btnAnnuler);
        buttons.add(btnAttaquer);

        add(content, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createUnitePanel(Unite unite, String role) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(role));

        // Image
        if (unite.getIcone() != null) {
            ImageIcon icon = new ImageIcon(unite.getIcone().getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            panel.add(new JLabel(icon), BorderLayout.WEST);
        }

        // Stats
        JPanel stats = new JPanel(new GridLayout(0, 1));
        stats.add(new JLabel(unite.getNom()));
        stats.add(new JLabel("PV: " + unite.getPointsVie()));
        stats.add(new JLabel("Attaque: " + unite.getAttaque()));
        stats.add(new JLabel("Défense: " + unite.getDefense()));

        // AJOUTEZ ICI LA NOUVELLE LIGNE
        stats.add(new JLabel("Attaque possible: " + (unite.peutAttaquer() ? "Oui" : "Non")));

        panel.add(stats, BorderLayout.CENTER);

        return panel;
    }

    public int getDecision() {
        return decision;
    }
}