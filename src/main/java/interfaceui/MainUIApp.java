package interfaceui;

import javax.swing.SwingUtilities;

public class MainUIApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Commencer par la page de connexion
            new LoginFrame().setVisible(true);
        });
    }
}