package analysis;

import analysis.structures.Position;
import data_creation.structures.HoleCards;
import data_creation.structures.HoleCardsTwoPlayers;
import analysis.structures.HoleCardsVersusRangeResult;
import data_creation.structures.OutcomeTallies;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static util.EquityCalculationFunctions.computeWinPercentageFromHoleCardsVersusRangeResult;
import static util.EquityCalculationFunctions.*;


public class ComputeLargestProfitableRange {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Parameters
        double startingStackBB = 5;
        double startingStackOpponentBB = 5;
        Position playerPosition = Position.SB;


        // Load the pre-flop tallies against a random hand.
        String holeCardTalliesFile = "holeCardTallies.dat";
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardTalliesFile)));
        Map<HoleCards, OutcomeTallies> holeCardTallies = (Map<HoleCards, OutcomeTallies>) in.readObject();

        // Load the tallies for every pre-flop combo.
        String holeCardComboTalliesFile = "holeCardComboTallies.dat";
        in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardComboTalliesFile)));
        Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies = (Map<HoleCardsTwoPlayers, OutcomeTallies>) in.readObject();
        in.close();

        // Sort the hole card tallies map based on the equity of the hole cards against a random hand.
        holeCardTallies = sortByValue(holeCardTallies);
        List<HoleCards> holeCardsAgainstRandomHand = new ArrayList<>(holeCardTallies.keySet());



        // Run iterations computing a new best range against the current best range until convergence.
        long start = System.nanoTime();

        List<HoleCards> curBestRange = new ArrayList<>(holeCardsAgainstRandomHand);
        int previousSizeOfBestRange = curBestRange.size();
        int newSizeOfBestRange = 0;
        while(previousSizeOfBestRange != newSizeOfBestRange) {

            previousSizeOfBestRange = newSizeOfBestRange;

            // Need to run two inner iterations so we can switch the player position back to their original position.
            for (int j = 0; j < 2; j++) {
                // Find all the hands that you can go all-in on that would still increase your chances of winning against the previously found range.
                List<HoleCards> newBestRange = new ArrayList<>();

                // Switch the player position since the previous range was computed for the player, and now we want the opponent
                // to have that range so we put them in the player's previous position.
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

            System.out.println("curBestRange size = " + curBestRange.size());
        }


        long end = System.nanoTime();

        System.out.println("time to compute largest profitable range = " + (end - start)/1e9);
        System.out.println("Range where it's better to go all-in than to fold in the " + playerPosition + " position when your stack is " + startingStackBB + " and your opponent's stack is " + startingStackOpponentBB + " = ");
        System.out.println(curBestRange.size() + " total hands : " + Arrays.toString(curBestRange.toArray()));
    }
}
