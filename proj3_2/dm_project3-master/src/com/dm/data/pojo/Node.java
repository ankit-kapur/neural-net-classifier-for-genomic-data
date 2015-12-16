package com.dm.data.pojo;

import com.dm.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {
    private List<DataPoint> dataPoints;
    private int index;
    private List<Node> children;

    private int classID;
    private double maxMajority;
    private Map<Integer, Integer> classCounts;
    private String attributeValue;

    public Node(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        this.index = -1;
        this.classID = 0;
        this.children = new ArrayList<>();
        this.maxMajority = Double.MIN_VALUE;
        this.classCounts = Utility.calculateClassCounts(dataPoints);
        calculateMajority(classCounts, dataPoints.size());
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    private void calculateMajority(Map<Integer, Integer> classCounts, double size) {
        double threshold = 60.0;
        for(Integer key: classCounts.keySet()) {
            int value = classCounts.get(key);
            double temp = (value / size) * 100;
            if(temp > threshold && temp > maxMajority) {
                classID = key;
                maxMajority = temp;
                break;
            }
        }
    }

    public Map<Integer, Integer> getClassCounts() {
        return classCounts;
    }

    public void setClassCounts(Map<Integer, Integer> classCounts) {
        this.classCounts = classCounts;
    }

    public double getMajority() {
        return this.maxMajority;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
