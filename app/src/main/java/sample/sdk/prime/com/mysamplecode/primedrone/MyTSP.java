package sample.sdk.prime.com.mysamplecode.primedrone;

import android.util.Log;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ngtims-01 on 2017-04-27.
 */

class MatrixIndexPair {
    public double[][] matrix;
    public int[] indexMap;

    public MatrixIndexPair(double[][] mat, int[] index) {
        matrix = mat;
        indexMap = index;
    }
}

public class MyTSP {

    private static MatrixIndexPair getMatrix(ArrayList<trapData> datas) {

        int length = datas.size();
        double[][] matrix = new double[length][length];
        int[] indexMap = new int[length];
        for(int i=0;i<length;i++){
            trapData dataA = datas.get(i);
            indexMap[i] = dataA.getid();
            for(int j=0;j<length;j++){
                if(i==j){
                    matrix[i][j] = Double.MAX_VALUE;
                    continue;
                }
                trapData dataB = datas.get(j);


                double distance = Math.pow(Math.pow(dataA.getLatitude() - dataB.getLatitude(), 2) + Math.pow(dataA.getLongitude() - dataB.getLongitude(),2),0.5);
                Log.e("distance","distance " + i +" to "+j +" : "+ distance);
                matrix[i][j] = distance;
            }
        }
        Log.e("getMatrix","id : "+datas.get(0).getid());
        return new MatrixIndexPair(matrix, indexMap);
    }

    public static ArrayList<Integer> TSP(ArrayList<trapData> datas) {
        MatrixIndexPair pair = getMatrix(datas);
        double[][] matrix = pair.matrix;
        int[] indexMap = pair.indexMap;

        ArrayList<Integer> visit = new ArrayList<>();
        visit.add(0);
        ArrayList<Integer> nodes = new ArrayList<>();
        for(int i = 1; i < indexMap.length; i++) {
            nodes.add(i);
        }

        while(!nodes.isEmpty()) {
            int current = visit.get(visit.size() - 1);
            int next = 0;
            double min = Double.MAX_VALUE;
            for(int i = 0; i < nodes.size(); i++) {
                int now = nodes.get(i);
                if(min > matrix[current][now]) {
                    next = now;
                    min = matrix[current][now];
                }
            }

            visit.add(next);
            nodes.remove((Integer)next);

            String s = "visit: ";
            for (int a : visit)
                s += a + " ";
            s += ", nodes: ";
            for (int a : nodes)
                s += a + " ";
            Log.e("test", s);
        }



        ArrayList<Integer> tspList = new ArrayList<Integer>();
/*
        int[] visited = new int[indexMap.length];
        visited[0] = 1;

        double minCost = Double.MAX_VALUE;

        int current = 0;
        int dst = 0;
        int count = 0;
        tspList.add(0);
        while(count < indexMap.length) {
            for (int i = 0; i < indexMap.length; i++) {
                if (matrix[current][i] < minCost && visited[current] == 0) {
                    minCost = matrix[current][i];
                    dst = i;
                    visited[i] = 1;
                }
            }
            current = dst;
            tspList.add(current);
            count++;
        }*/




        //Log.e("TSP","tsplist[0] : "+tspList.get(0).toString());
        return visit;
    }
    public static String returnList(ArrayList<Integer> path){
        String list = "";
        for(int i=0;i<path.size();i++){
            list+=path.get(i).toString()+",";

        }
        Log.e("TSP","tsplist : "+list);
        return list;
    }
}
