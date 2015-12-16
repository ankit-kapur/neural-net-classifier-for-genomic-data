package com.dm.classifiers;

import com.dm.data.pojo.DataPoint;

public interface Classifier {
    int classifyDataPoint(DataPoint d);
}
