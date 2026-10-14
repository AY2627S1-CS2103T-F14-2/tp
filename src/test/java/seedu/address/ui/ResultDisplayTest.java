package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ResultDisplayTest {

    @Test
    public void getVisibleRowCount_shortFeedback_returnsMinimumRows() {
        assertEquals(3, ResultDisplay.getVisibleRowCount("Invalid command format!"));
    }

    @Test
    public void getVisibleRowCount_longFeedback_returnsMaximumRows() {
        String feedback = "line 1\nline 2\nline 3\nline 4\nline 5\nline 6\nline 7";

        assertEquals(6, ResultDisplay.getVisibleRowCount(feedback));
    }
}
