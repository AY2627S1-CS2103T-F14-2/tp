package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final int MINIMUM_VISIBLE_ROWS = 3;
    private static final int MAXIMUM_VISIBLE_ROWS = 6;

    @FXML
    private TextArea resultDisplay;

    /** Creates a result display that adapts to its available width. */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.widthProperty().addListener((observable, oldWidth, newWidth) -> updateVisibleRowCount());
        resultDisplay.fontProperty().addListener((observable, oldFont, newFont) -> updateVisibleRowCount());
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        updateVisibleRowCount();
    }

    private void updateVisibleRowCount() {
        Region viewport = (Region) resultDisplay.lookup(".viewport");
        Region content = (Region) resultDisplay.lookup(".content");
        double width = viewport == null ? resultDisplay.getWidth() : viewport.getWidth();
        if (content != null) {
            width -= content.getInsets().getLeft() + content.getInsets().getRight();
        }
        resultDisplay.setPrefRowCount(getVisibleRowCount(resultDisplay.getText(), resultDisplay.getFont(), width));
    }

    /**
     * Returns enough rows for short feedback while bounding the area reserved for long feedback.
     */
    static int getVisibleRowCount(String feedbackToUser, Font font, double width) {
        Text measure = new Text("Ag");
        measure.setFont(font);
        double lineHeight = measure.getLayoutBounds().getHeight();
        int feedbackLineCount = 0;
        for (String line : feedbackToUser.split("\\R", -1)) {
            if (line.isEmpty() || width <= 0) {
                feedbackLineCount++;
            } else {
                measure.setText(line);
                measure.setWrappingWidth(width);
                feedbackLineCount += (int) Math.round(measure.getLayoutBounds().getHeight() / lineHeight);
            }
        }
        return Math.clamp(feedbackLineCount, MINIMUM_VISIBLE_ROWS, MAXIMUM_VISIBLE_ROWS);
    }

}
