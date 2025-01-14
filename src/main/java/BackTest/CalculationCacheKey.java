package BackTest;

import java.util.Arrays;
import java.util.Objects;

public class CalculationCacheKey {

    public int[] trendLengthValues;

    public double trendMultiplier;

    public double trendExponent;

    public double volLookback;

    public double ivWeighting;

    public double volumeWighting;

    public double realizedVolWeighting;
    public double discountWeighting;
    public double treasuryWeighting;

    public double fedAssetWeighting;

    public double vixWeighting;

    public double dollarWeighting;



    public CalculationCacheKey(int[] trendLengthValues, double trendMultiplier, double trendExponent,
                               double volLookback, double ivWeighting, double volumeWighting,
                               double realizedVolWeighting, double discountWeighting, double treasuryWeighting,
                               double fedAssetWeighting, double vixWeighting, double dollarWeighting) {
        this.trendLengthValues = trendLengthValues;
        this.trendMultiplier = trendMultiplier;
        this.trendExponent = trendExponent;
        this.volLookback = volLookback;
        this.ivWeighting = ivWeighting;
        this.volumeWighting = volumeWighting;
        this.realizedVolWeighting = realizedVolWeighting;
        this.discountWeighting = discountWeighting;
        this.treasuryWeighting = treasuryWeighting;
        this.fedAssetWeighting = fedAssetWeighting;
        this.vixWeighting = vixWeighting;
        this.dollarWeighting = dollarWeighting;
    }

    public int[] getTrendLengthValues() {
        return trendLengthValues;
    }

    public void setTrendLengthValues(int[] trendLengthValues) {
        this.trendLengthValues = trendLengthValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationCacheKey that = (CalculationCacheKey) o;
        return Double.compare(that.trendMultiplier, trendMultiplier) == 0 && Double.compare(that.trendExponent, trendExponent) == 0 && Double.compare(that.volLookback, volLookback) == 0 && Double.compare(that.ivWeighting, ivWeighting) == 0 && Double.compare(that.volumeWighting, volumeWighting) == 0 && Double.compare(that.realizedVolWeighting, realizedVolWeighting) == 0 && Double.compare(that.discountWeighting, discountWeighting) == 0 && Double.compare(that.treasuryWeighting, treasuryWeighting) == 0 && Double.compare(that.fedAssetWeighting, fedAssetWeighting) == 0 && Double.compare(that.vixWeighting, vixWeighting) == 0 && Double.compare(that.dollarWeighting, dollarWeighting) == 0 && Arrays.equals(trendLengthValues, that.trendLengthValues);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(trendMultiplier, trendExponent, volLookback, ivWeighting, volumeWighting, realizedVolWeighting, discountWeighting, treasuryWeighting, fedAssetWeighting, vixWeighting, dollarWeighting);
        result = 31 * result + Arrays.hashCode(trendLengthValues);
        return result;
    }
}

