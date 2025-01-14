package com.example.frontendlistener.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class DatabaseBar {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date date;
    private String ticker;
    private double low;

    private double high;

    private double close;

    private double open;

    private double wma;

    private double vwap;

    private double splitAdjustFactor;
    public Double treasuryRate;
    public double vixValue;
    private double volume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getWma() {
        return wma;
    }

    public void setWma(double wma) {
        this.wma = wma;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getSplitAdjustFactor() {
        return splitAdjustFactor;
    }

    public void setSplitAdjustFactor(double splitAdjustFactor) {
        this.splitAdjustFactor = splitAdjustFactor;
    }

    public Double getTreasuryRate() {
        return treasuryRate;
    }

    public void setTreasuryRate(Double treasuryRate) {
        this.treasuryRate = treasuryRate;
    }

    public double getVixValue() {
        return vixValue;
    }

    public void setVixValue(double vixValue) {
        this.vixValue = vixValue;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
