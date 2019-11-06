package analysis;

import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
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

        List<HoleCards> holeCardsAgainstRandomHand = new ArrayList<>(holeCardTallies.keySet());
        ComputeWinPercentageAgainstBestRangeResult result = computeWinPercentageAgainstBestRange(holeCards, startingStackBB, startingStackOpponentBB, playerPosition, holeCardsAgainstRandomHand, holeCardComboTallies);
        System.out.println("Best range against " + holeCards + " with a starting stack of " + startingStackBB + " BB and an opponent stack of " + startingStackOpponentBB + " BB starts at " + result.getRange().get(0) + ".");
        System.out.println("Win percentage against that range = " + result.getWinPercentage());
    }


    public static ComputeWinPercentageAgainstBestRangeResult computeWinPercentageAgainstBestRange(HoleCards holeCards,
                                                                                                  double startingStackBB,
                                                                                                  double startingStackOpponentBB,
                                                                                                  Position playerPosition,
                                                                                                  List<HoleCards> holeCardsAgainstRandomHand,
                                                                                                  Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies) {

        int indexOfBestRange = 0;
        double winPercentageAgainstBestRange = 1;
        int totalNumberOfHandsAgainstHoleCards = getTotalNumberOfHands(holeCardComboTallies, holeCardsAgainstRandomHand, holeCards);
        for (int i = 0; i < holeCardsAgainstRandomHand.size(); i++) {
            HoleCardsVersusRangeResult result = getEquityOfHoleCardsVersusRange(holeCardComboTallies, holeCardsAgainstRandomHand.subList(i, holeCardsAgainstRandomHand.size()), holeCards);
            double winPercentage = computeWinPercentageFromHoleCardsVersusRangeResult(startingStackBB, startingStackOpponentBB, playerPosition, totalNumberOfHandsAgainstHoleCards, result);

            if (winPercentage < winPercentageAgainstBestRange) {
                winPercentageAgainstBestRange = winPercentage;
                indexOfBestRange = i;
            }
        }

        return new ComputeWinPercentageAgainstBestRangeResult(winPercentageAgainstBestRange, holeCardsAgainstRandomHand.subList(indexOfBestRange, holeCardsAgainstRandomHand.size()));
    }


    public static double computeWinPercentageFromHoleCardsVersusRangeResult(double startingStackBB,
                                                                            double startingStackOpponentBB,
                                                                            Position playerPosition,
                                                                            double totalNumberOfHandsAgainstHoleCards,
                                                                            HoleCardsVersusRangeResult result) {

        double allInPot = startingStackBB < startingStackOpponentBB ? 2*startingStackBB : 2*startingStackOpponentBB;
        double stackAfterAllIn = startingStackBB - allInPot/2;
        double blindWonIfOpponentFolds = playerPosition == Position.SB ? 1 : .5;

        double equityAgainstRange = result.getEquity();
        double callPercentage = result.getNumberOfHandsInRange()/totalNumberOfHandsAgainstHoleCards;
        double averageStackAfterHand = stackAfterAllIn + allInPot*equityAgainstRange*callPercentage + (1 - callPercentage)*(blindWonIfOpponentFolds + allInPot/2);
        double averageOpponentStackAfterHand = startingStackOpponentBB - (averageStackAfterHand - startingStackBB);

        return averageStackAfterHand/(averageStackAfterHand + averageOpponentStackAfterHand);
    }
}
