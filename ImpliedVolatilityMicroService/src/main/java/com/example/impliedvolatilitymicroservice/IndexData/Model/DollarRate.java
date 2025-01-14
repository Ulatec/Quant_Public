package com.example.impliedvolatilitymicroservice.IndexData.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class DollarRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public Date date;

    public double dollarRate;

    public Date dataDate;


    public DollarRate() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getDollarRate() {
        return dollarRate;
    }

    public void setDollarRate(double dollarRate) {
        this.dollarRate = dollarRate;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }
}
