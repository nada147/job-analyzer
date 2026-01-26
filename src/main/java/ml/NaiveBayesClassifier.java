package ml;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;
public class NaiveBayesClassifier implements Classifier {
    private Map<String, Map<String, Integer>> wordCounts = new HashMap<>();
    private Map<String, Integer> classCounts = new HashMap<>();
    private Set<String> vocabulary = new HashSet<>();
    private int totalDocuments = 0;

    @Override
    public void train(List<MLRecord> dataset) {
        for (MLRecord record : dataset) {
            totalDocuments++;
            String label = record.getLabel();
            String text = record.getText();

            classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);
            wordCounts.putIfAbsent(label, new HashMap<>());

            for (String word : text.split(" ")) {
                if (word.isEmpty()) continue;
                vocabulary.add(word);
                Map<String, Integer> wc = wordCounts.get(label);
                wc.put(word, wc.getOrDefault(word, 0) + 1);
            }
        }
    }

    @Override
    public String predict(String inputText) {
        String text = clean(inputText);
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestClass = "Autre";

        for (String clazz : classCounts.keySet()) {
            double logProb = Math.log((double) classCounts.get(clazz) / totalDocuments);
            Map<String, Integer> wc = wordCounts.get(clazz);
            int totalWordsInClass = wc.values().stream().mapToInt(i -> i).sum();

            for (String word : text.split(" ")) {
                if (word.isEmpty()) continue;
                int count = wc.getOrDefault(word, 0) + 1;
                double prob = (double) count / (totalWordsInClass + vocabulary.size());
                logProb += Math.log(prob);
            }

            if (logProb > bestScore) {
                bestScore = logProb;
                bestClass = clazz;
            }
        }
        return bestClass;
    }

    @Override
    public String getName() {
        return "Naive Bayes";
    }

    private String clean(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("<[^>]+>", " ")
                .replaceAll("[^a-zàâäéèêëîïôöùûüç0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
