package analytics;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class TrendAnalysisService {

    //  Classe interne : résultat générique de tendance
    public static class TrendResult {
        private final String label;
        private final int value;

        public TrendResult(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    // =============================
    // TOP SKILLS
    // =============================
    public List<TrendResult> topSkills(int limit) {
        List<TrendResult> data = new ArrayList<>();

        String sql = " SELECT s.name, COUNT(*) AS cnt FROM job_skills js JOIN skills s ON s.id = js.skill_id GROUP BY s.name ORDER BY cnt DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new TrendResult(
                        rs.getString("name"),
                        rs.getInt("cnt")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // =============================
    // JOBS BY DOMAIN
    // =============================
    public List<TrendResult> jobsByDomain() {
        List<TrendResult> data = new ArrayList<>();

        String sql = " SELECT category, COUNT(*) AS cnt FROM job_classifications GROUP BY category  ORDER BY cnt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new TrendResult(
                        rs.getString("category"),
                        rs.getInt("cnt")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // =============================
    // JOBS BY CITY
    // =============================
    public List<TrendResult> jobsByCity(int limit) {
        List<TrendResult> data = new ArrayList<>();

        String sql = "SELECT location, COUNT(*) AS cnt  FROM job_announcements WHERE location IS NOT NULL  GROUP BY location ORDER BY cnt DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new TrendResult(
                        rs.getString("location"),
                        rs.getInt("cnt")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}