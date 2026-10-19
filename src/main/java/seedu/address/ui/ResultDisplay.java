package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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

    // Recalculate after the current layout pass so the new preferred height triggers another layout.
    private final ChangeListener<Number> viewportWidthListener = (observable, oldWidth, newWidth) ->
            Platform.runLater(this::updateVisibleRowCount);

    @FXML
    private TextArea resultDisplay;
    private Region observedViewport;

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

    /**
     * Updates the preferred row count using the feedback text, font and available content width.
     */
    private void updateVisibleRowCount() {
        Region viewport = (Region) resultDisplay.lookup(".viewport");
        if (viewport != null && viewport != observedViewport) {
            if (observedViewport != null) {
                observedViewport.widthProperty().removeListener(viewportWidthListener);
            }
            observedViewport = viewport;
            viewport.widthProperty().addListener(viewportWidthListener);
        }
        Region content = (Region) resultDisplay.lookup(".content");
        double width = viewport == null ? resultDisplay.getWidth() : viewport.getWidth();
        if (content != null) {
            width -= content.getInsets().getLeft() + content.getInsets().getRight();
        }
        resultDisplay.setPrefRowCount(getVisibleRowCount(resultDisplay.getText(), resultDisplay.getFont(), width));
    }

    /**
     * Returns the number of visible rows needed for the feedback, within the display's row limits.
     * Wrapped lines are measured at the given width; a non-positive width falls back to counting
     * explicit lines.
     */
    static int getVisibleRowCount(String feedbackToUser, Font font, double width) {
        Text textMeasure = new Text("Ag");
        textMeasure.setFont(font);
        double lineHeight = textMeasure.getLayoutBounds().getHeight();
        int feedbackLineCount = 0;
        for (String line : feedbackToUser.split("\\R", -1)) {
            if (line.isEmpty() || width <= 0) {
                feedbackLineCount++;
            } else {
                textMeasure.setText(line);
                textMeasure.setWrappingWidth(width);
                feedbackLineCount += (int) Math.round(textMeasure.getLayoutBounds().getHeight() / lineHeight);
            }
        }
        return Math.clamp(feedbackLineCount, MINIMUM_VISIBLE_ROWS, MAXIMUM_VISIBLE_ROWS);
    }

}
