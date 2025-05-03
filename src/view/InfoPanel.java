package view;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import model.Hexagone;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

public class InfoPanel extends JPanel {
    private JLabel joueurActifLabel = new JLabel("Joueur actif : ", SwingConstants.CENTER);
    private JLabel terrainEtDefenseLabel = new JLabel("-", SwingConstants.CENTER);

    private JLabel uniteImageLabel = new JLabel();

    private JLabel nomLabel = new JLabel("Nom : -");
    private JLabel joueurLabel = new JLabel("Joueur : -");
    private JLabel pvLabel = new JLabel("PV : -");
    private JLabel attaqueLabel = new JLabel("Attaque : -");
    private JLabel deplacementLabel = new JLabel("Déplacement : -");

    private JLabel descriptionLabel = new JLabel("Description : -");
    private JLabel attaqueDetailsLabel = new JLabel("Armes : -");

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

        joueurActifLabel.setFont(gothicFont.deriveFont(Font.BOLD, 18f));
        joueurActifLabel.setForeground(textColor);
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        joueurActifLabel.setOpaque(true);
        joueurActifLabel.setBackground(backgroundColor);
        add(joueurActifLabel, BorderLayout.NORTH);

        // Panel principal centré
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(backgroundColor);

        terrainEtDefenseLabel.setFont(gothicFont.deriveFont(Font.PLAIN, 13f));
        terrainEtDefenseLabel.setForeground(textColor);
        terrainEtDefenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(terrainEtDefenseLabel);

        // Image + stats
        // Image + stats côte à côte bien alignés
JPanel statsPanel = new JPanel();
statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.X_AXIS));
statsPanel.setBackground(backgroundColor);

uniteImageLabel.setPreferredSize(new Dimension(64, 64));
uniteImageLabel.setAlignmentY(Component.TOP_ALIGNMENT); // Aligner en haut
statsPanel.add(Box.createRigidArea(new Dimension(10, 0))); // marge gauche
statsPanel.add(uniteImageLabel);

JPanel rightStats = new JPanel();
rightStats.setLayout(new BoxLayout(rightStats, BoxLayout.Y_AXIS));
rightStats.setBackground(backgroundColor);
rightStats.setAlignmentY(Component.TOP_ALIGNMENT); // Aligner en haut

for (JLabel label : new JLabel[]{nomLabel, joueurLabel, pvLabel, attaqueLabel, deplacementLabel}) {
    label.setFont(gothicFont.deriveFont(Font.PLAIN, 13f));
    label.setForeground(textColor);
    label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
    rightStats.add(label);
}

statsPanel.add(Box.createRigidArea(new Dimension(10, 0))); // marge entre image et texte
statsPanel.add(rightStats);
centerPanel.add(statsPanel);


        // Description et armes
        descriptionLabel.setFont(gothicFont.deriveFont(Font.PLAIN, 12f));
        descriptionLabel.setForeground(textColor);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));

        attaqueDetailsLabel.setFont(gothicFont.deriveFont(Font.PLAIN, 12f));
        attaqueDetailsLabel.setForeground(textColor);
        attaqueDetailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        centerPanel.add(descriptionLabel);
        centerPanel.add(attaqueDetailsLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Bas
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
            uniteImageLabel.setIcon(null);
            nomLabel.setText("Nom : -");
            joueurLabel.setText("Joueur : -");
            pvLabel.setText("PV : -");
            attaqueLabel.setText("Attaque : -");
            deplacementLabel.setText("Déplacement : -");
            terrainEtDefenseLabel.setText("- : -");
            descriptionLabel.setText("Description : -");
            attaqueDetailsLabel.setText("Armes : -");
        } else {
            nomLabel.setText("Nom : " + u.getNom());
            joueurLabel.setText("Joueur : " + u.getJoueur());
            pvLabel.setText("PV : " + u.getPointsVie());
            attaqueLabel.setText("Attaque : " + u.getAttaque());
            deplacementLabel.setText("Déplacement : " + u.getDeplacementRestant());

            Image img = u.getIcone().getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            uniteImageLabel.setIcon(new ImageIcon(img));

            Hexagone hex = trouverHexagoneDeUnite(u);
            if (hex != null) {
                TypeTerrain terrain = hex.getTypeTerrain();
                terrainEtDefenseLabel.setText(terrain.name() + " : " + terrain.getBonusDefense() + "%");
            } else {
                terrainEtDefenseLabel.setText("? : ?");
            }

            descriptionLabel.setText("Description : Un combattant redoutable.");
            attaqueDetailsLabel.setText("Armes : Épée, Bouclier");
        }
    }

    private Hexagone trouverHexagoneDeUnite(Unite u) {
        for (int y = 0; y < plateau.getHauteur(); y++) {
            for (int x = 0; x < plateau.getLargeur(); x++) {
                if (plateau.getHexagone(x, y).getUnite() == u) {
                    return plateau.getHexagone(x, y);
                }
            }
        }
        return null;
    }

    public void majDeplacement(int valeur) {
        deplacementLabel.setText("Déplacement : " + valeur);
    }

    public void majJoueurActif(int num) {
        joueurActifLabel.setText("Joueur actif : " + (num == 1 ? nomJoueur1 : nomJoueur2));
    }

    public JButton getFinTourButton() { return finTourButton; }
    public JButton getAnnulerMouvementButton() { return annulerMouvementButton; }
    public JButton getSauvegarderButton() { return sauvegarderButton; }
    public JButton getFinPartieButton() { return finPartieButton; }

    public static boolean showStyledConfirmDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Confirmation", true);
        dialog.setUndecorated(true);
        dialog.setSize(550, 120);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(new Color(20, 20, 30));
        content.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Etes-vous sûr de vouloir terminer la partie ?", SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(gothicFont.deriveFont(Font.BOLD, 16f));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        content.add(label);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton yesBtn = createStyledButton("Oui");
        JButton noBtn = createStyledButton("Annuler");

        final boolean[] result = new boolean[1];
        yesBtn.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        noBtn.addActionListener(e -> { result[0] = false; dialog.dispose(); });

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
        label.setFont(gothicFont.deriveFont(Font.BOLD, 16f));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 20, 30));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 1));
        mainPanel.add(label, BorderLayout.NORTH);
        mainPanel.add(inputField, BorderLayout.CENTER);

        JButton ok = createStyledButton("OK");
        JButton cancel = createStyledButton("Annuler");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(20, 20, 30));
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(20, 20, 30));
        content.add(mainPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((Frame) null, "Sauvegarde", true);
        dialog.setUndecorated(true);
        dialog.setContentPane(content);
        dialog.setPreferredSize(new Dimension(400, 160));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return inputField.getText();
    }

    private static JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(30, 40, 60));
        b.setFont(gothicFont.deriveFont(Font.PLAIN, 13f));
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
