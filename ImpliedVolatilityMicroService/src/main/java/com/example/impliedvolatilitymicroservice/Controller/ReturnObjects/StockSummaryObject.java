package com.example.impliedvolatilitymicroservice.Controller.ReturnObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StockSummaryObject {
    private List<VolatilityReturnObject> volatilityReturnObject;
    private CorrelationObject correlationObject;

    private List<ShortSaleDataPoint> fiveDayShortSaleAverageData = new ArrayList<>();
    public StockSummaryObject(List<VolatilityReturnObject> volatilityReturnObject, CorrelationObject correlationObject) {
        this.volatilityReturnObject = volatilityReturnObject;
        this.correlationObject = correlationObject;
    }

    public CorrelationObject getCorrelationObject() {
        return correlationObject;
    }

    public void setCorrelationObject(CorrelationObject correlationObject) {
        this.correlationObject = correlationObject;
    }

    public List<VolatilityReturnObject> getVolatilityReturnObject() {
        return volatilityReturnObject;
    }

    public void setVolatilityReturnObject(List<VolatilityReturnObject> volatilityReturnObject) {
        this.volatilityReturnObject = volatilityReturnObject;
    }

    public List<ShortSaleDataPoint> getFiveDayShortSaleAverageData() {
        return fiveDayShortSaleAverageData;
    }

    public void setFiveDayShortSaleAverageData(List<ShortSaleDataPoint> fiveDayShortSaleAverageData) {
        this.fiveDayShortSaleAverageData = fiveDayShortSaleAverageData;
    }
}
