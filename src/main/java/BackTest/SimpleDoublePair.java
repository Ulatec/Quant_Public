package BackTest;

public class SimpleDoublePair {
    public double value;
    public double tiebreaker;

    public SimpleDoublePair() {
        value = 0;
        tiebreaker = 0;
    }

    public SimpleDoublePair(double value, double tiebreaker) {
        this.value = value;
        this.tiebreaker = tiebreaker;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTiebreaker() {
        return tiebreaker;
    }

    public void setTiebreaker(double tiebreaker) {
        this.tiebreaker = tiebreaker;
    }
}
