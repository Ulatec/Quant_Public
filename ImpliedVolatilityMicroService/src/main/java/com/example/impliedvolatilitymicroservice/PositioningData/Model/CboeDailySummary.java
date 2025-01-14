package com.example.impliedvolatilitymicroservice.PositioningData.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class CboeDailySummary {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public Date date;
    public long equityOptionsPuts;
    public long equityOptionsCalls;
    public long equityOptionsPutsOpenInterest;
    public long equityOptionsCallsOpenInterest;

    public CboeDailySummary(){

    }
    public CboeDailySummary(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getEquityOptionsPuts() {
        return equityOptionsPuts;
    }

    public void setEquityOptionsPuts(long equityOptionsPuts) {
        this.equityOptionsPuts = equityOptionsPuts;
    }

    public long getEquityOptionsCalls() {
        return equityOptionsCalls;
    }

    public void setEquityOptionsCalls(long equityOptionsCalls) {
        this.equityOptionsCalls = equityOptionsCalls;
    }

    public long getEquityOptionsPutsOpenInterest() {
        return equityOptionsPutsOpenInterest;
    }

    public void setEquityOptionsPutsOpenInterest(long equityOptionsPutsOpenInterest) {
        this.equityOptionsPutsOpenInterest = equityOptionsPutsOpenInterest;
    }

    public long getEquityOptionsCallsOpenInterest() {
        return equityOptionsCallsOpenInterest;
    }

    public void setEquityOptionsCallsOpenInterest(long equityOptionsCallsOpenInterest) {
        this.equityOptionsCallsOpenInterest = equityOptionsCallsOpenInterest;
    }
}
