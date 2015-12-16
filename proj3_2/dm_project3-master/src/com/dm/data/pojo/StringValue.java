package com.dm.data.pojo;

public class StringValue implements LeafValue{
    String data;

    public StringValue(String data) {
        this.data = data;
    }

    public void setValue(String data) {
        this.data = data;
    }

    public String getValue() {
        return data;
    }
}
