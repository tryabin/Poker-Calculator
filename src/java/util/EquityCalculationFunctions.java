package util;

import data_creation.structures.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EquityCalculationFunctions {

    public static HoleCardsVersusRangeResult getEquityOfHoleCardsVersusRange(Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies,
                                                                             List<HoleCards> range,
                                                                             HoleCards holeCards) {

//      Convert the hole cards to the version used in generating the combos.
        holeCards = convertHoleCardsToKeyVersion(holeCards);

//      Add up the tallies of the results between the given hole cards and every hand in the given range.
        int numberOfHandsInRange = 0;
        OutcomeTallies tallies = new OutcomeTallies();
        for (HoleCards baseRangeCards : range) {
            HoleCardsType curRangeCardsType = baseRangeCards.getType();

//          Generate variations of the current base range cards and add the tallies for the combos between the hole cards
//          and those variations.
            for (Suit suit1 : Suit.values()) {
                for (Suit suit2 : Suit.values()) {
                    Card rangeCard1 = new Card(baseRangeCards.getCard1().getRank(), suit1);
                    Card rangeCard2 = new Card(baseRangeCards.getCard2().getRank(), suit2);
                    HoleCards curRangeCardsVariation = new HoleCards(rangeCard1, rangeCard2);

//                  Skip if any of the cards in the variation equal one of the hole cards.
                    if (holeCards.anyCardsEqual(curRangeCardsVariation)) {
                        continue;
                    }

//                  Case when the base range cards are offsuit.
                    if (curRangeCardsType == HoleCardsType.OFFSUIT && suit1 == suit2) {
                        continue;
                    }

//                  Case when the base range cards are suited.
                    if (curRangeCardsType == HoleCardsType.SUITED && suit1 != suit2) {
                        continue;
                    }

//                  Case when the base range cards are pairs. The suit of the second card should always be greater.
                    if (curRangeCardsType == HoleCardsType.PAIR && suit1.ordinal() >= suit2.ordinal()) {
                        continue;
                    }

                    HoleCardsTwoPlayers combo = new HoleCardsTwoPlayers(holeCards, curRangeCardsVariation);
                    OutcomeTallies curTallies = holeCardComboTallies.get(combo);
                    tallies.addTallies(curTallies);
                    numberOfHandsInRange++;
                }
            }
        }

        return new HoleCardsVersusRangeResult(tallies.computeEquity(), numberOfHandsInRange);
    }


    public static int getTotalNumberOfHands(Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies,
                                            List<HoleCards> holeCardsAgainstRandomHand,
                                            HoleCards holeCards) {
        return getEquityOfHoleCardsVersusRange(holeCardComboTallies, holeCardsAgainstRandomHand, holeCards).getNumberOfHandsInRange();
    }


    private static HoleCards convertHoleCardsToKeyVersion(HoleCards holeCards) {

//      Make sure the rank of the first card is less than or equal to the rank of the second card, because that is how
//      the combos were generated.
        Rank firstCardRank = holeCards.getCard1().getRank();
        Rank secondCardRank = holeCards.getCard2().getRank();
        if (firstCardRank.ordinal() > secondCardRank.ordinal()) {
            firstCardRank = holeCards.getCard2().getRank();
            secondCardRank = holeCards.getCard1().getRank();
        }

//      Convert the given hole cards into a version of the hole cards that was used to compute the hole card combo tallies.
//      Default is that the first card is the lowest rank, and is clubs, while the second card is clubs for suited hands,
//      and diamonds for offsuit hands.
        if (holeCards.getType() == HoleCardsType.PAIR || holeCards.getType() == HoleCardsType.OFFSUIT) {
            return new HoleCards(new Card(firstCardRank, Suit.CLUBS), new Card(secondCardRank, Suit.DIAMONDS));
        }
        else {
            return new HoleCards(new Card(firstCardRank, Suit.CLUBS), new Card(secondCardRank, Suit.CLUBS));
        }
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
