package com.dm.data.reader;

import com.dm.Utility;
import com.dm.data.pojo.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class InputParser {

    BufferedReader br;
    static final Logger LOGGER = Logger.getLogger(InputParser.class.getName());
    static final ConsoleHandler handler = new ConsoleHandler();
    private List<Attribute> attributes;

    /**
     * Initialize
     *
     * @param filePath path of the file to be parsed
     */
    public InputParser(String filePath) {
        LOGGER.addHandler(handler);
        try {
            FileReader fr = new FileReader(filePath);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    /**
     * Parse the input file and return dataset
     *
     * @return list of DataPoint
     */
    public List<DataPoint> parseData() {
        String line;
        List<DataPoint> dataSet = new ArrayList<>();
        Utility.globalGroundTruth = new ArrayList<>();
        try {
            line = br.readLine();
            String[] temp = line.split(Utility.TAB);
            getDataTypesForDataPoint(temp);
            dataSet.add(createDataPoint(temp));

            while ((line = br.readLine()) != null) {
                temp = line.split(Utility.TAB);
                dataSet.add(createDataPoint(temp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    private DataPoint createDataPoint(String[] temp) {
        List<LeafValue> data = new ArrayList<>();

        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        for (int i = 0; i < temp.length - 1; i++) {
            switch (Utility.DATA_TYPE.get(i)) {
                case Utility.DOUBLE: {
                    data.add(new DoubleValue(temp[i]));
                    List<Double> tempData;
                    if (attributes.size() == temp.length - 1) {
                        tempData = ((DoubleAttribute) attributes.get(i)).getData();
                    } else {
                        tempData = new ArrayList<>();
                        Attribute tempAttr = new DoubleAttribute();
                        attributes.add(tempAttr);
                    }
                    tempData.add(Double.parseDouble(temp[i]));
                    ((DoubleAttribute) attributes.get(i)).setData(tempData);
                }
                break;
                case Utility.STRING: {
                    data.add(new StringValue(temp[i]));
                    List<String> tempData;
                    if (attributes.size() == temp.length - 1) {
                        tempData = ((StringAttribute) attributes.get(i)).getData();
                    } else {
                        tempData = new ArrayList<>();
                        Attribute tempAttr = new StringAttribute();
                        attributes.add(tempAttr);
                    }
                    tempData.add(temp[i]);
                    ((StringAttribute) attributes.get(i)).setData(tempData);
                }
                break;
            }
        }
        int groundTruth = Integer.parseInt(temp[temp.length - 1]);
        Utility.globalGroundTruth.add(groundTruth);
        return new DataPoint(data, groundTruth);
    }

    /**
     * Parse the line to detect data type for each column
     *
     * @param temp line to be parsed
     */
    private void getDataTypesForDataPoint(String[] temp) {

        for (int i = 0; i < temp.length - 1; i++) {
            if (Utility.doublePattern.matcher(temp[i]).matches()) {
                Utility.DATA_TYPE.add(Utility.DOUBLE);
            } else {
                Utility.DATA_TYPE.add(Utility.STRING);
            }
        }
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<DataPoint> parseTestData() {
        String line;
        List<DataPoint> dataSet = new ArrayList<>();
        try {
            line = br.readLine();
            String[] temp = line.split(Utility.TAB);
            getDataTypesForTestDataPoint(temp);
            dataSet.add(createTestDataPoint(temp));

            while ((line = br.readLine()) != null) {
                temp = line.split(Utility.TAB);
                dataSet.add(createTestDataPoint(temp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    private DataPoint createTestDataPoint(String[] temp) {
        List<LeafValue> data = new ArrayList<>();

        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        for (int i = 0; i < temp.length; i++) {
            switch (Utility.DATA_TYPE.get(i)) {
                case Utility.DOUBLE: {
                    data.add(new DoubleValue(temp[i]));
                    List<Double> tempData;
                    if (attributes.size() == temp.length) {
                        tempData = ((DoubleAttribute) attributes.get(i)).getData();
                    } else {
                        tempData = new ArrayList<>();
                        Attribute tempAttr = new DoubleAttribute();
                        attributes.add(tempAttr);
                    }
                    tempData.add(Double.parseDouble(temp[i]));
                    ((DoubleAttribute) attributes.get(i)).setData(tempData);
                }
                break;
                case Utility.STRING: {
                    data.add(new StringValue(temp[i]));
                    List<String> tempData;
                    if (attributes.size() == temp.length) {
                        tempData = ((StringAttribute) attributes.get(i)).getData();
                    } else {
                        tempData = new ArrayList<>();
                        Attribute tempAttr = new StringAttribute();
                        attributes.add(tempAttr);
                    }
                    tempData.add(temp[i]);
                    ((StringAttribute) attributes.get(i)).setData(tempData);
                }
                break;
            }
        }
        return new DataPoint(data, -1);
    }

    private void getDataTypesForTestDataPoint(String[] temp) {

        for (int i = 0; i < temp.length; i++) {
            if (Utility.doublePattern.matcher(temp[i]).matches()) {
                Utility.DATA_TYPE.add(Utility.DOUBLE);
            } else {
                Utility.DATA_TYPE.add(Utility.STRING);
            }
        }
    }
}

