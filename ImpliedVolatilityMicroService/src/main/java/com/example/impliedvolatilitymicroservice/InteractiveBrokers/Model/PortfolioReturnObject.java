package com.example.impliedvolatilitymicroservice.InteractiveBrokers.Model;

import java.util.List;

public class PortfolioReturnObject {

    public double totalValue;

    public double totalVol;

    public double percentAlert;

    public double dollarPercentAlert;

    public double spyCorrelation;

    public double qqqCorrelation;

    public double iwmCorrelation;

    public List<Position> positionList;

    public String id;

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Position> positionList) {
        this.positionList = positionList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalVol() {
        return totalVol;
    }

    public void setTotalVol(double totalVol) {
        this.totalVol = totalVol;
    }

    public double getPercentAlert() {
        return percentAlert;
    }

    public void setPercentAlert(double percentAlert) {
        this.percentAlert = percentAlert;
    }

    public double getSpyCorrelation() {
        return spyCorrelation;
    }

    public void setSpyCorrelation(double spyCorrelation) {
        this.spyCorrelation = spyCorrelation;
    }

    public double getQqqCorrelation() {
        return qqqCorrelation;
    }

    public void setQqqCorrelation(double qqqCorrelation) {
        this.qqqCorrelation = qqqCorrelation;
    }

    public double getIwmCorrelation() {
        return iwmCorrelation;
    }

    public void setIwmCorrelation(double iwmCorrelation) {
        this.iwmCorrelation = iwmCorrelation;
    }

    public double getDollarPercentAlert() {
        return dollarPercentAlert;
    }

    public void setDollarPercentAlert(double dollarPercentAlert) {
        this.dollarPercentAlert = dollarPercentAlert;
    }
}
