package analysis;

import data_creation.structures.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static analysis.ComputeWinPercentageAgainstBestRange.computeWinPercentageAgainstBestRange;
import static util.EquityCalculationFunctions.*;


public class ComputeLargestProfitableRange {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//      Parameters
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


//      Find the the worst hand that you can go all-in on that would still increase your chances of winning.
        double minWinPercentageIncrease = Double.POSITIVE_INFINITY;
        HoleCards worstHandWhereWinPercentageIncreases = null;
        for (HoleCards holeCards : holeCardTallies.keySet()) {

//          Find the best range against the current hole cards, taking the stack sizes into account.
            ComputeWinPercentageAgainstBestRangeResult result = computeWinPercentageAgainstBestRange(holeCards, startingStackBB, startingStackOpponentBB, playerPosition, holeCardTallies, holeCardComboTallies);

            double winPercentageAgainstBestRange = result.getWinPercentage();
            double blindLostIfYouFold = playerPosition == Position.SB ? .5 : 1;
            double winPercentageIncrease = winPercentageAgainstBestRange - (startingStackBB-blindLostIfYouFold)/(startingStackBB + startingStackOpponentBB);
            if (winPercentageIncrease > 0 && winPercentageIncrease < minWinPercentageIncrease) {
                minWinPercentageIncrease = winPercentageIncrease;
                worstHandWhereWinPercentageIncreases = holeCards;
            }
        }

        if (worstHandWhereWinPercentageIncreases == null) {
            System.out.println("There is no hand where it's ok to go all-in in the " + playerPosition + " position when your stack is " + startingStackBB + " and your opponent's stack is " + startingStackOpponentBB + ".");
        }
        else {
            System.out.println("Worst hand where it's ok to go all-in in the " + playerPosition + " position when your stack is " + startingStackBB + " and your opponent's stack is " + startingStackOpponentBB + " = " + worstHandWhereWinPercentageIncreases);
        }
    }
}
