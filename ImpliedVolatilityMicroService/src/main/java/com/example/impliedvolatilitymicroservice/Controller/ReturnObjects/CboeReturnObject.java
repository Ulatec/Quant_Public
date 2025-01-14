package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import jakarta.persistence.Entity;

import java.util.Date;


public class CboeReturnObject {
    public Date date;
    public double putAverage;
    public double callAverage;
    public double daysToAverage;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPutAverage() {
        return putAverage;
    }

    public void setPutAverage(double putAverage) {
        this.putAverage = putAverage;
    }

    public double getCallAverage() {
        return callAverage;
    }

    public void setCallAverage(double callAverage) {
        this.callAverage = callAverage;
    }

    public double getDaysToAverage() {
        return daysToAverage;
    }

    public void setDaysToAverage(double daysToAverage) {
        this.daysToAverage = daysToAverage;
    }
}
