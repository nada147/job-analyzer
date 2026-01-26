package interfaceui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        
        setTitle("Jobs Maroc - Inscription");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginFrame);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Nom complet
        addField(mainPanel, "Nom complet :", fullNameField = new JTextField());
        
        // Nom d'utilisateur
        addField(mainPanel, "Nom d'utilisateur :", usernameField = new JTextField());
        
        // Email
        addField(mainPanel, "Email :", emailField = new JTextField());
        
        // Mot de passe
        addField(mainPanel, "Mot de passe :", passwordField = new JPasswordField());
        
        // Confirmation mot de passe
        addField(mainPanel, "Confirmer mot de passe :", confirmPasswordField = new JPasswordField());

        mainPanel.add(Box.createVerticalStrut(20));

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton = new JButton("S'inscrire");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);

        cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private void register() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Les mots de passe ne correspondent pas.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Le mot de passe doit contenir au moins 6 caractères.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                "Veuillez entrer un email valide.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insertion dans la base de données
        String sql = "INSERT INTO users (username, password, email, full_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, fullName);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("username")) {
                JOptionPane.showMessageDialog(this,
                    "Ce nom d'utilisateur est déjà utilisé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } else if (e.getMessage().contains("email")) {
                JOptionPane.showMessageDialog(this,
                    "Cet email est déjà utilisé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'inscription : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}