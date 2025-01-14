package com.example.impliedvolatilitymicroservice.Range.Utils.Threads;

import com.example.impliedvolatilitymicroservice.API.MarketData;
import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.IVCalculator;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.ImpliedVolatility;
import com.example.impliedvolatilitymicroservice.Model.Ticker;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Repository.ImpliedVolatilityRepository;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RangeCalculatorThread extends Thread{

    int threadNum;
    List<Ticker> tickerList;
    List<RangeSummary> rangeSummaryList;

    List<TreasuryRate> treasuryRateList;
    ImpliedVolatilityRepository impliedVolatilityRepository;
    RangeCalculatorThreadMonitor rangeCalculatorThreadMonitor;
    public RangeCalculatorThread(int threadNum, List<Ticker> tickerList, RangeCalculatorThreadMonitor rangeCalculatorThreadMonitor){
        this.threadNum = threadNum;
        this.tickerList = tickerList;
        this.rangeCalculatorThreadMonitor = rangeCalculatorThreadMonitor;
        rangeSummaryList = new ArrayList<>();
    }

    public void setTreasuryRateList(List<TreasuryRate> treasuryRateList) {
        this.treasuryRateList = treasuryRateList;
    }

    public void setImpliedVolatilityRepository(ImpliedVolatilityRepository impliedVolatilityRepository) {
        this.impliedVolatilityRepository = impliedVolatilityRepository;
    }

    @Override
    public void run(){
        Polygon polygon = new Polygon();
        MarketData marketData = new MarketData();
        LocalDate localDate = LocalDate.now();
        Date parsedDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        LocalDate priorEndDate = LocalDate.from(parsedDate.toInstant().atZone(ZoneId.systemDefault()));
        LocalDate priorStartDate = priorEndDate.minusDays(300);
        List<Bar> spyBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "SPY");
        Collections.reverse(spyBars);
        List<Bar> qqqBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "QQQ");
        Collections.reverse(qqqBars);
        List<Bar> iwmBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "IWM");
        Collections.reverse(iwmBars);
        int complete = 0;
        for(Ticker ticker : tickerList) {

            List<Bar> barList = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, ticker.getTicker());
            //List<Bar> barList = marketData.getBarsBetweenDates(priorStartDate, priorEndDate, ticker.getTicker());
            try {
                if (barList != null) {
                    Bar bar = barList.get(barList.size() - 1);
                    calculateRange(barList, 17, 91, ticker.getTicker());
                    RangeSummary rangeSummary = new RangeSummary();
                    rangeSummary.setTicker(ticker);
                    rangeSummary.setClose(bar.getClose());
                    rangeSummary.setOpen(bar.getOpen());
                    rangeSummary.setRangeBot(bar.getBb_bottom());
                    rangeSummary.setRangeTop(bar.getBb_top());
                    rangeSummary.setUpside((bar.getBb_top() - bar.getClose()) / bar.getClose());
                    rangeSummary.setDownside((bar.getClose() - bar.getBb_bottom()) / bar.getClose());
                    rangeSummary.setDownsideRatio(rangeSummary.getDownside() / rangeSummary.getUpside());
                    rangeSummary.setUpsideRatio(rangeSummary.getUpside() / rangeSummary.getDownside());
                    rangeSummary.setTrend(bar.getTrend());
                    rangeSummary.setTrade(bar.getTrade());
                    rangeSummary.setSpyCorrelation(calculateCorrelation(barList, spyBars));
                    rangeSummary.setQqqCorrelation(calculateCorrelation(barList, qqqBars));
                    rangeSummary.setIwmCorrelation(calculateCorrelation(barList, iwmBars));
                    rangeSummary.setPctFromTrend((bar.getClose() - bar.getTrend()) / bar.getTrend());
                    rangeSummary.setChange((rangeSummary.getClose() - barList.get(1).getClose()) / barList.get(1).getClose());
                    rangeSummary.setVolumeChange(getVolumeChange(barList));
                    rangeSummary.setTickerString(ticker.getTicker());
                    double rv = StockCalculationLibrary.getLogVariance(barList, 0, 30, ticker.getTicker()) / 100;
                    ///////
                    List<ImpliedVolatility> ivList = impliedVolatilityRepository.findByTickerAndDaysVol(ticker.getTicker(), 30);
                    LocalDate converted = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int size = ivList.size();
                    boolean found = false;
                    //if(!localDate.equals(converted)) {
                    for (int i = 0; i < size; i++) {
                        ImpliedVolatility impliedVolatility = ivList.get(i);
                        if (impliedVolatility.getDate().getTime() == bar.getDate().getTime()) {
                            bar.setPut_implied_vol(impliedVolatility.getCalculatedVolatility());
                            found = true;
                            break;
                        }
                    }
                    //}else{
                    //    System.out.print("");
                    //}
                    if (!found) {
                        IVCalculator ivCalculator = new IVCalculator();
                        double iv = ivCalculator.calculatePutImpliedVol(barList.get(0), ticker.getTicker(), 30, treasuryRateList);
                        //double vol = calculatePutImpliedVol(bar,ticker,daysVol);
                        ImpliedVolatility impliedVolatility = new ImpliedVolatility();
                        impliedVolatility.setDate(bar.getDate());
                        impliedVolatility.setTicker(ticker.getTicker());
                        impliedVolatility.setDaysVol(30);
                        impliedVolatility.setCalculatedVolatility(iv);
                        bar.setPut_implied_vol(iv);
                        impliedVolatilityRepository.save(impliedVolatility);
                    }
                    ///////

                    double ivPremium = (bar.getPut_implied_vol() - rv) / rv;
                    if (bar.getPut_implied_vol() != 0.0) {
                        rangeSummary.setImpliedVolatility(bar.getPut_implied_vol());
                        rangeSummary.setIvPremium(ivPremium);
                    }

                    if (barList.size() > 30) {
                        rangeSummary.setMomentumChange30((bar.getTrend() - barList.get(30).getTrend()) / barList.get(30).getTrend());
                    }
                    if (barList.size() > 60) {
                        rangeSummary.setMomentumChange60((bar.getTrend() - barList.get(60).getTrend()) / barList.get(60).getTrend());
                    }
                    //double result =StockCalculationLibrary.getLogVariance(barList, 0, 30, ticker.getTicker()) / 100;
                    rangeSummary.setVolatility(rv);

                    rangeSummaryList.add(rangeSummary);
                    complete++;
                    System.out.println("[" + threadNum + "] " + "[" + complete + "/" + tickerList.size() + "]" + rangeSummary);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        rangeCalculatorThreadMonitor.threadFinished(threadNum, rangeSummaryList);
    }


    public void calculateRange(List<Bar> barList, int length, int trendLength, String ticker){
        StockCalculationLibrary stockCalculationLibrary = new StockCalculationLibrary();
        Collections.reverse(barList);
        for(int z = 0; z <barList.size(); z++){
            Bar bar = barList.get(z);
            bar.setWma(stockCalculationLibrary.weightedMovingAverage(barList, z, (int) length));
        }

        for(int z = 0; z < barList.size(); z++){
            Bar bar = barList.get(z);
            bar.setSlope(stockCalculationLibrary.getSlope(barList, z, length - 1));
            bar.setMinDiff(stockCalculationLibrary.min_diff(barList, z, bar.getSlope(), length - 1));
            bar.setMaxDiff(stockCalculationLibrary.max_diff(barList, z, bar.getSlope(), length - 1));
        }
        for(int z = 0; z < barList.size(); z++) {
            Bar bar = barList.get(z);
            bar.setStdDev(stockCalculationLibrary.stdDev(barList, z, length - 1));
            bar.setTrueRange(stockCalculationLibrary.averageTrueRange(barList, z, length));
        }

        for (int z = barList.size() - 1; z >= 0; z--) {
            Bar bar = barList.get(z);
            bar.setRma(stockCalculationLibrary.getRMA(barList, z, length));
        }

        for(int z = 0; z < barList.size(); z ++){
            Bar bar = barList.get(z);
            bar.setHurst(stockCalculationLibrary.getHurst(barList, z, length));
            bar.setBb_bottom(stockCalculationLibrary.getBridgeBottom(bar));
            bar.setBb_top(stockCalculationLibrary.getBridgeTop(bar));
            if (bar.getPut_implied_vol() != 0.0) {
                bar.setIvDiscount(bar.getPut_implied_vol() - ((bar.getBaseVolatility()) / 100));
            }
            double upper = bar.getBb_top() - bar.getClose();
            double lower = bar.getClose() - bar.getBb_bottom();
            bar.setUpsideRatio(upper/lower);
        }
        for(int z = 0; z < barList.size(); z ++){
            barList.get(z).setTrend(calculateLevel(barList,z,trendLength, ticker, true));
        }
        for(int z = 0; z < barList.size(); z ++){
            barList.get(z).setTrade(calculateLevel(barList,z,length,ticker, false));
        }

    }
    public double getVolumeChange(List<Bar> bars){
        try {
            double factor = 1;
            if (!isDayComplete()) {
                factor = 1 / calculateDayPercentageComplete();
            }
            bars.get(0).setVolume(bars.get(0).getVolume() * factor);
            double sum = 0.0;
            for (int x = 0; x < 4; x++) {
                sum += bars.get(x).getVolume();
            }
            sum = sum / 4;
            double oldSum = 0.0;
            for (int x = 4; x < 8; x++) {
                oldSum += bars.get(x).getVolume();
            }
            oldSum = oldSum / 4;

            return (sum - oldSum) / oldSum;
        }catch (Exception e){
            return 0.0;
        }
    }
    public double calculateDayPercentageComplete(){
        double minutes = 390;
        LocalDateTime now = LocalDateTime.now().minusMinutes(15);
        LocalDateTime startOfTrading = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 30);
        double minsBetween = ChronoUnit.MINUTES.between(startOfTrading, now);
        return minsBetween/minutes;
    }

    public boolean isDayComplete(){
        LocalDateTime now = LocalDateTime.now();
        if(now.getHour() >=17 && now.getMinute() >= 15){
            return true;
        }else{
            return false;
        }
    }
    public double calculateCorrelation(List<Bar> bars, List<Bar> referenceBars){
        List<Double> tempBars = new ArrayList<>();
        List<Double> tempBars2 = new ArrayList<>();
        try {
            for (int x = 0; x < 30; x++) {
                tempBars2.add(bars.get(x).getClose());
                tempBars.add(referenceBars.get(x).getClose());
            }
            double[] one = convertListToArray(tempBars2);
            double[] two = convertListToArray(tempBars);
            PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();

            return pearsonsCorrelation.correlation(one, two);
        }catch (Exception e){
            return 0.0;
        }
    }
    public double calculateLevel(List<Bar> barList, int index, int length,String ticker, boolean calculatingTrend) {
        if (index + 91< barList.size()) {
            try {
                double trend = 0.0;
                double volChange = 0.0;
                double discountToVolChangeCorrelation = 0.0;
                double discount = 0.0;
                List<Double> lows = new ArrayList<>();
                List<Bar> tempBars = new ArrayList<>();
                List<Bar> subList;
                if (calculatingTrend) {
                    subList = barList.subList(index + 17, index + 91);
                } else {
                    subList = barList.subList(index, index + 91);
                }
                for (Bar item : subList) {
                    try {
                        tempBars.add((Bar) item.clone());
                    } catch (Exception ignored) {

                    }
                }
                List<Bar> upBars = new ArrayList<>();
                List<Bar> downBars = new ArrayList<>();
                if(calculatingTrend){
                    for(int i = 0; i < length; i++){
                        if(barList.get(i).getClose() > barList.get(i).getOpen()){
                            //up
                            upBars.add(barList.get(i));
                        }else{
                            downBars.add(barList.get(i));
                        }
                    }
                }else{
                    for(int i = 0; i < length; i++){
                        if(barList.get(i).getClose() > barList.get(i).getOpen()){
                            //up
                            upBars.add(barList.get(i));
                        }else{
                            downBars.add(barList.get(i));
                        }
                    }
                }
                double totalUpVolume = 0.0;
                double totalDownVolume = 0.0;
                for(Bar bar : upBars){
                    totalUpVolume += bar.getVolume();
                }
                for(Bar bar : downBars){
                    totalDownVolume += bar.getVolume();
                }
                tempBars.sort(Comparator.comparing(Bar::getHigh).reversed());
                int count = 4;
                List<Integer> indexList = new ArrayList<>();
                double avgHigh = 0.0;
                if (calculatingTrend) {
                    int success = 0;
                    int i = 0;
                    while (success < count) {
                        //for(int i = 0; i <count; i++){
                        boolean ignore = false;
                        int originalIndex = 0;
                        for (int x = 0; x < subList.size(); x++) {
                            if (subList.get(x).getDate().equals(tempBars.get(i).getDate())) {
                                originalIndex = x;
                            }
                        }
                        for (int tempIndex : indexList) {
                            if (originalIndex < tempIndex + 2 && originalIndex > tempIndex - 2) {
                                ignore = true;
                                break;
                            }
                        }
                        if (!ignore) {
                            avgHigh += tempBars.get(i).getHigh();
                            for (int x = 0; x < subList.size(); x++) {
                                if (subList.get(x).getDate().equals(tempBars.get(i).getDate())) {
                                    indexList.add(x);
                                }
                            }
                            //indexList.add(i);
                            success++;
                        }
                        i++;
                    }
                    avgHigh = avgHigh / count;
                    tempBars.sort(Comparator.comparing(Bar::getLow));
                    double avgLow = 0.0;
                    success = 0;
                    indexList = new ArrayList<>();
                    while (success < count) {
                        //for(int i = 0; i <count; i++){
                        boolean ignore = false;
                        int originalIndex = 0;
                        for (int x = 0; x < subList.size(); x++) {
                            if (subList.get(x).getDate().equals(tempBars.get(i).getDate())) {
                                originalIndex = x;
                            }
                        }
                        for (int tempIndex : indexList) {
                            if (originalIndex < tempIndex + 2 && originalIndex > tempIndex - 2) {
                                ignore = true;
                                break;
                            }
                        }
                        if (!ignore) {
                            avgLow += tempBars.get(i).getLow();
                            for (int x = 0; x < subList.size(); x++) {
                                if (subList.get(x).getDate().equals(tempBars.get(i).getDate())) {
                                    indexList.add(x);
                                }
                            }
                            //indexList.add(i);
                            success++;
                        }
                        i++;
                    }
                    avgLow = avgLow / count;
                    trend = avgHigh - ((avgHigh - avgLow) / 2);
                    //trend = trend * ((totalUpVolume/(double)upBars.size())/(totalDownVolume/downBars.size()));
                    return trend;
                } else {

                    //find Low
                    double low = 999999999;
                    double high = -999999999;
                    for (int x = 0; x < length; x++) {
                        if (barList.get(index + x).getLow() < low) {
                            low = barList.get(index + x).getLow();
                        }
                    }
                    for (int x = 0; x < length; x++) {
                        if (barList.get(index + x).getHigh() > high) {
                            high = barList.get(index + x).getHigh();
                        }
                    }

                    trend = high - ((high - low) / 2);
                    //trend = trend * ((totalUpVolume/(double)upBars.size())/(totalDownVolume/downBars.size()));

                    return trend;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0.0;
    }
    public double[] convertListToArray(List<Double> originalList){
        double[] newArray = new double[originalList.size()];
        int size = originalList.size();
        for(int i = 0; i < size; i++){
            newArray[i] = originalList.get(i);
        }
        return newArray;
    }
}
