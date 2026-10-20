package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.scene.text.Font;

public class ResultDisplayTest {

    private static final Font FONT = Font.font("System", 12);

    @Test
    public void getVisibleRowCount_shortFeedback_returnsMinimumRows() {
        assertEquals(3, ResultDisplay.getVisibleRowCount("Invalid command format!", FONT, 500));
    }

    @Test
    public void getVisibleRowCount_longFeedback_returnsMaximumRows() {
        String feedback = "line 1\nline 2\nline 3\nline 4\nline 5\nline 6\nline 7";

        assertEquals(6, ResultDisplay.getVisibleRowCount(feedback, FONT, 500));
    }

    @Test
    public void getVisibleRowCount_wrappedFeedback_usesVisualLines() {
        String feedback = "First line\nSecond line\nThird line\n"
                + "A long final line that needs to wrap into multiple visual lines";

        assertEquals(4, ResultDisplay.getVisibleRowCount(feedback, FONT, 1000));
        assertEquals(5, ResultDisplay.getVisibleRowCount(feedback, FONT, 200));
        assertEquals(6, ResultDisplay.getVisibleRowCount(feedback, FONT, 110));
    }

    @Test
    public void getVisibleRowCount_widthUnavailable_countsExplicitLines() {
        assertEquals(4, ResultDisplay.getVisibleRowCount("one\ntwo\nthree\n", FONT, 0));
    }
}
