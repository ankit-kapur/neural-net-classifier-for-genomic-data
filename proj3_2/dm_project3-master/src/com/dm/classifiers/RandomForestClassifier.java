package com.dm.classifiers;

import com.dm.data.pojo.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hrishikesh on 12/8/2015.
 */
public class RandomForestClassifier implements Classifier{
    List<C45Classifier> classifiers;

    public RandomForestClassifier(List<C45Classifier> c45Classifiers) {
        classifiers = new ArrayList<>();
        classifiers.addAll(c45Classifiers);
    }
    @Override
    public int classifyDataPoint(DataPoint d) {
        int label = -1;
        int max = Integer.MIN_VALUE;
        Map<Integer, Integer> classCounts = new HashMap<>();
        for(C45Classifier c: classifiers) {
            c.runAlgorithm();
            int temp = c.classifyDataPoint(d);
            if(classCounts.containsKey(temp)) {
                classCounts.put(temp, classCounts.get(temp) + 1);
            } else {
                classCounts.put(temp, 1);
            }
        }
        for (Integer key: classCounts.keySet()) {
            if(classCounts.get(key) > max) {
                max = classCounts.get(key);
                label = key;
            }
        }
        return label;
    }
}
