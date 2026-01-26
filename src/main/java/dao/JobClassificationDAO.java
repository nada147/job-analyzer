package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class JobClassificationDAO {

    public void insert(int jobId, String category, double confidence) {
        String sql = "  INSERT INTO job_classifications (job_id, category, confidence) VALUES (?, ?, ?)";
        ;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setString(2, category);
            ps.setDouble(3, confidence);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
