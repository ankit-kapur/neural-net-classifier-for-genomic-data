package com.dm.classifiers;

import com.dm.data.pojo.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import javax.xml.crypto.Data;
import java.util.List;

public class NaiveBayesClassifier implements Classifier{

    static double classPriorProbForOne;
    static double classPriorProbForZero;
    static List<DataPoint> dataset;
    static List<Attribute> attributes;
    public NaiveBayesClassifier(List<DataPoint> dataset, List<Attribute> attributes){
        this.dataset = dataset;
        this.attributes = attributes;
        double count = 0;
        for (DataPoint d : dataset){
            if(d.getTruth() == 1){
                count++;
            }
        }
        classPriorProbForOne = count/dataset.size();
        classPriorProbForZero = 1 - classPriorProbForOne;
        //System.out.println("Prior Prob of 1" + classPriorProbForOne);
        //System.out.println("Prior prob of 0" + classPriorProbForZero);
    }
    //TODO
    public static double calculateDescriptorPostProb(DataPoint dataPoint, int hypothesis){
        double descriptorPostProb = 1;
        for(int i=0; i<attributes.size(); i++) {
            Attribute a = attributes.get(i);
            LeafValue l = dataPoint.getFeatures().get(i);
            if (a instanceof StringAttribute) {
                descriptorPostProb = descriptorPostProb * calculateProbabilityForAttribute(i, (StringValue)l ,hypothesis);
            }else if (a instanceof DoubleAttribute){
                descriptorPostProb = descriptorPostProb * calculateProbabilityForAttribute(i, (DoubleValue)l, hypothesis);
            }
        }
        return descriptorPostProb;
    }


    private static double calculateProbabilityForAttribute(int index, StringValue l,int hypothesis) {
        String value = l.getValue();
        double count = 0.0;
        for (DataPoint d : dataset){
            if (d.getTruth() == hypothesis){
                StringValue stringValue = (StringValue) d.getFeatures().get(index);
                if(value.equals(stringValue.getValue())){
                    count = count + 1;
                }
            }
        }
        return (count+1)/(dataset.size());
    }

    private static double calculateProbabilityForAttribute(int index, DoubleValue l, int hypothesis) {
        SummaryStatistics stats = new SummaryStatistics();
        for (DataPoint d : dataset){
            if (d.getTruth() == hypothesis){
                stats.addValue(((DoubleValue)(d.getFeatures().get(index))).getValue());
            }
        }
        NormalDistribution nd = new NormalDistribution(stats.getMean(),  stats.getStandardDeviation());
        double probability = nd.density(l.getValue());
        return  probability;
    }

    @Override
    public int classifyDataPoint(DataPoint d){
        if(classPriorProbForOne * calculateDescriptorPostProb(d, 1) > classPriorProbForZero * calculateDescriptorPostProb(d, 0)){
            return 1;
        }
        else
            return 0;
    }
}