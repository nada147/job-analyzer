package interfaceui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class JobListPanel extends JPanel {
    private DefaultListModel<String> listModel;
    private JList<String> jobList;
    private JobDetailsPanel jobDetailsPanel;
    private Map<String, Integer> jobIdMap;

    public JobListPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(350, 0));
        setBorder(BorderFactory.createTitledBorder("Liste des offres"));

        listModel = new DefaultListModel<>();
        jobList = new JList<>(listModel);
        jobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobIdMap = new HashMap<>();

        jobList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedTitle = jobList.getSelectedValue();
                    if (selectedTitle != null && jobDetailsPanel != null) {
                        Integer jobId = jobIdMap.get(selectedTitle);
                        if (jobId != null) {
                            jobDetailsPanel.loadJob(jobId);
                        }
                    }
                }
            }
        });

        add(new JScrollPane(jobList), BorderLayout.CENTER);
        loadJobs(null, null, null);
    }

    public void setJobDetailsPanel(JobDetailsPanel panel) {
        this.jobDetailsPanel = panel;
    }

    public void loadJobs(String category, String sourcesite, String contractType) {
        listModel.clear();
        jobIdMap.clear();

        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT j.id, j.title, j.company, jc.category, j.source_site " +
            "FROM job_announcements j " +
            "LEFT JOIN job_classifications jc ON jc.job_id = j.id " +
            "WHERE 1=1 "
        );

        java.util.List<String> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append("AND jc.category = ? ");
            params.add(category);
        }

        if (sourcesite != null && !sourcesite.isEmpty()) {
            sql.append("AND j.source_site = ? ");
            params.add(sourcesite);
        }

        if (contractType != null && !contractType.isEmpty()) {
            sql.append("AND j.contract_type = ? ");
            params.add(contractType);
        }

        sql.append("ORDER BY j.publish_date DESC LIMIT 500");

        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            int count = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String displayText = title != null ? title : "Sans titre";
                
                listModel.addElement(displayText);
                jobIdMap.put(displayText, id);
                count++;
            }

            if (count == 0) {
                listModel.addElement("Aucune offre trouvée avec ces filtres.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des offres : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}