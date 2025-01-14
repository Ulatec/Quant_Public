package com.example.impliedvolatilitymicroservice.AggregateOptions.Model;

import com.example.impliedvolatilitymicroservice.Model.Ticker;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class AggregateOptionDataPoint {


    private String ticker;

//    @ManyToOne
//    private Ticker tickerObject;

    private Date date;

    private long totalCallVolume;

    private long totalPutVolume;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    public AggregateOptionDataPoint() {

    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

//    public Ticker getTickerObject() {
//        return tickerObject;
//    }
//
//    public void setTickerObject(Ticker tickerObject) {
//        this.tickerObject = tickerObject;
//    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTotalCallVolume() {
        return totalCallVolume;
    }

    public void setTotalCallVolume(long totalCallVolume) {
        this.totalCallVolume = totalCallVolume;
    }

    public long getTotalPutVolume() {
        return totalPutVolume;
    }

    public void setTotalPutVolume(long totalPutVolume) {
        this.totalPutVolume = totalPutVolume;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
