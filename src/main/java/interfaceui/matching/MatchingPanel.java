package interfaceui.matching;

import ml.matching.MatchResult;
import ml.matching.MatchingService;
import ml.recommendations.JobRecommendationService;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class MatchingPanel extends JPanel {

    private final JComboBox<String> domainCombo = new JComboBox<>();
    private final JTextField cvSkillsField = new JTextField();

    private final JButton recommendBtn = new JButton("⭐ Recommended jobs");
    private final JButton similarBtn   = new JButton("🔁 Similar jobs");

    private final DefaultListModel<MatchResult> listModel = new DefaultListModel<>();
    private final JList<MatchResult> resultList = new JList<>(listModel);

    private final JTextArea detailsArea = new JTextArea();

    public MatchingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ================= TOP =================
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        top.add(new JLabel("ML Domain:"), c);

        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        domainCombo.setPreferredSize(new Dimension(250, 30));
        top.add(domainCombo, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        top.add(new JLabel("CV skills (comma separated):"), c);

        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        cvSkillsField.setPreferredSize(new Dimension(250, 30));
        cvSkillsField.setText("java, spring, sql");
        top.add(cvSkillsField, c);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        recommendBtn.setPreferredSize(new Dimension(220, 40));
        similarBtn.setPreferredSize(new Dimension(180, 40));
        btnPanel.add(recommendBtn);
        btnPanel.add(similarBtn);

        c.gridx = 2; c.gridy = 0; c.gridheight = 2; c.weightx = 0;
        top.add(btnPanel, c);

        add(top, BorderLayout.NORTH);

        // ================= CENTER =================
        resultList.setCellRenderer(new MatchResultRenderer());
        JScrollPane left = new JScrollPane(resultList);

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane right = new JScrollPane(detailsArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(450);
        add(split, BorderLayout.CENTER);

        // ================= INIT =================
        loadDomainsFromDB();

        // Actions
        recommendBtn.addActionListener(e -> runRecommendation());
        similarBtn.addActionListener(e -> runSimilarJobs());

        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MatchResult r = resultList.getSelectedValue();
                if (r != null) {
                    loadJobDetails(r.getJobId(), r.getScore());
                }
            }
        });
    }

    // ================= LOAD DOMAINS =================
    private void loadDomainsFromDB() {
        domainCombo.removeAllItems();
        domainCombo.addItem("-- Select domain --");

        String sql = "SELECT DISTINCT category FROM job_classifications WHERE category IS NOT NULL AND category <> '' ORDER BY category ";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                domainCombo.addItem(rs.getString("category"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading domains: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= MATCHING CV ↔ JOBS =================
    private void runRecommendation() {
        listModel.clear();
        detailsArea.setText("");

        String domain = (String) domainCombo.getSelectedItem();
        if (domain != null && domain.startsWith("--")) {
            domain = null;
        }

        String raw = cvSkillsField.getText().trim();
        if (raw.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter CV skills (e.g. java, spring, sql).");
            return;
        }

        List<String> cvSkills = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .toList();

        try {
            MatchingService service = new MatchingService();
            List<MatchResult> results = service.matchCVWithJobs(cvSkills, domain);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No matching jobs found.",
                        "Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            results.stream().limit(50).forEach(listModel::addElement);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error running matching: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SIMILAR JOBS =================
    private void runSimilarJobs() {
        MatchResult selected = resultList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a job first.");
            return;
        }

        listModel.clear();
        detailsArea.setText("");

        try {
            JobRecommendationService service = new JobRecommendationService();
            List<MatchResult> recs =
                    service.recommendSimilarJobs(selected.getJobId());

            if (recs.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No similar jobs found.");
                return;
            }

            recs.stream().limit(30).forEach(listModel::addElement);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading similar jobs: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= DETAILS =================
    private void loadJobDetails(int jobId, double score) {

        String sql = " SELECT j.title, j.company, j.location, j.contract_type, j.description, jc.category FROM job_announcements j LEFT JOIN job_classifications jc ON jc.job_id = j.id WHERE j.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String desc = rs.getString("description");
                if (desc != null) {
                    desc = desc.replaceAll("<[^>]+>", " ")
                               .replaceAll("\\s+", " ")
                               .trim();
                }

                detailsArea.setText(
                        "⭐ Matching score: " + String.format("%.0f", score * 100) + "%\n" +
                        "----------------------------------------\n" +
                        "Title: " + safe(rs.getString("title")) + "\n" +
                        "Company: " + safe(rs.getString("company")) + "\n" +
                        "Location: " + safe(rs.getString("location")) + "\n" +
                        "Contract: " + safe(rs.getString("contract_type")) + "\n" +
                        "ML Domain: " + safe(rs.getString("category")) + "\n\n" +
                        "Description:\n" + safe(desc)
                );
                detailsArea.setCaretPosition(0);
            }

        } catch (Exception ex) {
            detailsArea.setText("Error loading job details: " + ex.getMessage());
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }
}