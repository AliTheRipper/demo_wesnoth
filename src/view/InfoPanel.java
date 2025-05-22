package view;

import java.awt.*;
import java.io.File;
import java.util.Map;

import javax.swing.*;
import model.Hexagone;
import model.Joueur;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

/**
 * Panneau latéral affichant les informations du joueur : terrain occupé, bonus
 * de défense, statistiques de l'unité sélectionnée ainsi que les actions
 * courantes (fin de tour, annulation, sauvegarde…).
 */
public class InfoPanel extends JPanel {

    private final JLabel joueurActifLabel = new JLabel("Joueur actif : ", SwingConstants.CENTER);
    private final JLabel terrainEtDefenseLabel = new JLabel("-", SwingConstants.CENTER);

    private final JLabel coordLabel = new JLabel("Coord : -", SwingConstants.CENTER);

    private final JLabel uniteImageLabel = new JLabel();

    private final JLabel nomLabel = new JLabel("Nom : -");
    private final JLabel joueurLabel = new JLabel("Joueur : -");
    private final JLabel pvLabel = new JLabel("PV : -");
    private final JLabel attaqueLabel = new JLabel("Attaque : -");
    private final JLabel defenseLabel = new JLabel("Defense : -");
    private final JLabel deplacementLabel = new JLabel("Deplacement : -");

    private final JTextArea descriptionLabel = new JTextArea("Description : -");
    private final JTextArea attaqueDetailsLabel = new JTextArea("Armes : -");

    private final JButton finTourButton = new JButton("Fin du tour");
    private final JButton annulerMouvementButton = new JButton("Annuler mouvement");
    private final JButton sauvegarderButton = new JButton("Sauvegarder");
    private final JButton finPartieButton = new JButton("Fin de la partie");

    private final String nomJoueur1;
    private final String nomJoueur2;
    private final PlateauDeJeu plateau;

    public static final Color BACKGROUND = new Color(20, 20, 30);
    private static final Color GOLD = new Color(212, 175, 55);
    public static final Color TEXT = Color.WHITE;
    private static final Color BTN_BG = new Color(30, 40, 60);
    private static final Color BTN_HOVER = new Color(60, 90, 150);

    private final JButton zoomInButton = new JButton("+");
    private final JButton zoomOutButton = new JButton("-");
    private MiniMapPanel miniMapPanel;

    private static final Map<String, String> UNIT_DESCRIPTIONS = Map.of(
            "Archer", "Unite a distance, efficace pour harceler l ennemi a longue portee.",
            "Soldat", "Unite equilibree avec une bonne defense et attaque en melee.",
            "Cavalier", "Tres mobile, il excelle dans les attaques rapides.",
            "Mage", "Inflige de lourds degats a distance, mais reste fragile.",
            "Fantassin", "Robuste au corps a corps avec une hache redoutable.",
            "Voleur", "Rapide et discret, ideal pour les escarmouches et les fuites."
    );

    private final Font gothic;

    public static Font GOTHIC_FALLBACK;

    static {
        try {
            GOTHIC_FALLBACK = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/UnifrakturCook-Bold.ttf"))
                    .deriveFont(Font.PLAIN, 16f);
        } catch (Exception e) {
            GOTHIC_FALLBACK = new Font("Serif", Font.PLAIN, 16);
            System.err.println("Erreur chargement police gothique fallback : " + e.getMessage());
        }
    }

    public InfoPanel(String nomJoueur1, String nomJoueur2, PlateauDeJeu plateau, Font gothicFont) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
        this.plateau = plateau;
        this.gothic = gothicFont;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setMinimumSize(new Dimension(250, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(BACKGROUND);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BACKGROUND);

        miniMapPanel = new MiniMapPanel(plateau, null);
        miniMapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(0));
        topPanel.add(miniMapPanel);
        topPanel.add(Box.createVerticalStrut(10));

        joueurActifLabel.setFont(gothic.deriveFont(Font.BOLD, 18f));
        joueurActifLabel.setForeground(TEXT);
        joueurActifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        topPanel.add(joueurActifLabel);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BACKGROUND);
        center.setOpaque(false);

        terrainEtDefenseLabel.setFont(gothic.deriveFont(Font.PLAIN, 13f));
        terrainEtDefenseLabel.setForeground(TEXT);
        terrainEtDefenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(terrainEtDefenseLabel);
        coordLabel.setFont(gothic.deriveFont(Font.PLAIN, 13f));
        coordLabel.setForeground(TEXT);
        coordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(coordLabel);
        center.add(Box.createVerticalStrut(5));

        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
        stats.setBackground(BACKGROUND);
        stats.setOpaque(false);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setBackground(BACKGROUND);
        stats.setOpaque(false);

        uniteImageLabel.setPreferredSize(new Dimension(64, 64));
        uniteImageLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        stats.add(uniteImageLabel);

        stats.add(uniteImageLabel, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(BACKGROUND);
        right.setOpaque(false);

        for (JLabel l : new JLabel[]{nomLabel, joueurLabel, pvLabel, attaqueLabel, defenseLabel, deplacementLabel}) {
            l.setFont(gothic.deriveFont(Font.PLAIN, 13f));
            l.setForeground(TEXT);
            l.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
            right.add(l);
        }

        stats.add(right, BorderLayout.CENTER);
        center.add(stats);

        descriptionLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        descriptionLabel.setForeground(TEXT);
        descriptionLabel.setEditable(false);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 2, 10));
        attaqueDetailsLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        attaqueDetailsLabel.setForeground(TEXT);
        attaqueDetailsLabel.setEditable(false);
        attaqueDetailsLabel.setOpaque(false);
        attaqueDetailsLabel.setLineWrap(true);
        attaqueDetailsLabel.setWrapStyleWord(true);
        attaqueDetailsLabel.setRows(1);
        attaqueDetailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND);
        textPanel.setOpaque(false);

        descriptionLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        descriptionLabel.setForeground(TEXT);
        descriptionLabel.setEditable(false);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setRows(3);
        descriptionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 2, 10));

        attaqueDetailsLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        attaqueDetailsLabel.setForeground(TEXT);
        attaqueDetailsLabel.setEditable(false);
        attaqueDetailsLabel.setOpaque(false);
        attaqueDetailsLabel.setLineWrap(true);
        attaqueDetailsLabel.setWrapStyleWord(true);
        attaqueDetailsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        attaqueDetailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        textPanel.add(descriptionLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(attaqueDetailsLabel);

        center.add(textPanel);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(0, 1, 0, 10));
        south.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        south.setBackground(BACKGROUND);
        for (JButton b : new JButton[]{
            finTourButton,
            annulerMouvementButton,
            sauvegarderButton,
            finPartieButton}) {
            styliseBouton(b);
            south.add(b);
        }

        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        zoomPanel.setBackground(BACKGROUND);
        styliseBouton(zoomInButton);
        styliseBouton(zoomOutButton);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        south.add(zoomPanel);

        add(south, BorderLayout.SOUTH);

        majJoueurActif(new Joueur(nomJoueur1, false, ""));
    }

    public void majInfos(Unite u) {
        if (u == null) {
            uniteImageLabel.setIcon(null);
            nomLabel.setText("Nom : -");
            joueurLabel.setText("Joueur : -");
            pvLabel.setText("PV : -");
            attaqueLabel.setText("Attaque : -");
            defenseLabel.setText("Defense : -");
            deplacementLabel.setText("Deplacement : -");
            terrainEtDefenseLabel.setText("- : -");
            descriptionLabel.setText("Description : -");
            attaqueDetailsLabel.setText("Armes : -");
            return;
        }

        nomLabel.setText("Nom : " + u.getNom());
        String joueurNom = u.getJoueur().getNom();
        if (joueurNom.equals("Humain 1")) {
            joueurNom = nomJoueur1;
        } else if (joueurNom.equals("Humain 2")) {
            joueurNom = nomJoueur2;
        } else if (u.getJoueur().estIA() || joueurNom.equals("IA")) {
            joueurNom = "Robot";
        }
        joueurLabel.setText("Joueur : " + joueurNom);

        pvLabel.setText("PV : " + u.getPointsVie());
        attaqueLabel.setText("Attaque : " + u.getAttaque());
        defenseLabel.setText("Defense : " + u.getDefense());
        deplacementLabel.setText("Deplacement : " + u.getDeplacementRestant());

        if (u.getIcone() != null) {
            Image img = u.getIcone().getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            uniteImageLabel.setIcon(new ImageIcon(img));
        } else {
            uniteImageLabel.setIcon(null);
        }

        Hexagone hex = u.getPosition();
        if (hex != null) {
            TypeTerrain tt = hex.getTypeTerrain();
            terrainEtDefenseLabel.setText("Bonus defense : " + tt.getBonusDefense() + "%");
        } else {
            terrainEtDefenseLabel.setText("Bonus defense : ?");
        }

        String nom = u.getNom();
        String description = UNIT_DESCRIPTIONS.getOrDefault(nom, "Unité sans description.");
        descriptionLabel.setText("Description : " + description);

        attaqueDetailsLabel.setText("Armes : " + u.getArmes().stream()
                .map(arme -> arme.getNom())
                .reduce((a, b) -> a + ", " + b).orElse("-"));
    }

    public MiniMapPanel getMiniMapPanel() {
        return miniMapPanel;
    }

    public void majDeplacement(int val) {
        deplacementLabel.setText("Deplacement : " + val);
    }

    public void majJoueurActif(Joueur j) {
        String affichage;

        if (j.equals(plateau.getJoueur1())) {
            affichage = nomJoueur1;
        } else if (j.equals(plateau.getJoueur2())) {
            affichage = nomJoueur2;
        } else if (j.estIA()) {
            affichage = "Robot";
        } else {
            affichage = j.getNom();
        }

        joueurActifLabel.setText("Joueur actif : " + affichage);
    }

    public JButton getZoomInButton() {
        return zoomInButton;
    }

    public JButton getZoomOutButton() {
        return zoomOutButton;
    }

    private void styliseBouton(JButton b) {
        b.setFont(gothic.deriveFont(Font.BOLD, 13f));
        b.setForeground(Color.WHITE);
        b.setBackground(BTN_BG);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_BG);
            }
        });
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

    public static boolean showStyledConfirmDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Confirmation", true);
        d.setUndecorated(true);
        d.setSize(550, 120);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(BACKGROUND);

        JLabel lbl = new JLabel("Etes vous sur de vouloir terminer la partie ?");
        lbl.setForeground(TEXT);
        lbl.setFont(GOTHIC_FALLBACK.deriveFont(Font.BOLD, 16f));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        textPanel.add(lbl);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btns.setBackground(BACKGROUND);
        JButton yes = createStyledButton("Oui");
        JButton no = createStyledButton("Annuler");
        btns.add(yes);
        btns.add(no);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        content.add(textPanel, BorderLayout.CENTER);
        content.add(btns, BorderLayout.SOUTH);
        final boolean[] res = new boolean[1];

        yes.addActionListener(e -> {
            res[0] = true;
            d.dispose();
        });

        no.addActionListener(e -> {
            res[0] = false;
            d.dispose();
        });

        d.setContentPane(content);
        d.setVisible(true);

        return res[0];

    }

    public static String showCustomInputDialog(Component parent) {
        JTextField input = new JTextField();
        input.setForeground(TEXT);
        input.setBackground(BTN_BG);
        input.setCaretColor(TEXT);
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lbl = new JLabel("Nom de la sauvegarde :");
        lbl.setForeground(TEXT);
        lbl.setFont(GOTHIC_FALLBACK.deriveFont(Font.BOLD, 16f));
        lbl.setMaximumSize(new Dimension(460, 60));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createLineBorder(GOLD, 1));
        main.add(lbl, BorderLayout.NORTH);
        main.add(input, BorderLayout.CENTER);

        JButton ok = createStyledButton("OK");
        JButton cancel = createStyledButton("Annuler");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btns.setBackground(BACKGROUND);
        btns.add(ok);
        btns.add(cancel);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);
        content.add(main, BorderLayout.CENTER);
        content.add(btns, BorderLayout.SOUTH);

        JDialog d = new JDialog((Frame) null, "Sauvegarde", true);
        d.setUndecorated(true);
        d.setContentPane(content);
        d.setPreferredSize(new Dimension(450, 130));

        d.pack();
        d.setLocationRelativeTo(parent);

        final String[] res = new String[1];
        ok.addActionListener(e -> {
            res[0] = input.getText();
            d.dispose();
        });
        cancel.addActionListener(e -> {
            res[0] = null;
            d.dispose();
        });
        d.setVisible(true);
        return res[0];
    }

    public static JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setForeground(TEXT);
        b.setBackground(BTN_BG);
        b.setFocusPainted(false);
        b.setFont(GOTHIC_FALLBACK.deriveFont(Font.PLAIN, 13f));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(BTN_BG);
            }
        });
        return b;
    }

    public static void showStyledTurnDialog(JFrame parent, String joueurNom) {
        JDialog dialog = new JDialog(parent, "Tour", false);
        dialog.setUndecorated(true);
        dialog.setSize(350, 70);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(GOLD, 2));

        JLabel label = new JLabel("<html><div style='text-align: center;'>Tour de<br>" + joueurNom + "</div></html>", SwingConstants.CENTER);

        label.setForeground(TEXT);
        label.setFont(GOTHIC_FALLBACK.deriveFont(Font.BOLD, 14f));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        content.add(label, BorderLayout.CENTER);

        dialog.setContentPane(content);
        dialog.setVisible(true);

        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    public static void showStyledWarningDialog(JFrame parent, String message, String titre) {
        JDialog dialog = new JDialog(parent, titre, true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 160);

        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        content.setLayout(new BorderLayout());

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setForeground(TEXT);
        lbl.setFont(GOTHIC_FALLBACK.deriveFont(Font.BOLD, 16f));
        lbl.setMaximumSize(new Dimension(460, 60));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lbl);

        JButton ok = createStyledButton("OK");
        ok.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BACKGROUND);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnPanel.add(ok);

        content.add(btnPanel);
        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    public static void showStyledInfoDialog(JFrame parent, String message, String titre) {
        JDialog dialog = new JDialog(parent, titre, true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 160);
        dialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(GOLD, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setForeground(TEXT);
        lbl.setFont(GOTHIC_FALLBACK.deriveFont(Font.BOLD, 16f));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(lbl, gbc);

        JButton ok = createStyledButton("OK");
        ok.addActionListener(e -> dialog.dispose());

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        content.add(ok, gbc);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    /**
     * Met à jour l’affichage des coordonnées survolées
     */
    public void majCoordonnees(int x, int y) {
        if (x >= 0 && y >= 0) {
            coordLabel.setText("coordonnees : " + x + " , " + y);
        } else {
            coordLabel.setText("coordonnees : -");
        }
        coordLabel.revalidate();
        coordLabel.repaint();

    }

}
