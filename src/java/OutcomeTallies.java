import java.io.Serializable;

public final class OutcomeTallies implements Serializable {

    private long wins, losses, ties;

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

    public void addWins(long wins) {
        this.wins += wins;
    }

    public void addLosses(long losses) {
        this.losses += losses;
    }

    public void addTies(long ties) {
        this.ties += ties;
    }

    public void addTallies(OutcomeTallies otherTallies) {
        wins += otherTallies.getWins();
        losses += otherTallies.getLosses();
        ties += otherTallies.getTies();
    }
}
