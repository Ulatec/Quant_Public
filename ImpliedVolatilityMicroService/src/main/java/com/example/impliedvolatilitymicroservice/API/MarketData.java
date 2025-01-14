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

public class MarketData {

    public List<Bar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
        while(true) {
            try {
                String url = "https://api.marketdata.app/v1/stocks/candles/D/" + ticker + "?from=" +
                        DateFormatter.formatLocalDate(from) + "&to=" + DateFormatter.formatLocalDate(to) +
                        "&token=R3pONDdNLU1iNnl6LWJ0ZE5rRU5CSThrbnlMSHJqSzY2SmpUVHNfdXktVT0";
                Connection.Response oilResponse = Jsoup.connect
                                (url)
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .execute();
                if(oilResponse.statusCode() == 200) {
                    JSONObject JsonObject = new JSONObject(oilResponse.body());
                    if (JsonObject.getJSONArray("c").length() > 0) {
                        List<Bar> bars = buildBarFromJsonData(JsonObject);
//                    for (Object object : resultArray) {
//                        bars.add(buildBarFromJsonData(object));
//                    }
                        return bars;
                    } else {
                        return null;
                    }
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private List<Bar> buildBarFromJsonData(Object object){
        JSONObject resultObject = (JSONObject) object;
        JSONArray closeArray = resultObject.getJSONArray("c");
        JSONArray highArray = resultObject.getJSONArray("h");
        JSONArray lowArray = resultObject.getJSONArray("l");
        JSONArray openArray = resultObject.getJSONArray("o");
        JSONArray dateArray = resultObject.getJSONArray("t");
        JSONArray volumeArray = resultObject.getJSONArray("v");
        List<Bar> barList = new ArrayList<>();
        for(int i = 0; i < closeArray.length(); i++){
            Bar bar = new Bar();
            bar.setClose(closeArray.getDouble(i));
            bar.setOpen(openArray.getDouble(i));
            bar.setHigh(highArray.getDouble(i));
            bar.setLow(lowArray.getDouble(i));
            bar.setDate(new Date(dateArray.getLong(i) * 1000));
            bar.setVolume(volumeArray.getDouble(i));
            barList.add(bar);
        }

        try {
//            bar.setClose(resultObject.getDouble("c"));
//            bar.setOpen(resultObject.getDouble("o"));
//            bar.setHigh(resultObject.getDouble("h"));
//            bar.setLow(resultObject.getDouble("l"));
//            bar.setVolume(resultObject.getDouble("v"));
//            if(resultObject.has("vw")) {
//                bar.setVwap(resultObject.getDouble("vw"));
//            }
//            bar.setDate(new Date(resultObject.getLong("t") + (60000 * 1440)));
//            bar.setSplitAdjustFactor(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return barList;
    }
}
