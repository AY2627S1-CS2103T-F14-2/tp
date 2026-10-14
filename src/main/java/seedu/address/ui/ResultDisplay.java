package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final int MINIMUM_VISIBLE_ROWS = 3;
    private static final int MAXIMUM_VISIBLE_ROWS = 6;

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        resultDisplay.setPrefRowCount(getVisibleRowCount(feedbackToUser));
    }

    /**
     * Returns enough rows for short feedback while bounding the area reserved for long feedback.
     */
    static int getVisibleRowCount(String feedbackToUser) {
        int feedbackLineCount = feedbackToUser.split("\\R", -1).length;
        return Math.clamp(feedbackLineCount, MINIMUM_VISIBLE_ROWS, MAXIMUM_VISIBLE_ROWS);
    }

}
