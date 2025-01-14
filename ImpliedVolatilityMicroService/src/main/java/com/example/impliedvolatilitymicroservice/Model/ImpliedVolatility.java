package com.example.impliedvolatilitymicroservice.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class ImpliedVolatility {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    public Date date;
    public Date dateAdded;
    public int daysVol;

    public double calculatedVolatility;

    public String ticker;


    public ImpliedVolatility() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDaysVol() {
        return daysVol;
    }

    public void setDaysVol(int daysVol) {
        this.daysVol = daysVol;
    }

    public double getCalculatedVolatility() {
        return calculatedVolatility;
    }

    public void setCalculatedVolatility(double calculatedVolatility) {
        this.calculatedVolatility = calculatedVolatility;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
