package com.example.impliedvolatilitymicroservice.Utils;

import java.time.LocalDate;

public class DateFormatter {

    public static String formatLocalDate(LocalDate localDate){

        String dateString = localDate.getYear() + "-";
        if(localDate.getMonthValue()<10){
            dateString = dateString + "0" + localDate.getMonthValue();
        }else{
            dateString = dateString + localDate.getMonthValue();
        }

        dateString = dateString + "-";
        if(localDate.getDayOfMonth()<10){
            dateString = dateString + "0" + localDate.getDayOfMonth();
        }else{
            dateString = dateString + localDate.getDayOfMonth();
        }
        return dateString;
    }

    public static String formatFinraLocalDate(LocalDate localDate){

        String dateString = String.valueOf(localDate.getYear());
        if(localDate.getMonthValue()<10){
            dateString = dateString + "0" + localDate.getMonthValue();
        }else{
            dateString = dateString + localDate.getMonthValue();
        }

       // dateString = dateString + "-";
        if(localDate.getDayOfMonth()<10){
            dateString = dateString + "0" + localDate.getDayOfMonth();
        }else{
            dateString = dateString + localDate.getDayOfMonth();
        }
        return dateString;
    }
}
