package analysis.structures;

public class HoleCardsVersusRangeResult {

    private double equity;
    private int numberOfHandsInRange;

    public HoleCardsVersusRangeResult(double equity, int numberOfHandsInRange) {
        this.equity = equity;
        this.numberOfHandsInRange = numberOfHandsInRange;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }

    public int getNumberOfHandsInRange() {
        return numberOfHandsInRange;
    }

    public void setNumberOfHandsInRange(int numberOfHandsInRange) {
        this.numberOfHandsInRange = numberOfHandsInRange;
    }
}
