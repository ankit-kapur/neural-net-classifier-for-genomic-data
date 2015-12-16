package com.dm;

import com.dm.classifiers.C45Classifier;
import com.dm.classifiers.Classifier;
import com.dm.classifiers.NaiveBayesClassifier;
import com.dm.classifiers.RandomForestClassifier;
import com.dm.data.pojo.Attribute;
import com.dm.data.pojo.DataPoint;
import com.dm.data.reader.InputParser;

import java.util.*;

import com.dm.data.pojo.SingleFoldStats;

public class Main {
    static String filePath = "dataset/";
    static List<SingleFoldStats> stats;

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        String algorithm;
        String fileName;
        int k;

        System.out.println("-------------------------------- Classification Algorithms --------------------------------");
        System.out.println("Select Classification algorithm type. Enter B or b for Naive Bayes and D or d for Decision Tree or R or r for Random Trees:");
        algorithm = in.nextLine();
        System.out.println("Specify file name:");
        fileName = in.nextLine();
        System.out.println("Enter number of partitions 'K' desired for K-fold cross validation :");
        k = in.nextInt();


        List<DataPoint> dataPoints;
        List<DataPoint> testData;
        InputParser parser = new InputParser(filePath + fileName);
        dataPoints = parser.parseData();

        Collections.shuffle(dataPoints);
        List<Attribute> attributes = parser.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).setIndexInDataPoint(i);
        }
        if(fileName.equalsIgnoreCase("dataset3.txt")) {
            InputParser ip = new InputParser(filePath + "test3.txt");
            testData = ip.parseTestData();
            predictClass(attributes,algorithm, dataPoints, testData);
        }
        else {
            List<List<DataPoint>> dataPartitions = partitionData(dataPoints, k);
            stats = new ArrayList<>();
            performKFoldCrossValidation(attributes, algorithm, dataPartitions);
//        C45Classifier classifier = new C45Classifier(dataPoints, attributes);
//        classifier.runAlgorithm();
//        Utility.levelOrderTraversal(classifier.root);
        }
    }

    /**
     * Method to split given data into k number of partitions
     *
     * @param dataPoints         input data points
     * @param numberOfPartitions number of desired partitions
     * @return data partitions
     */
    public static List<List<DataPoint>> partitionData(List<DataPoint> dataPoints, int numberOfPartitions) {
        List<List<DataPoint>> dataPartitions = new ArrayList<>();
        for (int i = 0; i < numberOfPartitions; i++)
            dataPartitions.add(new ArrayList<>());
        int partitionSize = dataPoints.size() / numberOfPartitions;

        for (int i = 0; i < numberOfPartitions; ) {
            int j;
            for (j = 0; j < dataPoints.size() && i < numberOfPartitions; j++) {
                dataPartitions.get(i).add(dataPoints.get(j));
                if (dataPartitions.get(i).size() == partitionSize) {
                    i++;
                }
            }
            while (j < dataPoints.size()) {
                dataPartitions.get(i - 1).add(dataPoints.get(j));
                j++;
            }
        }
        return dataPartitions;
    }

    /**
     * Method to perform K-fold closs validation for Naive Bayes Classifier
     *
     * @param attributes attributes of given data points
     */
    public static void performKFoldCrossValidation(List<Attribute> attributes, String algorithm,
                                                   List<List<DataPoint>> dataPartitions) {
        List<DataPoint> trainingData;
        List<DataPoint> testingData;

        double avgAccuracy = 0;
        double avgPrecision = 0;
        double avgRecall = 0;
        double avgFMeasure = 0;

        for (int i = 0; i < dataPartitions.size(); i++) {
            int truePositives = 0;
            int trueNegatives = 0;
            int falsePositives = 0;
            int falseNegatives = 0;
            testingData = dataPartitions.get(i);
            trainingData = new ArrayList<>();
            for (int j = 0; j < dataPartitions.size(); j++) {
                if (i != j) {
                    trainingData.addAll(dataPartitions.get(j));
                }
            }
            Classifier classifier = null;
            switch (algorithm) {
                case "B":
                case "b":
                    classifier = new NaiveBayesClassifier(trainingData, attributes);
                    break;
                case "D":
                case "d": {
                    C45Classifier c = new C45Classifier(trainingData, attributes, false);
                    c.runAlgorithm();
//                    Utility.levelOrderTraversal(c.root);
                    classifier = c;
                }
                break;
                case "R":
                case "r": {
                    List<C45Classifier> classifiers = new ArrayList<>();
                    List<List<DataPoint>> dataPartitionsForests = partitionDataForRandomForests(trainingData, 5);
                    for (int j = 0; j < dataPartitionsForests.size(); j++) {
                        if (i != j) {
                            C45Classifier c = new C45Classifier(dataPartitionsForests.get(j), attributes, true);
                            classifiers.add(c);
                        }
                    }
                    classifier = new RandomForestClassifier(classifiers);
                }
                break;

            }
            for (DataPoint d : testingData) {
                int predictedClass = classifier.classifyDataPoint(d);
                int groundTruth = d.getTruth();

                if (groundTruth == 1 && predictedClass == 1)
                    truePositives++;
                else if (groundTruth == 0 && predictedClass == 0)
                    trueNegatives++;
                else if (groundTruth == 0 && predictedClass == 1)
                    falsePositives++;
                else
                    falseNegatives++;
            }
            SingleFoldStats stat = new SingleFoldStats();
//            System.out.println("TP: " + truePositives);
//            System.out.println("TN: " + trueNegatives);
//            System.out.println("FP: " + falsePositives);
//            System.out.println("FN: " + falseNegatives);
            double ppv = 0.0;
            double npv = 0.0;
            double tpr = 0.0;
            double tnr = 0.0;
            if (truePositives != 0) {
                ppv = (double) (truePositives) / (truePositives + falsePositives);
                tpr = (double) (truePositives) / (truePositives + falseNegatives);
            }
            if (trueNegatives != 0) {
                npv = (double) (trueNegatives) / (trueNegatives + falseNegatives);
                tnr = (double) (trueNegatives) / (trueNegatives + falsePositives);
            }
            double precision = (ppv + npv)/2;
            double recall = (tpr + tnr)/2;

            double accuracy = (double) (truePositives + trueNegatives) / testingData.size();
            stat.setAccuracy(accuracy);
            stat.setPrecision(precision);
            stat.setRecall(recall);
            stat.setfMeasure((2 * recall * precision) / (recall + precision));
            stats.add(stat);
        }
        for (int i = 0; i < stats.size(); i++) {
            avgAccuracy += stats.get(i).getAccuracy();
            avgPrecision += stats.get(i).getPrecision();
            avgRecall += stats.get(i).getRecall();
            avgFMeasure += stats.get(i).getfMeasure();
        }
        /*for (int i = 0; i < stats.size(); i++){
            System.out.print(stats.get(i).getAccuracy() + "\t" + stats.get(i).getPrecision() + "\t" + stats.get(i).getRecall() + "\t" + stats.get(i).getfMeasure() + "\n");
        }*/
        System.out.println("Average Accuracy: " + avgAccuracy / stats.size());
        System.out.println("Average Precision: " + avgPrecision / stats.size());
        System.out.println("Average Recall: " + avgRecall / stats.size());
        System.out.println("Average F-1 Measure: " + avgFMeasure / stats.size());

    }

    private static List<List<DataPoint>> partitionDataForRandomForests(List<DataPoint> trainingData, int numberOfPartitions) {
        List<List<DataPoint>> dataPoints = new ArrayList<>();
        for(int i = 0; i < numberOfPartitions; i++) {
            List<DataPoint> temp = new ArrayList<>();
            Collections.shuffle(trainingData);
            int size = (int) (trainingData.size() * 0.7);
            for(int j = 0; j < trainingData.size(); j++) {
                temp.add(trainingData.get(j));
            }
            dataPoints.add(temp);
        }
        return dataPoints;
    }

    public static void predictClass(List<Attribute> attributes, String algorithm,
                                    List<DataPoint> trainingData, List<DataPoint> testingData) {

        double avgAccuracy = 0;
        double avgPrecision = 0;
        double avgRecall = 0;
        double avgFMeasure = 0;


        Classifier classifier = null;
        switch (algorithm) {
            case "B":
            case "b":
                classifier = new NaiveBayesClassifier(trainingData, attributes);
                break;
            case "D":
            case "d": {
                C45Classifier c = new C45Classifier(trainingData, attributes, false);
                c.runAlgorithm();
                //Utility.levelOrderTraversal(c.root);
                classifier = c;
            }
            break;
            case "R":
            case "r": {
                List<C45Classifier> classifiers = new ArrayList<>();
                List<List<DataPoint>> dataPartitionsForests = partitionDataForRandomForests(trainingData, 5);
                for (int j = 0; j < dataPartitionsForests.size(); j++) {
                    C45Classifier c = new C45Classifier(dataPartitionsForests.get(j), attributes, true);
                    classifiers.add(c);
                }
                classifier = new RandomForestClassifier(classifiers);
            }
            break;

        }
        for (DataPoint d : testingData) {
            int predictedClass = classifier.classifyDataPoint(d);
            System.out.print(predictedClass + "  ");
        }

    }

}