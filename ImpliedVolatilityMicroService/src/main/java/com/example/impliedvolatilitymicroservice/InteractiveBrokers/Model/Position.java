package com.example.impliedvolatilitymicroservice.InteractiveBrokers.Model;

import com.example.impliedvolatilitymicroservice.Model.Ticker;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;

public class Position {

    public Ticker ticker;
    public String symbol;

    public double dollarAmount;

    public double shares;

    public double marketPrice;

    public double percentFromTrend;

    public double volatility;
    public double positionVol;

    public double upside;

    public double downside;
    public RangeSummary rangeSummary;

    public boolean alert;

    public String alertComment;

    public double upsideRatio;
    public double spyCorrelation;

    public double qqqCorrelation;

    public double iwmCorrelation;

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public double getDollarAmount() {
        return dollarAmount;
    }

    public void setDollarAmount(double dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    public double getShares() {
        return shares;
    }

    public void setShares(double shares) {
        this.shares = shares;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public RangeSummary getRangeSummary() {
        return rangeSummary;
    }

    public void setRangeSummary(RangeSummary rangeSummary) {
        this.rangeSummary = rangeSummary;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPercentFromTrend() {
        return percentFromTrend;
    }

    public void setPercentFromTrend(double percentFromTrend) {
        this.percentFromTrend = percentFromTrend;
    }

    public double getPositionVol() {
        return positionVol;
    }

    public void setPositionVol(double positionVol) {
        this.positionVol = positionVol;
    }

    public String getAlertComment() {
        return alertComment;
    }

    public void setAlertComment(String alertComment) {
        this.alertComment = alertComment;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getUpside() {
        return upside;
    }

    public void setUpside(double upside) {
        this.upside = upside;
    }

    public double getDownside() {
        return downside;
    }

    public void setDownside(double downside) {
        this.downside = downside;
    }

    public double getUpsideRatio() {
        return upsideRatio;
    }

    public void setUpsideRatio(double upsideRatio) {
        this.upsideRatio = upsideRatio;
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
}
