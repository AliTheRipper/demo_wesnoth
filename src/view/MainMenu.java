package view;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Fenêtre principale du jeu affichant le menu de démarrage. Permet de lancer
 * une nouvelle partie, charger une sauvegarde, ouvrir l’éditeur de carte,
 * consulter les crédits ou quitter le jeu. Gère également l'affichage d'une
 * animation musicale de fond et la navigation entre les descriptions des unités
 * disponibles.
 */
public class MainMenu extends JFrame {

    private static GameWindow currentGame = null;
    private int unitIndex = 0;

    private final String[] unitDescriptions = {
        "<html><div style='text-align: center;'><b>Archer :</b> Unite a distance, attaque avec un arc longue portee et un poignard en melee. Bonne mobilite.</html>",
        "<html><div style='text-align: center;'><b>Soldat :</b> Unite equilibree, bien armee pour le combat rapproche avec une epee solide.</html>",
        "<html><div style='text-align: center;'><b>Cavalier :</b> Unite tres mobile, inflige de lourds degats au corps-a-corps avec sa lance.</html>",
        "<html><div style='text-align: center;'><b>Mage :</b> Attaquant a distance, faible en defense mais puissant avec ses eclairs magiques.</html>",
        "<html><div style='text-align: center;'><b>Fantassin :</b> Unite robuste et puissante au corps-a-corps, armee d'une hache.</html>",
        "<html><div style='text-align: center;'><b>Voleur :</b> Agile et rapide, ideal pour les frappes legeres et les manœuvres d escarmouche avec sa dague.</html>"
    };

    private JLabel unitLabel;
    public static Font gothicFont;
    Color hoverColor = new Color(60, 90, 150);
    Color buttonBg = new Color(30, 40, 60);
    Color borderGold = new Color(212, 175, 55);

    /**
     * Initialise et affiche le menu principal du jeu avec tous les boutons et
     * les effets graphiques/stylistiques correspondants.
     */
    public MainMenu() {
        setTitle("Wargame - Menu Principal");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);
        loadCustomFont();

        BackgroundPanel background = new BackgroundPanel("resources/background.png");
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JPanel boutons = new JPanel();
        boutons.setLayout(new BoxLayout(boutons, BoxLayout.Y_AXIS));
        boutons.setOpaque(false);
        boutons.setBorder(BorderFactory.createEmptyBorder(300, 100, 300, 100));

        String[] btnLabels = {
            "Commencer une Partie",
            "Parties Sauvegardees",
            "Editeur de Map",
            "A propos du jeu",
            "Quitter"
        };

        JButton[] buttons = new JButton[btnLabels.length];

        for (int i = 0; i < btnLabels.length; i++) {
            JButton btn = new JButton(btnLabels[i]);
            btn.setFont(gothicFont);
            btn.setBackground(buttonBg);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderGold, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btn.setPreferredSize(new Dimension(200, 100));
            btn.setMaximumSize(new Dimension(200, 100));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(hoverColor);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonBg);
                }
            });

            boutons.add(btn);
            boutons.add(Box.createVerticalStrut(15));
            buttons[i] = btn;
        }

        background.add(boutons, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 20));
        bottomPanel.setOpaque(false);

        UnitInfoPanel UnitinfoPanel = new UnitInfoPanel();

        unitLabel = new JLabel(unitDescriptions[unitIndex]);
        unitLabel.setFont(gothicFont.deriveFont(Font.PLAIN, 14));
        unitLabel.setForeground(Color.WHITE);
        unitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unitLabel.setPreferredSize(new Dimension(500, 40));
        unitLabel.setOpaque(false);

        JButton prev = new JButton("Precedent");
        JButton next = new JButton("Suivant");

        for (JButton b : new JButton[]{prev, next}) {
            b.setFont(gothicFont.deriveFont(Font.PLAIN, 13));
            b.setForeground(Color.WHITE);
            b.setBackground(buttonBg);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderGold, 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(new Color(60, 90, 150));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(buttonBg);
                }
            });
        }

        prev.addActionListener(e -> updateUnitInfo(-1));
        next.addActionListener(e -> updateUnitInfo(1));

        JPanel controls = new JPanel(new FlowLayout());
        controls.setOpaque(false);
        controls.add(prev);
        controls.add(next);

        UnitinfoPanel.add(unitLabel, BorderLayout.CENTER);
        UnitinfoPanel.add(controls, BorderLayout.SOUTH);
        bottomPanel.add(UnitinfoPanel);
        background.add(bottomPanel, BorderLayout.SOUTH);

        buttons[0].addActionListener(e -> {
            StartDialog dialog = new StartDialog(this);
            dialog.setVisible(true);

            String nom1 = dialog.getJoueur1();
            String nom2 = dialog.getJoueur2();
            boolean joueur2IA = dialog.isJoueur2IA();

            if (nom1 != null && nom2 != null) {
                PlateauManager manager = PlateauManager.initialiserNouvellePartie(nom1, nom2, joueur2IA);

                manager.nomJoueur1 = nom1;
                manager.nomJoueur2 = nom2;
                manager.joueur1.setNom(nom1);
                manager.joueur2.setNom(nom2);

                getContentPane().removeAll();
                currentGame = new GameWindow(this, manager);
                getContentPane().add(currentGame, BorderLayout.CENTER);

                revalidate();
                repaint();
                requestFocusInWindow();
            }
        });

        buttons[1].addActionListener(e -> showStyledSaveDialog());

        buttons[2].addActionListener(e -> {
            MapEditor editor = new MapEditor();
            editor.setLocationRelativeTo(this);
            editor.setVisible(true);
        });

        buttons[3].addActionListener(e -> {
            JDialog dialog = new JDialog(this, "À propos du jeu", true);
            dialog.setUndecorated(true);
            dialog.setSize(400, 180);
            dialog.setLocationRelativeTo(this);

            Font font = gothicFont.deriveFont(Font.PLAIN, 15);
            Color bg = new Color(20, 20, 30);
            Color fg = Color.WHITE;
            Color borderColor = new Color(212, 175, 55);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(bg);
            panel.setBorder(BorderFactory.createLineBorder(borderColor, 2));

            JLabel message = new JLabel("<html><center>EMPIRE IN WAR<br>Cree par Khettou Hajar, Hijazi Yahya, Halmi Ilias, Zifouti Ali et Shel Hazem.</center></html>", SwingConstants.CENTER);
            message.setFont(font);
            message.setForeground(fg);
            message.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

            JButton okButton = new JButton("Fermer");
            okButton.setFocusPainted(false);
            okButton.setFont(font);
            okButton.setForeground(Color.WHITE);
            okButton.setBackground(new Color(30, 40, 60));
            okButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
            okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            okButton.addActionListener(ev -> dialog.dispose());

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(bg);
            btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            btnPanel.add(okButton);

            panel.add(message, BorderLayout.CENTER);
            panel.add(btnPanel, BorderLayout.SOUTH);

            dialog.setContentPane(panel);
            dialog.setVisible(true);
        });

        buttons[4].addActionListener(e -> System.exit(0));

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    showQuitOverlay();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
        playMusic("resources/music.wav");
    }

    /**
     * Affiche un panneau de superposition pour confirmer la fermeture du jeu.
     */
    private void showQuitOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(0, 0, 0, 180));
        overlay.setOpaque(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(420, 180));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));

        JLabel label = new JLabel("<html><center>Etes-vous sur de vouloir quitter le jeu ?</center></html>", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(gothicFont.deriveFont(Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.add(label, BorderLayout.CENTER);

        JButton yes = new JButton("Oui");
        JButton no = new JButton("Non");

        for (JButton b : new JButton[]{yes, no}) {
            b.setBackground(new Color(30, 40, 60));
            b.setForeground(Color.WHITE);
            b.setFont(gothicFont.deriveFont(Font.PLAIN, 14));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(212, 175, 55), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(20, 20, 30));
        btnPanel.add(yes);
        btnPanel.add(no);
        panel.add(btnPanel, BorderLayout.SOUTH);

        overlay.add(panel);

        JRootPane root = getRootPane();
        root.setGlassPane(overlay);
        overlay.setVisible(true);

        yes.addActionListener(e -> System.exit(0));
        no.addActionListener(e -> overlay.setVisible(false));
    }

    /**
     * Met à jour la description de l’unité actuellement affichée dans la
     * bannière inférieure.
     *
     * @param direction +1 pour suivant, -1 pour précédent
     */
    private void updateUnitInfo(int direction) {
        unitIndex = (unitIndex + direction + unitDescriptions.length) % unitDescriptions.length;
        unitLabel.setText(unitDescriptions[unitIndex]);
    }

    /**
     * Charge et applique une police personnalisée utilisée pour l'ensemble de
     * l’interface.
     */
    private void loadCustomFont() {
        try {
            gothicFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/UnifrakturCook-Bold.ttf")).deriveFont(13f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(gothicFont);
        } catch (Exception e) {
            gothicFont = new Font("Serif", Font.PLAIN, 14);
        }
    }

    /**
     * Joue la musique de fond en boucle.
     *
     * @param filePath Chemin vers le fichier audio à lire
     */
    private void playMusic(String filePath) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-15.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Erreur musique : " + e.getMessage());
        }
    }

    /**
     * Réinitialise l’instance de la partie en cours (GameWindow).
     */
    public static void clearCurrentGame() {
        currentGame = null;
    }

    /**
     * Affiche à nouveau le menu principal en supprimant le contenu actuel.
     */
    public void showMainMenu() {
        getContentPane().removeAll();
        new MainMenu();
    }

    /**
     * Affiche une boîte de dialogue personnalisée permettant de choisir une
     * sauvegarde existante parmi les fichiers présents.
     */
    private void showStyledSaveDialog() {
        File folder = new File("sauvegardes");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] saves = folder.listFiles((dir, name) -> name.endsWith(".save"));
        if (saves == null || saves.length == 0) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvee.");
            return;
        }

        List<String> noms = new ArrayList<>();
        for (File f : saves) {
            noms.add(f.getName().replace(".save", ""));
        }

        JDialog dialog = new JDialog(this, "Choisir une sauvegarde", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(20, 20, 30));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        searchField.setFont(gothicFont);
        searchField.setPreferredSize(new Dimension(200, 30));

        DefaultListModel<String> model = new DefaultListModel<>();
        noms.forEach(model::addElement);
        JList<String> list = new JList<>(model);
        list.setFont(gothicFont.deriveFont(14f));
        list.setBackground(Color.WHITE);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            void filter() {
                String text = searchField.getText().toLowerCase();
                model.clear();
                for (String name : noms) {
                    if (name.toLowerCase().contains(text)) {
                        model.addElement(name);
                    }
                }
            }
        });

        content.add(searchField, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(20, 20, 30));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");

        Color buttonBg = new Color(30, 40, 60);
        Color borderGold = new Color(212, 175, 55);

        for (JButton b : new JButton[]{okButton, cancelButton}) {
            b.setFont(gothicFont.deriveFont(Font.PLAIN, 14));
            b.setForeground(Color.WHITE);
            b.setBackground(buttonBg);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderGold, 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(new Color(60, 90, 150));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(buttonBg);
                }
            });
        }

        okButton.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                dialog.dispose();
                getContentPane().removeAll();
                currentGame = new GameWindow(this, selected);
                getContentPane().add(currentGame, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        content.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}

/**
 * Panneau personnalisé utilisé pour afficher une image de fond redimensionnée
 * dynamiquement.
 */
class BackgroundPanel extends JPanel {

    private final Image background;

    /**
     * Crée un panneau avec une image de fond à partir du chemin fourni.
     *
     * @param imagePath Chemin vers l'image à afficher
     */
    public BackgroundPanel(String imagePath) {
        background = new ImageIcon(imagePath).getImage();
    }

    /**
     * Dessine l’image de fond pour qu’elle s’ajuste à la taille de la fenêtre.
     *
     * @param g Contexte graphique
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
