package preflop_shove_adviser;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

import static java.lang.Math.min;

public class ChipCountTextField extends TextField {

    private final IntegerProperty maxDigits = new SimpleIntegerProperty(8);

    public final int getMaxDigits() {
        return maxDigits.getValue();
    }

    @Override
    public void replaceText(int start, int end, String insertedText) {

        // Only allow numeric characters to be in the text.
        if (!validate(insertedText)) {
            return;
        }

        // Get the text in the textfield, before the user enters something
        String currentText = this.getText() == null ? "" : this.getText();

        // Compute the text that should normally be in the text field now
        String finalText = currentText.substring(0, start) + insertedText + currentText.substring(end);

        // Count the number of commas removed to the left of start.
        int commasToThLeftOfStartCount = StringUtils.countMatches(currentText.substring(0, start), ",");
        int commasToThLeftOfEndCount = StringUtils.countMatches(currentText.substring(0, end), ",");

        // Remove all commas from the current and final texts to make processing it easier.
        String currentDigits = currentText.replace(",", "");
        finalText = finalText.replace(",", "");

        int numberOfExceedingDigits = finalText.length() - this.getMaxDigits();
        if (numberOfExceedingDigits > 0) {
            // Cut the the text that was going to be inserted if it exceeds the max length.
            insertedText = insertedText.substring(0, insertedText.length() - numberOfExceedingDigits);
            finalText = currentDigits.substring(0, start-commasToThLeftOfStartCount) + insertedText + currentDigits.substring(end-commasToThLeftOfEndCount);
        }

        // Remove any leading zeros.
        finalText = finalText.replaceFirst("^0+(?!$)", "");

        // Add a comma for every three digits.
        if (finalText.length() > 0) {
            finalText = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(finalText));
        }

        // Set the text and caret position.
        super.setText(finalText);
        int commasInsertedToTheLeft = StringUtils.countMatches(finalText.substring(0, min(start+insertedText.length(), finalText.length())), ",") - commasToThLeftOfStartCount;
        super.positionCaret(start + insertedText.length() + commasInsertedToTheLeft);
    }


    private boolean validate(String text) {
        return text.matches("[0-9]*");
    }
}