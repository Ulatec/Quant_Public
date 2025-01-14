package com.example.frontendlistener.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Ticker {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    String ticker;

    String name;

    String type;

    String market;

    String cik;
    public double thirtyDayDollarVolume;

    public Ticker() {

    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }


    public double getThirtyDayDollarVolume() {
        return thirtyDayDollarVolume;
    }

    public void setThirtyDayDollarVolume(double thirtyDayDollarVolume) {
        this.thirtyDayDollarVolume = thirtyDayDollarVolume;
    }
    @Override
    public String toString() {
        return "Ticker{" +
                "id=" + id +
                ", ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", market='" + market + '\'' +
                ", cik='" + cik + '\'' +
                '}';
    }
}
