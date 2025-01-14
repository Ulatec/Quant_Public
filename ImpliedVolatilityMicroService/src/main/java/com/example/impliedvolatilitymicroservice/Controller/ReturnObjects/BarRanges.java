package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import java.util.Date;

public class BarRanges {

    public double high;
    public double low;
    public Date date;

    public BarRanges(){

    }

    public BarRanges(double high, double low, Date date) {
        this.high = high;
        this.low = low;
        this.date = date;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
