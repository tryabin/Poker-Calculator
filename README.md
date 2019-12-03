# Poker-Calculator
A Texas Hold 'em poker calculator. Can compute pre-flop equities for a heads-up scenario using CUDA, and then advise if it is better to go all-in or to fold, depending on the stack sizes and player position.

# Requirements
* The CUDA Toolkit and the JCuda libraries are required to run the `ComputeHandPreFlopEquities` program, which calculates the results for every hand combination between two players. 
* JavaFX and the Commons Lang library are required to run the `PreFlopShoveAdviser` UI application.
* When running the `PreFlopShoveAdviser` application the following VM arguments are required:

  `--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.base`

  where `${PATH_TO_FX}` is the path to where JavaFX is installed.

# Usage
The `PreFlopShoveAdviser` UI application can be used to determine if it is better to go all-in or to fold in different heads-up scenarios. It assumes players will only fold or go all-in.

<p align="center">
  <img src="https://i.imgur.com/owuc0s4.png" alt="UI example"/>
</p>


* The "Hole Cards" field represents your hand, with the last letter being `s` for `suited` or `o` for `off-suit`. For example if your hand is `King of Hearts` and `Ten of Diamonds` then you would enter `KTo` in the "Hole Cards" field.
* When the "Calculate" button is pressed the application computes the profitable ranges for the given stack sizes. It then displays the size of those ranges below. Ranges that are colored green indicate that it is better to go all-in than to fold with your hand. There can be multiple profitable ranges, depending on your opponent's range. In the above example it is shown that if the opponent is playing very tight, such that their range is just 5 or 6 hands, then your profitable range is large. However if your opponent is playing looser, then your profitable range is smaller.

# Algorithm to Compute Profitable Ranges
1. Both players start with a range that includes all hands. 
2. A new range is computed for the opponent that includes all hands that are profitable against the player's current range.
3. A new range is computed for the player that includes all hands that are profitable against the opponent's current range.
4. Steps 2 and 3 are repeated until the set of ranges between the two players converges. The result is this set of ranges.

# Other Programs
The `ComputeHandPreFlopEquities` program can be run to reproduce the `holeCardComboTallies.dat` file, which contains the results for every possible combination of cards two players can have. This program uses CUDA to run on the GPU, and may take several minutes to complete. On Windows the TdrDelay registry value may need to be increased so that the CUDA kernel doesn't timeout when doing the computations.

The `CombineTalliesAgainstAllHands` program generates the `holeCardTallies.dat` file, which contains the equities against all hands for the 169 uniquely valued hands that can be dealt to a single player.

# Acknowledgements 
The CUDA code that performs the hand evaluations was adapted from https://github.com/HenryRLee/PokerHandEvaluator. 


