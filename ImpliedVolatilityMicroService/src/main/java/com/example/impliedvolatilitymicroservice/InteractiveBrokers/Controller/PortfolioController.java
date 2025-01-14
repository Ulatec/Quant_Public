package com.example.impliedvolatilitymicroservice.InteractiveBrokers.Controller;


import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.InteractiveBrokers.Constraints;
import com.example.impliedvolatilitymicroservice.InteractiveBrokers.Model.PortfolioReturnObject;
import com.example.impliedvolatilitymicroservice.InteractiveBrokers.Model.Position;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.Ticker;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Range.Utils.Service.RangeCalculationService;
import com.example.impliedvolatilitymicroservice.Range.Utils.Threads.RangeCalculatorThread;
import com.example.impliedvolatilitymicroservice.Range.Utils.Threads.RangeCalculatorThreadMonitor;
import com.example.impliedvolatilitymicroservice.Repository.ImpliedVolatilityRepository;
import com.example.impliedvolatilitymicroservice.Repository.TickerRepository;
import com.example.impliedvolatilitymicroservice.Utils.ListSplitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin("*")
public class PortfolioController {

    @Autowired
    private TickerRepository tickerRepository;

    @Autowired
    private ImpliedVolatilityRepository impliedVolatilityRepository;
    @GetMapping("/portfolio")
    public PortfolioReturnObject getPortfolio(){
        Polygon polygon = new Polygon();
        try{
            Connection.Response responseX = Jsoup.connect("http://localhost:5000/v1/api/portfolio/subaccounts")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            Connection.Response response = Jsoup.connect("http://localhost:5000/v1/api/portfolio/U1909472/positions")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            if(response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                List<Position> positionList = new ArrayList<>();
                List<String> positionStrings = new ArrayList<>();
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) object;
                    Position position = new Position();
                    if(jsonObject.getDouble("position") != 0.0){
                        position.setShares(jsonObject.getDouble("position"));
                        position.setMarketPrice(jsonObject.getDouble("mktPrice"));
                        position.setDollarAmount(jsonObject.getDouble("position") * jsonObject.getDouble("mktPrice"));
                        position.setSymbol(jsonObject.getString("contractDesc"));
                        positionList.add(position);
                        positionStrings.add(jsonObject.getString("contractDesc"));
                    }
                }
                PortfolioReturnObject portfolioReturnObject = new PortfolioReturnObject();

                Connection.Response summaryResponse = Jsoup.connect("http://localhost:5000/v1/api/portfolio/U1909472/summary")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .execute();
                if(summaryResponse.statusCode() == 200) {
                    JSONObject summaryArray = new JSONObject(summaryResponse.body());
                    portfolioReturnObject.setTotalValue(summaryArray.getJSONObject("netliquidation").getDouble("amount"));
                }
                //RangeCalculationService rangeCalculationService = new RangeCalculationService();
                List<RangeSummary> rangeSummaries = fetchRangesOfList(positionStrings);
                List<String> bondTickers = Stream.of(Constraints.BondTickers.values())
                        .map(Enum::name)
                        .toList();
                List<String> commodityTickers = Stream.of(Constraints.CommodityTickers.values())
                        .map(Enum::name)
                        .toList();
                List<String> currencyTickers = Stream.of(Constraints.CurrencyTickers.values())
                        .map(Enum::name)
                        .toList();
                double totalVol = 0.0;
                int alerts = 0;
                for(Position position : positionList){
                    for(RangeSummary rangeSummary : rangeSummaries){
                        String modifiedTickerString = rangeSummary.getTicker().getTicker();
                        if(modifiedTickerString.equals("C PRN")){
                            System.out.println("asdasd");
                        }
                        if(position.getSymbol().replace(" PR", "p").equals(modifiedTickerString)){
                            double positionPercent = position.getDollarAmount()/portfolioReturnObject.getTotalValue();
                            position.setTicker(rangeSummary.getTicker());
                            position.setRangeSummary(rangeSummary);
                            double pct = (rangeSummary.getClose() - rangeSummary.getTrend())/rangeSummary.getTrend();
                            position.setPercentFromTrend(pct);
                            if(position.getShares() > 0){
                                if(rangeSummary.getClose() < rangeSummary.getTrend()){
                                    position.setAlert(true);
                                    position.setAlertComment("bearish trend");
                                    alerts++;
                                }
                            }else if(position.getShares() < 0){
                                if(rangeSummary.getClose() > rangeSummary.getTrend()){
                                    position.setAlert(true);
                                    position.setAlertComment("bullish trend");
                                    alerts++;
                                }
                            }
                            if(position.getSymbol().contains(" ")){

                            }else
                            if(bondTickers.contains(position.getSymbol())){
                                //limit = 5%
                                if(positionPercent>=0.05){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }else if(positionPercent<=-0.025){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }
                            }
                            else if(commodityTickers.contains(position.getSymbol())){
                                if(positionPercent>=0.04){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }else if(positionPercent<=-0.02){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }
                            }
                            else if(currencyTickers.contains(position.getSymbol())){
                                if(positionPercent>=0.06){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }else if(positionPercent<=-0.030){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }
                            }else if(rangeSummary.getTicker().getType().equals("ETF")){
                                if(positionPercent>=0.05){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }else if(positionPercent<=-0.03){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }
                            }else{
                                //assume equity
                                if(positionPercent>=0.025){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }else if(positionPercent<=-0.0125){
                                    position.setAlert(true);
                                    position.setAlertComment("size");
                                    alerts++;
                                }
                            }
                            position.setPositionVol(rangeSummary.getVolatility() * positionPercent * 100);
                            totalVol +=rangeSummary.getVolatility() * positionPercent * 100;
//                            if(position.getTicker().getType().equals("ETF")){
//                                if(po)
//                            }
                            break;
                        }
                    }
                }
                Double dollarTotal = 0.0;
                List<Double> spyCorrelation = new ArrayList<>();
                List<Double> qqqCorrelation = new ArrayList<>();
                List<Double> iwmCorrelation = new ArrayList<>();
                for(Position position: positionList){
                    if(position.getRangeSummary() == null){
                        position.setRangeSummary(new RangeSummary());
                    }else{
                        position.setDownside(position.getRangeSummary().getDownside());
                        position.setUpside(position.getRangeSummary().getUpside());
                        position.setUpsideRatio(position.getRangeSummary().getUpsideRatio());
                    }
                    dollarTotal = dollarTotal + Math.abs(position.getDollarAmount());
                }
                for(Position position : positionList){
                    if(position.getShares()>0) {
                        spyCorrelation.add((Math.abs(position.getDollarAmount()) / dollarTotal) * position.getRangeSummary().getSpyCorrelation());
                    }else{
                        spyCorrelation.add((Math.abs(position.getDollarAmount()) / dollarTotal) * -1 * position.getRangeSummary().getSpyCorrelation());
                    }
                    if(position.getShares()>0) {
                        qqqCorrelation.add((Math.abs(position.getDollarAmount())/dollarTotal) * position.getRangeSummary().getQqqCorrelation());
                    }else{
                        qqqCorrelation.add((Math.abs(position.getDollarAmount())/dollarTotal) * -1 * position.getRangeSummary().getQqqCorrelation());
                    }
                    if(position.getShares()>0) {
                        iwmCorrelation.add((Math.abs(position.getDollarAmount()) / dollarTotal) * position.getRangeSummary().getIwmCorrelation());
                    }else{
                        iwmCorrelation.add((Math.abs(position.getDollarAmount()) / dollarTotal) * -1 * position.getRangeSummary().getIwmCorrelation());
                    }
                }
                double alertDollars = 0.0;
                for(Position position : positionList){
                    if(position.isAlert()){
                        alertDollars += Math.abs(position.getDollarAmount());
                    }
                }
                DoubleSummaryStatistics summaryStatistics = spyCorrelation.stream().collect(Collectors.summarizingDouble(e -> e));
                DoubleSummaryStatistics qqqSummary = qqqCorrelation.stream().collect(Collectors.summarizingDouble(e -> e));
                DoubleSummaryStatistics iwmSummary = iwmCorrelation.stream().collect(Collectors.summarizingDouble(e -> e));
                portfolioReturnObject.setSpyCorrelation(summaryStatistics.getSum());
                portfolioReturnObject.setQqqCorrelation(qqqSummary.getSum());
                portfolioReturnObject.setIwmCorrelation(iwmSummary.getSum());
                positionList.sort(Comparator.comparing(Position::getPositionVol).reversed());
                portfolioReturnObject.setTotalVol(totalVol);
                portfolioReturnObject.setPercentAlert((double)alerts/positionList.size());
                portfolioReturnObject.setPositionList(positionList);
                portfolioReturnObject.setDollarPercentAlert(alertDollars/portfolioReturnObject.getTotalValue());
                return portfolioReturnObject;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }
    public List<RangeSummary> fetchRangesOfList(List<String> tickerList){

        //String url="https://api.polygon.io/v3/reference/tickers?active=true&limit=1000&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
        List<Ticker> allTickers = new ArrayList<>();

        for(String tickerString : tickerList){
            tickerString = tickerString.replace(" PR", "p");
            String url = "https://api.polygon.io/v3/reference/tickers?ticker="+tickerString+"&active=true&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
            try {

                Connection.Response response = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                JSONObject jsonObject = new JSONObject(response.body());
                if(jsonObject.getInt("count") > 0) {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    JSONObject tickerObject = resultsArray.getJSONObject(0);
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
                        if (tickerObject.has("type")) {
                            if (!tickerObject.getString("type").equals("WARRANT") &&
                                    !tickerObject.getString("type").equals("UNIT") &&
                                    !tickerObject.getString("type").equals("RIGHT") &&
//                                    !tickerObject.getString("type").equals("PFD") &&
                                    !tickerObject.getString("type").equals("FUND") &&
                                    !tickerObject.getString("name").contains("Acquisition")
                            ) {
                                tempTicker.setType(tickerObject.getString("type"));
                                tempTicker.setTicker(tickerObject.getString("ticker"));
                                //tickerRepository.save(tempTicker);
                                ticker = tempTicker;
                                allTickers.add(ticker);
                            } else {
                                System.out.println("skip");
                            }
                            //  }

                        }
                    }
                }
            }catch (Exception e) {
                //   e.printStackTrace();
            }
        }




        List<List<Ticker>> tickerLists = ListSplitter.splitTickers(allTickers, 256);
        RangeCalculatorThreadMonitor rangeCalculatorThreadMonitor = new RangeCalculatorThreadMonitor(256,allTickers.size());

        for(int i = 0; i < 256; i++){
            RangeCalculatorThread rangeCalculatorThread = new RangeCalculatorThread(i, tickerLists.get(i), rangeCalculatorThreadMonitor);
            rangeCalculatorThread.setImpliedVolatilityRepository(impliedVolatilityRepository);
            rangeCalculatorThread.start();
        }
        while(rangeCalculatorThreadMonitor.getBackTestResults() == null){

        }
        List<RangeSummary> allSummaries = rangeCalculatorThreadMonitor.getBackTestResults();
        System.out.println(allSummaries.size());

//        allSummaries.sort(RangeSummary::getDownsideRatio);
//        allSummaries.sort(RangeSummary::compareTo);

        return allSummaries;
    }
}
