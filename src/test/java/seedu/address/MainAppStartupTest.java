package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;

public class MainAppStartupTest {
    @TempDir
    public Path folder;

    @Test
    public void init_migrationSaveFails_doesNotCreateEmptyEditableRoster() throws Exception {
        Path file = writeRoster();
        String original = Files.readString(file);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file) {
            @Override
            protected void saveAddressBookAtomically(ReadOnlyAddressBook roster, Path destination) throws IOException {
                throw new IOException("Simulated migration save failure");
            }
        };
        MainApp app = new MainApp(storage, new JsonUserPrefsStorage(folder.resolve("prefs.json")));

        app.init();

        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertEquals(original, Files.readString(file));
        assertFalse(storage.wereTagsMigrated());
        app.stop(); // A failed startup can shut down without dereferencing an absent model.
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void init_migrationSucceeds_opensNormalizedRoster() throws Exception {
        Path file = writeRoster();
        var storage = new JsonAddressBookStorage(file);
        MainApp app = new MainApp(storage, new JsonUserPrefsStorage(folder.resolve("prefs.json")));

        app.init();

        assertNotNull(app.logic);
        assertNotNull(app.ui);
        assertEquals(1, app.model.getAddressBook().getPersonList().size());
        assertEquals(Set.of(new Tag("exam-prep")),
                app.model.getAddressBook().getPersonList().getFirst().getTags());
        assertEquals(app.model.getAddressBook(), storage.readAddressBook().orElseThrow());
        app.stop();
    }

    private Path writeRoster() throws IOException {
        Path file = folder.resolve("roster.json");
        Files.writeString(file, """
                {"persons":[{"name":"Amy","phone":"123","subjects":["Math"],"level":"JC 1",
                "tags":["EXAM-PREP","exam-prep"]}]}
                """);
        return file;
    }
}
