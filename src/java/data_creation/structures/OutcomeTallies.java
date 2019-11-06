package data_creation.structures;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class OutcomeTallies implements Serializable, Comparable<OutcomeTallies> {

    private static final long serialVersionUID = -5368500688019335975L;

    private long wins, losses, ties;

    public OutcomeTallies() {}

    public OutcomeTallies(OutcomeTallies tallies) {
        this(tallies.wins, tallies.losses, tallies.ties);
    }

    public OutcomeTallies(long wins, long losses, long ties) {
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
    }

    public long getWins() {
        return wins;
    }

    public long getLosses() {
        return losses;
    }

    public long getTies() {
        return ties;
    }

    public void setWins(long wins) {
        this.wins = wins;
    }

    public void setLosses(long losses) {
        this.losses = losses;
    }

    public void setTies(long ties) {
        this.ties = ties;
    }


    public void addTallies(OutcomeTallies otherTallies) {
        wins += otherTallies.getWins();
        losses += otherTallies.getLosses();
        ties += otherTallies.getTies();
    }

    public double computeEquity() {
        int numberOfDecimalPlaces = 10;

        BigDecimal total = new BigDecimal(wins + losses + ties);
        BigDecimal w = new BigDecimal(wins);
        BigDecimal b = new BigDecimal(ties).divide(new BigDecimal(2), numberOfDecimalPlaces, RoundingMode.UP);
        BigDecimal equity = w.add(b).divide(total, numberOfDecimalPlaces, RoundingMode.UP);
        return equity.doubleValue();
    }

    @Override
    public int compareTo(OutcomeTallies o) {
        if (computeEquity() > o.computeEquity()) {
            return 1;
        }
        else if (computeEquity() < o.computeEquity()) {
            return -1;
        }
        return 0;
    }
}
