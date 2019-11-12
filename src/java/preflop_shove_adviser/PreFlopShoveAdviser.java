package preflop_shove_adviser;

import analysis.structures.Position;
import data_creation.structures.HoleCards;
import data_creation.structures.HoleCardsTwoPlayers;
import data_creation.structures.OutcomeTallies;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static analysis.ComputeLargestProfitableRange.getProfitableRanges;
import static util.EquityCalculationFunctions.sortByValue;

public class PreFlopShoveAdviser extends Application {

    // Data used for calculation.
    private Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies;
    private Set<HoleCards> entireRange;

    // UI elements that store parameters used in the calculation.
    private HoleCardsTextField holeCardsTextField;
    private ChipCountTextField mainPlayerChipCountTextField;
    private ChipCountTextField otherPlayerChipCountTextField;
    private ChipCountTextField bigBlindChipCountTextField;
    private Button positionButton;

    // The button used to start the calculation.
    private Button calculateButton;

    // The main action column.
    private VBox resultsColumnLeft;
    private VBox resultsColumnRight;

    // Configure the fonts.
    private Font textFieldFont = new Font(24);
    private Font textFieldDescriptionFont = new Font(14);
    private Font positionButtonFont = new Font(24);
    private Font calculateButtonFont = new Font(24);
    private Font resultsAreaFont = new Font(24);

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the data used for calculations.
        loadData();

        // Add all the UI elements.
        GridPane grid = setupUI();

        // Configure the text fields to switch to the next text field when ENTER is pressed, or
        // compute the best ranges if ENTER is pressed on the last text field.
        configureParameterElementHandlers();

        // Configure the Calculate handler.
        EventHandler<ActionEvent> calculateHandler = new EventHandler<>()  {
            public void handle(ActionEvent event) {
                handleCalculateEvent(Double.parseDouble(mainPlayerChipCountTextField.getText().replace(",", "")),
                                     Double.parseDouble(otherPlayerChipCountTextField.getText().replace(",", "")),
                                     Double.parseDouble(bigBlindChipCountTextField.getText().replace(",", "")),
                                     Position.valueOf(positionButton.getText()),
                                     entireRange,
                                     holeCardComboTallies,
                                     holeCardsTextField.getText());
            }
        };
        calculateButton.setOnAction(calculateHandler);


        Scene scene = new Scene(grid, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Pre-Flop Shove Adviser");
        primaryStage.show();
    }


    // Sets up what happens when ENTER is pressed for every parameter UI element.
    private void configureParameterElementHandlers() {
        holeCardsTextField.setOnAction(e -> mainPlayerChipCountTextField.requestFocus());
        mainPlayerChipCountTextField.setOnAction(e -> otherPlayerChipCountTextField.requestFocus());
        otherPlayerChipCountTextField.setOnAction(e -> bigBlindChipCountTextField.requestFocus());
        bigBlindChipCountTextField.setOnAction(e -> positionButton.requestFocus());
        positionButton.setOnAction(e -> {
            String newText = Position.valueOf(positionButton.getText()) == Position.SB ? Position.BB.toString() : Position.SB.toString();
            positionButton.setText(newText);
            calculateButton.requestFocus();
        });
    }



    private void handleCalculateEvent(double mainPlayerChips, double otherPlayerChips, double bigBlindChips,
                                      Position playerPosition,
                                      Set<HoleCards> entireRange,
                                      Map<HoleCardsTwoPlayers, OutcomeTallies> holeCardComboTallies,
                                      String holeCardsString) {

        // Convert the chip counts into blind sizes.
        double mainPlayerBB = mainPlayerChips/bigBlindChips;
        double otherPlayerBB = otherPlayerChips/bigBlindChips;
        double startingStackSB = playerPosition == Position.SB ? mainPlayerBB : otherPlayerBB;
        double startingStackBB = playerPosition == Position.BB ? mainPlayerBB : otherPlayerBB;

        // Parse the hole cards string.
        HoleCards holeCards = HoleCards.fromRankAndTypeString(holeCardsString);

        // Compute the profitable ranges for the given stack sizes.
        List<List<Set<HoleCards>>> bestRanges = getProfitableRanges(startingStackSB, startingStackBB, playerPosition, entireRange, holeCardComboTallies);

        // Display the results.
        resultsColumnLeft.getChildren().clear();
        resultsColumnRight.getChildren().clear();

        for (int i = 0; i < bestRanges.get(0).size(); i++) {
            Set<HoleCards> playerRange = bestRanges.get(0).get(i);
            Set<HoleCards> opponentRange = bestRanges.get(1).get(i);
            String leftString = "player range size = " + playerRange.size();
            String rightString = "opponent range size = " + opponentRange.size();

            // Set the left text.
            Text leftResultText = new Text();
            leftResultText.setFont(resultsAreaFont);
            leftResultText.setFill(playerRange.contains(holeCards) ? Color.GREEN : Color.RED);
            leftResultText.setText(leftString);
            resultsColumnLeft.getChildren().add(leftResultText);

            // Set the right text.
            Text rightResultText = new Text();
            rightResultText.setFont(resultsAreaFont);
            rightResultText.setFill(playerRange.contains(holeCards) ? Color.GREEN : Color.RED);
            rightResultText.setText(rightString);
            resultsColumnRight.getChildren().add(rightResultText);
        }
    }


    private void loadData() throws IOException, ClassNotFoundException {

        // Load the pre-flop tallies against a random hand.
        String holeCardTalliesFile = "holeCardTallies.dat";
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardTalliesFile)));
        Map<HoleCards, OutcomeTallies> holeCardTallies = (Map<HoleCards, OutcomeTallies>) in.readObject();

        // Load the tallies for every pre-flop combo.
        String holeCardComboTalliesFile = "holeCardComboTallies.dat";
        in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(holeCardComboTalliesFile)));
        this.holeCardComboTallies = (Map<HoleCardsTwoPlayers, OutcomeTallies>) in.readObject();
        in.close();

        // Sort the hole card tallies map based on the equity of the hole cards against a random hand.
        holeCardTallies = sortByValue(holeCardTallies);
        this.entireRange = new HashSet<>(holeCardTallies.keySet());
    }


    private GridPane setupUI() {

        // Hole Cards text field.
        holeCardsTextField = new HoleCardsTextField();
        holeCardsTextField.setFont(textFieldFont);
        holeCardsTextField.setPrefSize(100, 24);
        holeCardsTextField.setAlignment(Pos.CENTER);

        // Hole Cards text field description.
        Text holeCardsDescription = new Text(0, 0, "Hole Cards");
        holeCardsDescription.setFont(textFieldDescriptionFont);

        // VBox for the Hole Cards text field.
        VBox holeCardsColumn = new VBox();
        holeCardsColumn.setAlignment(Pos.TOP_CENTER);
        holeCardsColumn.getChildren().add(holeCardsTextField);
        holeCardsColumn.getChildren().add(holeCardsDescription);


        // Main player chip count text field.
        mainPlayerChipCountTextField = new ChipCountTextField();
        mainPlayerChipCountTextField.setFont(textFieldFont);
        mainPlayerChipCountTextField.setPrefSize(150, 24);
        mainPlayerChipCountTextField.setAlignment(Pos.CENTER);

        // Main player chip count text field description.
        Text mainPlayerChipCountDescription = new Text(0, 0, "My Chip Count");
        mainPlayerChipCountDescription.setFont(textFieldDescriptionFont);

        // VBox for the main player chip count text field.
        VBox mainPlayerChipCountColumn = new VBox();
        mainPlayerChipCountColumn.setAlignment(Pos.TOP_CENTER);
        mainPlayerChipCountColumn.getChildren().add(mainPlayerChipCountTextField);
        mainPlayerChipCountColumn.getChildren().add(mainPlayerChipCountDescription);


        // Other player chip count text field.
        otherPlayerChipCountTextField = new ChipCountTextField();
        otherPlayerChipCountTextField.setFont(textFieldFont);
        otherPlayerChipCountTextField.setPrefSize(150, 24);
        otherPlayerChipCountTextField.setAlignment(Pos.CENTER);

        // Other player chip count text field description.
        Text otherPlayerChipCountDescription = new Text(0, 0, "Opponent Chip Count");
        otherPlayerChipCountDescription.setFont(textFieldDescriptionFont);

        // VBox for the other player chip count text field.
        VBox otherPlayerChipCountColumn = new VBox();
        otherPlayerChipCountColumn.setAlignment(Pos.TOP_CENTER);
        otherPlayerChipCountColumn.getChildren().add(otherPlayerChipCountTextField);
        otherPlayerChipCountColumn.getChildren().add(otherPlayerChipCountDescription);


        // Big blind chip count text field.
        bigBlindChipCountTextField = new ChipCountTextField();
        bigBlindChipCountTextField.setFont(textFieldFont);
        bigBlindChipCountTextField.setPrefSize(150, 24);
        bigBlindChipCountTextField.setAlignment(Pos.CENTER);

        // Big blind chip count text field description.
        Text bigBlindChipCountDescription = new Text(0, 0, "Big Blind");
        bigBlindChipCountDescription.setFont(textFieldDescriptionFont);

        // VBox for the Big blind chip count text field.
        VBox bigBlindChipCountColumn = new VBox();
        bigBlindChipCountColumn.setAlignment(Pos.TOP_CENTER);
        bigBlindChipCountColumn.getChildren().add(bigBlindChipCountTextField);
        bigBlindChipCountColumn.getChildren().add(bigBlindChipCountDescription);


        // Position buttons.
        positionButton = new Button("SB");
        positionButton.setFont(positionButtonFont);
        positionButton.setPrefSize(50, 50);
        positionButton.setPadding(Insets.EMPTY);

        // Position buttons description.
        Text positionDescription = new Text(0, 0, "Position");
        positionDescription.setFont(textFieldDescriptionFont);

        // VBox for the Position elements.
        VBox positionColumn = new VBox();
        positionColumn.setAlignment(Pos.TOP_CENTER);
        positionColumn.getChildren().add(positionButton);
        positionColumn.getChildren().add(positionDescription);


        // Calculate button.
        calculateButton = new Button("Calculate");
        calculateButton.setFont(calculateButtonFont);
        calculateButton.setPrefSize(150, 50);
        calculateButton.setPadding(Insets.EMPTY);


        // An HBox that contains the columns.
        HBox inputRow = new HBox();
        inputRow.setSpacing(15);
        inputRow.getChildren().add(holeCardsColumn);
        inputRow.getChildren().add(mainPlayerChipCountColumn);
        inputRow.getChildren().add(otherPlayerChipCountColumn);
        inputRow.getChildren().add(bigBlindChipCountColumn);
        inputRow.getChildren().add(positionColumn);


        // Configure the results area.
        resultsColumnLeft = new VBox();
        resultsColumnLeft.setAlignment(Pos.TOP_LEFT);
        resultsColumnLeft.setPadding(new Insets(0, 30, 0, 0));

        resultsColumnRight = new VBox();
        resultsColumnRight.setAlignment(Pos.TOP_LEFT);

        HBox resultsRow = new HBox();
        resultsRow.getChildren().add(resultsColumnLeft);
        resultsRow.getChildren().add(resultsColumnRight);
        resultsRow.setAlignment(Pos.TOP_CENTER);
        resultsRow.setPadding(new Insets(0, 0, 0, 10));


        // VBox that holds the top row, the calculate button, and the results area.
        VBox actionColumn = new VBox();
        actionColumn.setAlignment(Pos.TOP_CENTER);
        actionColumn.setSpacing(10);
        actionColumn.getChildren().add(inputRow);
        actionColumn.getChildren().add(calculateButton);
        actionColumn.getChildren().add(resultsRow);

        // Add the elements to the root node.
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.getChildren().add(actionColumn);

        return grid;
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
