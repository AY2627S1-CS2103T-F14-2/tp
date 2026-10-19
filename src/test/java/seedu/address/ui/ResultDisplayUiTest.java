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

    @BeforeAll
    public static void startJavaFx() {
        Platform.startup(() -> { });
    }

    @Test
    public void setFeedbackToUser_updatesTextAndVisibleRows() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            ResultDisplay display = new ResultDisplay();
            TextArea textArea = (TextArea) display.getRoot().lookup(".text-area");

            String longFeedback = "one\ntwo\nthree\nfour\nfive\nsix\nseven";
            display.setFeedbackToUser(longFeedback);
            assertEquals(longFeedback, textArea.getText());
            assertEquals(6, textArea.getPrefRowCount());

            display.setFeedbackToUser("Short feedback");
            assertEquals("Short feedback", textArea.getText());
            assertEquals(3, textArea.getPrefRowCount());
            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser(null));
            assertEquals("Short feedback", textArea.getText());
            return null;
        });
        Platform.runLater(test);
        test.get(10, TimeUnit.SECONDS);
    }
}
