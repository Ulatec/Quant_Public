package com.example.impliedvolatilitymicroservice.AggregateOptions.Service;

import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.AggregateOptions.Model.AggregateOptionDataPoint;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Services.TreasuryRateFetchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class OptionDataFetchingService {

    private Logger logger = LoggerFactory.getLogger(TreasuryRateFetchingService.class);


    //@Scheduled(fixedDelay = 3600000)
    public List<AggregateOptionDataPoint> getAggregateDataPointsForTicker(String ticker){

        Polygon polygon = new Polygon();
        LocalDate localDate = LocalDate.now();
        Date parsedDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        LocalDate priorEndDate = LocalDate.from(parsedDate.toInstant().atZone(ZoneId.systemDefault()));
        LocalDate priorStartDate = priorEndDate.minusDays(30);
        List<Bar> barList = polygon.getBarsBetweenDates(priorStartDate,priorEndDate,ticker);

        for(Bar bar : barList){


        }


        return null;
    }

    public long getTotalTradedOptionsOnDate(String date, String ticker){

        return 0L;
    }
}
