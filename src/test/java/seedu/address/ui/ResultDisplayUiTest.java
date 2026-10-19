package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

@EnabledIfEnvironmentVariable(named = "RUN_JAVAFX_UI_TESTS", matches = "true")
public class ResultDisplayUiTest {

    private static final int JAVAFX_TIMEOUT_SECONDS = 10;

    @BeforeAll
    public static void startJavaFx() {
        Platform.startup(() -> { });
    }

    @Test
    public void setFeedbackToUser_updatesTextAndVisibleRows() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            TextArea textArea = (TextArea) display.getRoot().lookup(".text-area");

            String longFeedback = "one\ntwo\nthree\nfour\nfive\nsix\nseven";
            display.setFeedbackToUser(longFeedback);
            assertEquals(longFeedback, textArea.getText());
            assertEquals(6, textArea.getPrefRowCount());

            display.setFeedbackToUser("Short feedback");
            assertEquals("Short feedback", textArea.getText());
            assertEquals(3, textArea.getPrefRowCount());
        });
    }

    @Test
    public void setFeedbackToUser_nullFeedback_preservesDisplayedText() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            TextArea textArea = (TextArea) display.getRoot().lookup(".text-area");
            display.setFeedbackToUser("Short feedback");

            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser(null));
            assertEquals("Short feedback", textArea.getText());
        });
    }

    private static void runOnJavaFxThread(Runnable action) throws Exception {
        FutureTask<Void> test = new FutureTask<>(action, null);
        Platform.runLater(test);
        test.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
