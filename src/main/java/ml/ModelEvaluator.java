package ml;
import database.DatabaseConnection;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.sql.*;
import java.util.*;
class ModelEvaluator {
    
    public static class Result {
        public String modelName;
        public double accuracy;
        public long trainingTime;
        public long predictionTime;
        
        public Result(String name, double acc, long trainTime, long predTime) {
            this.modelName = name;
            this.accuracy = acc;
            this.trainingTime = trainTime;
            this.predictionTime = predTime;
        }
    }
    
    public static Result evaluate(Classifier classifier, List<MLRecord> train, List<MLRecord> test) {
        try {
            // Entraînement
            long startTrain = System.currentTimeMillis();
            classifier.train(train);
            long trainTime = System.currentTimeMillis() - startTrain;
            
            // Prédiction
            long startPred = System.currentTimeMillis();
            int correct = 0;
            for (MLRecord r : test) {
                if (classifier.predict(r.getText()).equalsIgnoreCase(r.getLabel())) {
                    correct++;
                }
            }
            long predTime = System.currentTimeMillis() - startPred;
            
            double accuracy = (double) correct / test.size();
            return new Result(classifier.getName(), accuracy, trainTime, predTime);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(classifier.getName(), 0.0, 0, 0);
        }
    }
}