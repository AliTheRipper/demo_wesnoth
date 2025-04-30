package view;

import javax.swing.*;

public class StartDialog extends JDialog {
    private JTextField joueur1Field = new JTextField(15);
    private JTextField joueur2Field = new JTextField(15);

    private String joueur1;
    private String joueur2;
    private boolean validerClique = false;

    public StartDialog(JFrame parent) {
        super(parent, "Saisir les noms des joueurs", true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Nom du joueur 1 :"));
        panel.add(joueur1Field);

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Nom du joueur 2 :"));
        panel.add(joueur2Field);

        panel.add(Box.createVerticalStrut(15));
        JButton valider = new JButton("Démarrer la partie");

        valider.addActionListener(e -> {
            joueur1 = joueur1Field.getText().trim();
            joueur2 = joueur2Field.getText().trim();
        
            if (joueur1.isEmpty() || joueur2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir les deux noms.");
            } else {
                validerClique = true;  // ✅ important
                dispose();
            }
        });
        

        panel.add(valider);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    public String getJoueur1() {
        return validerClique ? joueur1 : null;
    }
    
    public String getJoueur2() {
        return validerClique ? joueur2 : null;
    }
    
}
