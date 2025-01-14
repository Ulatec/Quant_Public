package com.example.impliedvolatilitymicroservice.Controller;

import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Range.Utils.Service.RangeCalculationService;
import com.example.impliedvolatilitymicroservice.Repository.RangeSummaryRepository;
import com.example.impliedvolatilitymicroservice.Repository.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.impliedvolatilitymicroservice.IVCalculatorOld.ANSI_ORANGE;
import static com.example.impliedvolatilitymicroservice.IVCalculatorOld.ANSI_RESET;

@RestController
@CrossOrigin("*")
public class TrendScreener {
    @Autowired
    private TickerRepository tickerRepository;
    @Autowired
    private RangeSummaryRepository rangeSummaryRepository;
    @GetMapping("/screener/downside/{limit}")
    public List<RangeSummary> test(@PathVariable int limit){
        System.out.println("msg received");
        Instant now;
        Instant start;
        double delta;
        double rate;
        start = Instant.now();
        RangeCalculationService rangeCalculationService = new RangeCalculationService();
        List<RangeSummary> rangeSummaries = rangeSummaryRepository.findAll();
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        rate =  (double)delta/1000;
        System.out.println(ANSI_ORANGE + "Games per second: " + rate + "\n" + ANSI_RESET);
        rangeSummaries.sort(Comparator.comparing(RangeSummary::getDownsideRatio).reversed());
        rangeSummaries = rangeSummaries.stream()
                .filter(s -> s.getTrend() > s.getClose())
                .filter(s -> !s.getTicker().getType().equals("WARRANT"))
                .filter(s -> !s.getTicker().getType().equals("UNIT"))
                .filter(s -> !s.getTicker().getType().equals("PFD"))
                .filter(s -> !s.getTicker().getType().equals("RIGHT"))
                .filter(s -> !s.getTicker().getType().equals("FUND"))
                .filter(s -> Double.isFinite(s.getRangeTop()))
                .filter(s -> !Double.isNaN(s.getUpsideRatio()))
                .filter(s -> s.getUpsideRatio() != 0)
                .filter(s -> !s.getTicker().getName().contains("Acquisition"))
                .filter(s -> s.getChange() > 0)
                .filter(s -> s.getVolumeChange() < 0)
                .filter(s -> s.getMomentumChange60() < 0)
                .limit(limit)
                .collect(Collectors.toList());
        return rangeSummaries;
    }
    @GetMapping("/screener/upside/{limit}")
    public List<RangeSummary> upside(@PathVariable int limit){
        Instant now;
        Instant start;
        double delta;
        double rate;
        start = Instant.now();
        RangeCalculationService rangeCalculationService = new RangeCalculationService();
        List<RangeSummary> rangeSummaries = rangeSummaryRepository.findAll();
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        rate =  (double)delta/1000;
        System.out.println(ANSI_ORANGE + "Games per second: " + rate + "\n" + ANSI_RESET);
        rangeSummaries.sort(Comparator.comparing(RangeSummary::getUpsideRatio).reversed());
        rangeSummaries = rangeSummaries.stream()
                .filter(s -> s.getTrend() < s.getClose())
                .filter(s -> !s.getTicker().getType().equals("WARRANT"))
                .filter(s -> !s.getTicker().getType().equals("UNIT"))
                .filter(s -> !s.getTicker().getType().equals("PFD"))
                .filter(s -> !s.getTicker().getType().equals("RIGHT"))
                .filter(s -> !s.getTicker().getType().equals("FUND"))
                .filter(s -> Double.isFinite(s.getRangeTop()))
                .filter(s -> !Double.isNaN(s.getUpsideRatio()))
                .filter(s -> s.getUpsideRatio() != 0)
                .filter(s -> !s.getTicker().getName().contains("Acquisition"))
                .filter(s -> s.getChange() < 0)
                .filter(s -> s.getVolumeChange() < 0)
                .filter(s -> s.getMomentumChange60() > 0)
                .limit(limit)
                .collect(Collectors.toList());
        return rangeSummaries;
    }
}
