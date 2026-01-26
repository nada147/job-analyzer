package interfaceui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        setTitle("Jobs Maroc - Connexion");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel principal avec padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Titre
        JLabel titleLabel = new JLabel("Connexion à Jobs Maroc");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Nom d'utilisateur
        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = new JButton("Se connecter");
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(140, 35));
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);

        registerButton = new JButton("S'inscrire");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(140, 35));
        registerButton.addActionListener(e -> openRegisterFrame());
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Permettre la connexion avec Enter
        passwordField.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id, username, email, full_name FROM users WHERE username = ? AND password = ?";

        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Connexion réussie
                int userId = rs.getInt("id");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");

                // Enregistrer la session
                UserSession.getInstance().setUserData(userId, username, email, fullName);

                // Mettre à jour la date de dernière connexion
                updateLastLogin(userId);

                // Ouvrir la fenêtre principale
                SwingUtilities.invokeLater(() -> {
                    new MainFrame();
                    dispose();
                });

            } else {
                JOptionPane.showMessageDialog(this,
                    "Nom d'utilisateur ou mot de passe incorrect.",
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la connexion : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRegisterFrame() {
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame(this);
        });
    }
}