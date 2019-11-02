import java.io.Serializable;
import java.util.Objects;

public final class HoleCards implements Serializable {
    private Card card1;
    private Card card2;

    public HoleCards(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
    }

    @Override
    public String toString() {
        return card1.getRank() + " " + card1.getSuit() + ", " + card2.getRank() + " " + card2.getSuit();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoleCards holeCards = (HoleCards) o;
        return Objects.equals(card1, holeCards.card1) &&
                Objects.equals(card2, holeCards.card2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card1, card2);
    }
}
