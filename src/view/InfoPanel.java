package view;

import java.awt.*;
import java.io.File;
import javax.swing.*;

import model.Hexagone;
import model.Joueur;
import model.PlateauDeJeu;
import model.TypeTerrain;
import model.Unite;

/**
 * Panneau latéral affichant les informations du joueur : terrain occupé,
 * bonus de défense, statistiques de l'unité sélectionnée ainsi que les
 * actions courantes (fin de tour, annulation, sauvegarde…).
 */
public class InfoPanel extends JPanel {

    /* ───────────────── TITRES / INFOS GÉNÉRALES ───────────────── */
    private final JLabel joueurActifLabel = new JLabel("Joueur actif : ", SwingConstants.CENTER);
    private final JLabel terrainEtDefenseLabel = new JLabel("-", SwingConstants.CENTER);

    /* ───────────────── IMAGE & STATS DE L'UNITÉ ───────────────── */
    private final JLabel uniteImageLabel = new JLabel();

    private final JLabel nomLabel = new JLabel("Nom : -");
    private final JLabel joueurLabel = new JLabel("Joueur : -");
    private final JLabel pvLabel = new JLabel("PV : -");
    private final JLabel attaqueLabel = new JLabel("Attaque : -");
    private final JLabel defenseLabel = new JLabel("Défense : -");
    private final JLabel deplacementLabel = new JLabel("Déplacement : -");

    /* ───────────────── DESCRIPTION & ARMES ───────────────── */
    private final JTextArea descriptionLabel = new JTextArea("Description : -");
    private final JLabel attaqueDetailsLabel = new JLabel("Armes : -");

    /* ───────────────── BOUTONS DE CONTRÔLE ───────────────── */
    private final JButton finTourButton = new JButton("Fin du tour");
    private final JButton annulerMouvementButton = new JButton("Annuler mouvement");
    private final JButton sauvegarderButton = new JButton("Sauvegarder");
    private final JButton finPartieButton = new JButton("Fin de la partie");

    private final String nomJoueur1;
    private final String nomJoueur2;
    private final PlateauDeJeu plateau;

    /* ───────────────── THÈME COULEUR ───────────────── */
    private static final Color BACKGROUND = new Color(20, 20, 30);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color TEXT = Color.WHITE;
    private static final Color BTN_BG = new Color(30, 40, 60);
    private static final Color BTN_HOVER = new Color(60, 90, 150);

    private static Font gothic;
    static {
        try {
            gothic = Font.createFont(Font.TRUETYPE_FONT,
                    new File("resources/fonts/UnifrakturCook-Bold.ttf")).deriveFont(16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(gothic);
        } catch (Exception e) {
            gothic = new Font("Serif", Font.PLAIN, 16);
            System.err.println("Erreur chargement police gothique : " + e.getMessage());
        }
    }

    /* ═════════════════════════════════════════════════════════════ */
    /* Constructeur */
    /* ═════════════════════════════════════════════════════════════ */
    public InfoPanel(String nomJoueur1, String nomJoueur2, PlateauDeJeu plateau) {
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
        this.plateau = plateau;

        setLayout(new BorderLayout());
        // setPreferredSize(new Dimension(310, 0)); // +10 px pour éviter coupures
        setPreferredSize(null); // → laisse le layout gérer
        setMinimumSize(new Dimension(250, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        setBackground(BACKGROUND);

        /* ---------- En‑tête ---------- */
        joueurActifLabel.setFont(gothic.deriveFont(Font.BOLD, 18f));
        joueurActifLabel.setForeground(TEXT);
        joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(joueurActifLabel, BorderLayout.NORTH);

        /* ---------- Zone centrale ---------- */
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BACKGROUND);

        terrainEtDefenseLabel.setFont(gothic.deriveFont(Font.PLAIN, 13f));
        terrainEtDefenseLabel.setForeground(TEXT);
        terrainEtDefenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(terrainEtDefenseLabel);

        // Image & stats
        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
        stats.setBackground(BACKGROUND);

        uniteImageLabel.setPreferredSize(new Dimension(64, 64));
        stats.add(Box.createRigidArea(new Dimension(10, 0)));
        stats.add(uniteImageLabel);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(BACKGROUND);

        for (JLabel l : new JLabel[] { nomLabel, joueurLabel, pvLabel, attaqueLabel, defenseLabel, deplacementLabel }) {
            l.setFont(gothic.deriveFont(Font.PLAIN, 13f));
            l.setForeground(TEXT);
            l.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
            right.add(l);
        }
        stats.add(Box.createRigidArea(new Dimension(10, 0)));
        stats.add(right);
        center.add(stats);

        // Description + armes
        // Appliquer les styles aux JLabel
        for (JLabel l : new JLabel[] { attaqueDetailsLabel }) {
            l.setFont(gothic.deriveFont(Font.PLAIN, 12f));
            l.setForeground(TEXT);
        }

        // Appliquer les styles au JTextArea
        descriptionLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        descriptionLabel.setForeground(TEXT);

        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));
        attaqueDetailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        descriptionLabel.setEditable(false);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setForeground(TEXT);
        descriptionLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));

        center.add(descriptionLabel);
        center.add(attaqueDetailsLabel);

        add(center, BorderLayout.CENTER);

        /* ---------- Boutons ---------- */
        JPanel south = new JPanel(new GridLayout(4, 1, 0, 10));
        south.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        south.setBackground(BACKGROUND);
        for (JButton b : new JButton[] { finTourButton, annulerMouvementButton, sauvegarderButton, finPartieButton }) {
            styliseBouton(b);
            south.add(b);
        }
        add(south, BorderLayout.SOUTH);

        majJoueurActif(new Joueur(nomJoueur1, false, ""));

    }

    /* ═════════════════════════════════════════════════════════════ */
    /* Mise à jour dynamique */
    /* ═════════════════════════════════════════════════════════════ */

    public void majInfos(Unite u) {
        if (u == null) {
            uniteImageLabel.setIcon(null);
            nomLabel.setText("Nom : -");
            joueurLabel.setText("Joueur : -");
            pvLabel.setText("PV : -");
            attaqueLabel.setText("Attaque : -");
            defenseLabel.setText("Défense : -");
            deplacementLabel.setText("Déplacement : -");
            terrainEtDefenseLabel.setText("- : -");
            descriptionLabel.setText("Description : -");
            attaqueDetailsLabel.setText("Armes : -");
            return;
        }

        nomLabel.setText("Nom : " + u.getNom());
        joueurLabel.setText("Joueur : " + u.getJoueur().getNom());
        pvLabel.setText("PV : " + u.getPointsVie());
        attaqueLabel.setText("Attaque : " + u.getAttaque());
        defenseLabel.setText("Défense : " + u.getDefense());
        deplacementLabel.setText("Déplacement : " + u.getDeplacementRestant());

        Image img = u.getIcone().getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        uniteImageLabel.setIcon(new ImageIcon(img));

        Hexagone hex = u.getPosition();
        if (hex != null) {
            TypeTerrain tt = hex.getTypeTerrain();
            terrainEtDefenseLabel.setText(tt.name() + " : " + tt.getBonusDefense() + "%");
        } else {
            terrainEtDefenseLabel.setText("? : ?");
        }

        descriptionLabel.setText(
                "Description : Un combattant redoutable.\nSes compétences sont redoutées sur tous les champs de bataille.");
        attaqueDetailsLabel.setText("Armes : " + u.getArmes().stream()
                .map(arme -> arme.getNom())
                .reduce((a, b) -> a + ", " + b).orElse("-"));
    }

    public void majDeplacement(int val) {
        deplacementLabel.setText("Déplacement : " + val);
    }

    public void majJoueurActif(Joueur j) {
        joueurActifLabel.setText("Joueur actif : " + j.getNom());
    }

    /* ═════════════════════════════════════════════════════════════ */
    /* Style des boutons */
    /* ═════════════════════════════════════════════════════════════ */
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

    /* ═════════════════════════════════════════════════════════════ */
    /* Accesseurs boutons */
    /* ═════════════════════════════════════════════════════════════ */
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

    /* ═════════════════════════════════════════════════════════════ */
    /* Dialogues statiques ré‑utilisables */
    /* ═════════════════════════════════════════════════════════════ */
    public static boolean showStyledConfirmDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Confirmation", true);
        d.setUndecorated(true);
        d.setSize(550, 120);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel("Êtes‑vous sûr de vouloir terminer la partie ?", SwingConstants.CENTER);
        lbl.setForeground(TEXT);
        lbl.setFont(gothic.deriveFont(Font.BOLD, 16f));
        lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        content.add(lbl);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        btns.setBackground(BACKGROUND);
        JButton yes = createStyledButton("Oui");
        JButton no = createStyledButton("Annuler");
        btns.add(yes);
        btns.add(no);
        content.add(btns);

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
        input.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Nom de la sauvegarde :");
        lbl.setForeground(TEXT);
        lbl.setFont(gothic.deriveFont(Font.BOLD, 16f));

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
        d.setPreferredSize(new Dimension(400, 160));
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

    private static JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setForeground(TEXT);
        b.setBackground(BTN_BG);
        b.setFocusPainted(false);
        b.setFont(gothic.deriveFont(Font.PLAIN, 13f));
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
}