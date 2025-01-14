package com.example.impliedvolatilitymicroservice.Range.Utils.Model;

import com.example.impliedvolatilitymicroservice.Model.Ticker;
import jakarta.persistence.*;

@Entity
public class RangeSummary implements Comparable<RangeSummary>{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    String tickerString;
    @OneToOne
    Ticker ticker;

    double rangeTop;

    double rangeBot;

    double trend;

    double trade;

    double open;

    double close;

    double change;

    double pctFromTrend;

    double volumeChange;

    double upside;

    double downside;

    double upsideRatio;

    double downsideRatio;

    double volatility;


    double impliedVolatility;

    double ivPremium;

    public double spyCorrelation;

    public double qqqCorrelation;

    public double iwmCorrelation;

    public double momentumChange30;

    public double momentumChange60;

    public RangeSummary() {

    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public double getRangeTop() {
        return rangeTop;
    }

    public void setRangeTop(double rangeTop) {
        this.rangeTop = rangeTop;
    }

    public double getRangeBot() {
        return rangeBot;
    }

    public void setRangeBot(double rangeBot) {
        this.rangeBot = rangeBot;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
    }

    public double getTrade() {
        return trade;
    }

    public void setTrade(double trade) {
        this.trade = trade;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
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

    public double getDownsideRatio() {
        return downsideRatio;
    }

    public void setDownsideRatio(double downsideRatio) {
        this.downsideRatio = downsideRatio;
    }

    @Override
    public String toString() {
        return "RangeSummary{" +
                "ticker=" + ticker +
                ", rangeTop=" + rangeTop +
                ", rangeBot=" + rangeBot +
                ", trend=" + trend +
                ", trade=" + trade +
                ", close=" + close +
                ", upside=" + upside +
                ", downside=" + downside +
                ", upsideRatio=" + upsideRatio +
                ", downsideRatio=" + downsideRatio +
                '}';
    }

    @Override
    public int compareTo(RangeSummary o ) {
        return this.ticker.getTicker().compareTo( o.ticker.getTicker());
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
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

    public double getPctFromTrend() {
        return pctFromTrend;
    }

    public void setPctFromTrend(double pctFromTrend) {
        this.pctFromTrend = pctFromTrend;
    }

    public double getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(double volumeChange) {
        this.volumeChange = volumeChange;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getMomentumChange30() {
        return momentumChange30;
    }

    public void setMomentumChange30(double momentumChange30) {
        this.momentumChange30 = momentumChange30;
    }

    public double getMomentumChange60() {
        return momentumChange60;
    }

    public void setMomentumChange60(double momentumChange60) {
        this.momentumChange60 = momentumChange60;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTickerString() {
        return tickerString;
    }

    public void setTickerString(String tickerString) {
        this.tickerString = tickerString;
    }

    public double getImpliedVolatility() {
        return impliedVolatility;
    }

    public void setImpliedVolatility(double impliedVolatility) {
        this.impliedVolatility = impliedVolatility;
    }

    public double getIvPremium() {
        return ivPremium;
    }

    public void setIvPremium(double ivPremium) {
        this.ivPremium = ivPremium;
    }
}
