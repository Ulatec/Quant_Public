package com.example.impliedvolatilitymicroservice.MarketPositioningData.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class FinraShortSaleDataPoint {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public Date date;
    public String ticker;
    public Long volume;

    public FinraShortSaleDataPoint() {
    }

    public FinraShortSaleDataPoint(Date date, String ticker, Long volume) {
        this.date = date;
        this.ticker = ticker;
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
