package com.dm.data.pojo;

public class DoubleValue implements LeafValue{
    Double data;

    public DoubleValue(String data) {
        this.data = Double.parseDouble(data);
    }

    public void setValue(Double data) {
        this.data = data;
    }

    public Double getValue() {
        return data;
    }
}
