package ml;
import database.DatabaseConnection;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.sql.*;
import java.util.*;

import java.util.Scanner;

public class MLComparison {

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

    public static Map<String, List<MLRecord>> stratifiedSplit(List<MLRecord> dataset, double trainRatio) {
        Map<String, List<MLRecord>> byLabel = new HashMap<>();
        for (MLRecord r : dataset) {
            byLabel.computeIfAbsent(r.getLabel(), k -> new ArrayList<>()).add(r);
        }

        List<MLRecord> train = new ArrayList<>();
        List<MLRecord> test = new ArrayList<>();
        Random rnd = new Random(42);

        for (List<MLRecord> records : byLabel.values()) {
            Collections.shuffle(records, rnd);
            int splitIndex = (int) (records.size() * trainRatio);
            train.addAll(records.subList(0, splitIndex));
            test.addAll(records.subList(splitIndex, records.size()));
        }

        Map<String, List<MLRecord>> result = new HashMap<>();
        result.put("train", train);
        result.put("test", test);
        return result;
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        System.out.println("🔍 Chargement du dataset...");
        List<MLRecord> dataset = loadDataset();
        System.out.println("✅ Dataset chargé : " + dataset.size() + " offres\n");

        System.out.println("🔀 Split stratifié (80/20)...");
        Map<String, List<MLRecord>> split = stratifiedSplit(dataset, 0.8);
        List<MLRecord> train = split.get("train");
        List<MLRecord> test = split.get("test");
        System.out.println("   Train : " + train.size());
        System.out.println("   Test  : " + test.size() + "\n");

        // ========== LISTE DES MODÈLES À TESTER ==========
        List<Classifier> models = Arrays.asList(
            new NaiveBayesClassifier(),
            new RandomForestClassifier(),
            new KNNClassifier()
        );

        System.out.println("========================================");
        System.out.println("📊 ÉVALUATION DES MODÈLES");
        System.out.println("========================================\n");

        List<ModelEvaluator.Result> results = new ArrayList<>();
        NaiveBayesClassifier naiveBayesForPrediction = null;

        for (Classifier model : models) {
            System.out.println("🤖 " + model.getName() + "...");
            ModelEvaluator.Result result = ModelEvaluator.evaluate(model, train, test);
            results.add(result);
            
            System.out.println("   Accuracy        : " + String.format("%.2f", result.accuracy * 100) + " %");
            System.out.println("   Training time   : " + result.trainingTime + " ms");
            System.out.println("   Prediction time : " + result.predictionTime + " ms\n");
            
            // Sauvegarder Naive Bayes pour la prédiction interactive
            if (model instanceof NaiveBayesClassifier) {
                naiveBayesForPrediction = (NaiveBayesClassifier) model;
            }
        }

        // ========== RÉSUMÉ ==========
        System.out.println("========================================");
        System.out.println("🏆 RÉSULTAT FINAL - COMPARAISON");
        System.out.println("========================================\n");
        
        // Trouver le meilleur modèle
        ModelEvaluator.Result best = results.stream()
                .max(Comparator.comparingDouble(r -> r.accuracy))
                .orElse(null);
        
        for (ModelEvaluator.Result r : results) {
            String marker = (r == best && r.accuracy == best.accuracy) ? " 🏆" : "";
            System.out.println(String.format("%-20s : %.2f %%%s", 
                r.modelName, r.accuracy * 100, marker));
        }
        
        if (best != null) {
            System.out.println("\n🎯 Meilleur modèle : " + best.modelName);
            System.out.println("   Accuracy : " + String.format("%.2f", best.accuracy * 100) + " %");
        }

       

        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("📝 Entrez une description d'offre d'emploi (ou 'quit' pour quitter) :\n> ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("👋 Au revoir !");
                break;
            }
            
            if (input.isEmpty()) {
                System.out.println("⚠️  Veuillez entrer du texte.\n");
                continue;
            }
            
            try {
                String prediction = naiveBayesForPrediction.predict(input);
                System.out.println("🎯 Catégorie prédite : " + prediction);
                System.out.println();
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de la prédiction : " + e.getMessage());
                System.out.println();
            }
        }
        
        scanner.close();
    }

}