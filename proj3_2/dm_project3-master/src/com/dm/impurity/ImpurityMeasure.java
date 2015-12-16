package com.dm.impurity;

import com.dm.Utility;
import com.dm.data.pojo.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpurityMeasure {
    private List<List<DataPoint>> splitDataPoints;
    private List<Map<Integer, Integer>> classCounts;
    private List<String> splitLabel;

    public double calculateImpurityMeasure(List<DataPoint> dataPoints, Attribute currentAttribute, int attributeIndex) {
        Map<String, Integer> distinctValues = new HashMap<>();
//        lessThanThreshold = new ArrayList<>();
//        moreThanThreshold = new ArrayList<>();
        splitDataPoints = new ArrayList<>();
        if (currentAttribute instanceof DoubleAttribute) {
            List<Double> midPoints = ((DoubleAttribute) currentAttribute).getMidPoints();
            classCounts = new ArrayList<>();
            for (int i = 0; i < midPoints.size() + 1; i++) {
                Map<Integer, Integer> tempMap = new HashMap<>();
                splitDataPoints.add(new ArrayList<>());
                classCounts.add(tempMap);
            }
            // separate data points into two halves - binary split
            for (int i = 0; i < dataPoints.size(); i++) {
                int groundTruth = dataPoints.get(i).getTruth();
                int index = -1;
                DoubleValue temp = (DoubleValue) dataPoints.get(i).getFeatures().get(attributeIndex);
                Map<Integer, Integer> tempMap = null;
                boolean assigned = false;
                for (int j = 0; j < midPoints.size(); j++) {
                    if (temp.getValue() < midPoints.get(j)) {
                        tempMap = classCounts.get(j);
                        index = j;
                        splitDataPoints.get(j).add(dataPoints.get(i));
                        assigned = true;
                        break;
                    }
                }
                if(!assigned) {
                    tempMap = classCounts.get(midPoints.size());
                    index = midPoints.size();
                    splitDataPoints.get(midPoints.size()).add(dataPoints.get(i));
                }
                if (tempMap.containsKey(groundTruth)) {
                    tempMap.put(groundTruth, tempMap.get(groundTruth) + 1);
                } else {
                    tempMap.put(groundTruth, 1);
                }
                classCounts.set(index, tempMap);
            } //end of for
        } else { //StringValue
            int counter = 0;
            classCounts = new ArrayList<>();
            splitDataPoints = new ArrayList<>();
            splitLabel = new ArrayList<>();
            for (int i = 0; i < dataPoints.size(); i++) {
                String temp = ((StringValue) dataPoints.get(i).getFeatures().get(attributeIndex)).getValue();
                int groundTruth = dataPoints.get(i).getTruth();
                if (distinctValues.containsKey(temp)) {
                    int index = distinctValues.get(temp);
                    Map<Integer, Integer> tempMap = classCounts.get(index);
                    List<DataPoint> d = splitDataPoints.get(index);
                    d.add(dataPoints.get(i));
                    if (tempMap.containsKey(groundTruth)) {
                        tempMap.put(groundTruth, tempMap.get(groundTruth) + 1);
                    } else {
                        tempMap.put(groundTruth, 1);
                    }
                    classCounts.set(index, tempMap);
                    splitDataPoints.set(index, d);
                } else {
                    Map<Integer, Integer> tempMap = new HashMap<>();
                    List<DataPoint> d = new ArrayList<>();
                    d.add(dataPoints.get(i));
                    tempMap.put(groundTruth, 1);
                    distinctValues.put(temp, counter);
                    classCounts.add(tempMap);
                    splitDataPoints.add(d);
                    splitLabel.add(temp);
                    counter++;
                }
            }
        }
//        return calculateGiniIndex(classCounts, dataPoints.size());
        return calculateInformationGain(classCounts, dataPoints.size());
    }

    private double calculateGiniIndex(List<Map<Integer, Integer>> classCounts, int size) {
        double gini[] = new double[classCounts.size()];
        double numberOfDataPoints[] = new double[classCounts.size()];
        for (int i = 0; i < classCounts.size(); i++) {
            for (Integer value : classCounts.get(i).values()) {
                numberOfDataPoints[i] += value;
            }
            for (Integer value : classCounts.get(i).values()) {
                gini[i] = gini[i] + Math.pow((value / numberOfDataPoints[i]), 2);
            }
            gini[i] = 1 - gini[i];
        }
        return calculateWeightedAverage(gini, numberOfDataPoints, size);
    }

    private double calculateWeightedAverage(double[] impurityMeasure, double[] numberOfDataPoints, int size) {
        double weightedAverage = 0.0;
        for (int i = 0; i < numberOfDataPoints.length; i++) {
            weightedAverage += ((numberOfDataPoints[i] / size) * impurityMeasure[i]);
        }
        return weightedAverage;
    }

    /**
     * calculate relative frequencies for the data set
     *
     * @param dataPoints data set
     * @return relative freq
     */
    private double calculateRelativeFrequency(List<DataPoint> dataPoints) {
        double relativeFrequency = 0.0;
        Map<Integer, Integer> classCounts = Utility.calculateClassCounts(dataPoints);
        for (Integer value : classCounts.values()) {
            relativeFrequency = relativeFrequency + Math.pow((value / Double.valueOf(dataPoints.size())), 2);
        }
        return relativeFrequency;
    }

    public double calculateGroundTruthGiniIndex(List<DataPoint> dataPoints) {
        return 1 - calculateRelativeFrequency(dataPoints);
    }

    public double calculateInformationGain(List<Map<Integer, Integer>> classCounts, int size) {
        double entropy[] = new double[classCounts.size()];
        double numberOfDataPoints[] = new double[classCounts.size()];

        for (int i = 0; i < classCounts.size(); i++) {
            for (Integer value : classCounts.get(i).values()) {
                numberOfDataPoints[i] += value;
            }
            for (Integer value : classCounts.get(i).values()) {
                double relativeFreq = value / numberOfDataPoints[i];
                entropy[i] = entropy[i] - relativeFreq * (Math.log(relativeFreq) / Math.log(2));
            }
        }

        return calculateWeightedAverage(entropy, numberOfDataPoints, size);
    }

    public List<List<DataPoint>> getSplitDataPoints() {
        return splitDataPoints;
    }

    public List<String> getSplitLabel() {
        return splitLabel;
    }
}
