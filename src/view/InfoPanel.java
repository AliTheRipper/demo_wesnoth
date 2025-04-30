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
    private JButton annulerMouvementButton = new JButton("Annuler mouvement");
    private JButton finPartieButton = new JButton("Fin de la partie");
    private JButton sauvegarderButton = new JButton("Sauvegarder");

    private String nomJoueur1;
    private String nomJoueur2;

    public InfoPanel(String nomJoueur1, String nomJoueur2) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(450, 0));

        setBackground(Color.WHITE);
        setOpaque(true);

        joueurActifLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(joueurActifLabel, BorderLayout.NORTH);

        JPanel infosPanel = new JPanel();
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.Y_AXIS));
        infosPanel.setBackground(Color.WHITE);
        infosPanel.setBorder(BorderFactory.createTitledBorder("Unité sélectionnée"));

        for (JLabel label : new JLabel[]{nomLabel, joueurLabel, pvLabel, attaqueLabel, deplacementLabel}) {
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            infosPanel.add(label);
        }

        add(infosPanel, BorderLayout.CENTER);

        JPanel basPanel = new JPanel();
        basPanel.setBackground(Color.WHITE);
        basPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        basPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Trois boutons maintenant
        basPanel.add(finTourButton);
        basPanel.add(annulerMouvementButton);
        basPanel.add(sauvegarderButton);
        basPanel.add(finPartieButton);

        add(basPanel, BorderLayout.SOUTH);

        majJoueurActif(1);
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

    public void majDeplacement(int valeur) {
        deplacementLabel.setText("Déplacement : " + valeur);
    }

    public void majJoueurActif(int num) {
        joueurActifLabel.setText("Joueur actif : " + (num == 1 ? nomJoueur1 : nomJoueur2));
    }

    public JButton getFinTourButton() {
        return finTourButton;
    }

    public JButton getAnnulerMouvementButton() {
        return annulerMouvementButton;
    }

    public JButton getSauvegarderButton() {
        return sauvegarderButton;
    }
    

    public JButton getFinPartieButton() {
        return finPartieButton;
    }
}
