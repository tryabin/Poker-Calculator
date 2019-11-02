import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class ComputeEquities {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String holeCardTalliesMapFile = "holeCardComboTallies.dat";

//      Load the data file.
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(holeCardTalliesMapFile));
        Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies = (Map<HoleCardsTwoPlayers, OutcomeTallies>) in.readObject();
        in.close();

//      Sum up the tallies for each hole card combo.
        Map<HoleCards, OutcomeTallies> holeCardTallies = new HashMap<>();
        for (HoleCardsTwoPlayers combo : holeCardComboTallies.keySet()) {

            if (!holeCardTallies.containsKey(combo.getMainPlayerCards())) {
                holeCardTallies.put(combo.getMainPlayerCards(), new OutcomeTallies(0, 0, 0));
            }

            OutcomeTallies curTallies = holeCardComboTallies.get(combo);
            holeCardTallies.get(combo.getMainPlayerCards()).addTallies(curTallies);
        }

//      Print the hole card equities against a random hand.
        for (Map.Entry<HoleCards, OutcomeTallies> entry : holeCardTallies.entrySet()) {
            OutcomeTallies tallies = entry.getValue();
            double total = tallies.getWins() + tallies.getLosses() + tallies.getTies();
            double equity = (tallies.getWins() + tallies.getTies()/2f) / total * 100;

            System.out.println(entry.getKey() + " : " + equity);
        }
    }
}
