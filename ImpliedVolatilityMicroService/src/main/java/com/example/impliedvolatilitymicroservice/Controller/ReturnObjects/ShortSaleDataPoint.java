package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import java.util.Date;

public class ShortSaleDataPoint {
    public Date date;
    public double average;

    public ShortSaleDataPoint(Date date, double average) {
        this.date = date;
        this.average = average;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
