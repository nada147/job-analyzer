package ml;

import database.DatabaseConnection;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.sql.*;
import java.util.*;

interface Classifier {
    void train(List<MLRecord> dataset) throws Exception;
    String predict(String text) throws Exception;
    String getName();
}