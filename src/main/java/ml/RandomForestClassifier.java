package ml;

import database.DatabaseConnection;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.sql.*;
import java.util.*;
//========== 4. RANDOM FOREST ==========
class RandomForestClassifier implements Classifier {
    private RandomForest model;
    private Instances structure;
    private Set<String> vocabulary;
    private List<String> vocabList;

    @Override
    public void train(List<MLRecord> dataset) throws Exception {
        // 1. Construire le vocabulaire (top 500 mots)
        Map<String, Integer> wordFreq = new HashMap<>();
        for (MLRecord r : dataset) {
            for (String word : r.getText().split(" ")) {
                if (!word.isEmpty()) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }
            }
        }
        
        // Garder les 500 mots les plus fréquents
        vocabList = wordFreq.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(500)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
        
        vocabulary = new HashSet<>(vocabList);

        // 2. Créer structure Weka avec attributs numériques
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        // Un attribut numérique par mot du vocabulaire
        for (String word : vocabList) {
            attributes.add(new Attribute("word_" + word));
        }
        
        // Attribut classe
        Set<String> labels = new HashSet<>();
        for (MLRecord r : dataset) labels.add(r.getLabel());
        attributes.add(new Attribute("class", new ArrayList<>(labels)));

        // 3. Créer dataset
        Instances data = new Instances("jobs", attributes, dataset.size());
        data.setClassIndex(vocabList.size()); // Dernière colonne = classe

        // 4. Remplir le dataset
        for (MLRecord r : dataset) {
            double[] values = new double[vocabList.size() + 1];
            
            // Compter les occurrences de chaque mot
            Map<String, Integer> wordCount = new HashMap<>();
            for (String word : r.getText().split(" ")) {
                if (vocabulary.contains(word)) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
            
            // Remplir le vecteur
            for (int i = 0; i < vocabList.size(); i++) {
                values[i] = wordCount.getOrDefault(vocabList.get(i), 0);
            }
            
            // Ajouter la classe
            DenseInstance inst = new DenseInstance(1.0, values);
            inst.setDataset(data);
            inst.setValue(vocabList.size(), r.getLabel());
            data.add(inst);
        }

        // 5. Entraîner Random Forest
        structure = new Instances(data, 0);
        model = new RandomForest();
        model.setNumIterations(50);
        model.setMaxDepth(15);
        model.buildClassifier(data);
    }

    @Override
    public String predict(String text) throws Exception {
        // 1. Compter les mots
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : text.toLowerCase()
                .replaceAll("<[^>]+>", " ")
                .replaceAll("[^a-zàâäéèêëîïôöùûüç0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .split(" ")) {
            if (vocabulary.contains(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        // 2. Créer vecteur
        double[] values = new double[vocabList.size() + 1];
        for (int i = 0; i < vocabList.size(); i++) {
            values[i] = wordCount.getOrDefault(vocabList.get(i), 0);
        }

        // 3. Prédire
        DenseInstance inst = new DenseInstance(1.0, values);
        inst.setDataset(structure);
        double classIdx = model.classifyInstance(inst);
        
        return structure.classAttribute().value((int) classIdx);
    }

    @Override
    public String getName() {
        return "Random Forest";
    }
}