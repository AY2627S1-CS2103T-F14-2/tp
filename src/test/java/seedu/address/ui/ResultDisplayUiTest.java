package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;

@EnabledIfEnvironmentVariable(named = "RUN_JAVAFX_UI_TESTS", matches = "true")
public class ResultDisplayUiTest {

    private static final int JAVAFX_TIMEOUT_SECONDS = 10;

    @BeforeAll
    public static void startJavaFx() {
        Platform.startup(() -> Platform.setImplicitExit(false));
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

    @Test
    public void feedbackBox_wrappedFeedback_growsAndShrinksWithAvailableWidth() throws Exception {
        FeedbackWindow fixture = new FeedbackWindow();
        try {
            runOnJavaFxThread(fixture::showLongFeedback);
            waitForLayout();
            runOnJavaFxThread(fixture::recordWideSizeAndNarrowWindow);
            waitForLayout();
            runOnJavaFxThread(fixture::checkNarrowSizeAndShowShortFeedback);
            waitForLayout();
            runOnJavaFxThread(fixture::checkShortFeedback);
        } finally {
            runOnJavaFxThread(fixture::close);
        }
    }

    private static void waitForLayout() throws Exception {
        CountDownLatch layoutCompleted = new CountDownLatch(1);
        runOnJavaFxThread(() -> {
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(event -> layoutCompleted.countDown());
            pause.play();
        });
        assertTrue(layoutCompleted.await(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS), "JavaFX layout timed out");
    }

    private static class FeedbackWindow {
        private Stage stage;
        private TextField commandField;
        private TextArea feedback;
        private double wideHeight;
        private double narrowHeight;
        private int wideRows;

        void showLongFeedback() {
            stage = new Stage();
            MainWindow window = new MainWindow(stage, new FeedbackLogic(), Path.of("test-data.json"));
            window.fillInnerParts();
            stage.setWidth(1000);
            stage.show();
            Region root = (Region) stage.getScene().getRoot();
            root.applyCss();
            commandField = (TextField) root.lookup(".text-field");
            feedback = (TextArea) root.lookup(".result-display");
            commandField.setText("A longer feedback message that wraps at narrower window widths. ".repeat(5));
            commandField.fireEvent(new ActionEvent());
        }

        void recordWideSizeAndNarrowWindow() {
            wideHeight = feedback.getHeight();
            wideRows = feedback.getPrefRowCount();
            stage.setWidth(620);
        }

        void checkNarrowSizeAndShowShortFeedback() {
            narrowHeight = feedback.getHeight();
            Region viewport = (Region) feedback.lookup(".viewport");
            Region content = (Region) feedback.lookup(".content");
            double contentWidth = viewport.getWidth() - content.getInsets().getLeft() - content.getInsets().getRight();
            int expectedRows = ResultDisplay.getVisibleRowCount(feedback.getText(), feedback.getFont(), contentWidth);
            assertTrue(expectedRows > wideRows, "The test feedback must wrap into more rows when narrow");
            assertEquals(expectedRows, feedback.getPrefRowCount());
            assertTrue(narrowHeight > wideHeight, "Heights: " + wideHeight + " and " + narrowHeight
                    + "; rows: " + wideRows + " and " + feedback.getPrefRowCount());
            commandField.setText("Short feedback");
            commandField.fireEvent(new ActionEvent());
        }

        void checkShortFeedback() {
            assertEquals(3, feedback.getPrefRowCount());
            assertTrue(feedback.getHeight() < narrowHeight);
        }

        void close() {
            if (stage != null) {
                stage.close();
            }
        }
    }

    private static class FeedbackLogic implements Logic {
        @Override
        public CommandResult execute(String commandText) {
            return new CommandResult(commandText);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public GuiSettings getGuiSettings() {
            return new GuiSettings();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
        }
    }

    private static void runOnJavaFxThread(Runnable action) throws Exception {
        FutureTask<Void> test = new FutureTask<>(action, null);
        Platform.runLater(test);
        test.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
