package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class JobSkillDAO {

    public void link(int jobId, int skillId) {
        String sql = "INSERT IGNORE INTO job_skills (job_id, skill_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, skillId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
