package com.example.impliedvolatilitymicroservice.InteractiveBrokers;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InteractiveBrokersService {
    String pingUrl = "http://localhost:5001/v1/api/tickle";
    @Scheduled(fixedDelay = 360000L)
    public void ping(){

        try{
            Connection.Response response = Jsoup.connect(pingUrl)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            if(response.statusCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.body());
                System.out.println(jsonObject);
                System.out.println("ibkr tickled.");
            }else{
                System.out.println("not authorized.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
