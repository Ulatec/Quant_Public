package Libraries;



import BackTest.CacheKey;
import Model.Bar;
import Model.ImpliedVolaility;
import Model.RealizedVolatility;
import Repository.RealizedVolatilityRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class StockCalculationLibrary {

    private HashMap<CacheKey, Double> varianceCache = new HashMap<>();
    private HashMap<CacheKey, Double> nasdaqVarianceCache = new HashMap<>();
    private HashMap<CacheKey, Double> stdDevCache = new HashMap<>();
    private HashMap<CacheKey, Double> atrCache = new HashMap<>();


    private List<Bar> nasdaqBars;

    private RealizedVolatilityRepository realizedVolatilityRepository;

    public StockCalculationLibrary(RealizedVolatilityRepository realizedVolatilityRepository, List<Bar> nasdaqBars) {
        this.realizedVolatilityRepository = realizedVolatilityRepository;
        this.nasdaqBars = nasdaqBars;
    }

    public double getLogVarianceReverse(List<Bar> barList, int dayOffset, int length, String ticker) {
        //length = length - 10;
        if (dayOffset - length - 1 > 0) {
            CacheKey cacheKey = new CacheKey(barList.get(dayOffset).getDate(), length, dayOffset, ticker);
            if (varianceCache.containsKey(cacheKey)) {
                return varianceCache.get(cacheKey);
            } else {
                //not in cache
                if (dayOffset - length - 1 > 0) {
                    //LocalDate startDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(length  + 1);
                    //LocalDate endDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
                    List<Double> returnsBetweenBars = new ArrayList<>();
                    // List<Double> oldBetween = new ArrayList<>();
                    long startTimestamp = barList.get(dayOffset).getDate().getTime() - ((long) (length + 1) * 3600000 * 24);
                    double sum = 0;
//                    double sumSq = 0;
                    int trackingVariable = dayOffset;
                    // Date date = Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    while (true) {
                        if (barList.get(trackingVariable).getDate().getTime() > (startTimestamp) && trackingVariable - 1 > 0) {
                            double a = barList.get(trackingVariable).getClose();

                            double b = barList.get(trackingVariable - 1).getClose();
                            double d = Math.log(a / b);
                            returnsBetweenBars.add(d);
                            sum += d;
                        } else {
                            break;
                        }
                        trackingVariable--;
                    }
//                    for(int x = 0; x < barList.size(); x++){
//                        if(barList.get(x).getDate().after(Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())) &&
//                                barList.get(x).getDate().before(Date.from(endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
//                        ){
//                            double a = barList.get(x).getClose();
//
//                            double b = barList.get(x - 1).getClose();
//                            double d = Math.log(a / b);
//
//                           // oldBetween.add(d);
//                            returnsBetweenBars.add(d);
//                            sum += d;
//                            //sumSq += d*d;
//                        }
//                    }


//                    for (int i = 0; i < length -1; i++) {
//                        double a = barList.get(dayOffset - i).getClose();
//
//                        double b = barList.get(dayOffset - i - 1).getClose();
//
//                        double d = Math.log(a / b);
//
//
//                        returnsBetweenBars.add(d);
//                        sum += d;
//                      //  sumSq += d*d;
//                    }


//                    DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
                    double variance = 0;
                    int size = returnsBetweenBars.size();
                    int oldSize = returnsBetweenBars.size();
                    for (int i = 0; i < size; i++) {
                        variance += Math.pow(returnsBetweenBars.get(i) - sum / oldSize, 2);
                    }
                    variance /= oldSize - 1;
                    double old = 100 * (Math.sqrt(variance) * Math.sqrt(252));
                    //System.out.println("variance: " + (variance * (252/length)/12) + " RV: " + (Math.sqrt(variance) * Math.sqrt(365)));
                    // if(Double.isNaN(100* (Math.sqrt(variance) * Math.sqrt(365)))){
                    //System.out.println("stop!");
                    //}
                    // double test =Math.sqrt((sumSq - sum * sum / (size + 1)) / size);
                    //double test2 = Math.sqrt(252) * test;
                    // double newCalc = Math.sqrt(doubleSummaryStatistics.getSum() * ((double)252/size - 1));
                    return old;
                    //double value = 100 * (Math.sqrt(variance) * Math.sqrt(252));
                    //return value;
                }


            }

        }
        return 0.0;
    }

}