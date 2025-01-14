package com.example.impliedvolatilitymicroservice;

import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.OptionContract;
import com.example.impliedvolatilitymicroservice.Utils.DateFormatter;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class IVCalculator {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_ORANGE = "\033[38;5;214m";
    Instant now;
    Instant start;
    double delta;
    double rate;
    public double calculatePutImpliedVol(Bar bar, String ticker, int dayCalc, List<TreasuryRate> treasuryRateList){

       // List<OptionContract> completedContracts = new ArrayList<>();
        Date date = bar.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        LocalDate thirtyDays = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(dayCalc);
        LocalDate fortyFiveDayForward = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(dayCalc + 17);
        LocalDate fifteenDayForward = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(dayCalc).minusDays(14);
        LocalDate fiveDaysBack = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(6);
        String dateString = DateFormatter.formatLocalDate(localDate);
        String fortyFiveDayString = DateFormatter.formatLocalDate(fortyFiveDayForward);
        String fifteenDayString = DateFormatter.formatLocalDate(fifteenDayForward);
        String fiveDaysBackString = DateFormatter.formatLocalDate(fiveDaysBack);
        List<Double> ivList = new ArrayList<>();
        try {
            String url3 = "https://api.marketdata.app/v1/options/chain/" +
                    ticker + "/" + "?dte=" + dayCalc + "&date=" + localDate +
                    "&side=put" +
                    "&token=R3pONDdNLU1iNnl6LWJ0ZE5rRU5CSThrbnlMSHJqSzY2SmpUVHNfdXktVT0";

            if(localDate.equals(now)){
                url3 = "https://api.marketdata.app/v1/options/chain/" +
                        ticker + "/" + "?dte=" + dayCalc +
                        "&side=put" +
                        "&token=R3pONDdNLU1iNnl6LWJ0ZE5rRU5CSThrbnlMSHJqSzY2SmpUVHNfdXktVT0";
            }


            Connection.Response response3 = Jsoup.connect
                            (url3)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject jsonObject3 = new JSONObject(response3.body());
            JSONArray contractSymbolArray = jsonObject3.getJSONArray("optionSymbol");
            JSONArray strikeArray = jsonObject3.getJSONArray("strike");
            //find last out of the money option
            int index = 0;
            double strikeSelector = 0.0;
            for(int i = 0; i < contractSymbolArray.length(); i++){
                if(strikeArray.getDouble(i) < bar.getClose()){
                    if(strikeArray.getDouble(i) >strikeSelector) {
                        index = i;
                        strikeSelector = strikeArray.getDouble(i);
                    }
                }
            }
            double close = bar.getClose() * bar.getSplitAdjustFactor();
            double bid = jsonObject3.getJSONArray("bid").getDouble(index);
            double ask = jsonObject3.getJSONArray("ask").getDouble(index);
            double days = jsonObject3.getJSONArray("dte").getInt(index);
            double strike = jsonObject3.getJSONArray("strike").getDouble(index);
            long time = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            for (int x = 0; x < treasuryRateList.size(); x++) {
                if (treasuryRateList.get(x).getDate().getTime() < time ) {
                    rate = treasuryRateList.get(x).gettBillRate();
                }
            }
            double iv = getImpliedVolatility((bid + ask)/2, false, close, strike, rate/100, (double) days /365);
            iv = iv * ((double)dayCalc/(double)days);
            return iv;

        }catch(Exception e){
            if(e instanceof HttpStatusException){
            }else {
                e.printStackTrace();
            }
        }
           return 0.0;
    }
    private double getImpliedVolatility(double target, boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime){
        int MAX_ITERATIONS = 100;
        double PRECISION = 1E-5;

        double sigma =0.5;
        for(int i = 0; i < MAX_ITERATIONS; i++){
            double price = getBsPrice(callFlag, stockPrice, strike, riskFreeRate, expirationTime, sigma);
            double vega = getBsVega(callFlag, stockPrice, strike, riskFreeRate, expirationTime,sigma);

            double diff = target - price;

            if(Math.abs(diff) < PRECISION){
                return sigma;
            }
            sigma = sigma + diff/vega;
        }

        return sigma;
    }
    private double getBsPrice(boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime, double sigma){
        double q =0.0;
        double d1 = ((Math.log(stockPrice/strike)) + (riskFreeRate+sigma*sigma/2.)*expirationTime)/(sigma*Math.sqrt(expirationTime));
        double d2 = d1-sigma*Math.sqrt(expirationTime);
        double price;
        NormalDistribution normalDistribution = new NormalDistribution();
        if(callFlag){
            price = stockPrice*Math.exp(-q * expirationTime)*normalDistribution.cumulativeProbability(d1)-strike*Math.exp(-riskFreeRate*expirationTime)*normalDistribution.cumulativeProbability(d2);
        }else{
            price = strike*Math.exp(-riskFreeRate * expirationTime)*normalDistribution.cumulativeProbability(-d2)-stockPrice*Math.exp(-q*expirationTime)*normalDistribution.cumulativeProbability(-d1);
        }
        return price;
    }
    private double getBsVega(boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime, double sigma){
        double d1 = (Math.log(stockPrice/strike) + (riskFreeRate+sigma*sigma/2.)*expirationTime)/(sigma*Math.sqrt(expirationTime));
        NormalDistribution normalDistribution = new NormalDistribution();
        return stockPrice * Math.sqrt(expirationTime)*normalDistribution.density(d1);
    }
}
