package ml;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;


public class MLService {

    // Charger les données depuis la BD
    public static List<MLRecord> loadDataset() {
        List<MLRecord> dataset = new ArrayList<>();
        String sql = "SELECT j.title, j.description, j.fonction, " +
                "GROUP_CONCAT(s.name SEPARATOR ' ') AS skills, jc.category " +
                "FROM job_announcements j " +
                "JOIN job_classifications jc ON jc.job_id = j.id " +
                "LEFT JOIN job_skills js ON js.job_id = j.id " +
                "LEFT JOIN skills s ON s.id = js.skill_id " +
                "WHERE jc.category IS NOT NULL GROUP BY j.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String text = String.join(" ",
                        rs.getString("title") != null ? rs.getString("title") : "",
                        rs.getString("description") != null ? rs.getString("description") : "",
                        rs.getString("fonction") != null ? rs.getString("fonction") : "",
                        rs.getString("skills") != null ? rs.getString("skills") : ""
                ).toLowerCase()
                 .replaceAll("<[^>]+>", " ")
                 .replaceAll("[^a-zàâäéèêëîïôöùûüç0-9 ]", " ")
                 .replaceAll("\\s+", " ")
                 .trim();

                dataset.add(new MLRecord(text, rs.getString("category")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    // Split stratifié + Évaluation
    public static void evaluate(List<MLRecord> dataset) {
        // Grouper par catégorie
        Map<String, List<MLRecord>> byLabel = new HashMap<>();
        for (MLRecord r : dataset) {
            byLabel.computeIfAbsent(r.getLabel(), k -> new ArrayList<>()).add(r);
        }

        // Split 80/20 par catégorie
        List<MLRecord> train = new ArrayList<>();
        List<MLRecord> test = new ArrayList<>();
        Random rnd = new Random(42);

        for (List<MLRecord> records : byLabel.values()) {
            Collections.shuffle(records, rnd);
            int splitIndex = (int) (records.size() * 0.8);
            train.addAll(records.subList(0, splitIndex));
            test.addAll(records.subList(splitIndex, records.size()));
        }

        // Entraînement
        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.train(train);

        // Évaluation
        int correct = 0;
        for (MLRecord r : test) {
            if (nb.predict(r.getText()).equalsIgnoreCase(r.getLabel())) {
                correct++;
            }
        }

        double accuracy = (double) correct / test.size();

        System.out.println("===== ÉVALUATION =====");
        System.out.println("Total : " + dataset.size());
        System.out.println("Train : " + train.size());
        System.out.println("Test  : " + test.size());
        System.out.println("Correct : " + correct);
        System.out.println("Accuracy : " + String.format("%.2f", accuracy * 100) + " %");
    }

    // MAIN
    public static void main(String[] args) {
        List<MLRecord> dataset = loadDataset();
        System.out.println("Dataset chargé : " + dataset.size());

        // Test rapide
        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.train(dataset);
        System.out.println("Prédiction : " + nb.predict("Développeur Java Spring Boot SQL"));

        // Évaluation
        evaluate(dataset);
    }
}
