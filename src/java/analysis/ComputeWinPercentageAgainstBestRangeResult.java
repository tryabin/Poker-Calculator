package analysis;

import data_creation.structures.HoleCards;

public class ComputeWinPercentageAgainstBestRangeResult {

    double winPercentage;
    HoleCards lowestCardsInBestRange;

    public ComputeWinPercentageAgainstBestRangeResult(double winPercentage, HoleCards lowestCardsInBestRange) {
        this.winPercentage = winPercentage;
        this.lowestCardsInBestRange = lowestCardsInBestRange;
    }

    public double getWinPercentage() {
        return winPercentage;
    }
    public void setWinPercentage(double winPercentage) {
        this.winPercentage = winPercentage;
    }
    public HoleCards getLowestCardsInBestRange() {
        return lowestCardsInBestRange;
    }
    public void setLowestCardsInBestRange(HoleCards lowestCardsInBestRange) {
        this.lowestCardsInBestRange = lowestCardsInBestRange;
    }
}
