package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

public class CommandBoxTest {

    @Test
    public void enterCommand_executesAndClearsInput() throws Exception {
        AtomicReference<String> executedCommand = new AtomicReference<>();

        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                executedCommand.set(commandText);
                return new CommandResult("success");
            });
            TextField commandField = getCommandTextField(commandBox);

            commandField.setText("list");
            getRunButton(commandBox).fire();

            assertEquals("list", executedCommand.get());
            assertEquals("", commandField.getText());
        });
    }

    @Test
    public void enterCommand_emptyInput_doesNotExecute() throws Exception {
        AtomicBoolean executed = new AtomicBoolean(false);

        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                executed.set(true);
                return new CommandResult("success");
            });

            getRunButton(commandBox).fire();

            assertFalse(executed.get());
        });
    }

    @Test
    public void enterCommand_commandFailure_marksInputAsInvalid() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                throw new CommandException("command failed");
            });
            TextField commandField = getCommandTextField(commandBox);

            commandField.setText("invalid");
            getRunButton(commandBox).fire();

            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void enterCommand_parseFailure_marksInputAsInvalid() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                throw new ParseException("parse failed");
            });
            TextField commandField = getCommandTextField(commandBox);

            commandField.setText("invalid");
            getRunButton(commandBox).fire();

            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void enterCommand_repeatedFailure_addsOnlyOneErrorStyle() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                throw new CommandException("command failed");
            });
            TextField commandField = getCommandTextField(commandBox);

            commandField.setText("invalid");
            getRunButton(commandBox).fire();
            getRunButton(commandBox).fire();

            long errorStyleCount = commandField.getStyleClass().stream()
                    .filter(CommandBox.ERROR_STYLE_CLASS::equals)
                    .count();
            assertEquals(1, errorStyleCount);
        });
    }

    @Test
    public void editCommand_afterFailure_clearsErrorStyle() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                throw new CommandException("command failed");
            });
            TextField commandField = getCommandTextField(commandBox);

            commandField.setText("invalid");
            getRunButton(commandBox).fire();
            commandField.setText("list");

            assertFalse(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void historyNavigation_upAndDown_preservesDraft() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> new CommandResult("success"));
            TextField commandField = getCommandTextField(commandBox);
            getRunButton(commandBox).fire();
            commandField.setText("list");
            getRunButton(commandBox).fire();
            commandField.setText("draft");

            KeyEvent upEvent = keyPressed(KeyCode.UP, false, false, false);
            commandField.getOnKeyPressed().handle(upEvent);
            KeyEvent downEvent = keyPressed(KeyCode.DOWN, false, false, false);
            commandField.getOnKeyPressed().handle(downEvent);

            assertEquals("draft", commandField.getText());
            assertTrue(upEvent.isConsumed());
            assertTrue(downEvent.isConsumed());
        });
    }

    @Test
    public void historyNavigation_withModifiers_ignoresArrowKeys() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> new CommandResult("success"));
            TextField commandField = getCommandTextField(commandBox);
            commandField.setText("draft");

            commandField.getOnKeyPressed().handle(keyPressed(KeyCode.UP, false, true, false));
            commandField.getOnKeyPressed().handle(keyPressed(KeyCode.UP, false, false, true));
            commandField.getOnKeyPressed().handle(keyPressed(KeyCode.UP, false, false, false));

            assertEquals("draft", commandField.getText());
        });
    }

    @Test
    public void historyNavigation_withControlKey_ignoresArrowKey() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> new CommandResult("success"));
            TextField commandField = getCommandTextField(commandBox);
            commandField.setText("draft");

            KeyEvent event = keyPressed(KeyCode.UP, false, true, false);
            commandField.getOnKeyPressed().handle(event);

            assertEquals("draft", commandField.getText());
            assertFalse(event.isConsumed());
        });
    }

    @Test
    public void historyNavigation_otherKey_leavesInputUnchanged() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> new CommandResult("success"));
            TextField commandField = getCommandTextField(commandBox);
            commandField.setText("draft");

            commandField.getOnKeyPressed().handle(keyPressed(KeyCode.LEFT, false, false, false));

            assertEquals("draft", commandField.getText());
        });
    }

    @Test
    public void focus_positionsCaretAtEndOfInput() throws Exception {
        runOnFxThread(() -> {
            CommandBox commandBox = new CommandBox(commandText -> new CommandResult("success"));
            TextField commandField = getCommandTextField(commandBox);
            commandField.setText("find Ryan");

            commandBox.focus();

            assertEquals(commandField.getText().length(), commandField.getCaretPosition());
            assertEquals(commandField.isFocused(), commandBox.isFocused());
        });
    }

    private static TextField getCommandTextField(CommandBox commandBox) {
        StackPane root = (StackPane) commandBox.getRoot();
        HBox commandRow = (HBox) root.getChildrenUnmodifiable().get(0);
        return (TextField) commandRow.getChildren().get(1);
    }

    private static Button getRunButton(CommandBox commandBox) {
        StackPane root = (StackPane) commandBox.getRoot();
        HBox commandRow = (HBox) root.getChildrenUnmodifiable().get(0);
        return (Button) commandRow.getChildren().get(2);
    }

    private static KeyEvent keyPressed(KeyCode keyCode, boolean controlDown, boolean altDown, boolean metaDown) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode, false,
                controlDown, altDown, metaDown);
    }

    private static void runOnFxThread(ThrowingRunnable action) throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // The JavaFX toolkit is already running.
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timed out waiting for JavaFX thread");
        if (failure.get() != null) {
            throw new AssertionError("Test failed in JavaFX thread", failure.get());
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
