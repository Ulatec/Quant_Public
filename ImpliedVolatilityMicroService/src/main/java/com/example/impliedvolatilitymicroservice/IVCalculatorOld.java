package com.example.impliedvolatilitymicroservice;

import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.OptionContract;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Utils.DateFormatter;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class IVCalculatorOld {
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
            String url3 = "https://api.polygon.io/v3/reference/options/contracts?underlying_ticker=" +
                    ticker +
                    "&contract_type=put" +
                    "&as_of=" + dateString +
                    "&expired=false" +
                    "&expiration_date.lte=" + fortyFiveDayString +
                    "&expiration_date.gte=" + fifteenDayString +
                    "&limit=1000" +
                    "&sort=expiration_date" +
                    "&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";

            Connection.Response response3 = Jsoup.connect
                            (url3)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject jsonObject3 = new JSONObject(response3.body());
            JSONArray resultArray3 = jsonObject3.getJSONArray("results");

            List<OptionContract> optionContracts = new ArrayList<>();
            for(Object object : resultArray3){
                JSONObject jsonObject = (JSONObject) object;
                OptionContract optionContract = new OptionContract();
                optionContract.setTicker(jsonObject.getString("ticker"));
                optionContract.setUnderlyingTicker(jsonObject.getString("underlying_ticker"));
                optionContract.setStrike(jsonObject.getDouble("strike_price"));
                String expDateString = jsonObject.getString("expiration_date");
                String[] split = expDateString.split("-");
                LocalDate expDate = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]),Integer.parseInt(split[2]));
                optionContract.setExpirationDate(expDate);
                optionContracts.add(optionContract);
            }
            OptionContract closestContract = null;
            OptionContract secondClosestContract = null;
            OptionContract thirdClosestContract = null;
            int closestDays = 10000;
            for(OptionContract optionContract : optionContracts){
                Period period = Period.between(thirtyDays, optionContract.getExpirationDate());
                if(Math.abs(period.getDays()) < closestDays){
                    thirdClosestContract = secondClosestContract;
                    secondClosestContract = closestContract;
                    closestContract = optionContract;
                    closestDays = Math.abs(period.getDays());
                }
            }

            List<OptionContract> filteredContracts = new ArrayList<>();

            for(OptionContract optionContract : optionContracts){
                try {
                    if(closestContract != null){
                        if(optionContract.getExpirationDate().equals(closestContract.getExpirationDate()) && optionContract.getStrike() < bar.getClose()){
                            filteredContracts.add(optionContract);
                        }
                    }
//                    if(secondClosestContract != null){
//                        if(optionContract.getExpirationDate().equals(secondClosestContract.getExpirationDate()) && optionContract.getStrike() < bar.getClose()){
//                            filteredContracts.add(optionContract);
//                        }
//                    }
//                    if(thirdClosestContract != null){
//                        if(optionContract.getExpirationDate().equals(thirdClosestContract.getExpirationDate()) && optionContract.getStrike() < bar.getClose()){
//                            filteredContracts.add(optionContract);
//                        }
//                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            filteredContracts.sort(Comparator.comparing(OptionContract::getStrike).reversed());


            int completedIVs = 0;
            int i = 0;
            while(completedIVs<1 && i<filteredContracts.size()){
                try {
                    Connection.Response contractResponse = null;

                    //String contractQuery = "https://api.polygon.io/v2/aggs/ticker/" + filteredContracts.get(i).getTicker() + "/range/1/day/" + fiveDaysBackString + "/" + dateString + "?adjusted=true&sort=asc&limit=120&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                    String contractQuery = "https://api.polygon.io/v3/quotes/" + filteredContracts.get(i).getTicker() + "?timestamp=" + dateString + "&limit=1000&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                    //System.out.println(contractQuery);
                    contractResponse = Jsoup.connect
                                    (contractQuery)
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .execute();
                    JSONObject contractJsonObject = new JSONObject(contractResponse.body());


                //    if(contractJsonObject.getInt("resultsCount")>0) {
                        if (contractJsonObject.getJSONArray("results").length() > 0) {

                            int foundIndex = 0;
                            for(int x = 0; x < contractJsonObject.getJSONArray("results").length(); x++  ){
                                JSONObject jsonObject = (JSONObject) contractJsonObject.getJSONArray("results").getJSONObject(x);
                                Long timestamp = jsonObject.getLong("sip_timestamp");
                                Date testDate = new Date(timestamp/1000000);
                                LocalDateTime localDate1 = LocalDateTime.from(testDate.toInstant().atZone(ZoneId.systemDefault()));
                                System.out.println(testDate);
                                if(localDate1.getHour()<15){
                                    foundIndex = x;
                                    break;
                                }
                                //foundIndex++;
                            }
              //          JSONArray contractArray = contractJsonObject.getJSONArray("results");
                        JSONObject jsonObject = (JSONObject) contractJsonObject.getJSONArray("results").get(foundIndex);
//                                totalMidPoints += (jsonObject.getDouble("ask_price") + jsonObject.getDouble("bid_price"))/2;
//                            }

                        double price = (jsonObject.getDouble("ask_price") + jsonObject.getDouble("bid_price"))/2;
                       // double price;
                       // price = contractArray.getJSONObject(contractArray.length() - 1).getDouble("c");
                        filteredContracts.get(i).setPrice(price);

                        long timeDiff = Math.abs(Date.from(filteredContracts.get(i).getExpirationDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()
                                - Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime());
                        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                        double rate = 0.0;
                        long time = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

                        for (int x = 0; x < treasuryRateList.size(); x++) {
                            if (treasuryRateList.get(x).getDate().getTime() < time ) {
                                rate = treasuryRateList.get(x).gettBillRate();
                            }
                        }


                        double iv = getImpliedVolatility(price, false, bar.getClose(), filteredContracts.get(i).getStrike(), rate/100, (double) daysDiff / 365);
                        if(!Double.isNaN(iv)){
                            iv = iv * ((double)30/(double)daysDiff);
                            ivList.add(iv);
                            completedIVs = completedIVs + 1;
                           // completedContracts.add(filteredContracts.get(i));
                        }

                    }
                    i = i + 1;
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("error: " + filteredContracts.get(i));
                }

            }
            i = 0;
            if(ivList.size() == 0) {
                filteredContracts = new ArrayList<>();

                for(OptionContract optionContract : optionContracts){
                    try {
                        if(closestContract != null){
                            if(optionContract.getExpirationDate().equals(closestContract.getExpirationDate()) && (int)(optionContract.getStrike()/ bar.getSplitAdjustFactor()) < bar.getClose()){
                                filteredContracts.add(optionContract);
                            }
                        }
                        if(secondClosestContract != null){
                            if(optionContract.getExpirationDate().equals(secondClosestContract.getExpirationDate()) && (int)(optionContract.getStrike()/ bar.getSplitAdjustFactor()) < bar.getClose()){
                                filteredContracts.add(optionContract);
                            }
                        }
                        if(thirdClosestContract != null){
                            if(optionContract.getExpirationDate().equals(thirdClosestContract.getExpirationDate()) && (int)(optionContract.getStrike()/ bar.getSplitAdjustFactor()) < bar.getClose()){
                                filteredContracts.add(optionContract);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                filteredContracts.sort(Comparator.comparing(OptionContract::getStrike).reversed());

                while (completedIVs <1 && i<filteredContracts.size()) {
                    Connection.Response contractResponse = null;
                    //boolean success = false;
                    //while(!success) {
                    //    try {
                    String contractQuery = "https://api.polygon.io/v2/aggs/ticker/" + filteredContracts.get(i).getTicker() + "/range/1/day/" + fiveDaysBackString + "/" + dateString + "?adjusted=true&sort=asc&limit=120&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                    System.out.println(contractQuery);
                    contractResponse = Jsoup.connect
                                    (contractQuery)
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .execute();
//                            System.out.println(contractResponse.statusMessage());
//                            if(contractResponse.statusCode() == 200) {
//                                success = true;
//                            }
//                        }catch(Exception e){
//                            e.printStackTrace();
//                           // System.out.println(e.printStackTrace());
//                        }
//                    }

                    JSONObject contractJsonObject = new JSONObject(contractResponse.body());

                    try {
                        if (contractJsonObject.getInt("resultsCount") > 0) {
                            JSONArray contractArray = contractJsonObject.getJSONArray("results");
                            //System.out.println(contractJsonObject);

                            double price;
                            price = contractArray.getJSONObject(contractArray.length() - 1).getDouble("c");
                            filteredContracts.get(i).setPrice(price);

                            long timeDiff = Math.abs(Date.from(filteredContracts.get(i).getExpirationDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()
                                    - Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime());
                            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                            double rate = 0.0;
                            long time = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
                            for (int x = 0; x < treasuryRateList.size(); x++) {
                                if (treasuryRateList.get(x).getDate().getTime() < time ) {
                                    rate = treasuryRateList.get(x).gettBillRate();
                                }
                            }

                            double close = bar.getClose() * bar.getSplitAdjustFactor();
                            double strike = filteredContracts.get(i).getStrike() ;
                            double iv = getImpliedVolatility(price, false, close, strike, rate/100, (double) daysDiff /365);
                            //System.out.println("iv: " + iv + " days: " + daysDiff);
                            iv = iv * ((double)30/(double)daysDiff);
                            ivList.add(iv);
                            completedIVs = completedIVs + 1;
                           // completedContracts.add(filteredContracts.get(i));
                        }else{

                            //success = false;
                            //while(!success) {
                            // try {

                        }
                        i = i + 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("error: " + filteredContracts.get(i));
                    }

                }
            }
            DoubleSummaryStatistics doubleSummaryStatistics = ivList.stream().mapToDouble(x -> x).summaryStatistics();
            if(Double.isNaN(doubleSummaryStatistics.getAverage())){
                System.out.println("stop.");
            }
            if(ivList.size() <1) {
                filteredContracts = new ArrayList<>();

                for(OptionContract optionContract : optionContracts){
                    try {
                        if(closestContract != null){
                            if(optionContract.getExpirationDate().equals(closestContract.getExpirationDate())){
                                filteredContracts.add(optionContract);
                            }
                        }
                        if(secondClosestContract != null){
                            if(optionContract.getExpirationDate().equals(secondClosestContract.getExpirationDate())){
                                filteredContracts.add(optionContract);
                            }
                        }
                        if(thirdClosestContract != null){
                            if(optionContract.getExpirationDate().equals(thirdClosestContract.getExpirationDate())){
                                filteredContracts.add(optionContract);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                i = 0;
                while (ivList.size() < 1 && i < filteredContracts.size()) {
                    try {

                        Connection.Response contractResponse1 = null;
                        String contractQuery1 = "https://api.polygon.io/v3/quotes/" + filteredContracts.get(i).getTicker() + "?timestamp=" + dateString + "&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                        //      String contractQuery1 = "https://api.polygon.io/v3/trades/" + filteredContracts.get(i).getTicker() + "?timestamp=" + dateString + "&adjusted=true&sort=asc&limit=120&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                        //    System.out.println(contractQuery1);
                        //String contractQuery = "https://api.polygon.io/v1/open-close/" + filteredContracts.get(i).getTicker() + "/" + dateString + "?adjusted=true&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                        contractResponse1 = Jsoup.connect
                                        (contractQuery1)
                                .method(Connection.Method.GET)
                                .ignoreContentType(true)
                                .execute();
                        JSONObject contractJsonObject1 = new JSONObject(contractResponse1.body());
                        if (contractJsonObject1.getJSONArray("results").length() > 0) {
                            //double totalMidPoints = 0.0;
//                            for(Object object : contractJsonObject1.getJSONArray("results")){
                            JSONObject jsonObject = (JSONObject) contractJsonObject1.getJSONArray("results").get(0);
//                                totalMidPoints += (jsonObject.getDouble("ask_price") + jsonObject.getDouble("bid_price"))/2;
//                            }

                            double price = (jsonObject.getDouble("ask_price") + jsonObject.getDouble("bid_price"))/2;

                            filteredContracts.get(i).setPrice(price);

                            long timeDiff = Math.abs(Date.from(filteredContracts.get(i).getExpirationDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()
                                    - Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime());
                            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                            double rate = 0.0;
                            long time = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
                            for (int x = 0; x < treasuryRateList.size(); x++) {
                                if (treasuryRateList.get(x).getDate().getTime() < time ) {
                                    rate = treasuryRateList.get(x).gettBillRate();
                                }
                            }

                            double close = bar.getClose() * bar.getSplitAdjustFactor();
                            double strike = filteredContracts.get(i).getStrike() ;
                            double iv = getImpliedVolatility(price, false, close, strike, rate/100, (double) daysDiff /365);
                            //System.out.println("iv: " + iv + " days: " + daysDiff);
                            iv = iv * ((double)30/(double)daysDiff);
                            ivList.add(iv);
                            completedIVs = completedIVs + 1;
                           // completedContracts.add(filteredContracts.get(i));
                        }
                        System.out.println( filteredContracts.get(i).getTicker() + " " + contractJsonObject1);
                        if (contractJsonObject1.getJSONArray("results").length() > 0) {
                            System.out.println("test");
                        }
                        System.out.println("stop.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;

                }
            }
            //complete++;
            if(ivList.size() == 0){
                System.out.println("stop.,");
            }
        //    System.out.println(ticker + " " + complete+"/"+barList.size() + " " + bar.getDate() + "samples: " + ivList.size() + " Avg Put IV: " + doubleSummaryStatistics.getAverage() + ivList.get(0));
            System.out.println(ticker + " " + bar.getDate() + "samples: " + ivList.size() + " Avg Put IV: " + doubleSummaryStatistics.getAverage() + ivList.get(0));

            return doubleSummaryStatistics.getAverage();

            //System.out.println(resultArray3);
        }catch (Exception e){
            e.printStackTrace();
        }
        //complete++;



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
