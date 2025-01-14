package com.example.frontendlistener.Service;


import com.example.frontendlistener.Model.DatabaseBar;
import com.example.frontendlistener.Model.Ticker;
import com.example.frontendlistener.Repository.DatabaseBarRepository;
import com.example.frontendlistener.Util.DateFormatter;
import org.h2.engine.Database;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
//@EnableScheduling
public class BarFetchingService {
    @Autowired
    DatabaseBarRepository barRepository;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Scheduled
    public void fetchAllBars() throws IOException, ParseException {
        LocalDate startDate = LocalDate.of(2020, 10, 8);
        LocalDate endDate = LocalDate.now();
//
        LocalDate priorEndDate = LocalDate.of(2023, 9, 29);
        LocalDate priorStartDate = LocalDate.of(2016, 9, 12);


        ArrayList<DatabaseBar> barListSafe = null;

        double pct = 1;


        Connection.Response bcomResponse = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                        "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints" +
                        "%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C" +
                        "%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series" +
                        "%22%3A%5B%7B%22Key%22%3A%22INDEX%2" +
                        "FXX%2F%2FBCOM%22%2C%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        JSONObject bcomData = new JSONObject(bcomResponse.body());
        JSONArray bcomDateArray = bcomData.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray bcomRateArray = bcomData.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");

        Connection.Response vixResponse = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?" +
                        "json=%7B%22Step%22%3A%22P1D%22%2C%22TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A" +
                        "%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22" +
                        "%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22" +
                        "%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C" +
                        "%22UseExtendedTimeFrame%22%3Atrue%2C%22WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C" +
                        "%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22Series%22%3A%5B%7B%22Key%22%3A%22INDEX%2FUS%2FCBSX%2FVIX%22%2C%22" +
                        "Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        JSONObject vixData = new JSONObject(vixResponse.body());
        JSONArray vixDateArray = vixData.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray vixRateArray = vixData.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");


        Connection.Response response = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                        "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22" +
                        "IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22" +
                        "IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22" +
                        "ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                        "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                        "Series%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD01M%22%2C%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22" +
                        "Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        JSONObject treasuryDataJson = new JSONObject(response.body());
        JSONArray dateArray = treasuryDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray rateArray = treasuryDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");

        Connection.Response twoYearResponse = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                        "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22" +
                        "IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22" +
                        "IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22" +
                        "ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                        "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                        "Series%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD02Y%22%2C" +
                        "%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        JSONObject twoYearJsonData = new JSONObject(twoYearResponse.body());
        JSONArray twoYearDateArray = twoYearJsonData.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray twoYearRateArray = twoYearJsonData.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");



      //  List<String> uniqueTickers = new ArrayList<>();
        List<Ticker> uniqueTickers = getAllTickerStrings(priorStartDate);
        for (Ticker uniqueTicker : uniqueTickers) {

            barListSafe = (ArrayList<DatabaseBar>) getBarsBetweenDates(priorStartDate, priorEndDate, uniqueTicker.getTicker());
            JSONObject jsonObject4;
            JSONArray resultArray4;
            String url4 = "https://api.polygon.io/v3/reference/splits?ticker=" + uniqueTicker + "&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
            while (true) {
                try {
                    Connection.Response response4 = Jsoup.connect
                                    (url4)
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .execute();

                    jsonObject4 = new JSONObject(response4.body());
                    resultArray4 = jsonObject4.getJSONArray("results");
                    break;
                } catch (Exception ignored) {

                }
            }

            for (Object object : resultArray4) {
                if (!resultArray4.isEmpty()) {
                    JSONObject resultObject = (JSONObject) object;
                    System.out.println(resultObject);
                    String dateString = resultObject.getString("execution_date");
                    Date date = sdf.parse(dateString);
                    double multiple = (double) resultObject.getInt("split_to") / resultObject.getInt("split_from");
                    for (DatabaseBar bar : barListSafe) {
                        if (bar.getDate() == date || bar.getDate().before(date)) {
                            bar.setSplitAdjustFactor(bar.getSplitAdjustFactor() * multiple);
                        }
                    }
                }
            }

            for (DatabaseBar bar : barListSafe) {
                for (int x = 1; x < dateArray.length(); x++) {
                    //if (dateArray.getLong(x) < (bar.getDate().getTime()-(60000*1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL && twoYearRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                    if (dateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && rateArray.getJSONArray(x).get(0) != null && rateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                        bar.setTreasuryRate(rateArray.getJSONArray(x).getDouble(0));
                        //bar.setYieldCurveValue(rateArray.getJSONArray(x).getDouble(0) - twoYearRateArray.getJSONArray(x).getDouble(0));
                    }
                }
            }
            for (DatabaseBar bar : barListSafe) {
                for (int x = 1; x < vixDateArray.length(); x++) {
                    if (vixDateArray.getLong(x) < (bar.getDate().getTime() - (60000 * 1440)) && vixRateArray.getJSONArray(x).get(0) != null && vixRateArray.getJSONArray(x).get(0) != JSONObject.NULL) {
                        bar.setVixValue(vixRateArray.getJSONArray(x).getDouble(0));
                    }
                }
            }
            for (DatabaseBar bar : barListSafe) {
                bar.setTicker(uniqueTicker.getTicker());
            }
            for(DatabaseBar bar : barListSafe){
                List<DatabaseBar> temp = barRepository.findAllByTickerAndDate(bar.getTicker(),bar.getDate());
                if(temp.size() == 0){
                    barRepository.save(bar);
                }
            }
        }

    }

    public List<Ticker> getAllTickerStrings(LocalDate priorStartDate){
                String url="https://api.polygon.io/v3/reference/tickers?active=true&limit=1000&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
        List<Ticker> allTickers = new ArrayList<>();
        List<Ticker> tickersToReturn = new ArrayList<>();
        try {



            int x = 0;
            //List<RangeSummary> rangeSummaryList = new ArrayList<>();

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
        double x = 0;
        for(Ticker ticker : allTickers){
            double sum = 0;
            List<DatabaseBar> bars = getBarsBetweenDates(priorStartDate.minusDays(30),priorStartDate,ticker.getTicker());
            if(bars != null) {
                for (DatabaseBar bar : bars) {
                    sum = sum + (bar.getVolume() * bar.getClose());
                }
                ticker.setThirtyDayDollarVolume(sum / bars.size());
            }
            System.out.println((new Date()) + "/" + x + "/" + allTickers.size());
            x++;
        }
        allTickers.sort(Comparator.comparing(Ticker::getThirtyDayDollarVolume).reversed());

        for(int i = 0; i < allTickers.size(); i++){
            System.out.println(allTickers.get(i).getTicker() + "\t" + allTickers.get(i).getThirtyDayDollarVolume());
        }

        for(int i = 0; i < 500; i++){
            tickersToReturn.add(allTickers.get(i));
        }
        return tickersToReturn;
    }

    public List<DatabaseBar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
        while(true) {
            try {
                String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" +
                        DateFormatter.formatLocalDate(from) + "/" + DateFormatter.formatLocalDate(to) + "?adjusted=true&sort=asc&limit=5500" +
                        "&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
                Connection.Response oilResponse = Jsoup.connect
                                (url)
                        .method(Connection.Method.GET)

                        .ignoreContentType(true)
                        .execute();
                JSONObject JsonObject = new JSONObject(oilResponse.body());
                if(JsonObject.has("results")) {
                    JSONArray resultArray = JsonObject.getJSONArray("results");
                    ArrayList<DatabaseBar> bars = new ArrayList<>();
                    for (Object object : resultArray) {
                        bars.add(buildBarFromJsonData(object));
                    }
                    return bars;
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public DatabaseBar buildBarFromJsonData(Object object){
        JSONObject resultObject = (JSONObject) object;
        DatabaseBar bar = new DatabaseBar();
        try {
            bar.setClose(resultObject.getDouble("c"));
            bar.setOpen(resultObject.getDouble("o"));
            bar.setHigh(resultObject.getDouble("h"));
            bar.setLow(resultObject.getDouble("l"));
            bar.setVolume(resultObject.getDouble("v"));
            if(resultObject.has("vw")) {
                bar.setVwap(resultObject.getDouble("vw"));
            }
            bar.setDate(new Date(resultObject.getLong("t") + (60000 * 1440)));
            bar.setSplitAdjustFactor(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bar;
    }
}