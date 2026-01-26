package ml;
import weka.classifiers.lazy.IBk;
import java.util.*;
import database.DatabaseConnection;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.sql.*;


//========== KNN (K-NEAREST NEIGHBORS) ==========
class KNNClassifier implements Classifier {
 private IBk model;
 private Instances structure;
 private Set<String> vocabulary;
 private List<String> vocabList;

 @Override
 public void train(List<MLRecord> dataset) throws Exception {
     // Vocabulaire (top 300 mots pour KNN - plus rapide)
     Map<String, Integer> wordFreq = new HashMap<>();
     for (MLRecord r : dataset) {
         for (String word : r.getText().split(" ")) {
             if (!word.isEmpty()) {
                 wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
             }
         }
     }
     
     vocabList = wordFreq.entrySet().stream()
             .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
             .limit(300)
             .map(Map.Entry::getKey)
             .collect(java.util.stream.Collectors.toList());
     vocabulary = new HashSet<>(vocabList);

     ArrayList<Attribute> attributes = new ArrayList<>();
     for (String word : vocabList) {
         attributes.add(new Attribute("word_" + word));
     }
     
     Set<String> labels = new HashSet<>();
     for (MLRecord r : dataset) labels.add(r.getLabel());
     attributes.add(new Attribute("class", new ArrayList<>(labels)));

     Instances data = new Instances("jobs", attributes, dataset.size());
     data.setClassIndex(vocabList.size());

     for (MLRecord r : dataset) {
         double[] values = new double[vocabList.size() + 1];
         Map<String, Integer> wordCount = new HashMap<>();
         for (String word : r.getText().split(" ")) {
             if (vocabulary.contains(word)) {
                 wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
             }
         }
         
         for (int i = 0; i < vocabList.size(); i++) {
             values[i] = wordCount.getOrDefault(vocabList.get(i), 0);
         }
         
         DenseInstance inst = new DenseInstance(1.0, values);
         inst.setDataset(data);
         inst.setValue(vocabList.size(), r.getLabel());
         data.add(inst);
     }

     structure = new Instances(data, 0);
     model = new IBk(5); // K=5 voisins
     model.buildClassifier(data);
 }

 @Override
 public String predict(String text) throws Exception {
     Map<String, Integer> wordCount = new HashMap<>();
     for (String word : text.toLowerCase()
             .replaceAll("<[^>]+>", " ")
             .replaceAll("[^a-zàâäéèêëîïôöùûüç0-9 ]", " ")
             .replaceAll("\\s+", " ").trim().split(" ")) {
         if (vocabulary.contains(word)) {
             wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
         }
     }

     double[] values = new double[vocabList.size() + 1];
     for (int i = 0; i < vocabList.size(); i++) {
         values[i] = wordCount.getOrDefault(vocabList.get(i), 0);
     }

     DenseInstance inst = new DenseInstance(1.0, values);
     inst.setDataset(structure);
     double classIdx = model.classifyInstance(inst);
     return structure.classAttribute().value((int) classIdx);
 }

 @Override
 public String getName() {
     return "KNN (k=5)";
 }
}
