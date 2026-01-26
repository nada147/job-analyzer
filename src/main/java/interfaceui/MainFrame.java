package interfaceui;

import interfaceui.analytics.MarketAnalysisFrame;
import interfaceui.matching.MatchingFrame;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        if (!UserSession.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(null,
                    "Vous devez vous connecter pour accéder à cette page.",
                    "Accès refusé", JOptionPane.WARNING_MESSAGE);
            new LoginFrame().setVisible(true);
            return;
        }

        String username = UserSession.getInstance().getUsername();
        setTitle("Jobs Maroc – Analyse & Classification (Connecté: " + username + ")");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ✅ MENU BAR
        setJMenuBar(buildMenuBar());

        JobListPanel jobListPanel = new JobListPanel();
        JobDetailsPanel jobDetailsPanel = new JobDetailsPanel();
        FilterPanel filterPanel = new FilterPanel(jobListPanel);

        jobListPanel.setJobDetailsPanel(jobDetailsPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userLabel = new JLabel("👤 " + username + "  ");
        userPanel.add(userLabel);

        JButton logoutButton = new JButton("🚪 Déconnexion");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        userPanel.add(logoutButton);

        topPanel.add(userPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(jobListPanel, BorderLayout.WEST);
        add(jobDetailsPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // ================= MENU =================
    private JMenuBar buildMenuBar() {

        JMenuBar bar = new JMenuBar();

        JMenu tools = new JMenu("Tools");

        JMenuItem matchingItem = new JMenuItem("⭐ Recommended Jobs");
        matchingItem.addActionListener(e ->
                new MatchingFrame().setVisible(true)
        );

        JMenuItem analyticsItem = new JMenuItem("📊 Market Analysis");
        analyticsItem.addActionListener(e ->
                new MarketAnalysisFrame().setVisible(true)
        );

        tools.add(matchingItem);
        tools.add(analyticsItem);

        bar.add(tools);
        return bar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.getInstance().clearSession();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}