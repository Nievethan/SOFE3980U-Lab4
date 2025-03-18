package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Binary Classification Model Performance
 */
public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double[] bceValues = new double[filePaths.length];
        double[] accuracyValues = new double[filePaths.length];
        double[] precisionValues = new double[filePaths.length];
        double[] recallValues = new double[filePaths.length];
        double[] f1Values = new double[filePaths.length];
        double[] aucRocValues = new double[filePaths.length];
        
        for (int i = 0; i < filePaths.length; i++) {
            double[] metrics = evaluateModel(filePaths[i]);
            bceValues[i] = metrics[0];
            accuracyValues[i] = metrics[1];
            precisionValues[i] = metrics[2];
            recallValues[i] = metrics[3];
            f1Values[i] = metrics[4];
            aucRocValues[i] = metrics[5];
            
            System.out.printf("For %s:\n", filePaths[i]);
            System.out.printf("\tBCE = %.7f\n", bceValues[i]);
            System.out.println("\tConfusion matrix");
            System.out.printf("\t\ty=1\ty=0\n");
            System.out.printf("\t y^=1\t%d\t%d\n", (int) metrics[6], (int) metrics[7]);
            System.out.printf("\t y^=0\t%d\t%d\n", (int) metrics[8], (int) metrics[9]);
            System.out.printf("\tAccuracy = %.4f\n", accuracyValues[i]);
            System.out.printf("\tPrecision = %.8f\n", precisionValues[i]);
            System.out.printf("\tRecall = %.8f\n", recallValues[i]);
            System.out.printf("\tF1 Score = %.8f\n", f1Values[i]);
            System.out.printf("\tAUC ROC = %.8f\n\n", aucRocValues[i]);
        }
        
        // Identify the best model
        reportBestModel("BCE", bceValues, filePaths, true);
        reportBestModel("Accuracy", accuracyValues, filePaths, false);
        reportBestModel("Precision", precisionValues, filePaths, false);
        reportBestModel("Recall", recallValues, filePaths, false);
        reportBestModel("F1 Score", f1Values, filePaths, false);
        reportBestModel("AUC ROC", aucRocValues, filePaths, false);
    }

    private static double[] evaluateModel(String filePath) {
        double bce = 0, tp = 0, tn = 0, fp = 0, fn = 0;
        int count = 0;
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]);
                float y_predicted = Float.parseFloat(row[1]);
                int y_hat = y_predicted >= 0.5 ? 1 : 0;
                
                if (y_true == 1 && y_hat == 1) tp++;
                if (y_true == 0 && y_hat == 0) tn++;
                if (y_true == 0 && y_hat == 1) fp++;
                if (y_true == 1 && y_hat == 0) fn++;
                
                bce += y_true * Math.log(y_predicted) + (1 - y_true) * Math.log(1 - y_predicted);
                count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return new double[]{Double.MAX_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        
        bce = -bce / count;
        double accuracy = (tp + tn) / (tp + tn + fp + fn);
        double precision = tp / (tp + fp);
        double recall = tp / (tp + fn);
        double f1 = 2 * (precision * recall) / (precision + recall);
        double aucRoc = (tp / (tp + fn) + tn / (tn + fp)) / 2;
        
        return new double[]{bce, accuracy, precision, recall, f1, aucRoc, tp, fp, fn, tn};
    }
    
    private static void reportBestModel(String metricName, double[] values, String[] filePaths, boolean lowerIsBetter) {
        int bestIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if ((lowerIsBetter && values[i] < values[bestIndex]) || (!lowerIsBetter && values[i] > values[bestIndex])) {
                bestIndex = i;
            }
        }
        System.out.printf("According to %s, the best model is %s\n", metricName, filePaths[bestIndex]);
    }
}
