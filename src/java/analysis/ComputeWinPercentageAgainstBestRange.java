package analysis;

import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static util.EquityCalculationFunctions.*;

public class ComputeWinPercentageAgainstBestRange {


    public static void main(String[] args) throws IOException, ClassNotFoundException {

//      Parameters
        HoleCards holeCards = new HoleCards(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.TEN, Suit.CLUBS));
        double startingStackBB = 50;
        double startingStackOpponentBB = 50;
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

//      Sort the hole card tallies map based on the equity of the hole cards against a random hand.
        holeCardTallies = sortByValue(holeCardTallies);

        ComputeWinPercentageAgainstBestRangeResult result = computeWinPercentageAgainstBestRange(holeCards, startingStackBB, startingStackOpponentBB, playerPosition, holeCardTallies, holeCardComboTallies);
        System.out.println("Best range against " + holeCards + " with a starting stack of " + startingStackBB + " BB and an opponent stack of " + startingStackOpponentBB + " BB starts at " + result.getLowestCardsInBestRange() + ".");
        System.out.println("Win percentage against that range = " + result.getWinPercentage());
    }


    public static ComputeWinPercentageAgainstBestRangeResult computeWinPercentageAgainstBestRange(HoleCards holeCards,
                                                                                                  double startingStackBB,
                                                                                                  double startingStackOpponentBB,
                                                                                                  Position playerPosition,
                                                                                                  Map<HoleCards, OutcomeTallies> holeCardTallies,
                                                                                                  Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies) {

        double allInPot = startingStackBB < startingStackOpponentBB ? 2*startingStackBB : 2*startingStackOpponentBB;

        int indexOfBestRange = 0;
        double winPercentageAgainstBestRange = 1;
        int totalNumberOfHandsAgainstHoleCards = getTotalNumberOfHands(holeCardComboTallies, holeCardTallies, holeCards);
        for (int j = 0; j < holeCardTallies.size(); j++) {
            HoleCardsVersusRangeResult result = getEquityOfHoleCardsVersusRange(holeCardComboTallies, holeCardTallies, j, holeCards);
            double equityAgainstRange = result.getEquity()/100;
            double callPercentage = result.getNumberOfHandsInRange()/(double) totalNumberOfHandsAgainstHoleCards;

            double stackAfterAllIn = startingStackBB - allInPot/2;
            double blindWonIfOpponentFolds = playerPosition == Position.SB ? 1 : .5;
            double averageStackAfterHand = stackAfterAllIn + allInPot*equityAgainstRange*callPercentage + (1 - callPercentage)*(blindWonIfOpponentFolds + allInPot/2);
            double averageOpponentStackAfterHand = startingStackOpponentBB - (averageStackAfterHand - startingStackBB);
            double winPercentage = averageStackAfterHand/(averageStackAfterHand + averageOpponentStackAfterHand);

            if (winPercentage < winPercentageAgainstBestRange) {
                winPercentageAgainstBestRange = winPercentage;
                indexOfBestRange = j;
            }
        }

        return new ComputeWinPercentageAgainstBestRangeResult(winPercentageAgainstBestRange, new ArrayList<>(holeCardTallies.keySet()).get(indexOfBestRange));
    }
}
