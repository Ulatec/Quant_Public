package com.example.impliedvolatilitymicroservice.Libraries;



import com.example.impliedvolatilitymicroservice.Model.Bar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class StockCalculationLibrary {


    public StockCalculationLibrary(){


    }

    public  double calculateStockBeta(){
        return 0.0;
    }
    //TESTING REVERSE ORDER
    public  double averageTrueRange(List<Bar> bars, int dayOffset, int length){
//    trueRange = na(high[1])? high-low : math.max(math.max(high - low, math.abs(high - close[1])), math.abs(low - close[1]))
//    //true range can be also calculated with ta.tr(true)
//    ta.rma(trueRange, length)

        if(dayOffset + 1 < bars.size()) {
            double max = -10000000.0;
            double min = 10000000.0;
            //dayOffset = dayOffset + 1;
            double rangeSum = 0.0;
//            for(int i = dayOffset; i < dayOffset + length -1; i++){
//                if(bars.get(i + 1).getHigh() > max){
//                    max = bars.get(i + 1).getHigh();
//                }
//                if(bars.get(i).getLow() < min){
//                    min = bars.get(i + 1).getLow();
//                }

                double num1 = Math.max(bars.get(dayOffset).getHigh() - bars.get(dayOffset).getLow(), (Math.abs(bars.get(dayOffset).getHigh() - bars.get(dayOffset + 1).getClose())));
                double num2 = Math.abs(bars.get(dayOffset).getLow() - bars.get(dayOffset + 1).getClose());
          //      rangeSum = rangeSum + Math.max(num1,num2);
//            }

            //double atr = rangeSum/length;


            return Math.max(num1,num2);

        }else{
            return 0.0;
        }
    }
    //TESTING REVERSE ORDER
    //DOUBLE CHECK THIS
    public  double getRMA(List<Bar> bars, int dayOffset, int length){
        if(dayOffset + length  + 1< bars.size()) {
            if (bars.get(dayOffset + 1).getTrueRange() == 0.0) {
                double sum = 0.0;
                for (int i = dayOffset; i < dayOffset + length; i++) {
                    sum = sum + bars.get(dayOffset).getTrueRange();
                }
                return sum / length;
            } else {
                double alpha = 1 / (double)length;
                double sum = 0.0;
                sum = sum + (alpha * bars.get(dayOffset).getTrueRange() + (1 - alpha) * bars.get(dayOffset + 1).getRma());
                return sum;
            }
        }else{
            return 0.0;
        }
    }


    //TESTING REVERSE ORDER
    private static double getValue(List<Bar> barList, int length, int dayOffset) {
        int complete = 0;
        int x = 0;
        List<Double> returnsBetweenBars = new ArrayList<>();
        double sum = 0;
        while (complete < length && x < length - 1) {
            if (dayOffset + x + 1 < barList.size() && dayOffset + x > 0) {
                double a = barList.get(dayOffset + x).getClose();

                double b = barList.get(dayOffset + x + 1).getClose();

                double d = Math.log(a / b);


                returnsBetweenBars.add(d);
                sum += d;
                complete++;
            }

            x++;

        }


        double variance = 0;
        for (int i = 0; i < returnsBetweenBars.size(); i++) {
            variance += Math.pow(returnsBetweenBars.get(i) - sum / returnsBetweenBars.size(), 2);
        }
        variance /= returnsBetweenBars.size();
        double value = 100 * (Math.sqrt(variance) * Math.sqrt(365));
        return value;
    }
    private static double getValueReverse(List<Bar> barList, int length, int dayOffset) {
        int complete = 0;
        int x = 0;
        List<Double> returnsBetweenBars = new ArrayList<>();
        double sum = 0;
        while (complete < length && x < length - 1) {
            if (dayOffset - x - 1 > 0 ) {
                double a = barList.get(dayOffset - x).getClose();

                double b = barList.get(dayOffset - x - 1).getClose();

                double d = Math.log(a / b);


                returnsBetweenBars.add(d);
                sum += d;
                complete++;
            }

            x++;

        }

        double variance = 0;
        int size = returnsBetweenBars.size();
        for (int i = 0; i < size; i++) {
            variance += Math.pow(returnsBetweenBars.get(i) - sum / size, 2);
        }
        variance /= size;
        double value = 100 * (Math.sqrt(variance) * Math.sqrt(365));
        return value;
    }
    //TESTING REVERSE ORDER
    public  static double getLogVariance(List<Bar> barList, int dayOffset, int length, String ticker){
        if (dayOffset + length < barList.size()) {
                //not in cache
                if (dayOffset + length  + 1 < barList.size() - 1) {
                    List<Double> returnsBetweenBars = new ArrayList<>();

                    LocalDate startDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(length  + 1);
                    LocalDate endDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
                    int trackingVariable = dayOffset;
                    double sum = 0;
                    while(true){
                        if(barList.get(trackingVariable).getDate().after(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))) {
                            double a = barList.get(trackingVariable).getClose();

                            double b = barList.get(trackingVariable + 1).getClose();
                            double d = Math.log(a / b);
                            returnsBetweenBars.add(d);
                            sum += d;
                        }else{
                            break;
                        }
                        trackingVariable++;
                    }

                    //DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
                    double variance = 0;
                    int size = returnsBetweenBars.size();
                    for (int i = 0; i < size; i++) {
                        variance += Math.pow(returnsBetweenBars.get(i) - sum / size, 2);
                    }
                    variance /= size-1;
                    //System.out.println("variance: " + (variance * (252/length)/12) + " RV: " + (Math.sqrt(variance) * Math.sqrt(365)));
                    // if(Double.isNaN(100* (Math.sqrt(variance) * Math.sqrt(365)))){
                    //System.out.println("stop!");
                    //}
                    double value = 100 * (Math.sqrt(variance) * Math.sqrt(365));
                    return value;
                }

        }
        return 0.0;
    }

    public  static double getLogVarianceReverse(List<Bar> barList, int dayOffset, int length, String ticker){
        if (dayOffset - length  > 0) {
            //not in cache
            if (dayOffset - length  - 1 < barList.size() - 1) {
                List<Double> returnsBetweenBars = new ArrayList<>();
                LocalDate startDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(length  + 1);
                LocalDate endDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
                int trackingVariable = dayOffset;
                double sum = 0;
                while(true){
                    if(barList.get(trackingVariable).getDate().after(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))) {
                        double a = barList.get(trackingVariable).getClose();

                        double b = barList.get(trackingVariable - 1).getClose();
                        double d = Math.log(a / b);
                        returnsBetweenBars.add(d);
                        sum += d;
                    }else{
                        break;
                    }
                    trackingVariable--;
                }

                //DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
                double variance = 0;
                int size = returnsBetweenBars.size();
                for (int i = 0; i < size; i++) {
                    variance += Math.pow(returnsBetweenBars.get(i) - sum / size, 2);
                }
                variance /= size-1;
                double value = 100 * (Math.sqrt(variance) * Math.sqrt(365));
                return value;
            }

        }
        return 0.0;
    }


    public  double getReturnVariance(List<Double> barList){

                    List<Double> returnBetweenTrades = new ArrayList<>();

                    double sum = 0;
                    for (int i = 0; i < barList.size() - 1; i++) {
                        double a = barList.get(i);

                        double b = barList.get(i + 1);

                        double d = Math.log(a / b);


                        returnBetweenTrades.add(d);
                        sum += d;
                    }


                    //DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
                    double variance = 0;
                    int size = returnBetweenTrades.size();
                    for (int i = 0; i < size; i++) {
                        variance += Math.pow(returnBetweenTrades.get(i) - sum / size, 2);
                    }
                    variance /= size;
                    //System.out.println("variance: " + (variance * (252/length)/12) + " RV: " + (Math.sqrt(variance) * Math.sqrt(365)));
                    // if(Double.isNaN(100* (Math.sqrt(variance) * Math.sqrt(365)))){
                    //System.out.println("stop!");
                    //}
                    double value = 100 * (Math.sqrt(variance) * Math.sqrt(252));
                    return value;
    }


    //TESTING REVERSE ORDER
    public  double min_diff(List<Bar> bars, int dayOffset, double slope, int lengthMinus1){
        if(dayOffset + (lengthMinus1 * 2) < bars.size()) {
            double m = 100000000;
            //List<Bar> subList = bars.subList(dayOffset, dayOffset + lengthMinus1 + 1);
            for (int i = 0; i < (lengthMinus1); i++) {

                double a = bars.get(dayOffset  + (lengthMinus1) - i).getClose() - (bars.get( dayOffset + lengthMinus1).getClose() + (slope * i));
                m = Math.min(m, a);
            }
            return m;
        }else{
            return 0.0;
        }
    }
    //TESTING REVERSE ORDER
    public  double max_diff(List<Bar> bars, int dayOffset, double slope, int lengthMinus1){
        if(dayOffset + (lengthMinus1 * 2) < bars.size()) {
            double m = -100000000;
            //List<Bar> subList = bars.subList(dayOffset, dayOffset + lengthMinus1 + 1);
            for (int i = 0; i < (lengthMinus1); i++) {
                double a = bars.get(dayOffset  + (lengthMinus1) - i).getClose() - (bars.get( dayOffset + lengthMinus1).getClose() + (slope * i));
                m = Math.max(m, a);
            }
            return m;
        }else{
            return 0.0;
        }
    }
//    public double weightedMovingAverage(List<Bar> bars, int dayOffset, int length){
//        //List<Double> dayCalcs = new ArrayList<>();
//        if(dayOffset + length < bars.size()) {
//            double norm = 0.0;
//            double norm2 = 0.0;
//            double sum = 0.0;
//            double totalVolume = 0.0;
//            for(int i = 0; i < length-1; i++){
//                totalVolume += bars.get(i + dayOffset).getBaseVolatility();
//            }
//            for(int i = 0; i < length-1; i++){
//               // double weightLength = ;
//                double weight = bars.get(i + dayOffset).getBaseVolatility() / totalVolume ;
//                norm = norm + weight;
//                //norm2 = norm2 + weightLength;
//                sum = sum + bars.get(i + dayOffset).getClose() * weight;
//            }
//            return sum / norm;
//        }else{
//            return 0.0;
//        }
//    }

    public double weightedMovingAverage(List<Bar> bars, int dayOffset, int length){
        if(dayOffset + length < bars.size()) {
            double norm = 0.0;
            double sum = 0.0;
            for(int i = 0; i < length; i++){
                double weight = (length - i) * length;
                norm = norm + weight;
                sum = sum + bars.get(i + dayOffset).getClose() * weight;
            }
            return sum / norm;
        }else{
            return 0.0;
        }
    }
    //TESTING REVERSE ORDER
    public  double getSlope(List<Bar> bars, int dayOffset, int lengthMinus1){
        if(dayOffset + lengthMinus1 < bars.size()) {
            double slope = (bars.get(dayOffset).getClose() - bars.get(dayOffset + lengthMinus1).getClose()) / lengthMinus1;
            return slope;
        }else{
            return 0.0;
        }
    }


    //TESTING REVERSE ORDER
    public  double getHurst(List<Bar> bars, int dayOffset, int length){
        if(dayOffset + length < bars.size()) {
            double high = -1000000.0;
            double low = 1000000.0;
            for(int i = dayOffset; i < dayOffset + length; i++){
                if(bars.get(i).getHigh() > high){
                    high = bars.get(i).getHigh();
                }
                if(bars.get(i).getLow() < low){
                    low = bars.get(i).getLow();
                }
            }
            double a = Math.log(high - low);
            double b = Math.log(bars.get(dayOffset).getRma());

            return (a -  b)/ Math.log(length);
        }else{
            return 0.0;
        }
    }

    //bb_bottom = WMA - (sd * 2)
    //
    //bb_top = WMA + (sd * 2)

    //bridge_band_bottom = bb_bottom + ((bridge_range_bottom - bb_bottom) * abs((hurst * 2) - 1))
    //
    //bridge_band_top = bb_top - ((bb_top - bridge_range_top) * abs((hurst * 2) - 1))
    //
    //bridge_band_mid = bridge_band_bottom + ((bridge_band_top - bridge_band_bottom) / 2)
    //
    //bridge_band_bottom_mid = bridge_band_bottom + ((bridge_band_mid - bridge_band_bottom) / 2)
    //
    //bridge_band_top_mid = bridge_band_mid + ((bridge_band_top - bridge_band_mid) / 2)

    public  double getBridgeBottom(Bar bar){
        return (bar.getWma() - (bar.getStdDev() * 2)) + (((bar.getClose() + bar.getMinDiff()) - (bar.getWma() - (bar.getStdDev() * 2))) * Math.abs((bar.getHurst() * 2) -1));
    }
    public  double getBridgeTop(Bar bar){
        return (bar.getWma() + (bar.getStdDev() * 2)) - (((bar.getWma() + (bar.getStdDev() * 2)) - ((bar.getClose() + bar.getMaxDiff()))) * Math.abs((bar.getHurst() * 2) -1));
    }
    public  double stdDev(List<Bar> bars, int dayOffset, int length){
        if(dayOffset + length < bars.size()) {
            List<Double> prices = new ArrayList<>();
            double sum = 0.0;
            for (int i = dayOffset; i < length + dayOffset; i++) {
                prices.add(bars.get(i).getClose());
                sum += bars.get(i).getClose();
            }
            int size = prices.size();
            //DoubleSummaryStatistics doubleSummaryStatistics = prices.stream().mapToDouble(x -> x).summaryStatistics();
            double average = sum/size;
            double variance = 0;

            for (int i = 0; i < size; i++) {
                variance += Math.pow(prices.get(i) - average, 2);
            }
            variance /= size;
            //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
            //return Math.sqrt(variance);
            return Math.sqrt(variance);
        }else{
            return 0.0;
        }
    }
}
