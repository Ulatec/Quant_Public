package BackTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class OptionTradeLog {
    List<OptionTrade> activeTradeList;
    List<OptionTrade> closedTradeList;

    int activeTrades;
    int closedTrades;
    double closedTradesPercentDelta;
    double shortBasis;
    double longBasis;
    boolean shortActive;
    boolean longActive;
    public OptionTradeLog() {
        activeTrades = 0;
        closedTrades = 0;

        activeTradeList = new ArrayList<>();
        closedTradeList = new ArrayList<>();

    }

    public void pushNewActiveTrade(OptionTrade trade){
        if(activeTradeList == null){
            activeTradeList = new ArrayList<>();
        }
        activeTradeList.add(trade);
        activeTrades++;
        if(trade.isLong()){
            longActive = true;
        }else{
            shortActive = true;
        }
        calculateTradeBasisForSide(trade.isLong());
    }

    public void calculateTradeBasisForSide(boolean isLong){
        int count = 0;
        double basis = 0;
        for(OptionTrade optionTrade : activeTradeList){
            if(optionTrade.isLong() == isLong){
                basis += optionTrade.getTradeBasis();
                count++;
            }
        }
        if(isLong){
            longBasis = basis/count;
        }else{
            shortBasis = basis/count;
        }
        checkForZeroTrades();
    }

    private void checkForZeroTrades() {
        boolean notFound = true;
        for(OptionTrade  optionTrade : activeTradeList){
            if (optionTrade.isLong) {
                notFound = false;
                break;
            }
        }
        if(notFound){
            longBasis = 0;
            longActive = false;
        }
        notFound = true;
        for(OptionTrade optionTrade : activeTradeList){
            if (!optionTrade.isLong) {
                notFound = false;
                break;
            }
        }
        if(notFound){
            shortBasis = 0;
            shortActive = false;
        }
    }
    public void closeAllOnSide(boolean isLong, double closingPrice, Date date){
        Iterator<OptionTrade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            OptionTrade optionTrade = iterator.next();
            //for(Trade trade : activeTradeList){
            if (optionTrade.isLong() == isLong) {
                optionTrade.setClosingPrice(closingPrice);
                optionTrade.setCloseDate(date);
                optionTrade.setOpen(false);
                closedTradeList.add(optionTrade);
                closedTrades++;
                activeTrades--;
                calculatePercentDelta();
                iterator.remove();
            }
        }
        calculateTradeBasisForSide(isLong);
    }
    public boolean closeFirstIn(int index, boolean isLong, double vwap, Date date ) {
//        List<OptionTrade> tradesToClose = new ArrayList<>();
//        for(OptionTrade optionTrade : activeTradeList){
//            if(optionTrade.isLong() == isLong){
//                tradesToClose.add(optionTrade);
//            }
//        }
        OptionTrade tempTrade = activeTradeList.get(index);
        tempTrade.setClosingPrice(vwap);
        tempTrade.setCloseDate(date);
        tempTrade.setOpen(false);
        closedTradeList.add(tempTrade);
        closedTrades++;
        activeTrades--;
        calculatePercentDelta();

        calculateTradeBasisForSide(isLong);
        return true;
    }
    public void closeSpecificTrade(OptionTrade trade, double closingPrice) {
        Iterator<OptionTrade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            OptionTrade tempTrade = iterator.next();
            if (tempTrade.getTradeBasis() == trade.getTradeBasis() && trade.isLong() == tempTrade.isLong()) {
                trade.setClosingPrice(closingPrice);
                trade.setOpen(false);
                closedTradeList.add(trade);
                closedTrades++;
                activeTrades--;
                calculatePercentDelta();
                iterator.remove();
            }
        }
        calculateTradeBasisForSide(trade.isLong());
    }

    private void calculatePercentDelta() {
        double deltaSum = 0.0;
        for(OptionTrade trade : closedTradeList){
            //if(trade.isLong){
                deltaSum = deltaSum + ((trade.getClosingPrice() - trade.getTradeBasis())/trade.getTradeBasis());
            //}else{
             //   deltaSum = deltaSum + ((trade.getTradeBasis() - trade.getClosingPrice())/trade.getTradeBasis());
            //}
        }
        closedTradesPercentDelta = deltaSum/closedTradeList.size();
    }

    public List<OptionTrade> getActiveTradeList() {
        return activeTradeList;
    }

    public void setActiveTradeList(List<OptionTrade> activeTradeList) {
        this.activeTradeList = activeTradeList;
    }

    public List<OptionTrade> getClosedTradeList() {
        return closedTradeList;
    }

    public void setClosedTradeList(List<OptionTrade> closedTradeList) {
        this.closedTradeList = closedTradeList;
    }

    public int getActiveTrades() {
        return activeTrades;
    }

    public void setActiveTrades(int activeTrades) {
        this.activeTrades = activeTrades;
    }

    public int getClosedTrades() {
        return closedTrades;
    }

    public void setClosedTrades(int closedTrades) {
        this.closedTrades = closedTrades;
    }

    public double getClosedTradesPercentDelta() {
        return closedTradesPercentDelta;
    }

    public void setClosedTradesPercentDelta(double closedTradesPercentDelta) {
        this.closedTradesPercentDelta = closedTradesPercentDelta;
    }

    public double getShortBasis() {
        return shortBasis;
    }

    public void setShortBasis(double shortBasis) {
        this.shortBasis = shortBasis;
    }

    public double getLongBasis() {
        return longBasis;
    }

    public void setLongBasis(double longBasis) {
        this.longBasis = longBasis;
    }

    public boolean isShortActive() {
        return shortActive;
    }

    public void setShortActive(boolean shortActive) {
        this.shortActive = shortActive;
    }

    public boolean isLongActive() {
        return longActive;
    }

    public void setLongActive(boolean longActive) {
        this.longActive = longActive;
    }

    @Override
    public String toString() {
        return "TradeLog{" +
                "closedTrades=" + closedTrades +
                ", closedTradesPercentDelta=" + closedTradesPercentDelta +
                '}';
    }
}
