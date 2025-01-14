package com.example.impliedvolatilitymicroservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

public class WatchlistItem {

    public Long id;
    String Ticker;
    Date dateAdded;

    boolean bullishTrade;
    boolean bullishTrend;

    double close;
    double volumeChange;
    double trade;
    double trend;
    double upside;
    double downside;

    double ivPremium;

    double momentum30;
    double momentum60;

    public String getTicker() {
        return Ticker;
    }

    public void setTicker(String ticker) {
        Ticker = ticker;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isBullishTrade() {
        return bullishTrade;
    }

    public void setBullishTrade(boolean bullishTrade) {
        this.bullishTrade = bullishTrade;
    }

    public boolean isBullishTrend() {
        return bullishTrend;
    }

    public void setBullishTrend(boolean bullishTrend) {
        this.bullishTrend = bullishTrend;
    }

    public double getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(double volumeChange) {
        this.volumeChange = volumeChange;
    }

    public double getTrade() {
        return trade;
    }

    public void setTrade(double trade) {
        this.trade = trade;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
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

    public double getMomentum30() {
        return momentum30;
    }

    public void setMomentum30(double momentum30) {
        this.momentum30 = momentum30;
    }

    public double getMomentum60() {
        return momentum60;
    }

    public void setMomentum60(double momentum60) {
        this.momentum60 = momentum60;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getIvPremium() {
        return ivPremium;
    }

    public void setIvPremium(double ivPremium) {
        this.ivPremium = ivPremium;
    }
}
