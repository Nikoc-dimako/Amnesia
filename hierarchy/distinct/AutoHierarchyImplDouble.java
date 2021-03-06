/* 
 * Copyright (C) 2015 "IMIS-Athena R.C.",
 * Institute for the Management of Information Systems, part of the "Athena" 
 * Research and Innovation Centre in Information, Communication and Knowledge Technologies.
 * [http://www.imis.athena-innovation.gr/]
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 */
package hierarchy.distinct;

import data.Data;
import hierarchy.NodeStats;
import hierarchy.ranges.RangeDouble;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


/**
 * Class for autogenerating double hierarchies
 * @author serafeim
 */
public class AutoHierarchyImplDouble extends HierarchyImplDouble{
    
    //variables needed for autogenerating 
    String attribute = null;
    String sorting = null;
    int fanout = 0;
    Data dataset = null;
    double randomNumber = 0;
    String hierarchyType = null;
    
    //generator for random numbers
    Random gen = new Random();
    
    /**
     * Class constructor
     * @param _name name of the hierarchy
     * @param _nodesType type of hierarchy's nodes
     * @param _hierarchyType type of hierarchy (distinct/range)
     * @param _attribute dataset attribute on which hierarchy will be autogenerated
     * @param _sorting type of sorting in the hierarchy
     * @param _fanout fanout to be used
     * @param _plusMinusFanout window of fanout
     * @param _data dataset loaded
     */
    public AutoHierarchyImplDouble(String _name, String _nodesType, String _hierarchyType, String _attribute, 
                                    String _sorting, int _fanout, Data _data) {
        super(_name, _nodesType);
        attribute = _attribute;
        sorting = _sorting;
        fanout = _fanout;
        dataset = _data;
        this.hierarchyType = _hierarchyType;
    }
    
    /**
     * Automatically generates hierarchy's structures
     */
    @Override
    public void autogenerate() {
        int column = dataset.getColumnByName(attribute);
        Set<Double> itemsSet = new TreeSet<>();
        

//        long start = System.currentTimeMillis();
        
//        long start1 = System.currentTimeMillis();

        for (double[] columnData : dataset.getDataSet()){
          itemsSet.add(columnData[column]);
        }
       
        height = computeHeight(fanout, itemsSet.size());
        int curHeight = height - 1;
//        System.out.println("size: " + itemsSet.size() + " fanout: " + fanout + " height: " + height);

        //build leaf level 
        ArrayList<Double> initList = new ArrayList<>(itemsSet);

        if ( initList.get(initList.size()-1) == 2147483646.0 ||  initList.get(initList.size()-1).isNaN() ){
            randomNumber = initList.get(initList.size()-2);
        }
        else{
            randomNumber = initList.get(initList.size()-1);
        }
        
        
        //apply sorting
        if(sorting.equals("random")){
            Collections.shuffle(initList);
        }
        else if(sorting.equals("alphabetical")){
            Comparator comp = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            };
            Collections.sort(initList, comp);
        }
        
        allParents.put(curHeight, initList);
 
        int count = 0;
        
        //build inner nodes of hierarchy
        while(curHeight > 0){
            
            Double[] prevLevel = allParents.get(curHeight).toArray(new Double[allParents.get(curHeight).size()]);
            int prevLevelIndex = 0;
            
            int curLevelSize = (int)(prevLevel.length / fanout + 1);
            if(fanout > 0){
                curLevelSize = prevLevel.length;
            }

            Double[] curLevel = new Double[curLevelSize];
            int curLevelIndex = 0;
            
            while(prevLevelIndex < prevLevel.length){
                
                Double ran = randomNumber();
                //int curFanout = calculateRandomFanout();
                Double[] tempArray = new Double[fanout];
                
                //assign a parent every #curFanout children
                int j;
                for(j=0; j<fanout && (prevLevelIndex < prevLevel.length); j++){
                    Double ch = prevLevel[prevLevelIndex];
                    prevLevelIndex++;
                    tempArray[j] = ch;
                    parents.put(ch, ran);
                    stats.put(ch, new NodeStats(curHeight));
                    count++;
                }
                
                //array size is not curFanout (elements finished), resize 
                if(j != fanout){
                    tempArray = Arrays.copyOf(tempArray, j);
                }

                children.put(ran, new ArrayList<>(Arrays.asList(tempArray)));

                curLevel[curLevelIndex] = ran;
                curLevelIndex++;
                
            }

            curHeight--;

            //resize if there are less items in level than initial level max prediction
            if(curLevelIndex != curLevelSize){
                curLevel = Arrays.copyOf(curLevel, curLevelIndex);
            }

            allParents.put(curHeight, new ArrayList<>(Arrays.asList(curLevel)));
        }

        //set root element
        root = allParents.get(0).get(0);
        stats.put(root, new NodeStats(0));
        System.out.println("counter " + count);
//        long end = System.currentTimeMillis();
//        System.out.println("Time: " + (end - start));


        /*System.out.println("all Parents");
        for (Map.Entry<Integer,ArrayList<Double>> entry : allParents.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("/////////////////////////////////////");
        
        System.out.println("Stats");
        for (Map.Entry<Double, NodeStats> entry : stats.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("/////////////////////////////////////");
        
        System.out.println("parents");
        for (Map.Entry<Double, Double> entry : parents.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("/////////////////////////////////////");
        
        System.out.println("children");
        for (Map.Entry<Double, List<Double>> entry : children.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("/////////////////////////////////////");*/


    }
    
    /**
     * Computes height of the autogenerated hierarchy
     * @param fanout fanout to be used
     * @param nodes total nodes of leaf level
     * @return height of the autogenerated hierarchy
     */
    private int computeHeight(int fanout, int nodes){
        int answer =  (int)(Math.log((double)nodes) / Math.log((double)fanout) + 1);
        if((Math.log((double)nodes) % Math.log((double)fanout)) != 0){
            answer++;
        }
        return answer;
    }
    
    /**
     * Generates a random double number
     * @return a random double number
     */
    private Double randomNumber(){
        return ++randomNumber;
    }
    
}
