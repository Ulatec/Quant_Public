package com.example.impliedvolatilitymicroservice.MacroData;

import com.example.impliedvolatilitymicroservice.MacroData.Model.MacroDataPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RedbookRetailSalesService {

    String url="https://sbcharts.investing.com/events_charts/us/911.json";

    @Scheduled(fixedDelay = 3600000L)
    public void redbook(){
        try{
            Connection.Response response = Jsoup.connect(url)
                    .method(org.jsoup.Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body());
            System.out.println(jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            String series = "redbook";
            for(Object object : jsonArray){
                MacroDataPoint macroDataPoint = new MacroDataPoint();

                JSONArray jsonArray1 = (JSONArray) object;
                Date date = new Date(jsonArray1.getLong(0));
                double value = jsonArray1.getDouble(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
