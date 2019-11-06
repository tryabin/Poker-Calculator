package analysis;

import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static analysis.ComputeWinPercentageAgainstBestRange.computeWinPercentageAgainstBestRange;
import static analysis.ComputeWinPercentageAgainstBestRange.computeWinPercentageFromHoleCardsVersusRangeResult;
import static util.EquityCalculationFunctions.*;


public class ComputeLargestProfitableRange {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//      Parameters
        double startingStackBB = 10;
        double startingStackOpponentBB = 2;
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

//      Find the all the hands that you can go all-in on that would still increase your chances of winning.
        List<HoleCards> handsThatIncreaseWinPercentage = new ArrayList<>();
        for (HoleCards holeCards : holeCardsAgainstRandomHand) {

//          Find the best range against the current hole cards, taking the stack sizes into account.
            ComputeWinPercentageAgainstBestRangeResult result = computeWinPercentageAgainstBestRange(holeCards, startingStackBB, startingStackOpponentBB, playerPosition, holeCardsAgainstRandomHand, holeCardComboTallies);

            double winPercentageAgainstBestRange = result.getWinPercentage();
            double blindLostIfYouFold = playerPosition == Position.SB ? .5 : 1;
            double winPercentageIncrease = winPercentageAgainstBestRange - (startingStackBB-blindLostIfYouFold)/(startingStackBB + startingStackOpponentBB);
            if (winPercentageIncrease > 0) {
                handsThatIncreaseWinPercentage.add(holeCards);
            }
        }


//      Run iterations computing a new best range against the current best range until convergence.
        List<HoleCards> curBestRange = new ArrayList<>(handsThatIncreaseWinPercentage);
        int previousSizeOfBestRange = curBestRange.size();
        int newSizeOfBestRange = 0;
        while(previousSizeOfBestRange != newSizeOfBestRange) {

            previousSizeOfBestRange = newSizeOfBestRange;

//          Need to run two inner iterations so we can switch the player position back to their original position.
            for (int j = 0; j < 2; j++) {
//              Find all the hands that you can go all-in on that would still increase your chances of winning against the previously found range.
                List<HoleCards> newBestRange = new ArrayList<>();

//              Switch the player position since the previous range was computed for the player, and now we want the opponent
//              to have that range so we put them in the player's previous position.
                playerPosition = playerPosition == Position.SB ? Position.BB : Position.SB;
                double blindLostIfYouFold = playerPosition == Position.SB ? .5 : 1;
                double temp = startingStackBB;
                startingStackBB = startingStackOpponentBB;
                startingStackOpponentBB = temp;

                for (HoleCards holeCards : holeCardsAgainstRandomHand) {
                    int totalNumberOfHandsAgainstHoleCards = getTotalNumberOfHands(holeCardComboTallies, holeCardsAgainstRandomHand, holeCards);
                    HoleCardsVersusRangeResult result = getEquityOfHoleCardsVersusRange(holeCardComboTallies, curBestRange, holeCards);
                    double winPercentage = computeWinPercentageFromHoleCardsVersusRangeResult(startingStackBB, startingStackOpponentBB, playerPosition, totalNumberOfHandsAgainstHoleCards, result);
                    double winPercentageIncrease = winPercentage - (startingStackBB - blindLostIfYouFold)/(startingStackBB + startingStackOpponentBB);
                    if (winPercentageIncrease > 0) {
                        newBestRange.add(holeCards);
                    }
                }

                curBestRange = newBestRange;
            }

            newSizeOfBestRange = curBestRange.size();
        }


        System.out.println("Range where it's ok to go all-in in the " + playerPosition + " position when your stack is " + startingStackBB + " and your opponent's stack is " + startingStackOpponentBB + " against the previously found range = ");
        System.out.println(curBestRange.size() + "   " + Arrays.toString(curBestRange.toArray()));
    }
}
