package com.example.impliedvolatilitymicroservice.MacroData.Model;

import java.util.Date;

public class MacroDataPoint {
    String seriesName;

    public Date date;
    public double value;

    MacroDataSeries macroDataSeries;

}
