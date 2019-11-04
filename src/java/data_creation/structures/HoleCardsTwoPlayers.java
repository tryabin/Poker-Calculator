package data_creation.structures;

import java.io.Serializable;
import java.util.Objects;

public final class HoleCardsTwoPlayers implements Serializable {
    private HoleCards mainPlayerCards;
    private HoleCards otherPlayerCards;
    private int uniqueId;

    public HoleCardsTwoPlayers(HoleCards mainPlayerCards, HoleCards otherPlayerCards) {
        this.mainPlayerCards = mainPlayerCards;
        this.otherPlayerCards = otherPlayerCards;
    }

    public HoleCardsTwoPlayers(HoleCards mainPlayerCards, HoleCards otherPlayerCards, int uniqueId) {
        this(mainPlayerCards, otherPlayerCards);
        this.uniqueId = uniqueId;
    }

    public HoleCards getMainPlayerCards() {
        return mainPlayerCards;
    }

    public void setMainPlayerCards(HoleCards mainPlayerCards) {
        this.mainPlayerCards = mainPlayerCards;
    }

    public HoleCards getOtherPlayerCards() {
        return otherPlayerCards;
    }

    public void setOtherPlayerCards(HoleCards otherPlayerCards) {
        this.otherPlayerCards = otherPlayerCards;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return "{" + mainPlayerCards + "}, {" + otherPlayerCards + "}, uniqueId=" + uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoleCardsTwoPlayers that = (HoleCardsTwoPlayers) o;
        return Objects.equals(mainPlayerCards, that.mainPlayerCards) &&
                Objects.equals(otherPlayerCards, that.otherPlayerCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainPlayerCards, otherPlayerCards);
    }
}
