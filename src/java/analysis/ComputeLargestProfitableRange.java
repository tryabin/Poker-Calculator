package analysis;

import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static util.EquityCalculationFunctions.*;


public class ComputeLargestProfitableRange {

    enum Position {
        SB, BB;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//      Parameters
        HoleCards holeCards = new HoleCards(new Card(Rank.TEN, Suit.CLUBS), new Card(Rank.KING, Suit.CLUBS));
//        HoleCards holeCards = new HoleCards(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.ACE, Suit.SPADES));

        double startingStackBB = 10;
        double startingStackOpponentBB = 10;
        double allInPot = startingStackBB < startingStackOpponentBB ? 2*startingStackBB : 2*startingStackOpponentBB;
        Position playerPosition = Position.SB;


//      Load the pre-flop tallies against a random hand.
        String holeCardTalliesFile = "holeCardTallies.dat";
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardTalliesFile)));
        Map<HoleCards, OutcomeTallies> holeCardTallies = (Map<HoleCards, OutcomeTallies>) in.readObject();

//      Load the tallies for every pre-flop combo.
        String holeCardComboTalliesFile = "holeCardComboTallies.dat";
        in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardComboTalliesFile)));
        Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies = (Map<HoleCardsTwoPlayers, OutcomeTallies>) in.readObject();
        in.close();

//      Sort the tallies against random hands.
        holeCardTallies = sortByValue(holeCardTallies);
        Map<HoleCards, Integer> sortedHoleCardIndices = new HashMap<>();
        int holeCardIndex = 0;
        for (Map.Entry<HoleCards, OutcomeTallies> entry : holeCardTallies.entrySet()) {
            sortedHoleCardIndices.put(entry.getKey(), holeCardIndex);
            holeCardIndex++;
        }


//      Find the best range against the hole cards, taking the stack sizes into account.
        int indexOfBestRange = 0;
        double winPercentageAgainstBestRange = 1;
        int totalNumberOfHandsAgainstHoleCards = getTotalNumberOfHands(holeCardComboTallies, holeCardTallies, holeCards);
        for (int i = 0; i < holeCardTallies.size(); i++) {
            HoleCardsVersusRangeResult result = getEquityOfHoleCardsVersusRange(holeCardComboTallies, holeCardTallies, i, holeCards);
            double equityAgainstRange = result.getEquity()/100;
            double callPercentage = result.getNumberOfHandsInRange() / (double)totalNumberOfHandsAgainstHoleCards;

            double winPercentage = 1;
            double stackAfterAllIn = startingStackBB - allInPot/2;
            double blindWonIfOpponentFolds = playerPosition == Position.SB ? 1 : .5;
            double averageStackAfterHand = stackAfterAllIn + allInPot*equityAgainstRange*callPercentage + (1 - callPercentage)*(blindWonIfOpponentFolds + allInPot/2);
            double averageOpponentStackAfterHand = startingStackOpponentBB - (averageStackAfterHand - startingStackBB);
            winPercentage = averageStackAfterHand/(averageStackAfterHand + averageOpponentStackAfterHand);

            if (winPercentage < winPercentageAgainstBestRange) {
                winPercentageAgainstBestRange = winPercentage;
                indexOfBestRange = i;
            }
        }

        System.out.println("Best range against " + holeCards + " starts at " + new ArrayList<>(holeCardTallies.keySet()).get(indexOfBestRange) + ", win % against that range = " + winPercentageAgainstBestRange);
    }
}
