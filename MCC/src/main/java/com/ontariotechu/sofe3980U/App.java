package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Multi-Class Classification Model Performance
 */
public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        evaluateModel(filePath);
    }

    private static void evaluateModel(String filePath) {
        double ce = 0;
        int[][] confusionMatrix = new int[5][5];
        int count = 0;
        
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            
            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]) - 1;
                double[] probabilities = new double[5];
                int y_pred = 0;
                double maxProb = 0;
                
                for (int i = 0; i < 5; i++) {
                    probabilities[i] = Double.parseDouble(row[i + 1]);
                    if (probabilities[i] > maxProb) {
                        maxProb = probabilities[i];
                        y_pred = i;
                    }
                }
                
                ce += -Math.log(probabilities[y_true]);
                confusionMatrix[y_pred][y_true]++;
                count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return;
        }
        
        ce /= count;
        System.out.printf("CE = %.7f\n", ce);
        
        System.out.println("Confusion matrix");
        System.out.println("\ty=1\ty=2\ty=3\ty=4\ty=5");
        for (int i = 0; i < 5; i++) {
            System.out.printf("y^=%d", i + 1);
            for (int j = 0; j < 5; j++) {
                System.out.printf("\t%d", confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}
