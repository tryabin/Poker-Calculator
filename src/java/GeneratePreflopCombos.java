import java.util.ArrayList;
import java.util.List;


public class GeneratePreflopCombos {

    public static List<HoleCardsTwoPlayers> generateHoleCardCombos() {

//      Generate the different types of combos.
        List<HoleCardsTwoPlayers> comboList = new ArrayList<>();
        int uniqueId = addSuitedCombos(comboList, 0);
        addOffSuitCombos(comboList, uniqueId);

        return comboList;
    }


    private static int addSuitedCombos(List<HoleCardsTwoPlayers> comboList, int uniqueId) {

        Rank[] ranks = Rank.values();

        for (int rank1 = 0; rank1 < ranks.length; rank1++) {
            for (int rank2 = rank1 + 1; rank2 < ranks.length; rank2++) {
                Card mainPlayerCard1 = new Card(ranks[rank1], Suit.CLUBS);
                Card mainPlayerCard2 = new Card(ranks[rank2], Suit.CLUBS);
                HoleCards mainPlayerCards = new HoleCards(mainPlayerCard1, mainPlayerCard2);

//              Generate the hole cards of the other player.
                for (int otherCard1 = 0; otherCard1 < 52; otherCard1++) {
                    for (int otherCard2 = otherCard1+1; otherCard2 < 52; otherCard2++) {

//                      Skip if any of the other player's cards are identical to one of main player's cards.
                        if (otherCard1 == mainPlayerCard1.getId() ||
                                otherCard2 == mainPlayerCard1.getId() ||
                                otherCard1 == mainPlayerCard2.getId() ||
                                otherCard2 == mainPlayerCard2.getId()) {
                            continue;
                        }

                        Card otherPlayerCard1 = new Card(otherCard1);
                        Card otherPlayerCard2 = new Card(otherCard2);
                        HoleCards otherPlayerCards = new HoleCards(otherPlayerCard1, otherPlayerCard2);

                        HoleCardsTwoPlayers combo = new HoleCardsTwoPlayers(mainPlayerCards, otherPlayerCards, uniqueId);
                        comboList.add(combo);
                    }
                }

                uniqueId++;
            }
        }

        return uniqueId;
    }


    private static int addOffSuitCombos(List<HoleCardsTwoPlayers> comboList, int uniqueId) {

        Rank[] ranks = Rank.values();

        for (int rank1 = 0; rank1 < ranks.length; rank1++) {
            for (int rank2 = rank1; rank2 < ranks.length; rank2++) {
                Suit mainSuit1 = Suit.CLUBS;
                Suit mainSuit2 = Suit.DIAMONDS;

                Card mainPlayerCard1 = new Card(ranks[rank1], mainSuit1);
                Card mainPlayerCard2 = new Card(ranks[rank2], mainSuit2);
                HoleCards mainPlayerCards = new HoleCards(mainPlayerCard1, mainPlayerCard2);

//              Generate the hole cards of the other player.
                for (int otherCard1 = 0; otherCard1 < 52; otherCard1++) {
                    for (int otherCard2 = otherCard1+1; otherCard2 < 52; otherCard2++) {

//                      Skip if any of the other player's cards are identical to one of main player's cards.
                        if (otherCard1 == mainPlayerCard1.getId() ||
                                otherCard2 == mainPlayerCard1.getId() ||
                                otherCard1 == mainPlayerCard2.getId() ||
                                otherCard2 == mainPlayerCard2.getId()) {
                            continue;
                        }

                        Card otherPlayerCard1 = new Card(otherCard1);
                        Card otherPlayerCard2 = new Card(otherCard2);
                        HoleCards otherPlayerCards = new HoleCards(otherPlayerCard1, otherPlayerCard2);

                        HoleCardsTwoPlayers combo = new HoleCardsTwoPlayers(mainPlayerCards, otherPlayerCards, uniqueId);
                        comboList.add(combo);
                    }
                }

                uniqueId++;
            }
        }

        return uniqueId;
    }
}




