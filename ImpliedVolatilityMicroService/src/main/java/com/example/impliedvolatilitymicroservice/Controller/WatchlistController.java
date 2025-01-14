package com.example.impliedvolatilitymicroservice.Controller;

import com.example.impliedvolatilitymicroservice.API.MarketData;
import com.example.impliedvolatilitymicroservice.API.Polygon;
import com.example.impliedvolatilitymicroservice.IVCalculator;
import com.example.impliedvolatilitymicroservice.Libraries.StockCalculationLibrary;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Model.Watchlist;
import com.example.impliedvolatilitymicroservice.Model.WatchlistItem;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import com.example.impliedvolatilitymicroservice.Repository.RangeSummaryRepository;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import com.example.impliedvolatilitymicroservice.Repository.WatchlistRepository;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@CrossOrigin("*")
public class WatchlistController {
    @Autowired
    private WatchlistRepository watchlistRepository;
    @Autowired
    private TreasuryRateRepository treasuryRateRepository;
    @Autowired
    private RangeSummaryRepository rangeSummaryRepository;
    @GetMapping("/allWatchLists/")
    public List<Watchlist> getAllWatchLists(){
        return watchlistRepository.findAll();
    }
    @PostMapping("/newWatchList/")
    public boolean newWatchlist(@RequestBody Watchlist watchlist) throws URISyntaxException{
        Watchlist newWatchlist = new Watchlist();
        List<Watchlist> allLists = watchlistRepository.findAll();
        boolean found = false;
        for(Watchlist list : allLists){
            if (list.getName().equalsIgnoreCase(watchlist.getName())) {
                found = true;
                break;
            }
        }
        if(!found){
            newWatchlist.setName(watchlist.getName());
            newWatchlist.setSize(0);
            watchlistRepository.save(newWatchlist);
            return true;
        }else {

            return false;
        }
    }
    @PostMapping("/addTicker/")
    public boolean addTicker(@RequestBody String test) throws URISyntaxException{
        System.out.println(test);
        JSONObject jsonObject = new JSONObject(test);
        boolean isShort = jsonObject.getBoolean("isShort");
        Optional<Watchlist> optional = watchlistRepository.findById(jsonObject.getLong("id"));
        if(optional.isPresent()){
            List<String> longTickers = optional.get().getLongTickers();
            List<String> shortTickers = optional.get().getShortTickers();
            if(isShort){
                boolean found = false;
                for(String string : longTickers){
                    if(string.equalsIgnoreCase(jsonObject.getString("ticker"))){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Watchlist watchlist = optional.get();
                    longTickers.add(jsonObject.getString("ticker"));
                    watchlist.setLongTickers(longTickers);
                    watchlist.setSize(longTickers.size());
                    watchlistRepository.save(watchlist);
                    return true;
                }else{
                    return false;
                }
            }else {
                boolean found = false;
                for (String string : shortTickers) {
                    if (string.equalsIgnoreCase(jsonObject.getString("ticker"))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Watchlist watchlist = optional.get();
                    shortTickers.add(jsonObject.getString("ticker"));
                    watchlist.setShortTickers(shortTickers);
                    watchlist.setSize(shortTickers.size());
                    watchlistRepository.save(watchlist);
                    return true;
                } else {
                    return false;
                }
            }
        }else{
            return false;
        }
    }
    @PostMapping("/deleteTicker/")
    public boolean deleteTicker(@RequestBody String test) throws URISyntaxException{
        System.out.println(test);
        JSONObject jsonObject = new JSONObject(test);

        Optional<Watchlist> optional = watchlistRepository.findById(jsonObject.getLong("id"));
        if(optional.isPresent()){
            List<String> longTickers = optional.get().getLongTickers();
            List<String> shortTickers = optional.get().getShortTickers();

            boolean found = false;
            for(String string : longTickers){
                if(string.equalsIgnoreCase(jsonObject.getString("ticker"))){
                    found = true;
                }
            }
            for(String string : shortTickers){
                if(string.equalsIgnoreCase(jsonObject.getString("ticker"))){
                    found = true;
                }
            }
            if(found){
                //LONG
                Watchlist watchlist = optional.get();
                Iterator<String> stringIterator = watchlist.getLongTickers().listIterator();
                while(stringIterator.hasNext()){
                    String string = stringIterator.next();
                    if(string.equalsIgnoreCase(jsonObject.getString("ticker"))){
                        stringIterator.remove();
                        break;
                    }
                }
                watchlist.setLongTickers(longTickers);
                watchlist.setSize(longTickers.size());
                watchlistRepository.save(watchlist);
                //SHORT
                Iterator<String> shortStringIterator = watchlist.getShortTickers().listIterator();
                while(shortStringIterator.hasNext()){
                    String string = shortStringIterator.next();
                    if(string.equalsIgnoreCase(jsonObject.getString("ticker"))){
                        shortStringIterator.remove();
                        break;
                    }
                }
                watchlist.setShortTickers(shortTickers);
                watchlist.setSize(shortTickers.size());
                watchlistRepository.save(watchlist);

                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    @GetMapping("/watchlist/{id}")
    public List<WatchlistItem> addTicker(@PathVariable int id) throws URISyntaxException{
        Optional<Watchlist> optional = watchlistRepository.findById((long) id);
        if(optional.isPresent()){
            List<WatchlistItem> watchlistItems = new ArrayList<>();
            for(String ticker : optional.get().getLongTickers()){
                WatchlistItem watchlistItem = new WatchlistItem();
                watchlistItem.setTicker(ticker);
                Optional<RangeSummary> rangeSummaryOptional = rangeSummaryRepository.findByTickerString(ticker);
                if(rangeSummaryOptional.isPresent()){
                    watchlistItem.setUpside(rangeSummaryOptional.get().getUpside());
                    watchlistItem.setDownside(rangeSummaryOptional.get().getDownside());
                    watchlistItem.setMomentum30(rangeSummaryOptional.get().getMomentumChange30());
                    watchlistItem.setMomentum60(rangeSummaryOptional.get().getMomentumChange60());
                    watchlistItem.setTrend(rangeSummaryOptional.get().getTrend());
                    watchlistItem.setTrade(rangeSummaryOptional.get().getTrade());
                    watchlistItem.setVolumeChange(rangeSummaryOptional.get().getVolumeChange());
                    watchlistItem.setBullishTrade(rangeSummaryOptional.get().getClose() > rangeSummaryOptional.get().getTrade());
                    watchlistItem.setBullishTrend(rangeSummaryOptional.get().getClose() > rangeSummaryOptional.get().getTrend());
                    watchlistItem.setClose(rangeSummaryOptional.get().getClose());
                    watchlistItem.setIvPremium(rangeSummaryOptional.get().getIvPremium());
                }
                watchlistItems.add(watchlistItem);
            }
            for(String ticker : optional.get().getShortTickers()){
                WatchlistItem watchlistItem = new WatchlistItem();
                watchlistItem.setTicker(ticker);
                Optional<RangeSummary> rangeSummaryOptional = rangeSummaryRepository.findByTickerString(ticker);
                if(rangeSummaryOptional.isPresent()){
                    watchlistItem.setUpside(rangeSummaryOptional.get().getUpside());
                    watchlistItem.setDownside(rangeSummaryOptional.get().getDownside());
                    watchlistItem.setMomentum30(rangeSummaryOptional.get().getMomentumChange30());
                    watchlistItem.setMomentum60(rangeSummaryOptional.get().getMomentumChange60());
                    watchlistItem.setTrend(rangeSummaryOptional.get().getTrend());
                    watchlistItem.setTrade(rangeSummaryOptional.get().getTrade());
                    watchlistItem.setVolumeChange(rangeSummaryOptional.get().getVolumeChange());
                    watchlistItem.setBullishTrade(rangeSummaryOptional.get().getClose() > rangeSummaryOptional.get().getTrade());
                    watchlistItem.setBullishTrend(rangeSummaryOptional.get().getClose() > rangeSummaryOptional.get().getTrend());
                    watchlistItem.setClose(rangeSummaryOptional.get().getClose());
                    watchlistItem.setIvPremium(rangeSummaryOptional.get().getIvPremium());
                }
                watchlistItems.add(watchlistItem);
            }
            return watchlistItems;
        }else{
            return null;
        }
    }

}
