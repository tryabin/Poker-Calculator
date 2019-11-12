package preflop_shove_adviser;

import data_creation.structures.HoleCardsType;
import data_creation.structures.Rank;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

public class HoleCardsTextField extends TextField {

    private final IntegerProperty maxLength = new SimpleIntegerProperty(3);

    public final int getMaxLength() {
        return maxLength.getValue();
    }

    @Override
    public void replaceText(int start, int end, String insertedText) {

        // Get the text in the text field, before the user enters something
        String currentText = this.getText() == null ? "" : this.getText();

        // Compute the text that should normally be in the text field now
        String finalText = currentText.substring(0, start) + insertedText + currentText.substring(end);

        int numberOfExceedingCharacters = finalText.length() - this.getMaxLength();
        if (numberOfExceedingCharacters > 0) {
            // Cut the the text that was going to be inserted if it exceeds the max length.
            insertedText = insertedText.substring(0, insertedText.length() - numberOfExceedingCharacters);
            finalText = currentText.substring(0, start) + insertedText + currentText.substring(end);
        }

        // Capitalize the first two characters, and lowercase the third.
        int[] capitalizationRules = {1, 1, 0};
        StringBuilder finalTextBuilder = new StringBuilder();
        for (int i = 0; i < capitalizationRules.length; i++) {
            if (i >= finalText.length()) {
                break;
            }

            String curChar = finalText.substring(i, i+1);
            String changedChar = capitalizationRules[i] == 1 ? curChar.toUpperCase() : curChar.toLowerCase();
            finalTextBuilder.append(changedChar);
        }

        // Only set the text if the first two characters represent valid hole cards, and the last
        // character is either 'o' or 's'.
        finalText = finalTextBuilder.toString();
        for (int i = 0; i < finalText.length(); i++) {
            String curChar = finalText.substring(i, i+1);
            if ((i < 2 && !Rank.getAbbreviations().contains(curChar)) ||
                (i == 2 && !HoleCardsType.getAbbreviations().contains(curChar))) {
                return;
            }
        }

        // Also don't set the text if it specifies that a paired hand is either suited or unsuited.
        if (finalText.length() == 3 && (Rank.get(finalText.substring(0, 1)) == Rank.get(finalText.substring(1, 2)))) {
            return;
        }


        // Set the text and caret position.
        super.setText(finalText);
        super.positionCaret(start + insertedText.length());
    }
}