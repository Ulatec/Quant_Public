package com.example.impliedvolatilitymicroservice.Range.Utils.Threads;

import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RangeCalculatorThreadMonitor {
    int numberOfThreads;
    int finishedThreads;
    List<RangeSummary> rangeSummaries ;
    int originalListSize;
    HashMap<Integer, List<RangeSummary>> barMap = new HashMap<>();

    boolean safeToKill = false;
    public RangeCalculatorThreadMonitor(int numberOfThreads, int originalListSize){
        this.numberOfThreads = numberOfThreads;
        this.originalListSize = originalListSize;
        rangeSummaries = new ArrayList<>();
        finishedThreads = 0;
    }
    public void run(){
        while(!safeToKill) {
            //DO NOTHING
        }

        System.out.println("ThreadMonitor finished.");
    }

    public synchronized void threadFinished(int threadNum, List<RangeSummary> rangeSummaries){
        this.rangeSummaries.addAll(rangeSummaries);
        barMap.put(threadNum, rangeSummaries);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            System.out.println();
        }
    }

    public synchronized List<RangeSummary> getBackTestResults(){
        if(barMap.entrySet().size()<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return rangeSummaries;
        }
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getFinishedThreads() {
        return finishedThreads;
    }

    public void setFinishedThreads(int finishedThreads) {
        this.finishedThreads = finishedThreads;
    }
}
