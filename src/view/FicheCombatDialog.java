package view;

import model.Arme;
import model.Unite;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Boîte de dialogue affichée lors d’une tentative d’attaque. Présente les
 * statistiques de l’attaquant et du défenseur, les dégâts estimés, les armes
 * disponibles, et permet au joueur de confirmer ou annuler l’attaque.
 */
public class FicheCombatDialog extends JDialog {

    public static final int DECISION_ANNULER = 0;
    public static final int DECISION_ATTAQUER = 1;

    private int decision = DECISION_ANNULER;

    /**
     * Construit la fenêtre de dialogue affichant les informations de combat.
     *
     * @param parent Fenêtre parente pour le positionnement du dialogue
     * @param attaquant Unité effectuant l'attaque
     * @param defenseur Unité ciblée par l’attaque
     */
    public FicheCombatDialog(JFrame parent, Unite attaquant, Unite defenseur) {
        super(parent, "", true);
        setUndecorated(true);

        setLayout(new BorderLayout());
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel(new GridLayout(2, 2, 10, 10));
        content.setBackground(InfoPanel.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel attaquantPanel = createUnitePanel(attaquant, "Attaquant");
        content.add(attaquantPanel);

        JPanel defenseurPanel = createUnitePanel(defenseur, "Defenseur");
        content.add(defenseurPanel);

        JPanel statsPanel = new JPanel(new GridLayout(0, 1));
        statsPanel.setBackground(InfoPanel.BACKGROUND);
        TitledBorder statsBorder = BorderFactory.createTitledBorder("Prevision de combat");
        statsBorder.setTitleColor(InfoPanel.TEXT);
        statsBorder.setTitleFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(Font.BOLD, 14f));
        statsBorder.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55)));
        statsPanel.setBorder(statsBorder);

        int degatsEstimes = attaquant.getAttaque() - defenseur.getDefense();
        degatsEstimes = Math.max(1, degatsEstimes);

        JLabel dmgLabel = new JLabel("Degats estimes : " + degatsEstimes);
        JLabel pvLabel = new JLabel("PV restants estimes : " + Math.max(0, defenseur.getPointsVie() - degatsEstimes));

        for (JLabel lbl : new JLabel[]{dmgLabel, pvLabel}) {
            lbl.setForeground(InfoPanel.TEXT);
            lbl.setFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(14f));
            statsPanel.add(lbl);
        }

        content.add(statsPanel);

        JPanel armesPanel = new JPanel(new GridLayout(0, 1));
        armesPanel.setBackground(InfoPanel.BACKGROUND);
        TitledBorder armesBorder = BorderFactory.createTitledBorder("Armes disponibles");
        armesBorder.setTitleColor(InfoPanel.TEXT);
        armesBorder.setTitleFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(Font.BOLD, 14f));
        armesBorder.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55)));
        armesPanel.setBorder(armesBorder);

        for (Arme arme : attaquant.getArmes()) {
            JLabel armeLabel = new JLabel(arme.getNom() + ": " + arme.getDegats() + " degats");
            armeLabel.setForeground(InfoPanel.TEXT);
            armeLabel.setFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(14f));
            armesPanel.add(armeLabel);
        }

        content.add(armesPanel);

        JPanel buttons = new JPanel();
        buttons.setBackground(InfoPanel.BACKGROUND);

        JButton btnAttaquer = InfoPanel.createStyledButton("Attaquer");
        JButton btnAnnuler = InfoPanel.createStyledButton("Annuler");

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

    /**
     * Crée un panneau affichant les informations d’une unité (nom, PV, attaque,
     * défense, icône...).
     *
     * @param unite L’unité à afficher
     * @param role Rôle de l’unité dans le combat (ex. "Attaquant", "Défenseur")
     * @return JPanel contenant les informations formatées
     */
    private JPanel createUnitePanel(Unite unite, String role) {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder unitBorder = BorderFactory.createTitledBorder(role);
        unitBorder.setTitleColor(InfoPanel.TEXT);
        unitBorder.setTitleFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(Font.BOLD, 14f));
        unitBorder.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55)));
        panel.setBorder(unitBorder);

        panel.setBackground(InfoPanel.BACKGROUND);

        if (unite.getIcone() != null) {
            ImageIcon icon = new ImageIcon(unite.getIcone().getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel iconLabel = new JLabel(icon);
            panel.add(iconLabel, BorderLayout.WEST);
        }

        JPanel stats = new JPanel(new GridLayout(0, 1));
        stats.setBackground(InfoPanel.BACKGROUND);

        JLabel nom = new JLabel(unite.getNom());
        JLabel pv = new JLabel("PV : " + unite.getPointsVie());
        JLabel atk = new JLabel("Attaque : " + unite.getAttaque());
        JLabel def = new JLabel("Defense : " + unite.getDefense());
        JLabel canAttack = new JLabel("Attaque possible : " + (unite.peutAttaquer() ? "Oui" : "Non"));

        for (JLabel l : new JLabel[]{nom, pv, atk, def, canAttack}) {
            l.setForeground(InfoPanel.TEXT);
            l.setFont(InfoPanel.GOTHIC_FALLBACK.deriveFont(13f));
            stats.add(l);
        }

        panel.add(stats, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Retourne la décision de l’utilisateur après la fermeture du dialogue.
     *
     * @return DECISION_ATTAQUER ou DECISION_ANNULER
     */
    public int getDecision() {
        return decision;
    }
}
