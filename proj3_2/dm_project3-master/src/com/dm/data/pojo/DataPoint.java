package com.dm.data.pojo;

import java.util.ArrayList;
import java.util.List;

public class DataPoint {
    private List<LeafValue> features;
    private int truth;

    public DataPoint(List<LeafValue> features, int truth) {
        this.features = features;
        this.truth = truth;
    }


    public List<LeafValue> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<LeafValue> dataPoint) {
        this.features = dataPoint;
    }

    public int getTruth() {
        return truth;
    }

    public void setTruth(int truth) {
        this.truth = truth;
    }
}
