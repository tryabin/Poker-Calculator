package data_creation.structures;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static util.EquityCalculationFunctions.convertHoleCardsToKeyVersion;

public final class HoleCards implements Serializable {

    private static final long serialVersionUID = -6064265357020050887L;

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

    public String toRankAndTypeString() {
        Rank highestRank = Rank.values()[max(card1.getRank().ordinal(), card2.getRank().ordinal())];
        Rank lowestRank = Rank.values()[min(card1.getRank().ordinal(), card2.getRank().ordinal())];
        return highestRank.toString() + lowestRank.toString() + this.getType().toString();
    }

    // Converts a string representing hole cards into a HoleCards object. Example "KTo" equals King-Ten Offsuit.
    public static HoleCards fromRankAndTypeString(String holeCardsString) {

        if (holeCardsString.length() < 2) {
            throw new IllegalArgumentException("Hole cards string length must be 2 or greater.");
        }

        Rank rank1 = Rank.get(holeCardsString.substring(0, 1));
        Rank rank2 = Rank.get(holeCardsString.substring(1, 2));

        if (rank1 != rank2 && holeCardsString.length() == 2) {
            throw new IllegalArgumentException("Unpaired hands must be specified as either suited or unsuited.");
        }

        HoleCards holeCards;
        if (rank1 == rank2) {
            holeCards = new HoleCards(new Card(rank1, Suit.CLUBS), new Card(rank2, Suit.DIAMONDS));

        }
        else {
            HoleCardsType type = HoleCardsType.get(holeCardsString.substring(2, 3));
            holeCards = type == HoleCardsType.OFFSUIT ? new HoleCards(new Card(rank1, Suit.CLUBS), new Card(rank2, Suit.DIAMONDS)) :
                                                        new HoleCards(new Card(rank1, Suit.CLUBS), new Card(rank2, Suit.CLUBS));
        }
        holeCards = convertHoleCardsToKeyVersion(holeCards);

        return holeCards;
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
