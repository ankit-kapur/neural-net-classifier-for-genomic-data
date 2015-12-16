package com.dm.data.pojo;

public abstract class Attribute {
    private boolean isUsed;
    private int indexInDataPoint;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public int getIndexInDataPoint() {
        return indexInDataPoint;
    }

    public void setIndexInDataPoint(int indexInDataPoint) {
        this.indexInDataPoint = indexInDataPoint;
    }
}
