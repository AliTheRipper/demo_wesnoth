package view;

import model.Unite;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private JLabel joueurActifLabel = new JLabel("Joueur actif : ", SwingConstants.CENTER);
    private JLabel nomLabel = new JLabel("Unité : -");
    private JLabel joueurLabel = new JLabel("Joueur : -");
    private JLabel pvLabel = new JLabel("PV : -");
    private JLabel attaqueLabel = new JLabel("Attaque : -");
    private JLabel deplacementLabel = new JLabel("Déplacement : -");

    private JButton finTourButton = new JButton("Fin du tour");

    private String nomJoueur1;
    private String nomJoueur2;

    public InfoPanel(String nomJoueur1, String nomJoueur2) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(new Color(240, 240, 240));
        setOpaque(true);

        // Titre joueur actif
        joueurActifLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(joueurActifLabel, BorderLayout.NORTH);

        // Partie centrale - infos unité
        JPanel infosPanel = new JPanel();
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.Y_AXIS));
        infosPanel.setBackground(new Color(240, 240, 240));
        infosPanel.setBorder(BorderFactory.createTitledBorder("Unité sélectionnée"));

        for (JLabel label : new JLabel[]{nomLabel, joueurLabel, pvLabel, attaqueLabel, deplacementLabel}) {
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            infosPanel.add(label);
        }

        add(infosPanel, BorderLayout.CENTER);

        // Bouton en bas
        JPanel basPanel = new JPanel();
        basPanel.setBackground(new Color(240, 240, 240));
        basPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        basPanel.setLayout(new BorderLayout());
        basPanel.add(finTourButton, BorderLayout.CENTER);

        add(basPanel, BorderLayout.SOUTH);

        majJoueurActif(1); // Afficher joueur 1 au lancement
    }

    public void majInfos(Unite u) {
        if (u == null) {
            nomLabel.setText("Unité : -");
            joueurLabel.setText("Joueur : -");
            pvLabel.setText("PV : -");
            attaqueLabel.setText("Attaque : -");
            deplacementLabel.setText("Déplacement : -");
        } else {
            nomLabel.setText("Unité : " + u.getNom());
            joueurLabel.setText("Joueur : " + u.getJoueur());
            pvLabel.setText("PV : " + u.getPointsVie());
            attaqueLabel.setText("Attaque : " + u.getAttaque());
            deplacementLabel.setText("Déplacement : " + u.getDeplacementRestant());
        }
    }

    public void majJoueurActif(int num) {
        if (num == 1) {
            joueurActifLabel.setText("Joueur actif : " + nomJoueur1);
        } else {
            joueurActifLabel.setText("Joueur actif : " + nomJoueur2);
        }
    }

    public JButton getFinTourButton() {
        return finTourButton;
    }
}
