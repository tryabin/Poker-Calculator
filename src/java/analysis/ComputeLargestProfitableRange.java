package analysis;

import analysis.structures.HoleCardsVersusRangeResult;
import analysis.structures.Position;
import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static util.EquityCalculationFunctions.*;


public class ComputeLargestProfitableRange {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Parameters
        double startingStackSB = 5;
        double startingStackBB = 2;
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
        Set<HoleCards> entireRange = new HashSet<>(holeCardTallies.keySet());

        // Get a map of the most profitable ranges for all hole cards.
        List<Set<HoleCards>> profitableRanges = getProfitableRanges(startingStackSB, startingStackBB, playerPosition, entireRange, holeCardComboTallies);

        // Find the best ranges for some test hole cards.
        HoleCards holeCardsToFindBestRangesFor = new HoleCards(new Card(Rank.KING, Suit.SPADES), new Card(Rank.TEN, Suit.CLUBS));
        holeCardsToFindBestRangesFor = convertHoleCardsToKeyVersion(holeCardsToFindBestRangesFor);

        double playerStartingStack = playerPosition == Position.SB ? startingStackSB : startingStackBB;
        double opponentStartingStack = playerPosition == Position.SB ? startingStackBB : startingStackSB;
        System.out.println("Ranges where it's better to go all-in than to fold with " + holeCardsToFindBestRangesFor.toRankAndTypeString() + " in the " + playerPosition + " position when your stack is " + playerStartingStack + " and your opponent's stack is " + opponentStartingStack + " = ");
        for (Set<HoleCards> range : profitableRanges) {
            String holeCardsPresentInRange = range.contains(holeCardsToFindBestRangesFor) ? "Yes" : "No";
            System.out.println(holeCardsPresentInRange + ", " + range.size());
        }
    }


    public static List<Set<HoleCards>> getProfitableRanges(double startingStackSB, double startingStackBB,
                                                           Position playerPosition,
                                                           Set<HoleCards> entireRange,
                                                           Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies) {

        // Run iterations computing a new best range against the current best range until convergence.
        long start = System.nanoTime();

        // Construct initial ranges for the player and opponent.
        Set<HoleCards> curBestRangePlayer = entireRange;
        Set<HoleCards> curBestRangeOpponent = entireRange;

        // Continuously find the best ranges against the current best ranges for the player and opponent until
        // the range for the player converges.
        int iterationsToConverge = 15;
        int maxNumberOfRangesToSave = 10;
        Position currentPosition = playerPosition;

        // There could be multiple best ranges so we save all of them.
        List<Set<HoleCards>> bestRanges = new ArrayList<>();
        for (int i = 0; i < iterationsToConverge + maxNumberOfRangesToSave; i++) {

            // Need to run two inner iterations so we can switch the player position to the other position
            // and then back to their original position.
            for (int j = 0; j < 2; j++) {

                Set<HoleCards> newBestRange = new HashSet<>();

                // Switch the current position since the previous range was computed for the other position.
                currentPosition = currentPosition == Position.SB ? Position.BB : Position.SB;

                // Find all the hands where going all-in is better than folding against the previously found range.
                for (HoleCards holeCards : entireRange) {

                    // Compute the total number of hands that can be played against the current hole cards.
                    int totalNumberOfHandsAgainstHoleCards = getTotalNumberOfHands(holeCardComboTallies, entireRange, holeCards);

                    // Compare the current hole cards against the current best range for the other position.
                    HoleCardsVersusRangeResult result;
                    if (currentPosition == playerPosition) {
                        result = getEquityOfHoleCardsVersusRange(holeCards, curBestRangeOpponent, holeCardComboTallies);
                    }
                    else {
                        result = getEquityOfHoleCardsVersusRange(holeCards, curBestRangePlayer, holeCardComboTallies);
                    }

                    // See if it is better to fold or go all-in for the current hand.
                    double stackSizeIfGoAllIn = computeAverageStackAfterHandFromHoleCardsVersusRangeResult(startingStackSB,
                                                                                                           startingStackBB,
                                                                                                           currentPosition,
                                                                                                           totalNumberOfHandsAgainstHoleCards,
                                                                                                           result,
                                                                                                           1);

                    double stackSizeIfFold = computeAverageStackAfterHandFromHoleCardsVersusRangeResult(startingStackSB,
                                                                                                        startingStackBB,
                                                                                                        currentPosition,
                                                                                                        totalNumberOfHandsAgainstHoleCards,
                                                                                                        result,
                                                                                                        0);


                    // Add a range element if the optimal play percentage for the current hole card is greater than 0.
                    if (stackSizeIfGoAllIn > stackSizeIfFold) {
                        newBestRange.add(holeCards);
                    }
                }

                // Set the new best range for the current position.
                if (currentPosition == playerPosition) {
                    curBestRangePlayer = newBestRange;
                }
                else {
                    curBestRangeOpponent = newBestRange;
                }
            }

            // Finish early if we have converged on a range for the main player.
            if (i >= iterationsToConverge) {
                if (bestRanges.contains(curBestRangePlayer)) {
                    break;
                }
                bestRanges.add(curBestRangePlayer);
            }
        }

        long end = System.nanoTime();

        return bestRanges;
    }
}
