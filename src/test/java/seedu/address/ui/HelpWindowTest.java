package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class HelpWindowTest {

    @Test
    public void f1KeyPress_hidesHelpWindow() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Toolkit already started
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                HelpWindow helpWindow = new HelpWindow(stage);
                helpWindow.show();
                assertTrue(helpWindow.isShowing());

                KeyEvent f1Event = new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, false, false);
                stage.fireEvent(f1Event);
                assertFalse(helpWindow.isShowing());
            } catch (Throwable t) {
                failure.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timed out waiting for JavaFX thread");
        if (failure.get() != null) {
            throw new AssertionError("Test failed in JavaFX thread", failure.get());
        }
    }
}
