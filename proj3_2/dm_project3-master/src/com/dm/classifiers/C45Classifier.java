package com.dm.classifiers;

import com.dm.data.pojo.*;
import com.dm.impurity.ImpurityMeasure;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class C45Classifier implements Classifier {
    double groundTruthGiniIndex;
    public Node root;
    List<Attribute> attributes;
    boolean useRandomForest;
    int k = 4;

    public C45Classifier(List<DataPoint> dataPoints, List<Attribute> attributes, boolean useRandomForest) {
        root = new Node(dataPoints);
        this.attributes = new ArrayList<>();
        this.attributes.addAll(attributes);
        this.useRandomForest = useRandomForest;
        calculateStatisticsAndResetAttributedUsed();
    }

    public void runAlgorithm() {
        ImpurityMeasure impMeasure = new ImpurityMeasure();
        groundTruthGiniIndex = impMeasure.calculateGroundTruthGiniIndex(root.getDataPoints());
        if (useRandomForest) {
            createDecisionTree(root, randomizeAttributes(attributes));
        } else {
            createDecisionTree(root, attributes);
        }
    }

    private List<Attribute> randomizeAttributes(List<Attribute> attributes) {
        int numberOfAttributesSelected = (int) (attributes.size() * 0.7);
        List<Attribute> temp = new ArrayList<>();
        temp.addAll(attributes);
        Collections.shuffle(temp);
        List<Attribute> toReturn = new ArrayList<>();
        for (int i = 0; i < numberOfAttributesSelected; i++) {
            toReturn.add(temp.get(i));
        }
        return toReturn;
    }

    private void createDecisionTree(Node root, List<Attribute> attributes) {
        double impurityMeasure = Double.MAX_VALUE;
        ImpurityMeasure minMeasure = null;
        int index = -1;
        int indexInList = -1;
        boolean allAttributesUsed = true;
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            if (!attr.isUsed()) {
                allAttributesUsed = false;
                ImpurityMeasure impMeasure = new ImpurityMeasure();
                double temp = impMeasure.calculateImpurityMeasure(root.getDataPoints(), attr, attr.getIndexInDataPoint());
                if (temp < impurityMeasure) {
                    impurityMeasure = temp;
                    index = attr.getIndexInDataPoint();
                    indexInList = i;
                    minMeasure = impMeasure;
                }
            }
        }
        if (allAttributesUsed || index == -1) {
            return;
        } else {
            attributes.get(indexInList).setUsed(true);
            root.setIndex(index);
            List<List<DataPoint>> dp = minMeasure.getSplitDataPoints();
            List<String> splitLabel = minMeasure.getSplitLabel();
            List<Node> children = root.getChildren();
            for (int j = 0; j < dp.size(); j++) {
                Node n = new Node(dp.get(j));
                if (attributes.get(indexInList) instanceof StringAttribute) {
                    n.setAttributeValue(splitLabel.get(j));
                }
                children.add(n);
            }
            root.setChildren(children);
            int minIndex = 0;
            double minMajority = Double.MAX_VALUE;
            /*int maxIndex = 0;
            double maxWeightedAverage = Double.MIN_VALUE;
            for (int j = 0; j < children.size(); j++) {
                double majority = children.get(j).getMajority();
                double size = root.getDataPoints().size();
                double weightedAverage = (children.get(j).getDataPoints().size() / size) * majority;
                if (weightedAverage > maxWeightedAverage) {
                    maxIndex = j;
                    maxWeightedAverage = weightedAverage;
                }
            }

            if (useRandomForest) {
                createDecisionTree(root.getChildren().get(maxIndex), randomizeAttributes(attributes));
            } else {
                createDecisionTree(root.getChildren().get(maxIndex), attributes);
            }

            if(root.getChildren().size() > 1) {
                for (int j = 0; j < root.getChildren().size(); j++) {
                    if (maxIndex != j) {
                        if(useRandomForest) {
                            createDecisionTree(root.getChildren().get(j), randomizeAttributes(attributes));
                        } else {
                            createDecisionTree(root.getChildren().get(j), attributes);
                        }
                    }
                }
            }*/
            for (int j = 0; j < children.size(); j++) {
                if (children.get(j).getMajority() < minMajority) {
                    minIndex = j;
                    minMajority = children.get(j).getMajority();
                }
            }
            if (minMajority < 96) {
                if (useRandomForest) {
                    createDecisionTree(root.getChildren().get(minIndex), randomizeAttributes(attributes));
                } else {
                    createDecisionTree(root.getChildren().get(minIndex), attributes);
                }
            }
            if (root.getChildren().size() > 1) {
                for (int j = 0; j < root.getChildren().size(); j++) {
                    if (minIndex != j && root.getChildren().get(j).getMajority() < 96) {
                        if(useRandomForest) {
                            createDecisionTree(root.getChildren().get(j), randomizeAttributes(attributes));
                        } else {
                            createDecisionTree(root.getChildren().get(j), attributes);
                        }
                    }
                }
            }

        }
    }

    @Override
    public int classifyDataPoint(DataPoint dataPoint) {
        List<LeafValue> features = dataPoint.getFeatures();
        int label = traverseTree(root, features);
        return label;
    }

    private int traverseTree(Node root, List<LeafValue> features) {
        int attributeIndex = root.getIndex();
        int label;
        if (attributeIndex == -1) {
            label = root.getClassID();
        } else if (root.getChildren().size() == 0) {
            label = root.getClassID();
        } else if (root.getChildren().size() == 1) {
            label = root.getChildren().get(0).getClassID();
        } else {
            if (attributes.get(attributeIndex) instanceof DoubleAttribute) {
                DoubleAttribute attr = (DoubleAttribute) attributes.get(attributeIndex);
                DoubleValue value = (DoubleValue) features.get(attributeIndex);
                List<Node> children = root.getChildren();
                List<Double> midPoints = attr.getMidPoints();
                int index = -1;
                for (int i = 0; i < midPoints.size(); i++) {
                    if (value.getValue() < midPoints.get(i)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    label = traverseTree(children.get(midPoints.size()), features);
                } else {
                    label = traverseTree(children.get(index), features);
                }

                /*if (value.getValue() < attr.getMean()) {
                    label = traverseTree(root.getChildren().get(0), features);
                } else {
                    label = traverseTree(root.getChildren().get(1), features);
                }*/
            } else {
                StringValue value = (StringValue) features.get(attributeIndex);
                if (value.getValue().equalsIgnoreCase(root.getChildren().get(0).getAttributeValue())) {
                    label = traverseTree(root.getChildren().get(0), features);
                } else {
                    label = traverseTree(root.getChildren().get(1), features);
                }
            }
        }
        return label;
    }

    public void calculateStatisticsAndResetAttributedUsed() {
        for (int i = 0; i < attributes.size(); i++) {
            SummaryStatistics statistics = new SummaryStatistics();
            for (int j = 0; j < root.getDataPoints().size(); j++) {
                if (attributes.get(i) instanceof DoubleAttribute) {
                    statistics.addValue(((DoubleValue) root.getDataPoints().get(j).getFeatures().get(i)).getValue());
                }
            }
            if (attributes.get(i) instanceof DoubleAttribute) {
                DoubleAttribute attr = (DoubleAttribute) attributes.get(i);
                attr.setMean(statistics.getMin() + ((statistics.getMax() - statistics.getMin()) / 2));
                attr.setMin(statistics.getMin());
                attr.setMax(statistics.getMax());
                List<Double> temp = new ArrayList<>();
                for (int l = 1; l <= k; l++) {
                    double midPoint = attr.getMin() + (l * ((attr.getMax() - attr.getMin()) / (k + 1)));
                    temp.add(midPoint);
                }
                attr.setMidPoints(temp);
            }
            attributes.get(i).setUsed(false);
        }
    }
}

