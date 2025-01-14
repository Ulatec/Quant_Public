package com.example.impliedvolatilitymicroservice.IndexData.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class TreasuryRate {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public Date date;

    public double tBillRate;

    public Date dataDate;


    public TreasuryRate() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double gettBillRate() {
        return tBillRate;
    }

    public void settBillRate(double tBillRate) {
        this.tBillRate = tBillRate;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }
}
