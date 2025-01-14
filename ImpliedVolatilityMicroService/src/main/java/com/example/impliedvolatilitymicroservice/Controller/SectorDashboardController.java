package com.example.impliedvolatilitymicroservice.Controller;


import com.example.impliedvolatilitymicroservice.API.MarketData;
import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.IVCalculator;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.Ticker;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Range.Utils.RangeCalculator;
import com.example.impliedvolatilitymicroservice.Range.Utils.Threads.RangeCalculatorThread;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@CrossOrigin("*")
public class SectorDashboardController {
    @Autowired
    TreasuryRateRepository treasuryRateRepository;

    @GetMapping("/dashboard/")
    public List<dashBoardReturnObject> test(){
        String[] tickers = {"XLK", "XLE","XRT","XLF", "XLP","XLU","SPY","QQQ","IWM","XLRE","XLY","XLI", "XLV", "XBI"};
        Polygon polygon = new Polygon();
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysBack = now.minusDays(360);
        List<dashBoardReturnObject> dashBoardReturnObjects = new ArrayList<>();
        for(String ticker : tickers){
            List<Bar> barList = polygon.getBarsBetweenDates(thirtyDaysBack,now,ticker);
            double mostRecentClose = barList.get(barList.size() - 1).getClose();
            double fiveDayClose = 0.0;
            for(Bar bar : barList){
                LocalDate convert = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fiveDay = now.minusDays(6);
                if(convert.isAfter(fiveDay)){
                    fiveDayClose = bar.getClose();
                    break;
                }
            }
            double fourteenDayClose = 0.0;
            for(Bar bar : barList){
                LocalDate convert = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fourteenDay = now.minusDays(15);
                if(convert.isAfter(fourteenDay)){
                    fourteenDayClose = bar.getClose();
                    break;
                }
            }

            double thirtyDayClose = 0.0;
            for(Bar bar : barList){
                LocalDate convert = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fourteenDay = now.minusDays(31);
                if(convert.isAfter(fourteenDay)){
                    thirtyDayClose = bar.getClose();
                    break;
                }
            }
            double rv = StockCalculationLibrary.getLogVarianceReverse(barList, barList.size() - 1, 30, ticker) / 100;
            IVCalculator ivCalculator = new IVCalculator();
            double iv = ivCalculator.calculatePutImpliedVol(barList.get(barList.size() - 1),ticker,30,treasuryRateRepository.findAll());

            RangeSummary rangeSummary = runSingleSummary(barList, ticker);

            dashBoardReturnObject dashBoardReturnObject = new dashBoardReturnObject(ticker,
                    (mostRecentClose-fiveDayClose)/fiveDayClose,
                    (mostRecentClose - fourteenDayClose)/fourteenDayClose,
                    (mostRecentClose - thirtyDayClose)/thirtyDayClose
                    );
            dashBoardReturnObject.setIvPremium((iv - rv)/iv);
            dashBoardReturnObject.setPctFromTrend(rangeSummary.getPctFromTrend());
            dashBoardReturnObject.setTrendMomentum30(rangeSummary.getMomentumChange30());
            dashBoardReturnObject.setTrendMomentum60(rangeSummary.getMomentumChange60());
            dashBoardReturnObject.setVolumeChange(getVolumeChange(barList));
            dashBoardReturnObject.setUpsideRatio(rangeSummary.getUpsideRatio());
            dashBoardReturnObject.setDownsideRatio(rangeSummary.getDownsideRatio());
            dashBoardReturnObjects.add(dashBoardReturnObject);
        }

        return dashBoardReturnObjects;
    }



    private class dashBoardReturnObject{
        private String ticker;

        private double fiveDayPerformance;

        private double fourteenDayPerformance;

        private double thirtyDayPerformance;

        private double ivPremium;

        private double volumeChange;

        private double pctFromTrend;
        private double trendMomentum30;

        private double trendMomentum60;
        private double upsideRatio;
        private double downsideRatio;

        public dashBoardReturnObject(String ticker, double fiveDayPerformance, double fourteenDayPerformance, double thirtyDayPerformance) {
            this.ticker = ticker;
            this.fiveDayPerformance = fiveDayPerformance;
            this.fourteenDayPerformance = fourteenDayPerformance;
            this.thirtyDayPerformance = thirtyDayPerformance;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public double getFiveDayPerformance() {
            return fiveDayPerformance;
        }

        public void setFiveDayPerformance(double fiveDayPerformance) {
            this.fiveDayPerformance = fiveDayPerformance;
        }

        public double getFourteenDayPerformance() {
            return fourteenDayPerformance;
        }

        public void setFourteenDayPerformance(double fourteenDayPerformance) {
            this.fourteenDayPerformance = fourteenDayPerformance;
        }

        public double getThirtyDayPerformance() {
            return thirtyDayPerformance;
        }

        public void setThirtyDayPerformance(double thirtyDayPerformance) {
            this.thirtyDayPerformance = thirtyDayPerformance;
        }

        public double getIvPremium() {
            return ivPremium;
        }

        public void setIvPremium(double ivPremium) {
            this.ivPremium = ivPremium;
        }

        public double getVolumeChange() {
            return volumeChange;
        }

        public void setVolumeChange(double volumeChange) {
            this.volumeChange = volumeChange;
        }

        public double getPctFromTrend() {
            return pctFromTrend;
        }

        public void setPctFromTrend(double pctFromTrend) {
            this.pctFromTrend = pctFromTrend;
        }

        public double getTrendMomentum30() {
            return trendMomentum30;
        }

        public void setTrendMomentum30(double trendMomentum30) {
            this.trendMomentum30 = trendMomentum30;
        }

        public double getTrendMomentum60() {
            return trendMomentum60;
        }

        public void setTrendMomentum60(double trendMomentum60) {
            this.trendMomentum60 = trendMomentum60;
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
    }

    public RangeSummary runSingleSummary(List<Bar> barList, String ticker){
        Polygon polygon = new Polygon();
        MarketData marketData = new MarketData();
        LocalDate localDate = LocalDate.now();
        Date parsedDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        LocalDate priorEndDate = LocalDate.from(parsedDate.toInstant().atZone(ZoneId.systemDefault()));
        LocalDate priorStartDate = priorEndDate.minusDays(360);
        List<Bar> spyBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "SPY");
        Collections.reverse(spyBars);
        List<Bar> qqqBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "QQQ");
        Collections.reverse(qqqBars);
        List<Bar> iwmBars = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, "IWM");
        Collections.reverse(iwmBars);

           // List<Bar> barList = polygon.getBarsBetweenDates(priorStartDate, priorEndDate, ticker);
            //List<Bar> barList = marketData.getBarsBetweenDates(priorStartDate, priorEndDate, ticker.getTicker());
            if (barList != null) {
                Bar bar = barList.get(barList.size()-1);
                calculateRange(barList, 17, 91, ticker);
                RangeSummary rangeSummary = new RangeSummary();
                //rangeSummary.setTicker(ticker);
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
                rangeSummary.setPctFromTrend((bar.getClose() - bar.getTrend())/bar.getTrend());
                rangeSummary.setVolumeChange(getVolumeChange(barList));
                rangeSummary.setMomentumChange30((bar.getTrend() - barList.get(30).getTrend())/barList.get(30).getTrend());
                rangeSummary.setMomentumChange60((bar.getTrend() - barList.get(60).getTrend())/barList.get(60).getTrend());
                double result =StockCalculationLibrary.getLogVariance(barList, 0, 30, ticker) / 100;
                rangeSummary.setVolatility(result);
                return rangeSummary;
            }

        return null;
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
            barList.get(z).setTrend(calculateLevel(barList,z,trendLength, ticker, true, 4));
        }
        for(int z = 0; z < barList.size(); z ++){
            barList.get(z).setTrade(calculateLevel(barList,z,length,ticker, false, 4));
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
    public double calculateLevel(List<Bar> barList, int index, int length,String ticker, boolean calculatingTrend, int count) {
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
                    subList = barList.subList(index, index + 91);
                } else {
                    subList = barList.subList(index, index + 17);
                }
                for (Bar item : subList) {
                    try {
                        tempBars.add((Bar) item.clone());
                    } catch (Exception ignored) {

                    }
                }

                tempBars.sort(Comparator.comparing(Bar::getHigh).reversed());
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

