package view;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class StartDialog extends JDialog {

    private JTextField joueur1Field = new JTextField(15);
    private JTextField joueur2Field = new JTextField(15);
    private String joueur1;
    private String joueur2;
    private boolean validerClique = false;
    private Font gothicFont;

    private JCheckBox iaCheckbox;

    public StartDialog(JFrame parent) {
        super(parent, "", true);
        setUndecorated(true);
        loadCustomFont();

        Color bg = new Color(20, 20, 30);
        Color fg = Color.WHITE;
        Color borderColor = new Color(212, 175, 55);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bg);
        mainPanel.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        JLabel title = new JLabel("<html><span style='letter-spacing:1.5px;'>Saisir les noms des joueurs</span></html>", SwingConstants.CENTER);
        title.setFont(gothicFont.deriveFont(Font.PLAIN, 18));
        title.setForeground(fg);
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        form.setBackground(bg);
        form.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        joueur1Field.setBackground(new Color(40, 40, 60));
        joueur1Field.setForeground(fg);
        joueur1Field.setCaretColor(fg);
        joueur1Field.setBorder(BorderFactory.createLineBorder(new Color(70, 120, 180)));

        joueur2Field.setBackground(new Color(40, 40, 60));
        joueur2Field.setForeground(fg);
        joueur2Field.setCaretColor(fg);
        joueur2Field.setBorder(BorderFactory.createLineBorder(new Color(70, 120, 180)));

        JLabel label1 = new JLabel("Joueur 1 :");
        JLabel label2 = new JLabel("Joueur 2 :");

        label1.setForeground(Color.LIGHT_GRAY);
        label2.setForeground(Color.LIGHT_GRAY);
        label1.setFont(gothicFont.deriveFont(Font.PLAIN, 12));
        label2.setFont(gothicFont.deriveFont(Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(label1, gbc);

        gbc.gridx = 1;
        form.add(joueur1Field, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(label2, gbc);

        gbc.gridx = 1;
        form.add(joueur2Field, gbc);

        iaCheckbox = new JCheckBox("Joueur IA");
        iaCheckbox.setBackground(bg);
        iaCheckbox.setForeground(Color.LIGHT_GRAY);
        iaCheckbox.setFont(gothicFont.deriveFont(Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        form.add(iaCheckbox, gbc);

        iaCheckbox.addActionListener(e -> {
            boolean visible = !iaCheckbox.isSelected();
            joueur2Field.setVisible(visible);
            label2.setVisible(visible);
            form.revalidate();
            form.repaint();
        });

        JButton valider = createStyledButton("Demarrer la partie");
        JButton annuler = createStyledButton("Annuler");

        valider.addActionListener(e -> {
            joueur1 = joueur1Field.getText().trim();
            joueur2 = joueur2Field.getText().trim();
            boolean ia = iaCheckbox.isSelected();
            if (joueur1.isEmpty() || (!ia && joueur2.isEmpty())) {
                showStyledWarning(parent);
            } else {
                validerClique = true;
                dispose();
            }
        });

        annuler.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(bg);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        btnPanel.add(valider);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(annuler);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setSize(420, 300);
        setLocationRelativeTo(parent);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(gothicFont.deriveFont(Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 40, 60));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 90, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 40, 60));
            }
        });

        return button;
    }

    private void showStyledWarning(Component parent) {
        JDialog warning = new JDialog((JFrame) parent, true);
        warning.setUndecorated(true);
        warning.setSize(350, 180);
        warning.setLocationRelativeTo(parent);

        Color bg = new Color(20, 20, 30);
        Color fg = Color.WHITE;
        Color border = new Color(212, 175, 55);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createLineBorder(border, 2));

        JLabel label = new JLabel("<html><center>Veuillez saisir les deux noms<br>pour commencer la partie.</center></html>", SwingConstants.CENTER);
        label.setFont(gothicFont.deriveFont(Font.PLAIN, 15));
        label.setForeground(fg);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JButton ok = createStyledButton("Fermer");
        ok.addActionListener(e -> warning.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(bg);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        btnPanel.add(ok);

        panel.add(label, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        warning.setContentPane(panel);
        warning.setVisible(true);
    }

    public String getJoueur1() {
        return validerClique ? joueur1 : null;
    }

    public String getJoueur2() {
        return validerClique ? joueur2 : null;
    }

    private void loadCustomFont() {
        try {
            gothicFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/UnifrakturCook-Bold.ttf")).deriveFont(16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(gothicFont);
        } catch (Exception e) {
            gothicFont = new Font("Serif", Font.PLAIN, 14);
        }
    }

    public boolean isJoueur2IA() {
        return iaCheckbox.isSelected();
    }

}
