package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainWindowShortcutTest {

    @Test
    public void isSlashKey_plainSlash_returnsTrue() {
        KeyEvent eventWithCodeAndText = new KeyEvent(
                KeyEvent.KEY_PRESSED, "/", "/", KeyCode.SLASH, false, false, false, false);
        assertTrue(MainWindow.isSlashKey(eventWithCodeAndText));

        KeyEvent eventWithTextOnly = new KeyEvent(
                KeyEvent.KEY_PRESSED, "/", "/", KeyCode.UNDEFINED, false, false, false, false);
        assertTrue(MainWindow.isSlashKey(eventWithTextOnly));

        KeyEvent eventWithCodeOnly = new KeyEvent(
                KeyEvent.KEY_PRESSED, "", "", KeyCode.SLASH, false, false, false, false);
        assertTrue(MainWindow.isSlashKey(eventWithCodeOnly));
    }

    @Test
    public void isSlashKey_questionMarkShift_returnsFalse() {
        KeyEvent questionMarkEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED, "?", "?", KeyCode.SLASH, true, false, false, false);
        assertFalse(MainWindow.isSlashKey(questionMarkEvent));
    }

    @Test
    public void isSlashKey_withControlOrAltOrMeta_returnsFalse() {
        KeyEvent ctrlSlash = new KeyEvent(
                KeyEvent.KEY_PRESSED, "/", "/", KeyCode.SLASH, false, true, false, false);
        assertFalse(MainWindow.isSlashKey(ctrlSlash));

        KeyEvent altSlash = new KeyEvent(
                KeyEvent.KEY_PRESSED, "/", "/", KeyCode.SLASH, false, false, true, false);
        assertFalse(MainWindow.isSlashKey(altSlash));

        KeyEvent metaSlash = new KeyEvent(
                KeyEvent.KEY_PRESSED, "/", "/", KeyCode.SLASH, false, false, false, true);
        assertFalse(MainWindow.isSlashKey(metaSlash));
    }

    @Test
    public void isSlashKey_otherKeys_returnsFalse() {
        KeyEvent letterA = new KeyEvent(
                KeyEvent.KEY_PRESSED, "a", "a", KeyCode.A, false, false, false, false);
        assertFalse(MainWindow.isSlashKey(letterA));

        KeyEvent escapeKey = new KeyEvent(
                KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
        assertFalse(MainWindow.isSlashKey(escapeKey));
    }
}
