package ml.matching;

import database.DatabaseConnection;
import nlp.SkillExtractor;
import nlp.TextPreprocessor;

import java.sql.*;
import java.util.*;

public class MatchingService {

    /**
     * Matching CV ↔ Jobs
     * Domaine ML optionnel
     */
    public List<MatchResult> matchCVWithJobs(
            List<String> cvSkills,
            String domainML   // peut être null
    ) {

        // =========================
        // 1️⃣ Normaliser CV skills
        // =========================
        Set<String> cvSet = new HashSet<>();
        for (String s : cvSkills) {
            if (s != null && !s.isBlank()) {
                cvSet.add(s.toLowerCase().trim());
            }
        }

        System.out.println("DEBUG CV skills = " + cvSet);
        System.out.println("DEBUG Domaine ML = " + domainML);

        List<MatchResult> results = new ArrayList<>();

        // =========================
        // 2️⃣ SQL dynamique
        // =========================
        StringBuilder sql = new StringBuilder(
            "SELECT j.id, j.title, j.description, j.secteur_activite, j.fonction " +
            "FROM job_announcements j "
        );

        // Filtrer par domaine SEULEMENT si choisi
        if (domainML != null && !domainML.isBlank()) {
            sql.append(
                "JOIN job_classifications jc ON jc.job_id = j.id " +
                "WHERE jc.category = ? "
            );
        }

        int total = 0;
        int matched = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (domainML != null && !domainML.isBlank()) {
                ps.setString(1, domainML);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                total++;

                int jobId = rs.getInt("id");
                String title = rs.getString("title");

                // =========================
                // 3️⃣ Texte NLP du job
                // =========================
                String jobText = TextPreprocessor.buildJobText(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("secteur_activite"),
                        rs.getString("fonction")
                );

                // =========================
                // 4️⃣ Extraction skills job
                // =========================
                Set<String> jobSkills = new HashSet<>();
                for (String s : SkillExtractor.extract(jobText)) {
                    jobSkills.add(s.toLowerCase());
                }

                // =========================
                // 5️⃣ Matching Jaccard
                // =========================
                double score = SkillMatcher.jaccard(
                        new ArrayList<>(jobSkills),
                        new ArrayList<>(cvSet)
                );

                if (score >= 0.10) { // 10 %
                    matched++;
                    results.add(new MatchResult(jobId, title, score));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n===== MATCHING SUMMARY =====");
        System.out.println("Jobs analysés : " + total);
        System.out.println("Jobs matchés : " + matched);
        System.out.println("============================\n");

        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results;
    }
}