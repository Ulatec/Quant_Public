package com.example.impliedvolatilitymicroservice.API;

import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Utils.DateFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Polygon {

    public List<Bar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
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
                if(JsonObject.getInt("resultsCount") > 0) {
                    JSONArray resultArray = JsonObject.getJSONArray("results");
                    ArrayList<Bar> bars = new ArrayList<>();
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
    private Bar buildBarFromJsonData(Object object){
        JSONObject resultObject = (JSONObject) object;
        Bar bar = new Bar();
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
