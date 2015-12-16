package com.dm.data.pojo;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;

public class DoubleAttribute extends Attribute {
    private Double min;
    private Double max;
    private Double mean;
    private Double standardDeviation;
    private List<Double> data;
    private List<Double> midPoints;
    SummaryStatistics statistics;

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public double getMean() {
        return mean;
    }

    public Double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public List<Double> getMidPoints() {
        return midPoints;
    }

    public void setMidPoints(List<Double> midPoints) {
        this.midPoints = midPoints;
    }
}
