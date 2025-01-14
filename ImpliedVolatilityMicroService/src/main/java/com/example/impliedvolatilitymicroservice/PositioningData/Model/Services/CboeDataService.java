package com.example.impliedvolatilitymicroservice.PositioningData.Model.Services;

import com.example.impliedvolatilitymicroservice.IndexData.Model.DollarRate;
import com.example.impliedvolatilitymicroservice.MarketPositioningData.Model.FinraShortSaleDataPoint;
import com.example.impliedvolatilitymicroservice.PositioningData.Model.CboeDailySummary;
import com.example.impliedvolatilitymicroservice.PositioningData.Model.Repository.CboeDailySummaryRepository;
import com.example.impliedvolatilitymicroservice.Services.TreasuryRateFetchingService;
import com.example.impliedvolatilitymicroservice.Utils.DateFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@EnableAsync
@EnableScheduling
public class CboeDataService {

    @Autowired
    private CboeDailySummaryRepository cboeDailySummaryRepository;
    private Logger logger = LoggerFactory.getLogger(CboeDataService.class);
    @Scheduled(fixedDelay = 3600000L)
    public void fetchDollarIndexValues(){
        LocalDate cutOff = LocalDate.of(2023,1,1);
       // LocalDate now = LocalDate.now();
        LocalDate trackingDate = LocalDate.now().minusDays(1);
        while(trackingDate.isAfter(cutOff)) {
            try {
                Connection.Response response = Jsoup.connect("https://cdn.cboe.com/data/us/options/market_statistics/daily/" + DateFormatter.formatLocalDate(trackingDate) + "_daily_options")
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .method(Connection.Method.GET)
                        .header("referer", "https://www.cboe.com/")
                        //.header("cookie", "_ga=GA1.2.80250311.1684518364; XSRF-TOKEN=bb6777ef-712d-4a5e-a2fc-bf8ea00be69c; AppSession=610ada04-b01d-4fb7-96c0-663e9927b738; _gid=GA1.2.316363237.1685024206; _gat_UA-134600757-1=1")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 OPR/98.0.0.0")
                        .header("x-requested-with", "XMLHttpRequest")
                        .execute();
                if(response.statusCode() == 200) {
                    String body = response.body();
                    JSONObject jsonObject = new JSONObject(body);
                    if(jsonObject.has("EQUITY OPTIONS")) {
                        CboeDailySummary cboeDailySummary = new CboeDailySummary();
                        cboeDailySummary.setDate(Date.from(trackingDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                        for (Object object : jsonObject.getJSONArray("EQUITY OPTIONS")) {
                            JSONObject dataObject = (JSONObject) object;
                            if (dataObject.getString("name").equals("VOLUME")) {
                                cboeDailySummary.setEquityOptionsCalls(dataObject.getLong("call"));
                                cboeDailySummary.setEquityOptionsPuts(dataObject.getLong("put"));
                            }
                            if (dataObject.getString("name").equals("OPEN INTEREST")) {
                                cboeDailySummary.setEquityOptionsCallsOpenInterest(dataObject.getLong("call"));
                                cboeDailySummary.setEquityOptionsPutsOpenInterest(dataObject.getLong("put"));
                            }
                        }


                            Optional<CboeDailySummary> dailySummaryOptional = cboeDailySummaryRepository.findCboeDailySummaryByDate(Date.from(trackingDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                            System.out.println(jsonObject);
                            if (dailySummaryOptional.isEmpty()) {
                                cboeDailySummaryRepository.save(cboeDailySummary);
                                logger.info("Ingest of CBOE data for " + trackingDate + " completed at " + new Date());
                            } else {
                                CboeDailySummary temp = dailySummaryOptional.get();
                                temp.setEquityOptionsPutsOpenInterest(cboeDailySummary.getEquityOptionsPutsOpenInterest());
                                temp.setEquityOptionsPuts(cboeDailySummary.getEquityOptionsPuts());
                                temp.setEquityOptionsCalls(cboeDailySummary.getEquityOptionsCalls());
                                temp.setEquityOptionsCallsOpenInterest(cboeDailySummary.getEquityOptionsCallsOpenInterest());
                                cboeDailySummaryRepository.save(temp);
                                logger.info("updated CBOE data to ingest at " + new Date());
                            }
                        }
                }
                // logger.info("Completed Dollar Data Ingest At " + new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
            trackingDate = trackingDate.minusDays(1);
        }
    }
}
