package analysis;

import data_creation.structures.HoleCards;

import java.util.List;

public class ComputeWinPercentageAgainstBestRangeResult {

    double winPercentage;
    List<HoleCards> range;

    public ComputeWinPercentageAgainstBestRangeResult(double winPercentage, List<HoleCards> range) {
        this.winPercentage = winPercentage;
        this.range = range;
    }

    public double getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(double winPercentage) {
        this.winPercentage = winPercentage;
    }

    public List<HoleCards> getRange() {
        return range;
    }

    public void setRange(List<HoleCards> range) {
        this.range = range;
    }
}
