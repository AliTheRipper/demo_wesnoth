package view;

import model.Hexagone;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class InfoPanel extends JPanel {
    private JLabel joueurActifLabel = new JLabel("Joueur actif : ", SwingConstants.CENTER);
    private JLabel nomLabel = new JLabel("Unité : -");
    private JLabel joueurLabel = new JLabel("Joueur : -");
    private JLabel pvLabel = new JLabel("PV : -");
    private JLabel attaqueLabel = new JLabel("Attaque : -");
    private JLabel deplacementLabel = new JLabel("Déplacement : -");
    private JLabel terrainLabel = new JLabel("Terrain : -");
    private JLabel defenseLabel = new JLabel("Bonus défense : -");

    private JButton finTourButton = new JButton("Fin du tour");
    private JButton annulerMouvementButton = new JButton("Annuler mouvement");
    private JButton sauvegarderButton = new JButton("Sauvegarder");
    private JButton finPartieButton = new JButton("Fin de la partie");

    private String nomJoueur1;
    private String nomJoueur2;
    private PlateauDeJeu plateau;

    private final Color backgroundColor = new Color(20, 20, 30);
    private final Color borderGold = new Color(212, 175, 55);
    private final Color textColor = Color.WHITE;
    private final Color buttonBg = new Color(30, 40, 60);
    private final Color hoverColor = new Color(60, 90, 150);
private static Font gothicFont;

static {
    try {
        gothicFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/UnifrakturCook-Bold.ttf")).deriveFont(16f);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(gothicFont);
    } catch (Exception e) {
        gothicFont = new Font("Serif", Font.PLAIN, 16);
        System.err.println("Erreur chargement police gothique : " + e.getMessage());
    }
}

    public InfoPanel(String nomJoueur1, String nomJoueur2, PlateauDeJeu plateau) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
        this.plateau = plateau;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setBackground(backgroundColor);

        joueurActifLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        joueurActifLabel.setBackground(backgroundColor); // nouvelle ligne
joueurActifLabel.setOpaque(true);                // nouvelle ligne

        joueurActifLabel.setForeground(textColor);
        joueurActifLabel.setBackground(backgroundColor);
        joueurActifLabel.setOpaque(true);
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(joueurActifLabel, BorderLayout.NORTH);

        JPanel infosPanel = new JPanel();
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.Y_AXIS));
        infosPanel.setBackground(backgroundColor);
        infosPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderGold), "Unité sélectionnée"));
        
        for (JLabel label : new JLabel[]{nomLabel, joueurLabel, pvLabel, attaqueLabel, deplacementLabel}) {
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setForeground(textColor);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            infosPanel.add(label);
        }

        add(infosPanel, BorderLayout.CENTER);

        JPanel basPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        basPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        basPanel.setBackground(backgroundColor);

        for (JButton b : new JButton[]{finTourButton, annulerMouvementButton, sauvegarderButton, finPartieButton}) {
            b.setFont(gothicFont.deriveFont(Font.BOLD, 13f));
            b.setForeground(Color.WHITE);
            b.setBackground(buttonBg);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderGold),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(hoverColor);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(buttonBg);
                }
            });
            basPanel.add(b);
        }

        add(basPanel, BorderLayout.SOUTH);
        majJoueurActif(1);
    }

    public void majInfos(Unite u) {
        if (u == null) {
            nomLabel.setText("Unite : -");
            joueurLabel.setText("Joueur : -");
            pvLabel.setText("PV : -");
            attaqueLabel.setText("Attaque : -");
            deplacementLabel.setText("Déplacement : -");
        } else {
            nomLabel.setText("Unite : " + u.getNom());
            joueurLabel.setText("Joueur : " + u.getJoueur());
            pvLabel.setText("PV : " + u.getPointsVie());
            attaqueLabel.setText("Attaque : " + u.getAttaque());
            deplacementLabel.setText("Déplacement : " + u.getDeplacementRestant());
        }
    }

    public void majDeplacement(int valeur) {
        deplacementLabel.setText("Deplacement : " + valeur);
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
    public static boolean showStyledConfirmDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Confirmation", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 120);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(new Color(20, 20, 30));
        content.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Êtes-vous sûr de vouloir terminer la partie ?", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        content.add(label);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton yesBtn = createStyledButton("Oui");
        JButton noBtn = createStyledButton("Annuler");

        final boolean[] result = new boolean[1];

        yesBtn.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        noBtn.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        content.add(buttonPanel);

        dialog.setContentPane(content);
        dialog.setVisible(true);

        return result[0];
    }

    public static String showCustomInputDialog(Component parent) {
        JTextField inputField = new JTextField();
        inputField.setForeground(Color.WHITE);
        inputField.setBackground(new Color(30, 40, 60));
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel label = new JLabel("Nom de la sauvegarde :");
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 20, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
        mainPanel.add(label, BorderLayout.NORTH);
        mainPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(20, 20, 30));

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annuler");

        for (JButton b : new JButton[]{ok, cancel}) {
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(30, 40, 60));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setFont(gothicFont.deriveFont(Font.BOLD, 12f));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(212, 175, 55)),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }

        final String[] result = new String[1];

        ok.addActionListener(e -> {
            result[0] = inputField.getText();
            ((Window) SwingUtilities.getWindowAncestor(ok)).dispose();
        });

        cancel.addActionListener(e -> {
            result[0] = null;
            ((Window) SwingUtilities.getWindowAncestor(cancel)).dispose();
        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(20, 20, 30));
        content.add(mainPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        // Now create undecorated dialog BEFORE packing or showing
        JDialog dialog = new JDialog((Frame) null, "Sauvegarde", true);
        dialog.setUndecorated(true);
        dialog.setContentPane(content);
        dialog.setPreferredSize(new Dimension(400, 160)); // More spacious
        dialog.pack();
        dialog.setLocationRelativeTo(parent); // Center on game window
        dialog.setVisible(true);


        return result[0];
    }
    
    
    public static boolean showStyledConfirmDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Confirmation", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 130);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
    
        JPanel content = new JPanel();
        content.setBackground(new Color(20, 20, 30));
        content.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    
        JLabel label = new JLabel("Êtes-vous sûr de vouloir terminer la partie ?", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        content.add(label);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
    
        JButton yesBtn = createStyledButton("Oui");
        JButton noBtn = createStyledButton("Annuler");
    
        final boolean[] result = new boolean[1];
    
        yesBtn.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
    
        noBtn.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });
    
        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        content.add(buttonPanel);
    
        dialog.setContentPane(content);
        dialog.setVisible(true);
    
        return result[0];
    }
    
    private static JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(30, 40, 60));
        b.setFont(gothicFont.deriveFont(Font.BOLD, 14f));;
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(60, 90, 150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(30, 40, 60));
            }
        });
        return b;
    }


}
