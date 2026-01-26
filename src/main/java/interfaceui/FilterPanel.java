package interfaceui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FilterPanel extends JPanel {
    private JComboBox<String> categoryCombo;
    private JComboBox<String> sourceCombo;
    private JComboBox<String> contractCombo;
    private JButton filterButton;
    private JButton resetButton;
    private JobListPanel jobListPanel;

    public FilterPanel(JobListPanel jobListPanel) {
        this.jobListPanel = jobListPanel;
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setBorder(BorderFactory.createTitledBorder("Filtres"));
        setPreferredSize(new Dimension(0, 80));

        // Label et ComboBox pour les catégories
        add(new JLabel("Catégorie :"));
        categoryCombo = new JComboBox<>();
        categoryCombo.setPreferredSize(new Dimension(200, 30));
        loadCategories();
        add(categoryCombo);

        // Label et ComboBox pour les sites sources
        add(new JLabel("Site source :"));
        sourceCombo = new JComboBox<>();
        sourceCombo.setPreferredSize(new Dimension(200, 30));
        loadSourceSites();
        add(sourceCombo);

        // Label et ComboBox pour les types de contrat
        add(new JLabel("Type de contrat :"));
        contractCombo = new JComboBox<>();
        contractCombo.setPreferredSize(new Dimension(200, 30));
        loadContractTypes();
        add(contractCombo);

        // Bouton Filtrer
        filterButton = new JButton("🔍 Filtrer");
        filterButton.setBackground(new Color(52, 152, 219));
        filterButton.setForeground(Color.WHITE);
        filterButton.setFocusPainted(false);
        filterButton.addActionListener(e -> applyFilters());
        add(filterButton);

        // Bouton Réinitialiser
        resetButton = new JButton("🔄 Réinitialiser");
        resetButton.addActionListener(e -> resetFilters());
        add(resetButton);
    }

    private void loadCategories() {
        categoryCombo.addItem("-- Toutes les catégories --");
        String sql = "SELECT DISTINCT category FROM job_classifications " +
                     "WHERE category IS NOT NULL ORDER BY category";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                String category = rs.getString("category");
                if (category != null && !category.trim().isEmpty()) {
                    categoryCombo.addItem(category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des catégories : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSourceSites() {
        sourceCombo.addItem("-- Tous les sites --");
        String sql = "SELECT DISTINCT source_site FROM job_announcements " +
                     "WHERE source_site IS NOT NULL ORDER BY source_site";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                String site = rs.getString("source_site");
                if (site != null && !site.trim().isEmpty()) {
                    sourceCombo.addItem(site);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des sites : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadContractTypes() {
        contractCombo.addItem("-- Tous les types --");
        String sql = "SELECT DISTINCT contract_type FROM job_announcements " +
                     "WHERE contract_type IS NOT NULL ORDER BY contract_type";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                String contract = rs.getString("contract_type");
                if (contract != null && !contract.trim().isEmpty()) {
                    contractCombo.addItem(contract);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des types de contrat : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        String selectedSource = (String) sourceCombo.getSelectedItem();
        String selectedContract = (String) contractCombo.getSelectedItem();
        
        // Remplacer les valeurs par défaut par null
        if (selectedCategory != null && selectedCategory.startsWith("--")) {
            selectedCategory = null;
        }
        if (selectedSource != null && selectedSource.startsWith("--")) {
            selectedSource = null;
        }
        if (selectedContract != null && selectedContract.startsWith("--")) {
            selectedContract = null;
        }
        
        jobListPanel.loadJobs(selectedCategory, selectedSource, selectedContract);
    }

    private void resetFilters() {
        categoryCombo.setSelectedIndex(0);
        sourceCombo.setSelectedIndex(0);
        contractCombo.setSelectedIndex(0);
        jobListPanel.loadJobs(null, null, null);
    }
}