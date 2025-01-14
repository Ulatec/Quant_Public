package Threads;

import Model.Bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class IVSolverMonitor{

    int numberOfThreads;
    int finishedThreads;
    List<Bar> barList ;
    List<IVSolverThread> threads;
    int originalListSize;

    HashMap<Integer, List<Bar>> barMap = new HashMap<>();

    boolean safeToKill = false;
    public IVSolverMonitor(int numberOfThreads, int originalListSize){
        this.numberOfThreads = numberOfThreads;
        this.originalListSize = originalListSize;
        barList = new ArrayList<>();
        finishedThreads = 0;
        threads = new ArrayList<>();
    }
    public void run(){
        while(!safeToKill) {
            //DO NOTHING
        }

        System.out.println("ThreadMonitor finished.");
    }

    public synchronized void threadFinished(int threadNum, List<Bar> barList){
        this.barList.addAll(barList);
        barMap.put(threadNum, barList);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            System.out.println();
        }
    }

    public synchronized List<Bar> getBackTestResults(){
        if(barMap.entrySet().size()<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return barList;
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

    public List<Bar> getBarList() {
        return barList;
    }

    public void setBarList(List<Bar> barList) {
        this.barList = barList;
    }

    public List<IVSolverThread> getThreads() {
        return threads;
    }

    public void setThreads(List<IVSolverThread> threads) {
        this.threads = threads;
    }

    public int getOriginalListSize() {
        return originalListSize;
    }

    public void setOriginalListSize(int originalListSize) {
        this.originalListSize = originalListSize;
    }

    public HashMap<Integer, List<Bar>> getBarMap() {
        return barMap;
    }

    public void setBarMap(HashMap<Integer, List<Bar>> barMap) {
        this.barMap = barMap;
    }

    public boolean isSafeToKill() {
        return safeToKill;
    }

    public void setSafeToKill(boolean safeToKill) {
        this.safeToKill = safeToKill;
    }
}
