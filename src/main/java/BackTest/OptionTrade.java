package BackTest;

import Model.OptionContract;

import java.util.Date;

public class OptionTrade {

    public OptionContract optionContract;
    double tradeBasis;

    double positionSize;

    boolean isLong;

    double closingPrice;

    boolean open;

    double percentage;

    double endingDollarTotal;

    boolean isOption;

    Date openDate;

    Date closeDate;


    public OptionTrade(OptionContract optionContract, double tradeBasis, boolean isLong, Date date, double trendVol) {
        this.optionContract = optionContract;
        this.tradeBasis = tradeBasis;
        this.isLong = isLong;
        this.openDate = date;
        this.positionSize = 1/Math.sqrt(trendVol/100);
        this.open = true;
    }
    public OptionTrade(OptionContract optionContract, double tradeBasis, boolean isLong, Date date, double trendVol, boolean isOption) {
        this.optionContract = optionContract;
        this.tradeBasis = tradeBasis;
        this.isLong = isLong;
        this.openDate = date;
        this.positionSize = 1/Math.sqrt(trendVol/100);
        this.open = true;
        this.isOption = isOption;
    }

    public double getTradeBasis() {
        return tradeBasis;
    }

    public void setTradeBasis(double tradeBasis) {
        this.tradeBasis = tradeBasis;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public double getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(double closingPrice) {

        this.closingPrice = closingPrice;
        //if(isLong){
            setPercentage((closingPrice - tradeBasis)/tradeBasis);
       // }else{
        //    setPercentage((tradeBasis - closingPrice)/tradeBasis);
        //}
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public double getEndingDollarTotal() {
        return endingDollarTotal;
    }

    public void setEndingDollarTotal(double endingDollarTotal) {
        this.endingDollarTotal = endingDollarTotal;
    }

    public double getPositionSize() {
        return positionSize;
    }

    public void setPositionSize(double positionSize) {
        this.positionSize = positionSize;
    }

    public boolean isOption() {
        return isOption;
    }

    public void setOption(boolean option) {
        isOption = option;
    }

    public OptionContract getOptionContract() {
        return optionContract;
    }

    public void setOptionContract(OptionContract optionContract) {
        this.optionContract = optionContract;
    }
}
