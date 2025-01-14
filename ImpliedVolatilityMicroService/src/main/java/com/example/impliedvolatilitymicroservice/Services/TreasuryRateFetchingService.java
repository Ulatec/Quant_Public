package com.example.impliedvolatilitymicroservice.Services;


import com.example.impliedvolatilitymicroservice.IndexData.Model.DollarRate;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Repository.DollarRateRepository;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@EnableScheduling
public class TreasuryRateFetchingService {
    @Autowired
    private TreasuryRateRepository treasuryRateRepository;

    @Autowired
    private DollarRateRepository dollarRateRepository;

    private Logger logger = LoggerFactory.getLogger(TreasuryRateFetchingService.class);


    @Scheduled(fixedDelay = 3600000L)
    public void fetchTreasuryRates(){
        try {
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
            int index = 0;
            Date refreshDate = new Date();
            List<TreasuryRate> treasuryRateList = new ArrayList<>();
            for(Object object  : dateArray){
                JSONArray jsonObject = (JSONArray) rateArray.get(index);
                if(jsonObject.get(0) != JSONObject.NULL) {
                    Long dateLong = (Long) object;
                    Date date = new Date(dateLong);
                    Double rate = jsonObject.getDouble(0);
                    TreasuryRate treasuryRate = new TreasuryRate();
                    treasuryRate.settBillRate(rate);
                    treasuryRate.setDate(date);
                    treasuryRate.setDataDate(refreshDate);
                    treasuryRateList.add(treasuryRate);
                }
                index++;
            }
            Collections.reverse(treasuryRateList);
            int foundCounter = 0;
            for(TreasuryRate treasuryRate : treasuryRateList){
                if(foundCounter >= 5){
                    break;
                }else{
                    Optional<TreasuryRate> treasuryRateOptional = treasuryRateRepository.findByDate(treasuryRate.getDate());
                    if(treasuryRateOptional.isEmpty()){
                        treasuryRateRepository.save(treasuryRate);
                    }else{
                        TreasuryRate existingTreasuryRate = treasuryRateOptional.get();
                        existingTreasuryRate.settBillRate(treasuryRate.gettBillRate());
                        treasuryRateRepository.save(existingTreasuryRate);
                        foundCounter++;
                    }
                }
            }
            logger.info("Completed Treasury Data Ingest At " + new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Scheduled(fixedDelay = 3600000L)
    public void fetchDollarIndexValues(){
        try {
            Connection.Response response = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                            "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22IncludeMockTick%22%3Atrue%2C%22" +
                            "FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22" +
                            "InjectOpen%22%3Afalse%2C%22ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                            "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                            "Series%22%3A%5B%7B%22Key%22%3A%22INDEX%2FUS%2FIFUS%2FDXY%22%2C%22Dialect%22%3A%22Charting%22%2C%22" +
                            "Kind%22%3A%22Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                    .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();
            JSONObject treasuryDataJson = new JSONObject(response.body());
            JSONArray dateArray = treasuryDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
            JSONArray rateArray = treasuryDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");
            int index = 0;
            Date refreshDate = new Date();
            List<DollarRate> dollarRateList = new ArrayList<>();
            for(Object object  : dateArray){
                JSONArray jsonObject = (JSONArray) rateArray.get(index);
                if(jsonObject.get(0) != JSONObject.NULL) {
                    Long dateLong = (Long) object;
                    Date date = new Date(dateLong);
                    Double rate = jsonObject.getDouble(0);
                    DollarRate dollarRate = new DollarRate();
                    dollarRate.setDollarRate(rate);
                    dollarRate.setDate(date);
                    dollarRate.setDataDate(refreshDate);
                    dollarRateList.add(dollarRate);
                }
                index++;
            }
            Collections.reverse(dollarRateList);
            int foundCounter = 0;
            for(DollarRate dollarRate : dollarRateList){
                if(foundCounter >= 5){
                    break;
                }else{
                    Optional<DollarRate> dollarRateOptional = dollarRateRepository.findByDate(dollarRate.getDate());
                    if(dollarRateOptional.isEmpty()){
                        dollarRateRepository.save(dollarRate);
                    }else{
                        DollarRate existingDollarRate = dollarRateOptional.get();
                        existingDollarRate.setDollarRate(dollarRate.getDollarRate());
                        dollarRateRepository.save(existingDollarRate);
                        foundCounter++;
                    }
                }
            }
            logger.info("Completed Dollar Data Ingest At " + new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
