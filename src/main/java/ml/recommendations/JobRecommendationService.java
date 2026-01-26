package ml.recommendations;

import database.DatabaseConnection;
import nlp.SkillExtractor;
import nlp.TextPreprocessor;
import ml.matching.MatchResult;
import ml.matching.SkillMatcher;

import java.sql.*;
import java.util.*;

public class JobRecommendationService {

    public List<MatchResult> recommendSimilarJobs(int jobId) {

        List<MatchResult> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1️⃣ Récupérer le job source
            String sqlJob = "SELECT title, description, secteur_activite, fonction FROM job_announcements WHERE id = ?";

            PreparedStatement psJob = conn.prepareStatement(sqlJob);
            psJob.setInt(1, jobId);
            ResultSet rsJob = psJob.executeQuery();

            if (!rsJob.next()) return results;

            String jobText = TextPreprocessor.buildJobText(
                    rsJob.getString("title"),
                    rsJob.getString("description"),
                    rsJob.getString("secteur_activite"),
                    rsJob.getString("fonction")
            );

            Set<String> baseSkills =
                    SkillExtractor.extract(jobText);

            // 2️⃣ Comparer avec les autres jobs
            String sqlAll = " SELECT id, title, description, secteur_activite, fonction FROM job_announcements  WHERE id <> ? ";


            PreparedStatement psAll = conn.prepareStatement(sqlAll);
            psAll.setInt(1, jobId);
            ResultSet rs = psAll.executeQuery();

            while (rs.next()) {

                String otherText = TextPreprocessor.buildJobText(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("secteur_activite"),
                        rs.getString("fonction")
                );

                Set<String> otherSkills =
                        SkillExtractor.extract(otherText);

                double score = SkillMatcher.jaccard(
                        new ArrayList<>(baseSkills),
                        new ArrayList<>(otherSkills)
                );

                if (score >= 0.15) {
                    results.add(new MatchResult(
                            rs.getInt("id"),
                            rs.getString("title"),
                            score
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        results.sort((a, b) ->
                Double.compare(b.getScore(), a.getScore()));

        return results;
    }
}