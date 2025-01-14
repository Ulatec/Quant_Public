package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import java.util.Date;

public class VolatilityReturnObject{
    public double impliedVolatility;
    public double realizedVolatility;
    public double fifteenDayRealizedVolatility;

    public double fifteenDayImpliedVolatility;
    public double fifteenPremium;
    public double ivPremium;
    public Date date;

    public VolatilityReturnObject(){}
    public VolatilityReturnObject(double impliedVolatility, double realizedVolatility, double fifteenDayImpliedVolatility, double fifteenDayRealizedVolatility, Date date) {
        this.impliedVolatility = impliedVolatility;
        this.realizedVolatility = realizedVolatility;
        this.date = date;
        this.ivPremium = (impliedVolatility-realizedVolatility)/realizedVolatility;
        this.fifteenDayImpliedVolatility = fifteenDayImpliedVolatility;
        this.fifteenDayRealizedVolatility = fifteenDayRealizedVolatility;
        this.fifteenPremium = (fifteenDayImpliedVolatility-fifteenDayRealizedVolatility)/fifteenDayRealizedVolatility;

    }

    public double getImpliedVolatility() {
        return impliedVolatility;
    }

    public void setImpliedVolatility(double impliedVolatility) {
        this.impliedVolatility = impliedVolatility;
    }

    public double getRealizedVolatility() {
        return realizedVolatility;
    }

    public void setRealizedVolatility(double realizedVolatility) {
        this.realizedVolatility = realizedVolatility;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getIvPremium() {
        return ivPremium;
    }

    public void setIvPremium(double ivPremium) {
        this.ivPremium = ivPremium;
    }

    public double getFifteenDayRealizedVolatility() {
        return fifteenDayRealizedVolatility;
    }

    public void setFifteenDayRealizedVolatility(double fifteenDayRealizedVolatility) {
        this.fifteenDayRealizedVolatility = fifteenDayRealizedVolatility;
    }

    public double getFifteenDayImpliedVolatility() {
        return fifteenDayImpliedVolatility;
    }

    public void setFifteenDayImpliedVolatility(double fifteenDayImpliedVolatility) {
        this.fifteenDayImpliedVolatility = fifteenDayImpliedVolatility;
    }
}
