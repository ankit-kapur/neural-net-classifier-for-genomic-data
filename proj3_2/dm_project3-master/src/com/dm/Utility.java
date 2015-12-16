package com.dm;

import com.dm.data.pojo.DataPoint;
import com.dm.data.pojo.Node;

import java.util.*;
import java.util.regex.Pattern;

public class Utility {
    public static List<String> DATA_TYPE = new ArrayList<>();
    public static final String TAB = "\t";
    public static final String DOUBLE_REGEX = "\\-*\\d+[\\.\\d]+";
//    public static final String STRING_REGEX = "\\-*\\d+";
    public static final String DOUBLE = "DOUBLE";
    public static final String STRING = "STRING";

    public static final Pattern doublePattern = Pattern.compile(DOUBLE_REGEX);
//    public static final Pattern stringPattern = Pattern.compile(STRING_REGEX);
    public static boolean[] isAttributeUsed;
    public static ArrayList<Integer> globalGroundTruth;


    /**
     * Calculate counts for each class in the data points
     * @param dataPoints the list of data points
     * @return map of class label vs count
     */
    public static Map<Integer, Integer> calculateClassCounts(List<DataPoint> dataPoints) {
        Map<Integer, Integer> classCounts = new HashMap<>();
        for(int i = 0; i < dataPoints.size(); i++) {
            int groundTruth = dataPoints.get(i).getTruth();
            if(classCounts.containsKey(groundTruth)) {
                classCounts.put(groundTruth, classCounts.get(groundTruth) + 1);
            } else {
                classCounts.put(groundTruth, 1);
            }
        }
        return classCounts;
    }

    /*
     * traverse the decision tree
     * @param root root node of the decision tree
     */
    public static void levelOrderTraversal(Node root) {
        Queue<Node> q = new LinkedList<>();
        Node temp;
        q.add(root);
        q.add(null);
        while (!q.isEmpty()) {
            temp = q.poll();
            if (temp == null) {
                if (!q.isEmpty()) {
                    q.add(null);
                }
                System.out.println("--------------------------------------------------------------------------------");
            } else {
                System.out.println("Index : " + temp.getIndex() + "Majority: " + temp.getMajority() + "Class ID: " + temp.getClassID() + "Number of points: " + temp.getDataPoints().size());
                for(int i = 0; i < temp.getChildren().size(); i++) {
                    q.add(temp.getChildren().get(i));
                }
            }
        }
    }
}