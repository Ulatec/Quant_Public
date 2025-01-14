package Threads;

import Model.Bar;
import Model.ImpliedVolaility;
import Model.OptionContract;
import Repository.ImpliedVolailityRepository;
import Util.DateFormatter;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.cglib.core.Local;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static Fetchers.StockRangeTester.ANSI_ORANGE;
import static Fetchers.StockRangeTester.ANSI_RESET;

public class IVSolverThread extends Thread{

    public ImpliedVolailityRepository impliedVolailityRepository;

    private List<Bar> barList;

    private String ticker;
    private IVSolverMonitor ivSolverMonitor;

    private JSONArray dateArray;

    private JSONArray rateArray;

    private int daysVol;

    private int threadNum;

    private int complete;

    private Instant now;
    private double delta;
    private double rate;
    private Instant start;

    public IVSolverThread(int threadNum, List<Bar> barList, IVSolverMonitor ivSolverMonitor, String ticker, int daysVol,JSONArray dateArray, JSONArray rateArray,
                          ImpliedVolailityRepository impliedVolailityRepository){
        this.barList = barList;
        this.ivSolverMonitor = ivSolverMonitor;
        this.daysVol = daysVol;
        this.threadNum = threadNum;
        this.dateArray = dateArray;
        this.rateArray = rateArray;
        this.ticker = ticker;
        this.impliedVolailityRepository = impliedVolailityRepository;
        complete = 0;
    }


    public void run(){
        start = Instant.now();

       // HashMap<String, List<Bar>> outputMap = new HashMap<>();
      //  for(Map.Entry<String, List<Bar>> entry : outputMap)
//        for(String ticker : tickers) {
            List<ImpliedVolaility> ivList = impliedVolailityRepository.findByTickerAndDaysVol(ticker,daysVol);
            for (Bar bar : barList) {
                LocalDate localDate = LocalDate.now();
                LocalDate converted = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                //check if in database
                //Optional<ImpliedVolaility> optional = impliedVolailityRepository.findByTickerAndDaysVolAndDate(ticker,daysVol,bar.getDate());
                int size = ivList.size();
                boolean found = false;
                if(!localDate.equals(converted)) {
                    for (int i = 0; i < size; i++) {
                        ImpliedVolaility impliedVolaility = ivList.get(i);
                        if (impliedVolaility.getDate().getTime() == bar.getDate().getTime()) {
                            bar.setPut_implied_vol(impliedVolaility.getCalculatedVolatility());
                            found = true;
                            break;
                        }
                    }
                }else{
                    System.out.println("");
                }
                if(!found){
                    double vol = calculatePutImpliedVol(bar,ticker,daysVol, barList);
                    ImpliedVolaility impliedVolaility = new ImpliedVolaility();
                    impliedVolaility.setDate(bar.getDate());
                    impliedVolaility.setTicker(ticker);
                    impliedVolaility.setDaysVol(daysVol);
                    impliedVolaility.setCalculatedVolatility(vol);
                    bar.setPut_implied_vol(vol);
                    if(vol != 0.0) {
                        impliedVolailityRepository.save(impliedVolaility);
                    }
                }
                complete++;
                System.out.println(ticker + " " + complete + "/" + barList.size() + " " + bar.getDate() + " Avg Put IV: " + bar.getPut_implied_vol());


            }

        //}
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        rate = ((float) barList.size() / delta) * 1000;
        System.out.println(ANSI_ORANGE + "Completion rate: " + rate + "\n" + ANSI_RESET);
        System.gc();
        ivSolverMonitor.threadFinished(threadNum, barList);
    }

    public double calculatePutImpliedVol(Bar bar, String ticker, int dayCalc, List<Bar> barList){

        // List<OptionContract> completedContracts = new ArrayList<>();
        Date date = bar.getDate();
        int index = barList.indexOf(bar);
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
        if(!localDate.toString().equals("2023-07-26")) {
            return fetchImpliedVolForDate(dayCalc,localDate,bar);
        }else{

            double before = fetchImpliedVolForDate(dayCalc,barList.get(index -1).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),barList.get(index - 1));
            double after = fetchImpliedVolForDate(dayCalc,barList.get(index +1).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),barList.get(index + 1));
            return (before + after)/2;
        }
    }
    public double fetchImpliedVolForDate(int dayCalc, LocalDate localDate, Bar bar){
        while (true) {
            try {
                System.out.println(localDate);
                String url3 = "https://api.marketdata.app/v1/options/chain/" +
                        ticker + "/" + "?dte=" + dayCalc + "&date=" + localDate +
                        "&side=put" +
                        "&token=";

                if (localDate.equals(now)) {
                    url3 = "https://api.marketdata.app/v1/options/chain/" +
                            ticker + "/" + "?dte=" + dayCalc +
                            "&side=put" +
                            "&token=";
                }


                Connection.Response response3 = Jsoup.connect
                                (url3)
                        .method(Connection.Method.GET)
                        .timeout(1200000)
                        .ignoreContentType(true)
                        .execute();

                JSONObject jsonObject3 = new JSONObject(response3.body());
                JSONArray contractSymbolArray = jsonObject3.getJSONArray("optionSymbol");
                JSONArray strikeArray = jsonObject3.getJSONArray("strike");
                //find last out of the money option
                int index = 0;
                double strikeSelector = 0.0;
                for (int i = 0; i < contractSymbolArray.length(); i++) {
                    if (strikeArray.getDouble(i) < bar.getClose()) {
                        if (strikeArray.getDouble(i) > strikeSelector) {
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
                for (int x = 1; x < dateArray.length(); x++) {
                    if (dateArray.getDouble(x) < time && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                        rate = rateArray.getJSONArray(x).getDouble(0);
                    }
                }
                double iv = getImpliedVolatility((bid + ask) / 2, false, close, strike, rate / 100, (double) days / 365);
                iv = iv * ((double) dayCalc / (double) days);
                return iv;

            } catch (Exception e) {
                if(e instanceof HttpStatusException){
                    return 0.0;
                }
                e.printStackTrace();
            }
        }
    }



    public double getImpliedVolatility(double target, boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime){
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
    public double getBsPrice(boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime, double sigma){
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
    public double getBsVega(boolean callFlag, double stockPrice, double strike, double riskFreeRate, double expirationTime, double sigma){
        double d1 = (Math.log(stockPrice/strike) + (riskFreeRate+sigma*sigma/2.)*expirationTime)/(sigma*Math.sqrt(expirationTime));
        NormalDistribution normalDistribution = new NormalDistribution();
        return stockPrice * Math.sqrt(expirationTime)*normalDistribution.density(d1);
    }

}
