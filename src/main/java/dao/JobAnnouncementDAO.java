package dao;

import database.DatabaseConnection;
import model.JobAnnouncement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobAnnouncementDAO {
    
    /**
     * Insère une offre en évitant les doublons (source_url unique)
     */
    public boolean insert(JobAnnouncement job) {
        // ✅ NOUVEAU (String classique - compatible toutes versions)
        String sql = "INSERT INTO job_announcements " +
                     "(title, company, description, location, contract_type, " +
                     "experience_level, experience_requise, niveau_etude, " +
                     "secteur_activite, fonction, type_teletravail, " +
                     "nombre_postes, salary, source_url, source_site, " +
                     "publish_date, publish_date_string) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "updated_at = CURRENT_TIMESTAMP, " +
                     "title = VALUES(title), " +
                     "description = VALUES(description)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, job.getTitle());
            stmt.setString(2, job.getCompany());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getLocation());
            stmt.setString(5, job.getContractType());
            stmt.setString(6, job.getExperienceLevel());
            stmt.setString(7, job.getExperienceRequise());
            stmt.setString(8, job.getNiveauEtude());
            stmt.setString(9, job.getSecteurActivite());
            stmt.setString(10, job.getFonction());
            stmt.setString(11, job.getTypeTeletravail());
            stmt.setInt(12, job.getNombrePostes());
            stmt.setString(13, job.getSalary());
            stmt.setString(14, job.getSourceUrl());
            stmt.setString(15, job.getSourceSite());
            stmt.setDate(16, job.getPublishDate() != null ? 
                new java.sql.Date(job.getPublishDate().getTime()) : null);
            stmt.setString(17, job.getPublishDateString());
            
            int affected = stmt.executeUpdate();
            
            // Récupérer l'ID généré
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    job.setId(rs.getInt(1));
                }
            }
            
            // Insérer les skills
            if (!job.getSkills().isEmpty()) {
                insertSkills(job);
            }
            
            return affected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Insère toutes les offres d'un coup (batch)
     */
    public int insertBatch(List<JobAnnouncement> jobs) {
        int count = 0;
        for (JobAnnouncement job : jobs) {
            if (insert(job)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Récupère toutes les offres
     */
    public List<JobAnnouncement> findAll() {
        List<JobAnnouncement> jobs = new ArrayList<>();
        String sql = "SELECT * FROM job_announcements ORDER BY publish_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération : " + e.getMessage());
        }
        
        return jobs;
    }
    
    /**
     * Recherche par critères
     */
    public List<JobAnnouncement> search(String location, String contractType, String sector) {
        List<JobAnnouncement> jobs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM job_announcements WHERE 1=1");
        
        if (location != null && !location.isEmpty()) {
            sql.append(" AND location = ?");
        }
        if (contractType != null && !contractType.isEmpty()) {
            sql.append(" AND contract_type = ?");
        }
        if (sector != null && !sector.isEmpty()) {
            sql.append(" AND secteur_activite = ?");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (location != null && !location.isEmpty()) {
                stmt.setString(paramIndex++, location);
            }
            if (contractType != null && !contractType.isEmpty()) {
                stmt.setString(paramIndex++, contractType);
            }
            if (sector != null && !sector.isEmpty()) {
                stmt.setString(paramIndex++, sector);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche : " + e.getMessage());
        }
        
        return jobs;
    }
    
    /**
     * Mapper ResultSet → JobAnnouncement
     */
    private JobAnnouncement mapResultSetToJob(ResultSet rs) throws SQLException {
        JobAnnouncement job = new JobAnnouncement();
        job.setId(rs.getInt("id"));
        job.setTitle(rs.getString("title"));
        job.setCompany(rs.getString("company"));
        job.setDescription(rs.getString("description"));
        job.setLocation(rs.getString("location"));
        job.setContractType(rs.getString("contract_type"));
        job.setExperienceLevel(rs.getString("experience_level"));
        job.setExperienceRequise(rs.getString("experience_requise"));
        job.setNiveauEtude(rs.getString("niveau_etude"));
        job.setSecteurActivite(rs.getString("secteur_activite"));
        job.setFonction(rs.getString("fonction"));
        job.setTypeTeletravail(rs.getString("type_teletravail"));
        job.setNombrePostes(rs.getInt("nombre_postes"));
        job.setSalary(rs.getString("salary"));
        job.setSourceUrl(rs.getString("source_url"));
        job.setSourceSite(rs.getString("source_site"));
        
        Date publishDate = rs.getDate("publish_date");
        if (publishDate != null) {
            job.setPublishDate(new java.util.Date(publishDate.getTime()));
        }
        job.setPublishDateString(rs.getString("publish_date_string"));
        
        return job;
    }
    
    /**
     * Insère les compétences
     */
    private void insertSkills(JobAnnouncement job) {
        String insertSkill = "INSERT IGNORE INTO skills (name) VALUES (?)";
        String linkSkill = "INSERT IGNORE INTO job_skills (job_id, skill_id) " +
                "SELECT ?, id FROM skills WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtSkill = conn.prepareStatement(insertSkill);
             PreparedStatement stmtLink = conn.prepareStatement(linkSkill)) {
            
            for (String skill : job.getSkills()) {
                // Insérer skill
                stmtSkill.setString(1, skill);
                stmtSkill.executeUpdate();
                
                // Lier skill au job
                stmtLink.setInt(1, job.getId());
                stmtLink.setString(2, skill);
                stmtLink.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur skills : " + e.getMessage());
        }
    }
    
    /**
     * ⭐ AJOUT 1 : Récupère les catégories ML disponibles
     */
    
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tous");
        
        String sql = "SELECT DISTINCT category FROM job_classifications " +
                    "WHERE category IS NOT NULL " +
                    "ORDER BY category";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String category = rs.getString("category");
                if (category != null && !category.isEmpty()) {
                    categories.add(category);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        
        return categories;
    }
    
    
    
    
    public List<String> getAllSourceSites() {
        List<String> sites = new ArrayList<>();
        sites.add("Tous");
        
        String sql = "SELECT DISTINCT source_site FROM job_announcements " +
                    "WHERE source_site IS NOT NULL " +
                    "ORDER BY source_site";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String site = rs.getString("source_site");
                if (site != null && !site.isEmpty()) {
                    sites.add(site);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
        
        return sites;
    }
    
    
    
    
    /**
     * ⭐ AJOUT 3 : Recherche par site ET catégorie ML
     */
    public List<JobAnnouncement> searchBySiteAndCategory(String sourceSite, String category) {
        List<JobAnnouncement> jobs = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT ja.* FROM job_announcements ja " +
            "LEFT JOIN job_classifications jc ON ja.id = jc.job_id " +
            "WHERE 1=1"
        );
        
        if (sourceSite != null && !sourceSite.isEmpty() && !sourceSite.equals("Tous")) {
            sql.append(" AND ja.source_site = ?");
        }
        
        if (category != null && !category.isEmpty() && !category.equals("Tous")) {
            sql.append(" AND jc.category = ?");
        }
        
        sql.append(" ORDER BY ja.publish_date DESC LIMIT 100");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (sourceSite != null && !sourceSite.isEmpty() && !sourceSite.equals("Tous")) {
                stmt.setString(paramIndex++, sourceSite);
            }
            
            if (category != null && !category.isEmpty() && !category.equals("Tous")) {
                stmt.setString(paramIndex++, category);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche : " + e.getMessage());
        }
        
        return jobs;
    }
    
    /**
     * ⭐ AJOUT 4 : Récupère les compétences d'une offre
     */
    public List<String> getJobSkills(int jobId) {
        List<String> skills = new ArrayList<>();
        
        String sql = "SELECT s.name FROM skills s " +
                    "JOIN job_skills js ON s.id = js.skill_id " +
                    "WHERE js.job_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                skills.add(rs.getString("name"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur skills : " + e.getMessage());
        }
        
        return skills;
    }
    
    
}