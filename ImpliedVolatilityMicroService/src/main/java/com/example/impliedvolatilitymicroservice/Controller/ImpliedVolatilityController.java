package com.example.impliedvolatilitymicroservice.Controller;

import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.Controller.ReturnObjects.CorrelationObject;
import com.example.impliedvolatilitymicroservice.Controller.ReturnObjects.ShortSaleDataPoint;
import com.example.impliedvolatilitymicroservice.Controller.ReturnObjects.StockSummaryObject;
import com.example.impliedvolatilitymicroservice.Controller.ReturnObjects.VolatilityReturnObject;

import com.example.impliedvolatilitymicroservice.IVCalculator;
import com.example.impliedvolatilitymicroservice.IndexData.Model.DollarRate;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.MarketPositioningData.Model.FinraShortSaleDataPoint;
import com.example.impliedvolatilitymicroservice.MarketPositioningData.Repository.FinraShortSaleDataRepository;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.ImpliedVolatility;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Repository.DollarRateRepository;
import com.example.impliedvolatilitymicroservice.Repository.ImpliedVolatilityRepository;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.example.impliedvolatilitymicroservice.IVCalculator.ANSI_ORANGE;
import static com.example.impliedvolatilitymicroservice.IVCalculator.ANSI_RESET;

@RestController
@CrossOrigin("*")
public class ImpliedVolatilityController {

    @Autowired
    ImpliedVolatilityRepository impliedVolatilityRepository;
    @Autowired
    TreasuryRateRepository treasuryRateRepository;
    @Autowired
    DollarRateRepository dollarRateRepository;

    @Autowired
    FinraShortSaleDataRepository finraShortSaleDataRepository;
    private Logger logger = LoggerFactory.getLogger(ImpliedVolatilityController.class);

    @GetMapping("/impliedVolatility/{ticker}/")
    public StockSummaryObject retrieveImpliedVolatility(@PathVariable String ticker){
        Instant now;
        Instant start;
        double delta;
        double rate;
        start = Instant.now();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        double iv;
        double rv;
        try {
            LocalDate localDate = LocalDate.now();
            Date parsedDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            Polygon polygon = new Polygon();
            LocalDate priorEndDate = LocalDate.from(parsedDate.toInstant().atZone(ZoneId.systemDefault()));
            LocalDate priorStartDate = priorEndDate.minusDays(90);
            List<Bar> barList = polygon.getBarsBetweenDates(priorStartDate,priorEndDate,ticker);
            CorrelationObject correlationObject = null;
            List<TreasuryRate> ratesList = treasuryRateRepository.findAll();
            List<DollarRate> dollarRateList = dollarRateRepository.findAll();
            List<VolatilityReturnObject> volatilityReturnObjects = new ArrayList<>();
            for( int i = 0; i < 30; i++) {
                LocalDate converted = barList.get(barList.size() - 1 - i).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date convertedDate = Date.from(converted.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Optional<ImpliedVolatility> impliedVolatilityOptional = impliedVolatilityRepository.findByTickerAndDaysVolAndDate(ticker, 30, convertedDate);
                if(impliedVolatilityOptional.isPresent() && !localDate.equals(converted)) {
                    iv = impliedVolatilityOptional.get().getCalculatedVolatility();
                }else{
                    IVCalculator ivCalculator = new IVCalculator();
                    iv = ivCalculator.calculatePutImpliedVol(barList.get(barList.size() - 1 - i),ticker,30,ratesList);
                    now = Instant.now();
                    delta = Duration.between(start, now).toMillis();
                    rate =  (double)delta/1000;
                    System.out.println(ANSI_ORANGE + "Games per second: " + rate + "\n" + ANSI_RESET);
                }
                rv = StockCalculationLibrary.getLogVarianceReverse(barList, barList.size() - 1 - i, 30, ticker) / 100;
                double fifteenDayRV = StockCalculationLibrary.getLogVarianceReverse(barList, barList.size() - 1 - i, 15, ticker) / 100;
                double fifteenIV;
                Optional<ImpliedVolatility> fifteenDayOptional = impliedVolatilityRepository.findByTickerAndDaysVolAndDate(ticker, 15, convertedDate);
                if(fifteenDayOptional.isPresent() && !localDate.equals(converted)) {
                    fifteenIV = fifteenDayOptional.get().getCalculatedVolatility();
                }else{
                    IVCalculator ivCalculator = new IVCalculator();
                    fifteenIV = ivCalculator.calculatePutImpliedVol(barList.get(barList.size() - 1 - i),ticker,15,ratesList);
                    now = Instant.now();
                    delta = Duration.between(start, now).toMillis();
                    rate =  (double)delta/1000;
                    System.out.println(ANSI_ORANGE + "Games per second: " + rate + "\n" + ANSI_RESET);
                }
                volatilityReturnObjects.add(new VolatilityReturnObject(iv,rv, fifteenIV,fifteenDayRV,barList.get(barList.size()-1-i).getDate()));
            }
            correlationObject = getCorrelationObject(ticker,barList,ratesList,dollarRateList);
            List<FinraShortSaleDataPoint> finraShortSaleDataPoints = finraShortSaleDataRepository.findFinraShortSaleDataPointByTickerOrderByDate(ticker);
            StockSummaryObject stockSummaryObject = new StockSummaryObject(volatilityReturnObjects, correlationObject);
            stockSummaryObject.setFiveDayShortSaleAverageData(getShortSaleAverages(barList,finraShortSaleDataPoints));
            return stockSummaryObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/impliedVolatility/{ticker}/{date}/{dayCount}")
    public Double retrieveImpliedVolatilityHistory(@PathVariable String ticker, @PathVariable String date,
                                                   @PathVariable String dayCount){
        LocalDate localDate = LocalDate.now();
        Date convertedDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Optional<ImpliedVolatility> impliedVolatilityOptional = impliedVolatilityRepository.findByTickerAndDaysVolAndDate(ticker,30,convertedDate);
        if(impliedVolatilityOptional.isEmpty()){

        }else{
            return impliedVolatilityOptional.get().getCalculatedVolatility();
        }
        return null;
    }


    public CorrelationObject getCorrelationObject(String ticker, List<Bar> barList, List<TreasuryRate> treasuryRateList, List<DollarRate> dollarRateList){
        List<Double> stockChanges = new ArrayList<>();
        List<Double> treasuryChanges = new ArrayList<>();
        List<Double> dollarChanges = new ArrayList<>();
        treasuryRateList.sort(Comparator.comparing(TreasuryRate::getDate));
        dollarRateList.sort(Comparator.comparing(DollarRate::getDate));
        for(Bar bar : barList) {
            for (int x = 0; x < treasuryRateList.size(); x++) {
                if (treasuryRateList.get(x).getDate().getTime() < bar.getDate().getTime()) {
                    bar.setTreasuryRate(treasuryRateList.get(x).gettBillRate());
                }
            }
        }
        for(Bar bar : barList) {
            for (int x = 0; x < dollarRateList.size(); x++) {
                if (dollarRateList.get(x).getDate().getTime() < bar.getDate().getTime()) {
                    bar.setDollarValue(dollarRateList.get(x).getDollarRate());
                }
            }
        }
        for(int i =barList.size() - 1; i>0; i--){
            Bar current = barList.get(i);

//            stockChanges.add((current.getClose() - prior.getClose())
//                    / prior.getClose());
//            treasuryChanges.add((current.getTreasuryRate() - prior.getTreasuryRate())
//                    / prior.getTreasuryRate());
            stockChanges.add(current.getClose());
            treasuryChanges.add(current.getTreasuryRate());
            dollarChanges.add(current.getDollarValue());
        }

        LinkedHashMap<Date,Double> treasuryCorrelation30Days = new LinkedHashMap<>();
        LinkedHashMap<Date,Double> dollarCorrelation30Days = new LinkedHashMap<>();
        for(int i = 0; i <30; i++){
            List<Double> priceSublist = stockChanges.subList(i,i+30);
            List<Double> treasurySubList = treasuryChanges.subList(i,i+30);
            List<Double> dollarSubList = dollarChanges.subList(i,i+30);
            double[] x = convertListToArray(priceSublist);
            double[] y1 = convertListToArray(treasurySubList);
            double[] y2 = convertListToArray(dollarSubList);
            PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
            treasuryCorrelation30Days.put(barList.get(barList.size() - 1 - i).getDate(), pearsonsCorrelation.correlation(x,y1));
            dollarCorrelation30Days.put(barList.get(barList.size() - 1 - i).getDate(), pearsonsCorrelation.correlation(x,y2));
        }
        CorrelationObject correlationObject = new CorrelationObject(ticker,30,dollarCorrelation30Days, null,treasuryCorrelation30Days);
        return correlationObject;
    }


    public List<ShortSaleDataPoint> getShortSaleAverages(List<Bar> barList, List<FinraShortSaleDataPoint> finraShortSaleDataPointList){
        List<ShortSaleDataPoint> list = new ArrayList<>();
        finraShortSaleDataPointList.sort(Comparator.comparing(FinraShortSaleDataPoint::getDate));

        for(Bar bar : barList) {
            for (int x = 0; x < finraShortSaleDataPointList.size(); x++) {
                if (finraShortSaleDataPointList.get(x).getDate().getTime() < bar.getDate().getTime()) {
                    bar.setDailyShortSaleVolume(finraShortSaleDataPointList.get(x).getVolume());
                }
            }
        }

        for(int i =barList.size() - 1; i>0; i--) {
            List<Double> rawVolumes = new ArrayList<>();
            double sum = 0.0;
            Bar current = barList.get(i);
            for(int x = 0; x < 5; x++){
                if(i-x>0) {
                    rawVolumes.add(barList.get(i - x).getDailyShortSaleVolume());
                    sum += barList.get(i - x).getDailyShortSaleVolume();
                }
            }
            List<Double> totalVolumes = new ArrayList<>();
            double tvSum = 0.0;
            for(int x = 0; x < 5; x++){
                if(i-x>0) {
                    totalVolumes.add(barList.get(i - x).getVolume());
                    tvSum += barList.get(i - x).getVolume();
                }
            }
            list.add(new ShortSaleDataPoint(barList.get(i).getDate(),(sum/rawVolumes.size())/(tvSum/totalVolumes.size()) ));

        }

        return list;

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
