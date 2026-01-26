package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillDAO {

    public int findOrCreate(String skillName) {
        String insert = "INSERT IGNORE INTO skills (name) VALUES (?)";
        String select = "SELECT id FROM skills WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, skillName);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setString(1, skillName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
