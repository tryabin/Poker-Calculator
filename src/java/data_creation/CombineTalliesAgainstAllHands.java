package data_creation;

import data_creation.structures.HoleCards;
import data_creation.structures.HoleCardsTwoPlayers;
import data_creation.structures.OutcomeTallies;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CombineTalliesAgainstAllHands {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//      Load the data file.
        String holeCardTalliesMapFile = "holeCardComboTallies.dat";
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardTalliesMapFile)));
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

//      Output a map of the tallies for each hole card combo against all hands.
        String holeCardTalliesFile = "holeCardTallies.dat";
        ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(holeCardTalliesFile)));
        out.writeObject(holeCardTallies);
        out.flush();
        out.close();
    }
}
