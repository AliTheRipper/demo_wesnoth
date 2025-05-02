package view;

import model.Arme;
import model.Unite;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CombatPopup {

    public static void afficher(JFrame parent, Unite attaquant, Unite defenseur) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBackground(new Color(30, 30, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Attaquant : " + attaquant.getNom()));
        panel.add(new JLabel("Défenseur : " + defenseur.getNom()));

        panel.add(new JLabel("PV : " + attaquant.getPointsVie()));
        panel.add(new JLabel("PV : " + defenseur.getPointsVie()));

        panel.add(new JLabel("Armes attaquant :"));
        panel.add(new JLabel("Armes défenseur :"));

        JTextArea armeA = new JTextArea();
        JTextArea armeD = new JTextArea();

        armeA.setEditable(false);
        armeD.setEditable(false);

        for (Arme a : attaquant.getArmes()) {
            armeA.append(a.getNom() + " (" + a.getType() + ", " + a.getDegats() + "x" + a.getCoups() + ", " + a.getPrecision() + "%)\n");
        }

        for (Arme a : defenseur.getArmes()) {
            armeD.append(a.getNom() + " (" + a.getType() + ", " + a.getDegats() + "x" + a.getCoups() + ", " + a.getPrecision() + "%)\n");
        }

        panel.add(armeA);
        panel.add(armeD);

        JOptionPane.showMessageDialog(parent, panel, "Fiche Combat", JOptionPane.PLAIN_MESSAGE);
    }
}
