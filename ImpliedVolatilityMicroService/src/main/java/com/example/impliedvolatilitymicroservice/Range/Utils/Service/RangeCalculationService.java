package com.example.impliedvolatilitymicroservice.Range.Utils.Service;


import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.Ticker;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Range.Utils.RangeCalculator;
import com.example.impliedvolatilitymicroservice.Range.Utils.Threads.RangeCalculatorThread;
import com.example.impliedvolatilitymicroservice.Range.Utils.Threads.RangeCalculatorThreadMonitor;
import com.example.impliedvolatilitymicroservice.Repository.ImpliedVolatilityRepository;
import com.example.impliedvolatilitymicroservice.Repository.RangeSummaryRepository;
import com.example.impliedvolatilitymicroservice.Repository.TickerRepository;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import com.example.impliedvolatilitymicroservice.Utils.ListSplitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@EnableScheduling
public class RangeCalculationService {

    @Autowired
    TreasuryRateRepository treasuryRateRepository;
    @Autowired
    ImpliedVolatilityRepository impliedVolatilityRepository;
    //@Autowired
   // private TickerRepository tickerRepository;
@Autowired
private RangeSummaryRepository rangeSummaryRepository;
    @Autowired
    private TickerRepository tickerRepository;
    @Scheduled(fixedDelay = 1800000L)
    public void fetchAllTickers(){
        List<TreasuryRate> ratesList = treasuryRateRepository.findAll();
        String url="https://api.polygon.io/v3/reference/tickers?active=true&limit=1000&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
        List<Ticker> allTickers = new ArrayList<>();
        try {



            int x = 0;
            List<RangeSummary> rangeSummaryList = new ArrayList<>();

            boolean hasNextUrl = true;
            while(hasNextUrl) {
                Connection.Response response = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject jsonObject = new JSONObject(response.body());
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                while (x < resultsArray.length()) {

                    JSONObject tickerObject = resultsArray.getJSONObject(x);
                    if (tickerObject.has("cik")) {
                        Ticker ticker;
//                        Optional<Ticker> tickerOptional = tickerRepository.findByTickerAndCik(tickerObject.getString("ticker"), tickerObject.getString("cik"));
//                        if (tickerOptional.isPresent()) {
//                            ticker = tickerOptional.get();
//                            allTickers.add(ticker);
//                        } else {
                            Ticker tempTicker = new Ticker();
                            tempTicker.setCik(tickerObject.getString("cik"));
                            tempTicker.setMarket(tickerObject.getString("market"));
                            tempTicker.setName(tickerObject.getString("name"));
                            if(tickerObject.has("type")) {
                                if(!tickerObject.getString("type").equals("WARRANT") &&
                                        !tickerObject.getString("type").equals("UNIT") &&
                                !tickerObject.getString("type").equals("RIGHT") &&
                                        !tickerObject.getString("type").equals("PFD") &&
                                        !tickerObject.getString("type").equals("FUND") &&
                                        !tickerObject.getString("name").contains("Acquisition")
                                ) {
                                    tempTicker.setType(tickerObject.getString("type"));
                                    tempTicker.setTicker(tickerObject.getString("ticker"));
                                    //tickerRepository.save(tempTicker);
                                    ticker = tempTicker;
                                    allTickers.add(ticker);
                                }
                          //  }

                        }

                    }
                    x++;
                }
                if(jsonObject.has("next_url")){
                    //hasNextUrl = true;
                    x=0;
                    url = jsonObject.getString("next_url") + "&active=true&limit=10&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                }else{
                    hasNextUrl = false;
                }
            }
            }catch (Exception e){
            e.printStackTrace();
        }
        List<List<Ticker>> tickerLists = ListSplitter.splitTickers(allTickers, 66);
        RangeCalculatorThreadMonitor rangeCalculatorThreadMonitor = new RangeCalculatorThreadMonitor(66,allTickers.size());

        for(int i = 0; i < 66; i++){
            RangeCalculatorThread rangeCalculatorThread = new RangeCalculatorThread(i, tickerLists.get(i), rangeCalculatorThreadMonitor);
            rangeCalculatorThread.setTreasuryRateList(ratesList);
            rangeCalculatorThread.setImpliedVolatilityRepository(impliedVolatilityRepository);
            rangeCalculatorThread.start();
        }
        while(rangeCalculatorThreadMonitor.getBackTestResults() == null){

        }
        List<RangeSummary> allSummaries = rangeCalculatorThreadMonitor.getBackTestResults();

        for(RangeSummary rangeSummary : allSummaries){
            String ticker = rangeSummary.getTicker().getTicker();
            Optional<RangeSummary> rangeSummaryTemp = rangeSummaryRepository.findByTickerString(ticker);
            Optional<Ticker> optionalTicker = tickerRepository.findByTickerAndCik(ticker, rangeSummary.getTicker().getCik());
            Ticker ticker1;
            if(optionalTicker.isEmpty()){
                tickerRepository.save(rangeSummary.getTicker());
                ticker1 = tickerRepository.findByTickerAndCik(rangeSummary.getTicker().getTicker(), rangeSummary.getTicker().getCik()).get();
            }else{
                ticker1 = tickerRepository.findByTickerAndCik(rangeSummary.getTicker().getTicker(), rangeSummary.getTicker().getCik()).get();
            }

            if(rangeSummaryTemp.isPresent()){
                RangeSummary rangeSummary1 = rangeSummaryTemp.get();
                rangeSummary1.setChange(rangeSummary.getChange());
                rangeSummary1.setVolumeChange(rangeSummary.getVolumeChange());
                rangeSummary1.setVolatility(rangeSummary.getVolatility());
                rangeSummary1.setRangeBot(rangeSummary.getRangeBot());
                rangeSummary1.setRangeTop(rangeSummary.getRangeTop());
                rangeSummary1.setPctFromTrend(rangeSummary.getPctFromTrend());
                rangeSummary1.setMomentumChange30(rangeSummary.getMomentumChange30());
                rangeSummary1.setMomentumChange60(rangeSummary.getMomentumChange60());
                rangeSummary1.setUpsideRatio(rangeSummary.getUpsideRatio());
                rangeSummary1.setDownsideRatio(rangeSummary.getDownsideRatio());
                rangeSummary1.setTrend(rangeSummary.getTrend());
                rangeSummary1.setTrade(rangeSummary.getTrade());
                rangeSummary1.setTicker(rangeSummary.getTicker());
                rangeSummary1.setIwmCorrelation(rangeSummary.getIwmCorrelation());
                rangeSummary1.setQqqCorrelation(rangeSummary.getQqqCorrelation());
                rangeSummary1.setSpyCorrelation(rangeSummary.getSpyCorrelation());
                rangeSummary1.setUpside(rangeSummary.getUpside());
                rangeSummary1.setDownside(rangeSummary.getDownside());
                rangeSummary1.setClose(rangeSummary.getClose());
                rangeSummary1.setOpen(rangeSummary.getOpen());
                rangeSummary1.setTicker(ticker1);
                rangeSummary1.setIvPremium(rangeSummary.getIvPremium());
                rangeSummary1.setImpliedVolatility(rangeSummary.getImpliedVolatility());
                rangeSummary1.setVolatility(rangeSummary.getVolatility());
                rangeSummaryRepository.save(rangeSummary1);
            }else{
                rangeSummary.setTicker(ticker1);
                rangeSummaryRepository.save(rangeSummary);
            }
        }
        System.out.println(allSummaries.size());

    }


}
