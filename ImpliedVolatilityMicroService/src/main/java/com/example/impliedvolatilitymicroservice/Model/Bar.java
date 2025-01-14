package com.example.impliedvolatilitymicroservice.Model;

import java.util.Date;

public class Bar implements Cloneable{

    private Date date;

    private double low;

    private double high;

    private double close;

    private double optionsClose;

    private double open;

    private double wma;

    private double vwap;

    private double slope;

    private double minDiff;

    private double maxDiff;

    private double stdDev;

    private double trueRange;

    private double averageTrueRange;

    private double hurst;

    private double bb_bottom;

    private double bb_top;

    private double rma;

    private double trendVol;

    private double tradeVol;
    private double trend;

    private double volume;

    private double trendWMA;

    private double trade;

    private double put_implied_vol;

    private int movingTrendLength;

    private int movingTradeLength;

    private int trendVolCalcDays;

    private double splitAdjustFactor;

    private double volumeCorrelationFactor;
    private double tradeVolumeCorrelationFactor;
    private double discountCorrelationFactor;
    private double tradeDiscountCorrelationFactor;
    private double impliedVolCorrelationFactor;
    private double tradeImpliedVolCorrelationFactor;
    private double dollarCorrelationFactor;

    private double tradeDollarCorrelationFactor;
    private double realizedVolCorrelationFactor;
    private double tradeRealizedVolCorrelationFactor;
    private double treasuryCorrelationFactor;
    private double tradeTreasuryCorrelationFactor;
    private double volZscore;
    private double ivDiscount;

    private double tradeIvDiscount;

    public int volatilityBucket;

    public int economicQuadrant;
    public double commodityRateOfChange;

    public double commodityCorrelationFactor;
    public double tradeCommodityCorrelationFactor;
    public double commodityIndexValue;

    public double fedTotalAssetsRateOfChange;

    public double fedTotalAssetsCorrelationFactor;
    public double tradeFedTotalAssetsCorrelationFactor;

    public double fredLoansCorrelationFactor;
    public double tradeFredLoansCorrelationFactor;
    public double fedTotalAssetsValue;

    public double revenueRateOfChange;

    public double revenueRateOfChangeCorrelationFactor;

    public double incomeRateOfChange;

    public double incomeRateOfChangeCorrelationFactor;
    public Double treasuryRate;

    public int lookback;

    public double baseVolatility;

    public double nasdaqVol;

    public Date revenueDate;

    public Date oldRevenueDate;

    public double oilValue;
    public double oilCorrelationFactor;
    public double tradeOilCorrelationFactor;
    public double goldCorrelationFactor;
    public double tradeGoldCorrelationFactor;

    public double gasolineCorrelationFactor;

    public double tradeGasolineCorrelationFactor;

    public double vixValue;
    public double vixCorrelationFactor;
    public double tradeVixCorrelationFactor;

    public double discountWeighting;

    public double volumeWeighting;
    public double impliedVolatilityWeighting;
    public double realizedVolatilityWeighting;

    public double treasuryWeighting;
    public double secondDerivativeVolChange;

    public double wmaAdjustment;

    public double historicVolChange;
    public double tradeHistoricVolChange;
    public double fredCommericialLoanYearOverYearChange;

    public double autoLoansValue;

    public double autoLoansCorrelationFactor;
    public double tradeAutoLoansCorrelationFactor;

    public double yieldCurveValue;

    public double yieldCurveAdjustment;

    public double yieldCurveCorrelationFactor;

    public double tradeYieldCurveCorrelationFactor;

    public double discountToVolChangeCorrelation;

    public double tradeDiscountToVolChangeCorrelation;

    public double highBetaCorrelationFactor;
    public double lowBetaCorrelationFactor;
    public double tradeLowBetaCorrelationFactor;
    public double tradeHighBetaCorrelationFactor;

    public double upsideRatio;

    public double dollarValue;

    public double dailyShortSaleVolume;

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



    @Override
    public String toString() {
        return "Bar{" +
                "date=" + date +
                ", low=" + low +
                ", high=" + high +
                ", close=" + close +
                ", open=" + open +
                ", wma=" + wma +
                '}';
    }

    public double getWma() {
        return wma;
    }

    public void setWma(double wma) {
        this.wma = wma;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getMinDiff() {
        return minDiff;
    }

    public void setMinDiff(double minDiff) {
        this.minDiff = minDiff;
    }

    public double getMaxDiff() {
        return maxDiff;
    }

    public void setMaxDiff(double maxDiff) {
        this.maxDiff = maxDiff;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getAverageTrueRange() {
        return averageTrueRange;
    }

    public void setAverageTrueRange(double averageTrueRange) {
        this.averageTrueRange = averageTrueRange;
    }

    public double getHurst() {
        return hurst;
    }

    public void setHurst(double hurst) {
        this.hurst = hurst;
    }

    public double getBb_bottom() {
        return bb_bottom;
    }

    public void setBb_bottom(double bb_bottom) {
        this.bb_bottom = bb_bottom;
    }

    public double getBb_top() {
        return bb_top;
    }

    public void setBb_top(double bb_top) {
        this.bb_top = bb_top;
    }

    public double getRma() {
        return rma;
    }

    public void setRma(double rma) {
        this.rma = rma;
    }

    public double getTrendVol() {
        return trendVol;
    }

    public void setTrendVol(double realVol) {
        this.trendVol = realVol;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
    }


    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTrendWMA() {
        return trendWMA;
    }

    public void setTrendWMA(double trendWMA) {
        this.trendWMA = trendWMA;
    }

    public double getTrade() {
        return trade;
    }

    public void setTrade(double trade) {
        this.trade = trade;
    }

    public double getTradeVol() {
        return tradeVol;
    }

    public void setTradeVol(double tradeVol) {
        this.tradeVol = tradeVol;
    }

    public double getPut_implied_vol() {
        return put_implied_vol;
    }

    public void setPut_implied_vol(double put_implied_vol) {
        this.put_implied_vol = put_implied_vol;
    }

    public int getMovingTrendLength() {
        return movingTrendLength;
    }

    public void setMovingTrendLength(int movingTrendLength) {
        this.movingTrendLength = movingTrendLength;
    }

    public int getMovingTradeLength() {
        return movingTradeLength;
    }

    public void setMovingTradeLength(int movingTradeLength) {
        this.movingTradeLength = movingTradeLength;
    }

    public int getTrendVolCalcDays() {
        return trendVolCalcDays;
    }

    public void setTrendVolCalcDays(int trendVolCalcDays) {
        this.trendVolCalcDays = trendVolCalcDays;
    }

    public double getSplitAdjustFactor() {
        return splitAdjustFactor;
    }

    public void setSplitAdjustFactor(double splitAdjustFactor) {
        this.splitAdjustFactor = splitAdjustFactor;
    }

    public double getIvDiscount() {
        return ivDiscount;
    }

    public void setIvDiscount(double ivDiscount) {
        this.ivDiscount = ivDiscount;
    }

    public double getTradeIvDiscount() {
        return tradeIvDiscount;
    }

    public void setTradeIvDiscount(double tradeIvDiscount) {
        this.tradeIvDiscount = tradeIvDiscount;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        Bar clone = null;
        try  {
            clone = (Bar) super.clone();
            //Copy new date object to cloned method
//            clone.setDob((Date) this.getDob().clone());
        }
        catch (CloneNotSupportedException e)  {
            throw new RuntimeException(e);
        }
        return clone;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getVolumeCorrelationFactor() {
        return volumeCorrelationFactor;
    }

    public void setVolumeCorrelationFactor(double volumeCorrelationFactor) {
        this.volumeCorrelationFactor = volumeCorrelationFactor;
    }

    public double getDiscountCorrelationFactor() {
        return discountCorrelationFactor;
    }

    public void setDiscountCorrelationFactor(double discountCorrelationFactor) {
        this.discountCorrelationFactor = discountCorrelationFactor;
    }

    public double getImpliedVolCorrelationFactor() {
        return impliedVolCorrelationFactor;
    }

    public void setImpliedVolCorrelationFactor(double impliedVolCorrelationFactor) {
        this.impliedVolCorrelationFactor = impliedVolCorrelationFactor;
    }

    public double getDollarCorrelationFactor() {
        return dollarCorrelationFactor;
    }

    public void setDollarCorrelationFactor(double dollarCorrelationFactor) {
        this.dollarCorrelationFactor = dollarCorrelationFactor;
    }

    public double getRealizedVolCorrelationFactor() {
        return realizedVolCorrelationFactor;
    }

    public void setRealizedVolCorrelationFactor(double realizedVolCorrelationFactor) {
        this.realizedVolCorrelationFactor = realizedVolCorrelationFactor;
    }

    public int getVolatilityBucket() {
        return volatilityBucket;
    }

    public void setVolatilityBucket(int volatilityBucket) {
        this.volatilityBucket = volatilityBucket;
    }

    public double getVolZscore() {
        return volZscore;
    }

    public void setVolZscore(double volZscore) {
        this.volZscore = volZscore;
    }

    public int getEconomicQuadrant() {
        return economicQuadrant;
    }

    public void setEconomicQuadrant(int economicQuadrant) {
        this.economicQuadrant = economicQuadrant;
    }

    public Double getTreasuryRate() {
        return treasuryRate;
    }

    public void setTreasuryRate(Double treasuryRate) {
        this.treasuryRate = treasuryRate;
    }

    public double getTreasuryCorrelationFactor() {
        return treasuryCorrelationFactor;
    }

    public void setTreasuryCorrelationFactor(double treasuryCorrelationFactor) {
        this.treasuryCorrelationFactor = treasuryCorrelationFactor;
    }

    public double getCommodityRateOfChange() {
        return commodityRateOfChange;
    }

    public void setCommodityRateOfChange(double commodityRateOfChange) {
        this.commodityRateOfChange = commodityRateOfChange;
    }

    public double getCommodityCorrelationFactor() {
        return commodityCorrelationFactor;
    }

    public void setCommodityCorrelationFactor(double commodityCorrelationFactor) {
        this.commodityCorrelationFactor = commodityCorrelationFactor;
    }

    public double getCommodityIndexValue() {
        return commodityIndexValue;
    }

    public void setCommodityIndexValue(double commodityIndexValue) {
        this.commodityIndexValue = commodityIndexValue;
    }

    public double getFedTotalAssetsRateOfChange() {
        return fedTotalAssetsRateOfChange;
    }

    public void setFedTotalAssetsRateOfChange(double fedTotalAssetsRateOfChange) {
        this.fedTotalAssetsRateOfChange = fedTotalAssetsRateOfChange;
    }

    public double getFedTotalAssetsCorrelationFactor() {
        return fedTotalAssetsCorrelationFactor;
    }

    public void setFedTotalAssetsCorrelationFactor(double fedTotalAssetsCorrelationFactor) {
        this.fedTotalAssetsCorrelationFactor = fedTotalAssetsCorrelationFactor;
    }

    public double getFedTotalAssetsValue() {
        return fedTotalAssetsValue;
    }

    public void setFedTotalAssetsValue(double fedTotalAssetsValue) {
        this.fedTotalAssetsValue = fedTotalAssetsValue;
    }

    public int getLookback() {
        return lookback;
    }

    public void setLookback(int lookback) {
        this.lookback = lookback;
    }

    public double getBaseVolatility() {
        return baseVolatility;
    }

    public void setBaseVolatility(double baseVolatility) {
        this.baseVolatility = baseVolatility;
    }

    public double getNasdaqVol() {
        return nasdaqVol;
    }

    public void setNasdaqVol(double nasdaqVol) {
        this.nasdaqVol = nasdaqVol;
    }

    public double getRevenueRateOfChange() {
        return revenueRateOfChange;
    }

    public void setRevenueRateOfChange(double revenueRateOfChange) {
        this.revenueRateOfChange = revenueRateOfChange;
    }

    public double getRevenueRateOfChangeCorrelationFactor() {
        return revenueRateOfChangeCorrelationFactor;
    }

    public void setRevenueRateOfChangeCorrelationFactor(double revenueRateOfChangeCorrelationFactor) {
        this.revenueRateOfChangeCorrelationFactor = revenueRateOfChangeCorrelationFactor;
    }

    public Date getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(Date revenueDate) {
        this.revenueDate = revenueDate;
    }

    public Date getOldRevenueDate() {
        return oldRevenueDate;
    }

    public void setOldRevenueDate(Date oldRevenueDate) {
        this.oldRevenueDate = oldRevenueDate;
    }

    public double getOilValue() {
        return oilValue;
    }

    public void setOilValue(double oilValue) {
        this.oilValue = oilValue;
    }

    public double getOilCorrelationFactor() {
        return oilCorrelationFactor;
    }

    public void setOilCorrelationFactor(double oilCorrelationFactor) {
        this.oilCorrelationFactor = oilCorrelationFactor;
    }

    public double getVixValue() {
        return vixValue;
    }

    public void setVixValue(double vixValue) {
        this.vixValue = vixValue;
    }

    public double getVixCorrelationFactor() {
        return vixCorrelationFactor;
    }

    public void setVixCorrelationFactor(double vixCorrelationFactor) {
        this.vixCorrelationFactor = vixCorrelationFactor;
    }

    public double getDiscountWeighting() {
        return discountWeighting;
    }

    public void setDiscountWeighting(double discountWeighting) {
        this.discountWeighting = discountWeighting;
    }

    public double getVolumeWeighting() {
        return volumeWeighting;
    }

    public void setVolumeWeighting(double volumeWeighting) {
        this.volumeWeighting = volumeWeighting;
    }

    public double getImpliedVolatilityWeighting() {
        return impliedVolatilityWeighting;
    }

    public void setImpliedVolatilityWeighting(double impliedVolatilityWeighting) {
        this.impliedVolatilityWeighting = impliedVolatilityWeighting;
    }

    public double getRealizedVolatilityWeighting() {
        return realizedVolatilityWeighting;
    }

    public void setRealizedVolatilityWeighting(double realizedVolatilityWeighting) {
        this.realizedVolatilityWeighting = realizedVolatilityWeighting;
    }

    public double getTreasuryWeighting() {
        return treasuryWeighting;
    }

    public void setTreasuryWeighting(double treasuryWeighting) {
        this.treasuryWeighting = treasuryWeighting;
    }

    public double getSecondDerivativeVolChange() {
        return secondDerivativeVolChange;
    }

    public void setSecondDerivativeVolChange(double secondDerivativeVolChange) {
        this.secondDerivativeVolChange = secondDerivativeVolChange;
    }

    public double getWmaAdjustment() {
        return wmaAdjustment;
    }

    public void setWmaAdjustment(double wmaAdjustment) {
        this.wmaAdjustment = wmaAdjustment;
    }

    public double getHistoricVolChange() {
        return historicVolChange;
    }

    public void setHistoricVolChange(double historicVolChange) {
        this.historicVolChange = historicVolChange;
    }

    public double getTradeVixCorrelationFactor() {
        return tradeVixCorrelationFactor;
    }

    public void setTradeVixCorrelationFactor(double tradeVixCorrelationFactor) {
        this.tradeVixCorrelationFactor = tradeVixCorrelationFactor;
    }

    public double getTradeDollarCorrelationFactor() {
        return tradeDollarCorrelationFactor;
    }

    public void setTradeDollarCorrelationFactor(double tradeDollarCorrelationFactor) {
        this.tradeDollarCorrelationFactor = tradeDollarCorrelationFactor;
    }

    public double getTradeDiscountCorrelationFactor() {
        return tradeDiscountCorrelationFactor;
    }

    public void setTradeDiscountCorrelationFactor(double tradeDiscountCorrelationFactor) {
        this.tradeDiscountCorrelationFactor = tradeDiscountCorrelationFactor;
    }

    public double getTradeVolumeCorrelationFactor() {
        return tradeVolumeCorrelationFactor;
    }

    public void setTradeVolumeCorrelationFactor(double tradeVolumeCorrelationFactor) {
        this.tradeVolumeCorrelationFactor = tradeVolumeCorrelationFactor;
    }

    public double getTradeOilCorrelationFactor() {
        return tradeOilCorrelationFactor;
    }

    public void setTradeOilCorrelationFactor(double tradeOilCorrelationFactor) {
        this.tradeOilCorrelationFactor = tradeOilCorrelationFactor;
    }

    public double getTradeTreasuryCorrelationFactor() {
        return tradeTreasuryCorrelationFactor;
    }

    public void setTradeTreasuryCorrelationFactor(double tradeTreasuryCorrelationFactor) {
        this.tradeTreasuryCorrelationFactor = tradeTreasuryCorrelationFactor;
    }

    public double getTradeRealizedVolCorrelationFactor() {
        return tradeRealizedVolCorrelationFactor;
    }

    public void setTradeRealizedVolCorrelationFactor(double tradeRealizedVolCorrelationFactor) {
        this.tradeRealizedVolCorrelationFactor = tradeRealizedVolCorrelationFactor;
    }

    public double getTradeFedTotalAssetsCorrelationFactor() {
        return tradeFedTotalAssetsCorrelationFactor;
    }

    public void setTradeFedTotalAssetsCorrelationFactor(double tradeFedTotalAssetsCorrelationFactor) {
        this.tradeFedTotalAssetsCorrelationFactor = tradeFedTotalAssetsCorrelationFactor;
    }

    public double getTradeCommodityCorrelationFactor() {
        return tradeCommodityCorrelationFactor;
    }

    public void setTradeCommodityCorrelationFactor(double tradeCommodityCorrelationFactor) {
        this.tradeCommodityCorrelationFactor = tradeCommodityCorrelationFactor;
    }

    public double getTradeImpliedVolCorrelationFactor() {
        return tradeImpliedVolCorrelationFactor;
    }

    public void setTradeImpliedVolCorrelationFactor(double tradeImpliedVolCorrelationFactor) {
        this.tradeImpliedVolCorrelationFactor = tradeImpliedVolCorrelationFactor;
    }

    public double getGoldCorrelationFactor() {
        return goldCorrelationFactor;
    }

    public void setGoldCorrelationFactor(double goldCorrelationFactor) {
        this.goldCorrelationFactor = goldCorrelationFactor;
    }

    public double getTradeGoldCorrelationFactor() {
        return tradeGoldCorrelationFactor;
    }

    public void setTradeGoldCorrelationFactor(double tradeGoldCorrelationFactor) {
        this.tradeGoldCorrelationFactor = tradeGoldCorrelationFactor;
    }

    public double getFredCommericialLoanYearOverYearChange() {
        return fredCommericialLoanYearOverYearChange;
    }

    public void setFredCommericialLoanYearOverYearChange(double fredCommericialLoanYearOverYearChange) {
        this.fredCommericialLoanYearOverYearChange = fredCommericialLoanYearOverYearChange;
    }

    public double getFredLoansCorrelationFactor() {
        return fredLoansCorrelationFactor;
    }

    public void setFredLoansCorrelationFactor(double fredLoansCorrelationFactor) {
        this.fredLoansCorrelationFactor = fredLoansCorrelationFactor;
    }

    public double getTradeFredLoansCorrelationFactor() {
        return tradeFredLoansCorrelationFactor;
    }

    public void setTradeFredLoansCorrelationFactor(double tradeFredLoansCorrelationFactor) {
        this.tradeFredLoansCorrelationFactor = tradeFredLoansCorrelationFactor;
    }

    public double getAutoLoansValue() {
        return autoLoansValue;
    }

    public void setAutoLoansValue(double autoLoansValue) {
        this.autoLoansValue = autoLoansValue;
    }

    public double getAutoLoansCorrelationFactor() {
        return autoLoansCorrelationFactor;
    }

    public void setAutoLoansCorrelationFactor(double autoLoansCorrelationFactor) {
        this.autoLoansCorrelationFactor = autoLoansCorrelationFactor;
    }

    public double getTradeAutoLoansCorrelationFactor() {
        return tradeAutoLoansCorrelationFactor;
    }

    public void setTradeAutoLoansCorrelationFactor(double tradeAutoLoansCorrelationFactor) {
        this.tradeAutoLoansCorrelationFactor = tradeAutoLoansCorrelationFactor;
    }

    public double getGasolineCorrelationFactor() {
        return gasolineCorrelationFactor;
    }

    public void setGasolineCorrelationFactor(double gasolineCorrelationFactor) {
        this.gasolineCorrelationFactor = gasolineCorrelationFactor;
    }

    public double getTradeGasolineCorrelationFactor() {
        return tradeGasolineCorrelationFactor;
    }

    public void setTradeGasolineCorrelationFactor(double tradeGasolineCorrelationFactor) {
        this.tradeGasolineCorrelationFactor = tradeGasolineCorrelationFactor;
    }

    public double getYieldCurveValue() {
        return yieldCurveValue;
    }

    public void setYieldCurveValue(double yieldCurveValue) {
        this.yieldCurveValue = yieldCurveValue;
    }

    public double getYieldCurveAdjustment() {
        return yieldCurveAdjustment;
    }

    public void setYieldCurveAdjustment(double yieldCurveAdjustment) {
        this.yieldCurveAdjustment = yieldCurveAdjustment;
    }

    public double getYieldCurveCorrelationFactor() {
        return yieldCurveCorrelationFactor;
    }

    public void setYieldCurveCorrelationFactor(double yieldCurveCorrelationFactor) {
        this.yieldCurveCorrelationFactor = yieldCurveCorrelationFactor;
    }

    public double getTradeYieldCurveCorrelationFactor() {
        return tradeYieldCurveCorrelationFactor;
    }

    public void setTradeYieldCurveCorrelationFactor(double tradeYieldCurveCorrelationFactor) {
        this.tradeYieldCurveCorrelationFactor = tradeYieldCurveCorrelationFactor;
    }

    public double getTradeHistoricVolChange() {
        return tradeHistoricVolChange;
    }

    public void setTradeHistoricVolChange(double tradeHistoricVolChange) {
        this.tradeHistoricVolChange = tradeHistoricVolChange;
    }

    public double getIncomeRateOfChange() {
        return incomeRateOfChange;
    }

    public void setIncomeRateOfChange(double incomeRateOfChange) {
        this.incomeRateOfChange = incomeRateOfChange;
    }

    public double getIncomeRateOfChangeCorrelationFactor() {
        return incomeRateOfChangeCorrelationFactor;
    }

    public void setIncomeRateOfChangeCorrelationFactor(double incomeRateOfChangeCorrelationFactor) {
        this.incomeRateOfChangeCorrelationFactor = incomeRateOfChangeCorrelationFactor;
    }

    public double getDiscountToVolChangeCorrelation() {
        return discountToVolChangeCorrelation;
    }

    public void setDiscountToVolChangeCorrelation(double discountToVolChangeCorrelation) {
        this.discountToVolChangeCorrelation = discountToVolChangeCorrelation;
    }

    public double getTradeDiscountToVolChangeCorrelation() {
        return tradeDiscountToVolChangeCorrelation;
    }

    public void setTradeDiscountToVolChangeCorrelation(double tradeDiscountToVolChangeCorrelation) {
        this.tradeDiscountToVolChangeCorrelation = tradeDiscountToVolChangeCorrelation;
    }

    public double getTrueRange() {
        return trueRange;
    }

    public void setTrueRange(double trueRange) {
        this.trueRange = trueRange;
    }

    public double getHighBetaCorrelationFactor() {
        return highBetaCorrelationFactor;
    }

    public void setHighBetaCorrelationFactor(double highBetaCorrelationFactor) {
        this.highBetaCorrelationFactor = highBetaCorrelationFactor;
    }

    public double getLowBetaCorrelationFactor() {
        return lowBetaCorrelationFactor;
    }

    public void setLowBetaCorrelationFactor(double lowBetaCorrelationFactor) {
        this.lowBetaCorrelationFactor = lowBetaCorrelationFactor;
    }

    public double getTradeLowBetaCorrelationFactor() {
        return tradeLowBetaCorrelationFactor;
    }

    public void setTradeLowBetaCorrelationFactor(double tradeLowBetaCorrelationFactor) {
        this.tradeLowBetaCorrelationFactor = tradeLowBetaCorrelationFactor;
    }

    public double getTradeHighBetaCorrelationFactor() {
        return tradeHighBetaCorrelationFactor;
    }

    public void setTradeHighBetaCorrelationFactor(double tradeHighBetaCorrelationFactor) {
        this.tradeHighBetaCorrelationFactor = tradeHighBetaCorrelationFactor;
    }

    public double getOptionsClose() {
        return optionsClose;
    }

    public void setOptionsClose(double optionsClose) {
        this.optionsClose = optionsClose;
    }

    public double getUpsideRatio() {
        return upsideRatio;
    }

    public void setUpsideRatio(double upsideRatio) {
        this.upsideRatio = upsideRatio;
    }

    public double getDollarValue() {
        return dollarValue;
    }

    public void setDollarValue(double dollarValue) {
        this.dollarValue = dollarValue;
    }

    public double getDailyShortSaleVolume() {
        return dailyShortSaleVolume;
    }

    public void setDailyShortSaleVolume(double dailyShortSaleVolume) {
        this.dailyShortSaleVolume = dailyShortSaleVolume;
    }
}
