package view;





import java.awt.*;
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
// ────────────── New field ──────────────
private final JLabel coordLabel = new JLabel("Coord : -", SwingConstants.CENTER);

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
   private final JTextArea attaqueDetailsLabel = new JTextArea("Armes : -");


    /* ───────────────── BOUTONS DE CONTRÔLE ───────────────── */
    private final JButton finTourButton = new JButton("Fin du tour");
    private final JButton annulerMouvementButton = new JButton("Annuler mouvement");
    private final JButton sauvegarderButton = new JButton("Sauvegarder");
    private final JButton finPartieButton = new JButton("Fin de la partie");

    private final String nomJoueur1;
    private final String nomJoueur2;
    private final PlateauDeJeu plateau;

    /* ───────────────── THÈME COULEUR ───────────────── */
    public static final Color BACKGROUND = new Color(20, 20, 30);
    private static final Color GOLD = new Color(212, 175, 55);
    public static final Color TEXT = Color.WHITE;
    private static final Color BTN_BG = new Color(30, 40, 60);
    private static final Color BTN_HOVER = new Color(60, 90, 150);

    private final JButton zoomInButton = new JButton("+");
    private final JButton zoomOutButton = new JButton("–");
    private MiniMapPanel miniMapPanel;



    public static Font gothic;
    static {
        try {
            // Test d’une police système compatible accents
            gothic = new Font("Segoe UI", Font.PLAIN, 16);
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
        setPreferredSize(new Dimension(300, 0)); // largeur propre
        setMinimumSize(new Dimension(250, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(BACKGROUND);

      /* ---------- TOP PANEL with MiniMap and Joueur Actif ---------- */
JPanel topPanel = new JPanel();
topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
topPanel.setBackground(BACKGROUND);

// MiniMap first
miniMapPanel = new MiniMapPanel(plateau, null);
miniMapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
topPanel.add(Box.createVerticalStrut(0));
topPanel.add(miniMapPanel);
topPanel.add(Box.createVerticalStrut(10));

// Joueur actif below minimap
joueurActifLabel.setFont(gothic.deriveFont(Font.BOLD, 18f));
joueurActifLabel.setForeground(TEXT);
joueurActifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
joueurActifLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
topPanel.add(joueurActifLabel);
topPanel.add(Box.createVerticalStrut(10));

add(topPanel, BorderLayout.NORTH);

        /* ---------- Zone centrale (scrollable) ---------- */
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

        // Image & stats
        JPanel stats = new JPanel();
stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
stats.setBackground(BACKGROUND);
stats.setOpaque(false);
stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setBackground(BACKGROUND);
        stats.setOpaque(false);


       uniteImageLabel.setPreferredSize(new Dimension(64, 64));
uniteImageLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // spacing
stats.add(uniteImageLabel);

        stats.add(uniteImageLabel, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(BACKGROUND);
        right.setOpaque(false);

        for (JLabel l : new JLabel[] { joueurLabel, pvLabel, attaqueLabel, defenseLabel, deplacementLabel }) {
            l.setFont(gothic.deriveFont(Font.PLAIN, 13f));
            l.setForeground(TEXT);
            l.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
            right.add(l);
        }

        //stats.add(Box.createRigidArea(new Dimension(10, 0)));
        stats.add(right, BorderLayout.CENTER);
        center.add(stats);

        // Description & armes

        descriptionLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        descriptionLabel.setForeground(TEXT);
        descriptionLabel.setEditable(false);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 10)); // no bottom gap

        attaqueDetailsLabel.setFont(gothic.deriveFont(Font.PLAIN, 12f));
        attaqueDetailsLabel.setForeground(TEXT);
        attaqueDetailsLabel.setEditable(false);
        attaqueDetailsLabel.setOpaque(false);
        attaqueDetailsLabel.setLineWrap(true);
        attaqueDetailsLabel.setWrapStyleWord(true);
        attaqueDetailsLabel.setRows(1); // avoid auto-expansion
        attaqueDetailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // no top/bottom gap

        center.add(descriptionLabel);
        center.add(attaqueDetailsLabel);

        // Ajoute le panneau central dans un JScrollPane
        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(12); // défilement fluide

        add(scroll, BorderLayout.CENTER);

        /* ---------- Boutons ---------- */
        JPanel south = new JPanel(new GridLayout(0, 1, 0, 10));
        south.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        south.setBackground(BACKGROUND);
        for (JButton b : new JButton[] {
    finTourButton,
    annulerMouvementButton,
    sauvegarderButton,
    finPartieButton }) {
    styliseBouton(b);
    south.add(b);
}

// Add zoom buttons side by side
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
    uniteImageLabel.setIcon(null); // fallback
}


        Hexagone hex = u.getPosition();
        if (hex != null) {
    TypeTerrain tt = hex.getTypeTerrain();
    terrainEtDefenseLabel.setText("Bonus defense : " + tt.getBonusDefense() + "%");
} else {
    terrainEtDefenseLabel.setText("Bonus defense : ?");
}


        descriptionLabel.setText(
                "Description : Un combattant redoutable.\nSes competences sont redoutées sur tous les champs de bataille.");
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

        JLabel lbl = new JLabel("Etes‑vous sur de vouloir terminer la partie ?", SwingConstants.CENTER);
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

    public static JButton createStyledButton(String text) {
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
    public static void showStyledTurnDialog(JFrame parent, String joueurNom) {
    JDialog dialog = new JDialog(parent, "Tour", false); // non-modal
    dialog.setUndecorated(true);
    dialog.setSize(300, 100);
    dialog.setLocationRelativeTo(parent);

    JPanel content = new JPanel(new BorderLayout());
    content.setBackground(BACKGROUND);
    content.setBorder(BorderFactory.createLineBorder(GOLD, 2));

    JLabel label = new JLabel("Tour de " + joueurNom, SwingConstants.CENTER);
    label.setForeground(TEXT);
    label.setFont(gothic.deriveFont(Font.BOLD, 14f));
    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    content.add(label, BorderLayout.CENTER);

    dialog.setContentPane(content);
    dialog.setVisible(true);

    // Auto-close after 5 seconds
    Timer timer = new Timer(3000, e -> dialog.dispose());
    timer.setRepeats(false);
    timer.start();
}
public static void showStyledWarningDialog(JFrame parent, String message, String titre) {
    JDialog dialog = new JDialog(parent, titre, true);
    dialog.setUndecorated(true);
    dialog.setSize(500, 140);
    dialog.setLocationRelativeTo(parent);
    dialog.setLayout(new BorderLayout());

    JPanel content = new JPanel();
    content.setBackground(BACKGROUND);
    content.setBorder(BorderFactory.createLineBorder(GOLD, 2));
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    JLabel lbl = new JLabel(message, SwingConstants.CENTER);
    lbl.setForeground(TEXT);
    lbl.setFont(gothic.deriveFont(Font.BOLD, 16f));
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
    dialog.setSize(450, 140);
    dialog.setLocationRelativeTo(parent);
    dialog.setLayout(new BorderLayout());

    JPanel content = new JPanel();
    content.setBackground(BACKGROUND);
    content.setBorder(BorderFactory.createLineBorder(GOLD, 2));
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    JLabel lbl = new JLabel(message, SwingConstants.CENTER);
    lbl.setForeground(TEXT);
    lbl.setFont(gothic.deriveFont(Font.BOLD, 16f));
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
/** Met à jour l’affichage des coordonnées survolées */
public void majCoordonnees(int x, int y) {
    if (x >= 0 && y >= 0) {
        coordLabel.setText("Coord : " + x + " , " + y);
    } else {
        coordLabel.setText("Coord : -");
    }
    coordLabel.revalidate();
coordLabel.repaint();

}

}