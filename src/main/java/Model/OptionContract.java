package Model;

import java.time.LocalDate;

public class OptionContract {

    public String ticker;

    public LocalDate expirationDate;

    public String underlyingTicker;

    public double strike;

    public double impliedVolatility;

    public double price;

    public double vwap;

    public double delta;


    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getUnderlyingTicker() {
        return underlyingTicker;
    }

    public void setUnderlyingTicker(String underlyingTicker) {
        this.underlyingTicker = underlyingTicker;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public double getImpliedVolatility() {
        return impliedVolatility;
    }

    public void setImpliedVolatility(double impliedVolatility) {
        this.impliedVolatility = impliedVolatility;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "OptionContract{" +
                "ticker='" + ticker + '\'' +
                ", expirationDate=" + expirationDate +
                ", underlyingTicker='" + underlyingTicker + '\'' +
                ", strike=" + strike +
                ", impliedVolatility=" + impliedVolatility +
                ", price=" + price +
                ", vwap=" + vwap +
                ", delta=" + delta +
                '}';
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }
}
