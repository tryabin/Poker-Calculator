package data_creation.structures;

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

    public HoleCardsType getType() {
        if (card1.getRank() == card2.getRank()) {
            return HoleCardsType.PAIR;
        }
        else if (card1.getSuit() == card2.getSuit()) {
            return HoleCardsType.SUITED;
        }
        return HoleCardsType.OFFSUIT;
    }

    public boolean anyCardsEqual(HoleCards otherHoleCards) {
        return  card1.equals(otherHoleCards.getCard1()) ||
                card1.equals(otherHoleCards.getCard2()) ||
                card2.equals(otherHoleCards.getCard1()) ||
                card2.equals(otherHoleCards.getCard2());
    }

    @Override
    public String toString() {
        return "[" + card1.getRank() + " " + card1.getSuit() + ", " + card2.getRank() + " " + card2.getSuit() + "]";
    }

    @Override
//  Order-independent equals.
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoleCards holeCards = (HoleCards) o;
        return (Objects.equals(card1, holeCards.card1) && Objects.equals(card2, holeCards.card2)) ||
               (Objects.equals(card1, holeCards.card2) && Objects.equals(card2, holeCards.card1)) ;
    }

    @Override
//  Order-independent hash.
    public int hashCode() {
        return card1.hashCode() + card2.hashCode();
    }
}
