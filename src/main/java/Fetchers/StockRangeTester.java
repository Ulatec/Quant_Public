package Fetchers;

import BackTest.*;
import Model.*;
import Repository.DatabaseBarRepository;
import Repository.ImpliedVolailityRepository;
import Repository.RealizedVolatilityRepository;
import Threads.*;
import Util.DateFormatter;
import Util.ListSplitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class StockRangeTester implements ApplicationRunner {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_GREEN_NEW = "\u001B[32;1;4m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_ORANGE = "\033[38;5;214m";
    Instant now;
    Instant start;
    double delta;
    double rate;
    boolean dollarInput = true;
    boolean multiTicker = false;

    boolean multithreadedNonVerbose = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ImpliedVolailityRepository impliedVolailityRepository;
    @Autowired
    public RealizedVolatilityRepository realizedVolatilityRepository;
    @Autowired
    public DatabaseBarRepository databaseBarRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        boolean[] directions = new boolean[]{false};
        boolean fullDateRange = true;
        LocalDate startDate = LocalDate.of(2020,10,8);
        LocalDate endDate = LocalDate.now();
//
        LocalDate priorEndDate = LocalDate.of(2024,12,20);
        LocalDate fiveDaysBack = priorEndDate.minusDays(5);
        LocalDate priorStartDate = LocalDate.of(2007,9,9);
        LocalDate barRequestStart = LocalDate.of(2007,9,9);
        int threads = 128;
        boolean update = false;
        boolean ohShit = true;
        boolean gradient = false;
        boolean fullLog = false;
        boolean multiTickerMode = true;
        //List<QuarterlyQuad> quads = assembleQuads();
        // boolean log = false;
        boolean logBuckets = true;
        double[] dollarsPercentToTest = {0.01};
        int dayVolToTest = 30;
        double[] rangeThresholdToTest = {0.9,};
        boolean[] priceRocFlips = {false};
        boolean[] priceSlopeFlips = {false};
        boolean[] volatilityRocFlips = {false};
        boolean[] volatilitySlopeFlips = {false,};
        boolean[] signalRocFlips = {false};
        boolean[] signalValueFlips = {true};
        boolean[] volumeFlip = {false};
        boolean[] signalRocEnable = {true};
        boolean[] signalValueEnable= {true};
        boolean[] volatilityRocEnable= {false,};
        boolean[] volatilitySlopeEnable= {true};
        boolean[] vixRocEnable= {false};
        boolean[] oilEnable= {false};
        boolean[] treasuryEnable= {true};
        boolean[] commodityEnable= {false};
        double[] percentOfVolProfit = {1};
        double[] ivWeighting = {4e-11};
        double[] discountWeighting = {-2e-11};
        double[] realizedVolWeighting = {-0.8};
        double[] volumeWeighting = {-0.17};
        double[] treasuryWeighting = {11};
        double[] commodityWeighting = {30};
        double[] fedTotalAssetsWeighting = {3e-11};
        double[] revenueRocWeighting = {3};
        double[] dollarWeighting = {10};
        double[] vixWeighting = {87};
        double[] oilWeighting = {7};
        double[] longOpenSignalValueThreshold = {-9e-11};
        double[] longOpenSignalRocThreshold = {-8e-11};
        double[] longOpenVolatilitySlopeThreshold = {-0.26};
        double[] longOpenVolatilityRocThreshold = {-0.005};
        double[] shortOpenSignalValueThreshold = {3};
        double[] shortOpenSignalRocThreshold = {1};
        double[] shortOpenVolatilitySlopeThreshold = {1};
        double[] shortOpenVolatilityRocThreshold = {1};
        double[] shortExitTarget = {0.005};
        double[] shortExitVolatility1 = {25};
        double[] shortExitVolatility2 ={48};
        double[] shortExitVolatility3 = {68};
        double[] longExitTarget = {0.018};
        double[] longExitVolatility1 = {21};
        double[] longExitVolatility2 = {45};
        double[] longExitVolatility3 = {64};
        int[] correlationDays ={9};
        int[] ivTypes = {1};
        int[] ivTrendType = {1};
        double[] stopLoss = {0.12};
        int[] trendLengthToTest1 = {60};
        int[] trendLengthToTest2 = {3};
        int[] trendLengthToTest3 = {13};
        int[] trendLengthToTest4 = {36};
        int[] trendLengthToTest5 = {53};
        int[] trendLengthToTest6 = {380};
        int[] trendLengthToTest7 = {65000};
        int[] trendLengthToTest8 = {500};
        int[] trendLengthToTest = {110};
        int[] tradeLengthToTest = {35};
        int[] trendConfirmationLengthToTest = {2};
        int[] trendBreakConfirmationToTest = {85};
        int[] volLookback = {63};
        boolean useVolatilitySurface = true;


        double initialLongOpenVolatilitySlope = 0;
        double incrementerLongOpenVolatilitySlope = 0.05;
        int longOpenVolatilitySlopeParamCount = 12;
        double[] workingArray = new double[2];
        double[] incrementerArray = new double[longOpenVolatilitySlopeParamCount];
        boolean[] priorFlipArray = new boolean[longOpenVolatilitySlopeParamCount];
       // boolean[] directionArray = new boolean[longOpenVolatilitySlopeParamCount];
        boolean[] reversedFlagArray = new boolean[longOpenVolatilitySlopeParamCount];
        boolean[] priorSuccessArray = new boolean[longOpenVolatilitySlopeParamCount];
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            workingArray[i]=initialLongOpenVolatilitySlope;
//        }
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            incrementerArray[i]=incrementerLongOpenVolatilitySlope;
//        }
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            priorFlipArray[i]=false;
//        }
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            directionArray[i]=false;
//        }
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            reversedFlagArray[i]=false;
//        }
//        for(int i =0; i < longOpenVolatilitySlopeParamCount; i++){
//            priorSuccessArray[i]=false;
//        }
        double[] volSlopeValues = new double[6];
        volSlopeValues[0] = -0.12;
        for(int j = 1; j <volSlopeValues.length ; j++){
            volSlopeValues[j] = volSlopeValues[j-1] + 0.04;
        }

        ArrayList<double[]> workingArrays = new ArrayList<>();
        workingArrays.add(new double[]{});
       // for(int j = 0; j < workingArray.length; j++) {
            for (double volSlope1 : volSlopeValues) {
                for (double volSlope2 : volSlopeValues) {
                    for (double volSlope3 : volSlopeValues) {
                        //for(double volSlope4 : volSlopeValues) {
                           // for(double volSlope5 : volSlopeValues) {
          //                        workingArrays.add(new double[]{volSlope1, volSlope2,volSlope3});
                         //   }
                       // }
                    }
                }
            }
       // }
        ArrayList<boolean[]> directionArrays = new ArrayList<>();
        // workingArrays.add(new double[]{});
        // for(int j = 0; j < workingArray.length; j++) {
        for (boolean direction: directions) {
            //for (double volSlope2 : volSlopeValues) {
            directionArrays.add(new boolean[]{direction});
            // }
        }
        //HashMap<String, ConfigurationTest> settingsMap = getAllSettings();

        ArrayList<int[]> trendLengths = new ArrayList<>();
        for (int movingTrendLength : trendLengthToTest1) {
            for (int movingTrendLength2 : trendLengthToTest2) {
                for (int movingTrendLength3 : trendLengthToTest3) {
                    for (int movingTrendLength4 : trendLengthToTest4) {
                        for (int movingTrendLength5 : trendLengthToTest5) {
                            for (int movingTrendLength6 : trendLengthToTest6) {
                                for (int movingTrendLength7 : trendLengthToTest7) {
                                    for (int movingTrendLength8 : trendLengthToTest8) {
                                        trendLengths.add(new int[]{movingTrendLength, movingTrendLength2,
                                                movingTrendLength3, movingTrendLength4, movingTrendLength5,
                                                movingTrendLength6, movingTrendLength7, movingTrendLength8});
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        List<ConfigurationTest> configurationTestList = new ArrayList<>();
        for (int trendLength : trendLengthToTest) {
            for (int tradeLength : tradeLengthToTest) {
                for (int trendConfirm : trendConfirmationLengthToTest) {
                    for (int trendBreak : trendBreakConfirmationToTest) {
                        for (double rangeThreshold : rangeThresholdToTest) {
                            for (int ivType : ivTypes) {
                                for (boolean priceRocFlip : priceRocFlips) {
                                    for (boolean volatilityRocFlip : volatilityRocFlips) {
                                        for (boolean volatilitySlopeFlip : volatilitySlopeFlips) {
                                            for (int IVTrendCalc : ivTrendType) {
                                                for (double stopLoss1 : stopLoss) {
                                                    for (double pctOfVol : percentOfVolProfit) {
                                                        for (boolean signalRocFlip : signalRocFlips) {
                                                            for (double ivWeight : ivWeighting) {
                                                                for (double discount : discountWeighting) {
                                                                    for (double volume : volumeWeighting) {
                                                                        for (double realized : realizedVolWeighting) {
                                                                            for (int[] movingTrendLengths : trendLengths) {
                                                                                for (int vollLookback : volLookback) {
                                                                                    for (double dollarPercent : dollarsPercentToTest) {
                                                                                        for (double treasury : treasuryWeighting) {
                                                                                            for(double commodity : commodityWeighting) {
                                                                                                for(double fedAssets : fedTotalAssetsWeighting) {
                                                                                                    for(double revenue : revenueRocWeighting) {
                                                                                                        for(double dollar : dollarWeighting) {
                                                                                                            for(int correlation : correlationDays) {
                                                                                                                for(double oil : oilWeighting) {
                                                                                                                    for(double vix : vixWeighting) {
                                                                                                                        for(double longOpenSignalValueThresholdValue: longOpenSignalValueThreshold) {
                                                                                                                            for(double longOpenSignalRocThresholdValue : longOpenSignalRocThreshold) {
                                                                                                                                for(double longOpenVolatilitySlopeThresholdValue : longOpenVolatilitySlopeThreshold) {
                                                                                                                                    for(double shortOpenSignalValueThresholdValue : shortOpenSignalValueThreshold) {
                                                                                                                                        for(double shortOpenSignalRocThresholdValue : shortOpenSignalRocThreshold) {
                                                                                                                                            for(double shortOpenVolatilitySlopeThresholdValue : shortOpenVolatilitySlopeThreshold) {
                                                                                                                                                for(double shortExitTargetValue : shortExitTarget) {
                                                                                                                                                    for(double shortExitVolatility1Value : shortExitVolatility1) {
                                                                                                                                                        for(double shortExitVolatility2Value : shortExitVolatility2) {
                                                                                                                                                            for(double shortExitVolatility3Value : shortExitVolatility3) {
                                                                                                                                                                for(double longExitTargetValue : longExitTarget) {
                                                                                                                                                                    for(double longExitVolatility1Value : longExitVolatility1) {
                                                                                                                                                                        for(double longExitVolatility2Value : longExitVolatility2) {
                                                                                                                                                                            for (double longExitVolatility3Value : longExitVolatility3) {
                                                                                                                                                                                for(boolean signalValueFlip : signalValueFlips) {
                                                                                                                                                                                    for (boolean priceSlopeFlip : priceSlopeFlips) {
                                                                                                                                                                                        for(boolean signalRoc : signalRocEnable) {
                                                                                                                                                                                            for (boolean signalValue : signalValueEnable) {
                                                                                                                                                                                                for(boolean volatilityRoc : volatilityRocEnable) {
                                                                                                                                                                                                    for(boolean volatilitySlope : volatilitySlopeEnable) {
                                                                                                                                                                                                        for(boolean vixRoc : vixRocEnable) {
                                                                                                                                                                                                            for(boolean oilE : oilEnable) {
                                                                                                                                                                                                                for(boolean treasuryE : treasuryEnable) {
                                                                                                                                                                                                                    for(boolean commodityE : commodityEnable) {
                                                                                                                                                                                                                        for(double longVolRoc : longOpenVolatilityRocThreshold) {
                                                                                                                                                                                                                            for(double shortVolRoc : shortOpenVolatilityRocThreshold) {
                                                                                                                                                                                                                                for(double[] workingArry : workingArrays) {
                                                                                                                                                                                                                                    for(boolean[] directionArray : directionArrays) {
                                                                                                                                                                                                                                        for(boolean volumeFlips : volumeFlip) {
                                                                                                                                                                                                                                            ConfigurationTest configurationTest = new ConfigurationTest();
                                                                                                                                                                                                                                            configurationTest.setLongOpenSignalValueThreshold(longOpenSignalValueThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setLongOpenSignalRocThreshold(longOpenSignalRocThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setLongOpenVolatilitySlopeThreshold(longOpenVolatilitySlopeThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenSignalValueThreshold(shortOpenSignalValueThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenSignalRocThreshold(shortOpenSignalRocThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenVolatilitySlopeThreshold(shortOpenVolatilitySlopeThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortExitTarget(shortExitTargetValue);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility1(shortExitVolatility1Value);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility2(shortExitVolatility2Value);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility3(shortExitVolatility3Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitTarget(longExitTargetValue);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility1(longExitVolatility1Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility2(longExitVolatility2Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility3(longExitVolatility3Value);
                                                                                                                                                                                                                                            configurationTest.setTradeLength(tradeLength);
                                                                                                                                                                                                                                            configurationTest.setTrendLength(trendLength);
                                                                                                                                                                                                                                            configurationTest.setTrendConfirmationLength(trendConfirm);
                                                                                                                                                                                                                                            configurationTest.setTrendBreakLength(trendBreak);
                                                                                                                                                                                                                                            configurationTest.setRangeThreshold(rangeThreshold);
                                                                                                                                                                                                                                            configurationTest.setPriceSlopeFlip(priceSlopeFlip);
                                                                                                                                                                                                                                            // configurationTest.setDaysVol(daysVol);
                                                                                                                                                                                                                                            configurationTest.setPercentOfVolatility(pctOfVol);
                                                                                                                                                                                                                                            configurationTest.setStopLoss(stopLoss1);
                                                                                                                                                                                                                                            configurationTest.setIvAdjustmentType(ivType);
                                                                                                                                                                                                                                            configurationTest.setPriceRocFlip(priceRocFlip);
                                                                                                                                                                                                                                            configurationTest.setVolatilitySlopeFlips(volatilitySlopeFlip);
                                                                                                                                                                                                                                            configurationTest.setVolatilityRocFlip(volatilityRocFlip);
                                                                                                                                                                                                                                            configurationTest.setIvTrendCalcType(IVTrendCalc);
                                                                                                                                                                                                                                            configurationTest.setSignalRocFlips(signalRocFlip);
                                                                                                                                                                                                                                            configurationTest.setIvWeighting(ivWeight);
                                                                                                                                                                                                                                            configurationTest.setDiscountWeighting(discount);
                                                                                                                                                                                                                                            configurationTest.setVolumeWeighting(volume);
                                                                                                                                                                                                                                            configurationTest.setRealizedVolWeighting(realized);
                                                                                                                                                                                                                                            configurationTest.setMovingTrendLength(movingTrendLengths);
                                                                                                                                                                                                                                            configurationTest.setVolLookback(vollLookback);
                                                                                                                                                                                                                                            configurationTest.setUseVolatilitySurface(useVolatilitySurface);
                                                                                                                                                                                                                                            configurationTest.setDollarPercent(dollarPercent);
                                                                                                                                                                                                                                            configurationTest.setTreasuryWeighting(treasury);
                                                                                                                                                                                                                                            configurationTest.setCommodityWeighting(commodity);
                                                                                                                                                                                                                                            configurationTest.setFedTotalAssetsWeighting(fedAssets);
                                                                                                                                                                                                                                            configurationTest.setRevenueRocWeighting(revenue);
                                                                                                                                                                                                                                            configurationTest.setDollarWeighting(dollar);
                                                                                                                                                                                                                                            configurationTest.setCorrelationDays(correlation);
                                                                                                                                                                                                                                            configurationTest.setOilWeighting(oil);
                                                                                                                                                                                                                                            configurationTest.setVixWeighting(vix);
                                                                                                                                                                                                                                            configurationTest.setSignalValueFlip(signalValueFlip);
                                                                                                                                                                                                                                            configurationTest.setLongOpenVolatilityRocThreshold(longVolRoc);
                                                                                                                                                                                                                                            configurationTest.setShortOpenVolatilityRocThreshold(shortVolRoc);
                                                                                                                                                                                                                                       //     configurationTest.setWorkingArray(workingArry);
                                                                                                                                                                                                                                        //    configurationTest.setPriorSuccessArray(priorSuccessArray);
                                                                                                                                                                                                                                       //     configurationTest.setIncrementerArray(incrementerArray);
                                                                                                                                                                                                                                            configurationTest.setVolumeFlip(volumeFlips);
                                                                                                                                                                                                                                            configurationTestList.add(configurationTest);
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                }
                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Connection.Response response;

        response = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                        "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22" +
                        "IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22" +
                        "IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22" +
                        "ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                        "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                        "Series%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD01M%22%2C%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22" +
                        "Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        JSONObject treasuryDataJson = new JSONObject(response.body());
        JSONArray dateArray = treasuryDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray rateArray = treasuryDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");
        int backTestThreads = threads;
        if(ohShit){
            update = true;
            priorEndDate = LocalDate.now();
            fiveDaysBack = priorEndDate.minusDays(5);
            priorStartDate = LocalDate.of(2022,12,9);
            barRequestStart = priorEndDate.minusDays(15);
            backTestThreads =1;
            fullDateRange = false;
        }
        LocalDate today = LocalDate.now();
        // ArrayList<Bar> oilBars = (ArrayList<Bar>) getBarsBetweenDates(startDate, endDate, "USO");
        //  ArrayList<Bar> priorOilBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "USO");
        //ArrayList<Bar> dollarBars = (ArrayList<Bar>) getBarsBetweenDates(startDate, endDate, "UUP");
        //ArrayList<Bar> priordollarBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "UUP");
       // Collections.reverse(priordollarBars);
        // ArrayList<Bar> nasdaqBars = (ArrayList<Bar>) getBarsBetweenDates(startDate, endDate, "QQQ");
        ArrayList<Bar> priorNasdaqBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "QQQ");
        Collections.reverse(priorNasdaqBars);
        // ArrayList<Bar> priorGoldBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "GLD");
        // ArrayList<Bar> priorGasolineBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "UGA");
        // ArrayList<Bar> lowBetaBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "SIXL");
        // ArrayList<Bar> highBetaBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "SPHB");
        List<ConfigurationTest> savedTest = new ArrayList<>();
        double pct = 1;
//        if (!isDayComplete()) {
//            LocalDateTime localDateTime = LocalDateTime.now();
//            pct = calculateVolumeAverage(localDateTime);
//        }
        ExecutorService executorService2 = Executors.newScheduledThreadPool(64);
        ExecutorService executorService3 = Executors.newScheduledThreadPool(16);


        List<Ticker> uniqueTickers = assembleQuadTemplates(priorStartDate, priorEndDate);
        if(update) {
            int count = 0;
            BarSavingThreadMonitor barSavingThreadMonitor = new BarSavingThreadMonitor(threads, uniqueTickers.size());
            List<List<Ticker>> listOfTickers = ListSplitter.splitTickers(uniqueTickers, threads);
            for (int z = 0; z < threads; z++) {
                BarSavingThread barSavingThread = new BarSavingThread(listOfTickers.get(z), databaseBarRepository, barRequestStart, priorEndDate, barSavingThreadMonitor);
                executorService2.submit(barSavingThread);
              //  barSavingThread.start();
            }
            while (!barSavingThreadMonitor.getBackTestResults()) {

            }
        }


        int count = 0;
//        if(multiTicker){
//            for (Ticker uniqueTicker : uniqueTickers) {
//                List<Bar> bars =
//
//            }
//        }else{
//
//        }
       // HashMap<Ticker, List<Bar>> barMap = new HashMap<>();
        int z = 0;
//        Date priorDate = Date.from(priorStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//        Date priorDateEnd = Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());


        BarFetchingThreadMonitor barFetchingThreadMonitor = new BarFetchingThreadMonitor(threads, uniqueTickers.size());
        for(Ticker uniqueTicker : uniqueTickers){


            BarFetchingThread barFetchingThread = new BarFetchingThread(barFetchingThreadMonitor,databaseBarRepository,uniqueTicker,priorStartDate,priorEndDate,0);
            barFetchingThread.setFullDateRange(fullDateRange);
            executorService2.submit(barFetchingThread);
        }

        while(barFetchingThreadMonitor.getBarResults() == null){

        }

        final HashMap<Ticker,List<Bar>> barMap = barFetchingThreadMonitor.getBarResults();
        final Set<Map.Entry<Ticker,List<Bar>>> entrySet  = barMap.entrySet();
        Iterator<Map.Entry<Ticker, List<Bar>>> iterator = entrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<Ticker,List<Bar>> entry = iterator.next();
           // List<String> CIKsFounds = new ArrayList<>();
            for(Map.Entry<Ticker, List<Bar>> entry1 : entrySet){
                if(entry1.getKey().getTicker().equals(entry.getKey().getTicker()) &&
                !entry1.getKey().getCik().equals(entry.getKey().getCik()) && entry1.getValue().size() == entry.getValue().size()){
                    iterator.remove();
                    break;
                }
            }
        }
        start = Instant.now();
        Iterator<Map.Entry<Ticker, List<Bar>>> iterator2 = entrySet.iterator();
        while(iterator2.hasNext()){
         //   for (Map.Entry<Ticker, List<Bar>> entry : entrySet) {
                Map.Entry<Ticker,List<Bar>> entry = iterator2.next();
                List<Bar> barListSafe = new ArrayList<>();
                int size = entry.getValue().size();
                for (int itemIndex = 0; itemIndex < size; itemIndex++) {
                    Bar item = entry.getValue().get(itemIndex);
                    try {
                        barListSafe.add((Bar) item.clone());
                    } catch (Exception ignored) {

                    }
                }
                System.out.println("Building tickers... " + z++ + "/" + uniqueTickers.size());

                BackTestThreadMonitor backTestThreadMonitor = new BackTestThreadMonitor(backTestThreads, configurationTestList.size());
                List<List<ConfigurationTest>> listOfConfigs = ListSplitter.splitConfigs(configurationTestList, backTestThreads);
                for (int x = 0; x < listOfConfigs.size(); x++) {

                    BackTestThread backTestThread = new BackTestThread(listOfConfigs.get(x), barListSafe, configurationTestList.size() == 1,
                            backTestThreadMonitor, entry.getKey(), x,
                            realizedVolatilityRepository, multithreadedNonVerbose, priorEndDate);
                    //   executorService3.submit(backTestThread);
                    executorService3.execute(backTestThread);
                    // backTestThread.start();
                }

                while (backTestThreadMonitor.getBackTestResults() == null) {

                }
                count++;
                now = Instant.now();
                delta = Duration.between(start, now).toMillis();
                rate = ((float) count / delta) * 1000;
                System.out.println(ANSI_ORANGE + "Tickers per second: " + rate + "\n" + ANSI_RESET);

                System.out.println("Complete: " + count + " / " + entrySet.size());
//                if(count % 500 == 0){
//                    System.gc();
//                }
                iterator2.remove();
            }
            ReplayThreadMonitor replayThreadMonitor = new ReplayThreadMonitor(threads * 4, configurationTestList.size());
            List<List<ConfigurationTest>> listOfConfigs2 = ListSplitter.splitConfigs(configurationTestList, threads * 4);
            List<Bar> referenceBars =
                    databaseBarRepository.findAllByCikAndTickerAndDateAfterAndDateBefore("0000723125", "MU", Date.from(priorStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

            for (int x = 0; x < listOfConfigs2.size(); x++) {
                ReplayThread replayThread = new ReplayThread(endDate, startDate, referenceBars, replayThreadMonitor, listOfConfigs2.get(x), x);
                replayThread.start();
            }
            while (replayThreadMonitor.getBackTestResults() == null) {

            }
            List<ConfigurationTest> finishedConfigs = replayThreadMonitor.getBackTestResults();
            if (finishedConfigs.size() == 1) {
                //for (Bar bar : barListSafe) {
                for (ConfigurationTest configurationTest1 : finishedConfigs) {
                    // for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {

                    TradeLog tradeLog = configurationTest1.getStockTestList().get(finishedConfigs.size() - 1).getTradeLog();
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        for (Trade trade : individualStockTest.getTradeLog().getClosedTradeList()) {
                            LocalDate localTradeDate = trade.getCloseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localTradeDate.getYear() == endDate.getYear() &&
                                    localTradeDate.getMonthValue() == endDate.getMonthValue() &&
                                    localTradeDate.getDayOfMonth() == endDate.getDayOfMonth()
                            ) {
                                System.out.println("Close: " + trade.getTicker() + trade.getCloseDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getClosingPrice());
                            }
                        }
                    }
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        for (Trade trade : individualStockTest.getTradeLog().getActiveTradeList()) {
                            LocalDate localTradeDate = trade.getOpenDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localTradeDate.getYear() == endDate.getYear() &&
                                    localTradeDate.getMonthValue() == endDate.getMonthValue() &&
                                    localTradeDate.getDayOfMonth() == endDate.getDayOfMonth()
                            ) {
                                System.out.println("Open: " + trade.getTicker() + trade.getOpenDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getTradeBasis());
                            }
                        }
                    }
                    System.out.println("Active Trades: ");
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        tradeLog = individualStockTest.getTradeLog();
                        for (Trade trade : tradeLog.getActiveTradeList()) {
                            //LocalDate localTradeDate = trade.getOpenDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            System.out.println("Active: " + trade.getTicker() + trade.getOpenDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getTradeBasis());
                            //}
                        }
                    }

                }

            }
            finishedConfigs.sort(Comparator.comparing(ConfigurationTest::getSuccessRate));
            for (ConfigurationTest configurationTest : finishedConfigs) {
                if (configurationTest.getSuccessfulTrades() != 0) {
                       // if(configurationTest.getCategorySuccesses()[27] >= 0.75 && configurationTest.getCategoryCounts()[27] >= 45) {
                        StringBuilder stringBuilder = new StringBuilder();
                        boolean allPositive = true;

                        for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                            if (individualStockTest.getDollars() < 100) {
                                allPositive = false;
                                break;
                            }
                        }
                        if (allPositive) {
                            savedTest.add(configurationTest);
                            stringBuilder.append(ANSI_ORANGE);
                        } else {
                            stringBuilder.append(ANSI_RED);
                        }
                        stringBuilder.append(configurationTest);


                        stringBuilder.append(ANSI_RESET);
                        // if(configurationTest.getSuccessfulTrades() + configurationTest.getFailedTrades() > 20 && configurationTest.getDollars() > 10000 && configurationTest.getSuccessRate()>0.65 && configurationTest.getAverageReturnPerTradingDayHeld() > 1e-6) {
                        System.out.println(stringBuilder);
                        // }
                 //   }
                }
                //}
            }
            for (ConfigurationTest configurationTest : finishedConfigs) {
                configurationTest.getStockTestList().sort(Comparator.comparing(IndividualStockTest::getDollars));
            }
            finishedConfigs.sort(Comparator.comparing(ConfigurationTest::getReturnRsquared));
            for (ConfigurationTest configurationTest : savedTest) {
                if (configurationTest.getSuccessfulTrades() > 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(ANSI_ORANGE);
                    stringBuilder.append(configurationTest);
                    stringBuilder.append(ANSI_RESET);
                    //if(configurationTest.getSuccessfulTrades() + configurationTest.getFailedTrades() > 20 && configurationTest.getDollars() > 10000 && configurationTest.getSuccessRate()>0.65 && configurationTest.getAverageReturnPerTradingDayHeld() > 1e-6) {
                    System.out.println(stringBuilder);
                    //}
                }
            }
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        System.out.println(ANSI_ORANGE + "TIME TO COMPLETE:  " + delta + "\n" + ANSI_RESET);
        //System.out.println(barList);
    }





    public ConfigurationTest createNewIncrementalConfig(ConfigurationTest savedConfig, ConfigurationTest oldConfig, int paramIndex, double step, boolean success, boolean priorSuccess,boolean gayBoolean){
        try {
            ConfigurationTest newConfig = (ConfigurationTest) oldConfig.clone();
           // newConfig.setWorkingArray(oldConfig.getWorkingArray().clone());
           // newConfig.setIncrementerArray(oldConfig.getIncrementerArray().clone());
            newConfig.setSuccessfulTrades1(0);
            newConfig.setSuccessfulTrades2(0);
            newConfig.setSuccessfulTrades3(0);
            newConfig.setSuccessRate1(0);
            newConfig.setSuccessRate2(0);
            newConfig.setSuccessRate3(0);
            newConfig.setFailedTrades1(0);
            newConfig.setFailedTrades2(0);
            newConfig.setFailedTrades3(0);
            newConfig.setAverageReturnPerTradingDayHeld(0);
            newConfig.setReturnRsquared(0);
            newConfig.setShortRsquared(0);
            newConfig.setReplayDollars(0);
            newConfig.setTradeLog(null);
            newConfig.setAverageCapitalInUse(0);
            newConfig.setLongDollars(0);
            newConfig.setShortDollars(0);
            newConfig.setDollars(0);
          //  newConfig.getWorkingArray()[paramIndex] = step;
//            double[] workingArray = oldConfig.getWorkingArray();
//            if (!success && !priorSuccess) {
//                step = step / 2;
//                workingArray[paramIndex] = workingArray[paramIndex] + step;
//                newConfig.getPriorSuccessArray()[paramIndex] = true;
//            } else if (!success){
//                workingArray[paramIndex] = savedConfig.getWorkingArray()[paramIndex] - step;
//            }else {
//                workingArray[paramIndex] = workingArray[paramIndex] + step;
//            }
//            newConfig.getDirectionArray()[paramIndex] = gayBoolean;
//
//            //if no successful scenarios, then move step
//
//            double[] incrementarArray = oldConfig.getIncrementerArray();
//            incrementarArray[paramIndex] = step;
//            newConfig.setWorkingArray(workingArray);


            return newConfig;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }





    public List<Ticker> assembleQuadTemplates(LocalDate priorStartDate, LocalDate priorEndDate){
        List<Ticker> newList = new ArrayList<>();
        List<String> uniqueTickers = databaseBarRepository.findAllUniqueTickers();
        //List<Bar> all = databaseBarRepository.all();

        for(int i = 0; i < uniqueTickers.size(); i++) {
            String[] strings = uniqueTickers.get(i).split(",");
            newList.add(new Ticker(strings[0],strings[1]));
        }
//        LocalDate endDateCopy = priorEndDate.minusDays(1);
//while(endDateCopy.isAfter(priorStartDate)) {
//    String url = "https://api.polygon.io/v3/reference/tickers?active=true&date=" + endDateCopy + "&limit=1000&apiKey=";
//    List<Ticker> allTickers = new ArrayList<>();
//    try {
////
////
////
//        int x = 0;
////            //List<RangeSummary> rangeSummaryList = new ArrayList<>();
////
//        boolean hasNextUrl = true;
//        while (hasNextUrl) {
//            Connection.Response response = Jsoup.connect(url)
//                    .method(Connection.Method.GET)
//                    .ignoreContentType(true)
//                    .execute();
//            JSONObject jsonObject = new JSONObject(response.body());
//            JSONArray resultsArray = jsonObject.getJSONArray("results");
//            while (x < resultsArray.length()) {
////
//                JSONObject tickerObject = resultsArray.getJSONObject(x);
//                if (tickerObject.has("cik")) {
//                    Ticker ticker;
//////                        Optional<Ticker> tickerOptional = tickerRepository.findByTickerAndCik(tickerObject.getString("ticker"), tickerObject.getString("cik"));
////                        if (tickerOptional.isPresent()) {
////                            ticker = tickerOptional.get();
////                            allTickers.add(ticker);
////                        } else {
//                    Ticker tempTicker = new Ticker();
//                    tempTicker.setCik(tickerObject.getString("cik"));
//                    tempTicker.setMarket(tickerObject.getString("market"));
//                    tempTicker.setName(tickerObject.getString("name"));
//                    if (tickerObject.has("type")) {
//                        if (!tickerObject.getString("type").equals("WARRANT") &&
//                                !tickerObject.getString("type").equals("UNIT") &&
//                                !tickerObject.getString("type").equals("RIGHT") &&
//                                !tickerObject.getString("type").equals("PFD") &&
//                                !tickerObject.getString("type").equals("FUND") &&
//                                !tickerObject.getString("name").contains("Acquisition")
//                        ) {
//                            if(tickerObject.getString("ticker").contains("/")){
//                                tempTicker.setTicker(tickerObject.getString("ticker").replace("/","."));
//                            }else{
//                                tempTicker.setTicker(tickerObject.getString("ticker"));
//                            }
//                            tempTicker.setType(tickerObject.getString("type"));
//
//
//                            //tickerRepository.save(tempTicker);
//                            ticker = tempTicker;
//                            allTickers.add(ticker);
//                        }
//                        //  }
//
//                    }
////
//                }
//                x++;
//            }
//            if (jsonObject.has("next_url")) {
//                //hasNextUrl = true;
//                x = 0;
//                url = jsonObject.getString("next_url") + "&active=true&date= "+endDateCopy +"&limit=1000&apiKey=rcJCxVUqKDfgcSgLSkDkQpnfVn0rk9Ne";
//             //   break;
//            } else {
//                hasNextUrl = false;
//            }
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//    double x = 0;
//    LocalDate date = endDateCopy.minusDays(15);
//    for (Ticker ticker : allTickers) {
//        double sum = 0;
//        List<Bar> bars = getBarsBetweenDates(date, endDateCopy, ticker.getTicker());
//        if (bars != null) {
//            for (Bar bar : bars) {
//                sum = sum + (bar.getVolume() * bar.getClose());
//            }
//            ticker.setThirtyDayDollarVolume(sum / bars.size());
//        }
//        System.out.println((new Date()) + "/" + x + "/" + allTickers.size() + "\t" + endDateCopy);
//        x++;
//    }
//    allTickers.sort(Comparator.comparing(Ticker::getThirtyDayDollarVolume).reversed());
////
//    for (int i = 0; i < allTickers.size(); i++) {
//        System.out.println(allTickers.get(i).getTicker() + "\t" + allTickers.get(i).getName() + "\t" + allTickers.get(i).getThirtyDayDollarVolume() + "\t" + endDateCopy);
//    }
//    int boundary = 4200;
//    if (allTickers.size() < boundary) {
//
//
//        boundary = allTickers.size();
//    }
//    for (int i = 0; i < boundary; i++) {
//        boolean copyFound = false;
//        for(Ticker ticker : newList){
//            if(ticker.getTicker().equals(allTickers.get(i).getTicker()) && ticker.getCik().equals(allTickers.get(i).getCik())){
//                copyFound = true;
//                break;
//            }
//        }
//        if(!copyFound) {
//            newList.add(allTickers.get(i));
//        }
//        //newList.add(new TickerQuadTemplate(allTickers.get(i).getTicker(), new ArrayList<>(Arrays.asList(1,2,3,4)), new ArrayList<>(Arrays.asList(1,2,3,4))));
//    }
//    endDateCopy = endDateCopy.minusYears(30);
//
//}

        newList.add(new Ticker("QQQ", "0001067839", "ETF"));
 //       newList.add(new Ticker("BIIB", "0000875045", "ETF"));
//        newList.add(new Ticker("NVDA"));
//        newList.add(new Ticker("GDX",true));
//        newList.add(new Ticker("XLV", true));
//        newList.add(new Ticker("URA",true));
//        newList.add(new Ticker("BJ"));
//        newList.add(new Ticker("XLU", true));
//        newList.add(new Ticker("XLP", true));
//        newList.add(new Ticker("WMT"));
//        newList.add(new Ticker("XLY", true));
//        newList.add(new Ticker("XLV", true));
//        newList.add(new Ticker("XLF",true));
//        newList.add(new Ticker("SMH",true));
//        newList.add(new Ticker("XLRE",true));
//        newList.add(new Ticker("XLB",true));
//        newList.add(new Ticker("EEM",true));
//       newList.add(new Ticker("XLI",true));
//        newList.add(new Ticker("XLK",true));
//        newList.add(new Ticker("IWM",true));
//newList.add(new Ticker("FXI",true));
//        newList.add(new Ticker("EWZ",true));
//        newList.add(new Ticker("ARKK",true));
//        newList.add(new Ticker("XHB",true));
//        newList.add(new Ticker("XBI",true));
//        newList.add(new Ticker("SBUX"));
//        newList.add(new Ticker("MSFT"));
//        newList.add(new Ticker("W"));
//        newList.add(new Ticker("NFLX"));
//        newList.add(new Ticker("CVNA"));
//        newList.add(new Ticker("GOOG"));
//        newList.add(new Ticker("TSLA"));
//        newList.add(new Ticker("RUN"));
//        newList.add(new Ticker("DE"));
//        newList.add(new Ticker("AAPL"));
//        newList.add(new Ticker("CHTR"));
//        newList.add(new Ticker("MMM"));
//        newList.add(new Ticker("JETS"));
//        newList.add(new Ticker("KMX"));
//        newList.add(new Ticker("BITO"));
//        newList.add(new Ticker("DASH"));
//        newList.add(new Ticker("MGM"));
//        newList.add(new Ticker("AMZN"));
//        newList.add(new Ticker("META"));
//        newList.add(new Ticker("V"));
//        newList.add(new Ticker("UNH"));
//        newList.add(new Ticker("XOM"));
//        newList.add(new Ticker("JNJ"));
//        newList.add(new Ticker("TSM"));
//        newList.add(new Ticker("JPM"));
//        newList.add(new Ticker("PG"));
//        newList.add(new Ticker("LLY"));
//        newList.add(new Ticker("MA"));
//        newList.add(new Ticker("AVGO"));
//        newList.add(new Ticker("NVO"));
//        newList.add(new Ticker("HD"));
//        newList.add(new Ticker("CVX"));
//        newList.add(new Ticker("TM"));
//        newList.add(new Ticker("ORCL"));
//        newList.add(new Ticker("MRK"));
//        newList.add(new Ticker("ABBV"));
//        newList.add(new Ticker("ASML"));
//        newList.add(new Ticker("KO"));
//        newList.add(new Ticker("ADBE"));
//        newList.add(new Ticker("PEP"));
//        newList.add(new Ticker("CSCO"));
//        newList.add(new Ticker("CRM"));
//        newList.add(new Ticker("TMO"));
//        newList.add(new Ticker("MCD"));
//        newList.add(new Ticker("LLY"));
//        newList.add(new Ticker("DHR"));
//        newList.add(new Ticker("PFE"));
//        newList.add(new Ticker("LIN"));
//        newList.add(new Ticker("CMCSA"));
//        newList.add(new Ticker("NWL"));
//        newList.add(new Ticker("DXC"));
//        newList.add(new Ticker("LNC"));
//        newList.add(new Ticker("ALK"));
//        newList.add(new Ticker("MHK"));
//        newList.add(new Ticker("VFC"));
//        newList.add(new Ticker("BBWI"));
//        newList.add(new Ticker("AAL"));
//        newList.add(new Ticker("NRG"));
//        newList.add(new Ticker("UHS"));
//        newList.add(new Ticker("NEE"));
//        newList.add(new Ticker("URNM"));
//        newList.add(new Ticker("AMC"));
//        newList.add(new Ticker("AFRM"));
//        newList.add(new Ticker("SKX"));
//        newList.add(new Ticker("FUBO"));
//        newList.add(new Ticker("ROKU"));
//        newList.add(new Ticker("DIA",true));
//        newList.add(new Ticker("KEY"));
//        newList.add(new Ticker("BXP"));
//        newList.add(new Ticker("CPT"));
//        newList.add(new Ticker("MTCH"));
//        newList.add(new Ticker("CPB"));
//        newList.add(new Ticker("STX"));
//        newList.add(new Ticker("WDC"));
//        newList.add(new Ticker("SYF"));
//        newList.add(new Ticker("POOL"));
//        newList.add(new Ticker("DPZ"));
//        newList.add(new Ticker("TLRY"));
//        newList.add(new Ticker("SAH"));
//        newList.add(new Ticker("SBLK"));
//        newList.add(new Ticker("SAVE"));
//        newList.add(new Ticker("CWH"));
//        newList.add(new Ticker("PTON"));
//        newList.add(new Ticker("SIMO"));
//        newList.add(new Ticker("RRGB"));
//        newList.add(new Ticker("QCOM"));
//        newList.add(new Ticker("HZO"));
//        newList.add(new Ticker("SIG"));
//        newList.add(new Ticker("GOOS"));
//        newList.add(new Ticker("INDA",true));
//        newList.add(new Ticker("EWO",true));
//        newList.add(new Ticker("EWQ",true));
//        newList.add(new Ticker("CNP"));
//        newList.add(new Ticker("PM"));
//        newList.add(new Ticker("FOX"));
//        newList.add(new Ticker("F"));
//        newList.add(new Ticker("LNG"));
//        newList.add(new Ticker("AEM"));
//        newList.add(new Ticker("XLE",true));
//        newList.add(new Ticker("XOP",true));
//        newList.add(new Ticker("COST"));
//        newList.add(new Ticker("ON"));
//        newList.add(new Ticker("ALL"));
//        newList.add(new Ticker("DFS"));
//        newList.add(new Ticker("SYY"));
//        newList.add(new Ticker("YUM"));
//        newList.add(new Ticker("RIOT"));
//        newList.add(new Ticker("BX"));
//        newList.add(new Ticker("INTC"));
//        newList.add(new Ticker("UBER"));
//        newList.add(new Ticker("PLTR"));
//        newList.add(new Ticker("SHOP"));
//        newList.add(new Ticker("IBM"));
//        newList.add(new Ticker("T"));
//        newList.add(new Ticker("SQ"));
//        newList.add(new Ticker("SLB"));
//        newList.add(new Ticker("TGT"));
//        newList.add(new Ticker("KWEB"));
//        newList.add(new Ticker("MAR"));
//        newList.add(new Ticker("CCL"));
//        newList.add(new Ticker("NEM"));
//        newList.add(new Ticker("SLV"));
//        newList.add(new Ticker("LEN"));
//        newList.add(new Ticker("RBLX"));
//        newList.add(new Ticker("KDP"));
//        newList.add(new Ticker("WING"));
//        newList.add(new Ticker("CNK"));
//        newList.add(new Ticker("HLT"));
//        newList.add(new Ticker("PBR"));
//        newList.add(new Ticker("KR"));
//        newList.add(new Ticker("ODFL"));
//        newList.add(new Ticker("DOCU"));
//        newList.add(new Ticker("DASH"));
//        newList.add(new Ticker("SOFI"));
//        newList.add(new Ticker("RTX"));
//        newList.add(new Ticker("ROST"));
//        newList.add(new Ticker("TJX"));
//        newList.add(new Ticker("SPOT"));
//        newList.add(new Ticker("CELH"));
//        newList.add(new Ticker("NUE"));
//        newList.add(new Ticker("EXPE"));
//        newList.add(new Ticker("ITW"));
//        newList.add(new Ticker("BTAL"));
//        newList.add(new Ticker("PCAR"));
//        newList.add(new Ticker("MSTR"));
//        newList.add(new Ticker("EWY"));
//        newList.add(new Ticker("SNAP"));
//        newList.add(new Ticker("WSM"));
//        newList.add(new Ticker("XME",true));
//        newList.add(new Ticker("UNG"));
//        newList.add(new Ticker("SIL"));
//        newList.add(new Ticker("EWJ",true));
//        newList.add(new Ticker("CMG"));
//        newList.add(new Ticker("VCSH"));
//        newList.add(new Ticker("USO"));
//        newList.add(new Ticker("IAK"));
//        newList.add(new Ticker("IFF"));
//        newList.add(new Ticker("GPS"));
//        newList.add(new Ticker("BITO"));
//        newList.add(new Ticker("AXP"));
//        newList.add(new Ticker("CTA"));
//        newList.add(new Ticker("OPEN"));
//        newList.add(new Ticker("SPWR"));
//        newList.add(new Ticker("TRIP"));
//        newList.add(new Ticker("AMD"));
//        newList.add(new Ticker("AMN"));
//        newList.add(new Ticker("FAST"));
//        newList.add(new Ticker("BBY"));
//        newList.add(new Ticker("CHWY"));
//        newList.add(new Ticker("FND"));
//        newList.add(new Ticker("M"));
//        newList.add(new Ticker("IRM"));
//        newList.add(new Ticker("KBE"));
//        newList.add(new Ticker("YUMC"));
//        newList.add(new Ticker("WYNN"));
//        newList.add(new Ticker("TAP"));
//        newList.add(new Ticker("C"));
//        newList.add(new Ticker("TPR"));
//        newList.add(new Ticker("EWH",true));
//        newList.add(new Ticker("JNK",true));
//        newList.add(new Ticker("FCX"));
//        newList.add(new Ticker("CORN"));
//        newList.add(new Ticker("WEAT"));
//        newList.add(new Ticker("TUR",true));
//        newList.add(new Ticker("GNK"));
//        newList.add(new Ticker("BOOT"));
//        newList.add(new Ticker("QSR"));
//        newList.add(new Ticker("LOW"));
//        newList.add(new Ticker("JWN"));
//        newList.add(new Ticker("ONON"));
//        newList.add(new Ticker("PTC"));
//        newList.add(new Ticker("DUOL"));
//        newList.add(new Ticker("MCHP"));
//        newList.add(new Ticker("DBX"));
//        newList.add(new Ticker("ZI"));
//        newList.add(new Ticker("DUOL"));
//        newList.add(new Ticker("NET"));
//        newList.add(new Ticker("HCP"));
//        newList.add(new Ticker("TWLO"));
//        newList.add(new Ticker("BMBL"));
//        newList.add(new Ticker("CARG"));
//        newList.add(new Ticker("Z"));
//        newList.add(new Ticker("AMT"));
//        newList.add(new Ticker("DISH"));
//        newList.add(new Ticker("FTI"));
//        newList.add(new Ticker("BLMN"));
//        newList.add(new Ticker("TXRH"));
//        newList.add(new Ticker("EBAY"));
//        newList.add(new Ticker("WBD"));
//        newList.add(new Ticker("SMIN",true));
//        newList.add(new Ticker("INDY",true));
//        newList.add(new Ticker("EWJV",true));
//        newList.add(new Ticker("JPXN",true));
//        newList.add(new Ticker("CPER",true));
//        newList.add(new Ticker("SNBR"));
//        newList.add(new Ticker("WELL"));
//        newList.add(new Ticker("KMI"));
//        newList.add(new Ticker("MET"));
//        newList.add(new Ticker("CCJ"));
//        newList.add(new Ticker("PDD"));
//        newList.add(new Ticker("JD"));
//        newList.add(new Ticker("SPLG"));
//        newList.add(new Ticker("LW"));
//        newList.add(new Ticker("EZU",true));
//        newList.add(new Ticker("IVOL",true));
//        newList.add(new Ticker("TTWO"));
//        newList.add(new Ticker("EWW",true));
//        newList.add(new Ticker("IWN",true));
//        newList.add(new Ticker("MCHI",true));
//        newList.add(new Ticker("EWT",true));
//        newList.add(new Ticker("EWG",true));
//        newList.add(new Ticker("ITA",true));
//        newList.add(new Ticker("SCZ",true));
//        newList.add(new Ticker("EWU",true));
//        newList.add(new Ticker("EWC",true));
//        newList.add(new Ticker("IEMG",true));
//        newList.add(new Ticker("GDXJ",true));
//        newList.add(new Ticker("OIH",true));
//        newList.add(new Ticker("OHI"));
//        newList.add(new Ticker("BKLN",true));
//        newList.add(new Ticker("SPG"));
//        newList.add(new Ticker("JBL"));
//        newList.add(new Ticker("TSCO"));
//        newList.add(new Ticker("ROK"));
//        newList.add(new Ticker("AKAM"));
//        newList.add(new Ticker("BABA"));
//        newList.add(new Ticker("RIVN"));
//        newList.add(new Ticker("SMCI"));
//        newList.add(new Ticker("INTU"));
//        newList.add(new Ticker("ACN"));
//        newList.add(new Ticker("OXY"));
//        newList.add(new Ticker("SG"));
//        newList.add(new Ticker("LMND"));
//        newList.add(new Ticker("PATH"));
//        newList.add(new Ticker("LCID"));
//        newList.add(new Ticker("BROS"));
//        newList.add(new Ticker("ADI"));
//        newList.add(new Ticker("UPS"));
//        newList.add(new Ticker("COIN"));
//        newList.add(new Ticker("BAP"));
//        newList.add(new Ticker("REPL"));
//        newList.add(new Ticker("VLO"));
//        newList.add(new Ticker("PPC"));
//        newList.add(new Ticker("HES"));

        newList.add(new Ticker("MU","0000723125", "STOCK"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BE"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SR"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BBX"));
        newList.removeIf(ticker -> ticker.getTicker().equals("CVA"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SDOW"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SRTY"));
        newList.removeIf(ticker -> ticker.getTicker().equals("QID"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SQQQ"));
        newList.removeIf(ticker -> ticker.getTicker().equals("TECS"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BBVA"));

  //      newList.removeIf(ticker -> !ticker.getTicker().equals("URTY"));
        return newList;
    }


    public Bar buildBarFromJsonData(Object object){
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
          //  bar.setDay(localDate.getDayOfMonth());
        }catch (Exception e){
            e.printStackTrace();
        }
        return bar;
    }




    public List<Bar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
        while(true) {
            try {
                String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" +
                        DateFormatter.formatLocalDate(from) + "/" + DateFormatter.formatLocalDate(to) + "?adjusted=true&sort=asc&limit=5500" +
                        "&apiKey=";
                Connection.Response oilResponse = Jsoup.connect
                                (url)
                        .method(Connection.Method.GET)

                        .ignoreContentType(true)
                        .execute();
                JSONObject JsonObject = new JSONObject(oilResponse.body());
                if(JsonObject.has("results")) {
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
}