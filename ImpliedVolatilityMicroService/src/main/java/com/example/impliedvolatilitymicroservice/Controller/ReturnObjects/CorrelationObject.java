package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CorrelationObject {

    public String requestedTicker;

    public int days;

    public HashMap<Date,Double> dollarCorrelation;

    public Double nasdaqCorrelation;

    public HashMap<Date,Double> treasuryCorrelation;


    public CorrelationObject() {
    }

    public CorrelationObject(String requestedTicker, int days, HashMap<Date,Double> dollarCorrelation, Double nasdaqCorrelation, HashMap<Date,Double> treasuryCorrelation) {
        this.requestedTicker = requestedTicker;
        this.days = days;
        this.dollarCorrelation = dollarCorrelation;
        this.nasdaqCorrelation = nasdaqCorrelation;
        this.treasuryCorrelation = treasuryCorrelation;
    }

    public String getRequestedTicker() {
        return requestedTicker;
    }

    public void setRequestedTicker(String requestedTicker) {
        this.requestedTicker = requestedTicker;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public HashMap<Date, Double> getDollarCorrelation() {
        return dollarCorrelation;
    }

    public void setDollarCorrelation(HashMap<Date, Double> dollarCorrelation) {
        this.dollarCorrelation = dollarCorrelation;
    }

    public Double getNasdaqCorrelation() {
        return nasdaqCorrelation;
    }

    public void setNasdaqCorrelation(Double nasdaqCorrelation) {
        this.nasdaqCorrelation = nasdaqCorrelation;
    }

    public HashMap<Date, Double> getTreasuryCorrelation() {
        return treasuryCorrelation;
    }

    public void setTreasuryCorrelation(HashMap<Date, Double> treasuryCorrelation) {
        this.treasuryCorrelation = treasuryCorrelation;
    }
}
