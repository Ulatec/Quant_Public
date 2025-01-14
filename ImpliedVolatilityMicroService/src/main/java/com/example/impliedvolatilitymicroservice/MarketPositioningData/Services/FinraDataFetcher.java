package com.example.impliedvolatilitymicroservice.MarketPositioningData.Services;


import com.example.impliedvolatilitymicroservice.MarketPositioningData.Model.FinraShortSaleDataPoint;
import com.example.impliedvolatilitymicroservice.MarketPositioningData.Repository.FinraShortSaleDataRepository;
import com.example.impliedvolatilitymicroservice.Services.TreasuryRateFetchingService;
import com.example.impliedvolatilitymicroservice.Utils.DateFormatter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class FinraDataFetcher {

    @Autowired
    FinraShortSaleDataRepository finraShortSaleDataRepository;

    private Logger logger = LoggerFactory.getLogger(TreasuryRateFetchingService.class);
    @Scheduled(fixedDelay = 3600000L)
    private void fetchFinraData(){
        LocalDate cutOff = LocalDate.of(2023,1,1);

        LocalDate trackingDate = LocalDate.now().minusDays(1);
        while(trackingDate.isAfter(cutOff)) {
            try {
                Connection.Response response = Jsoup.connect("https://cdn.finra.org/equity/regsho/daily/CNMSshvol" + DateFormatter.formatFinraLocalDate(trackingDate) + ".txt")
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .method(Connection.Method.GET)
                        .header("referer", "https://www.finra.org/")
                        .header("cookie", "_ga=GA1.2.80250311.1684518364; XSRF-TOKEN=bb6777ef-712d-4a5e-a2fc-bf8ea00be69c; AppSession=610ada04-b01d-4fb7-96c0-663e9927b738; _gid=GA1.2.316363237.1685024206; _gat_UA-134600757-1=1")
                        .execute();
                if(response.statusCode() != 403) {
                    String body = response.body();
                    List<FinraShortSaleDataPoint> dataPointList = parseFinraBody(body, trackingDate);

                    Optional<FinraShortSaleDataPoint> optional = finraShortSaleDataRepository.findFinraShortSaleDataPointByTickerAndDate(dataPointList.get(0).getTicker(), dataPointList.get(0).getDate());
                    if (optional.isEmpty()) {
                        finraShortSaleDataRepository.saveAll(dataPointList);
                        logger.info("Ingest of FINRA data for " + trackingDate + " completed at " + new Date());
                    } else {
                        logger.info("No additional FINRA data to ingest at " + new Date());
                    }
                }
                trackingDate = trackingDate.minusDays(1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public List<FinraShortSaleDataPoint> parseFinraBody(String body, LocalDate localDate){
        List<FinraShortSaleDataPoint> dataPointList = new ArrayList<>();
        Scanner scanner = new Scanner(body);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            try {
                String line = scanner.nextLine();
                String[] strings = line.split("\\|");

               // System.out.println(strings);
                FinraShortSaleDataPoint finraShortSaleDataPoint = new FinraShortSaleDataPoint(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),strings[1],Long.parseLong(strings[2]));
                dataPointList.add(finraShortSaleDataPoint);
            }catch (Exception e){
               // e.printStackTrace();
            }
        }
        scanner.close();
        return dataPointList;
    }
}
