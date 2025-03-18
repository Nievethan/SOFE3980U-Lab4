package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double[] mseValues = new double[filePaths.length];
        double[] maeValues = new double[filePaths.length];
        double[] mareValues = new double[filePaths.length];
        
        for (int i = 0; i < filePaths.length; i++) {
            double[] metrics = evaluateModel(filePaths[i]);
            mseValues[i] = metrics[0];
            maeValues[i] = metrics[1];
            mareValues[i] = metrics[2];
            
            System.out.printf("For %s:\n", filePaths[i]);
            System.out.printf("\tMSE = %.5f\n", mseValues[i]);
            System.out.printf("\tMAE = %.5f\n", maeValues[i]);
            System.out.printf("\tMARE = %.8f\n\n", mareValues[i]);
        }
        
        // Identify the best model
        int bestMSEIndex = findBestModel(mseValues);
        int bestMAEIndex = findBestModel(maeValues);
        int bestMAREIndex = findBestModel(mareValues);
        
        System.out.printf("According to MSE, the best model is %s\n", filePaths[bestMSEIndex]);
        System.out.printf("According to MAE, the best model is %s\n", filePaths[bestMAEIndex]);
        System.out.printf("According to MARE, the best model is %s\n", filePaths[bestMAREIndex]);
    }

    private static double[] evaluateModel(String filePath) {
        double mse = 0, mae = 0, mare = 0;
        int count = 0;
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            for (String[] row : allData) {
                float y_true = Float.parseFloat(row[0]);
                float y_predicted = Float.parseFloat(row[1]);
                double error = y_true - y_predicted;
                
                mse += Math.pow(error, 2);
                mae += Math.abs(error);
                mare += Math.abs(error) / Math.abs(y_true);
                count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        }
        
        return new double[]{mse / count, mae / count, mare / count};
    }
    
    private static int findBestModel(double[] values) {
        int bestIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[bestIndex]) {
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
