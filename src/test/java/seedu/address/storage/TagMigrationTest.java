package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.TagMigrationException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.tag.Tag;

public class TagMigrationTest {
    @TempDir
    public Path folder;

    @Test
    public void read_mixedCaseDuplicates_rewritesOnceAndPreservesFields() throws Exception {
        Path file = folder.resolve("roster.json");
        Files.writeString(file, roster("EXAM-Prep", "exam-prep", "Group-Class"));
        RecordingStorage storage = new RecordingStorage(file);

        ReadOnlyAddressBook loaded = storage.readAddressBook().orElseThrow();
        var student = loaded.getPersonList().getFirst();
        assertEquals(Set.of(new Tag("exam-prep"), new Tag("group-class")), student.getTags());
        assertEquals("Amy", student.getName().fullName);
        assertEquals("123", student.getPhone().value);
        assertEquals("amy@example.com", student.getEmail().value);
        assertEquals(List.of("Math", "Physics"), student.getSubjects());
        assertEquals("JC 1", student.getLevel());
        assertEquals("Keep these notes", student.getNotes());
        assertEquals("10 Test Street", student.getAddress().value);
        assertTrue(storage.wereTagsMigrated());
        assertEquals(1, storage.saves);
        String normalized = Files.readString(file);
        assertFalse(normalized.contains("EXAM-Prep"));
        assertTrue(normalized.indexOf("exam-prep") < normalized.indexOf("group-class"));

        assertEquals(loaded, storage.readAddressBook().orElseThrow());
        assertFalse(storage.wereTagsMigrated());
        assertEquals(1, storage.saves);
        assertEquals(normalized, Files.readString(file));
    }

    @Test
    public void read_lowercaseDuplicates_rewritesEvenWithoutCaseChanges() throws Exception {
        Path file = folder.resolve("duplicates.json");
        Files.writeString(file, roster("exam-prep", "exam-prep"));
        var storage = new RecordingStorage(file);
        var loaded = storage.readAddressBook().orElseThrow();
        assertEquals(Set.of(new Tag("exam-prep")), loaded.getPersonList().getFirst().getTags());
        assertTrue(storage.wereTagsMigrated());
        assertEquals(1, storage.saves);
        assertEquals(loaded, storage.readAddressBook().orElseThrow());
        assertEquals(1, storage.saves);
    }

    @Test
    public void read_overCurrentLimits_retainsEveryLegacyTag() throws Exception {
        String longName = "A".repeat(31);
        List<String> names = IntStream.range(0, 21).mapToObj(i -> "TAG" + i)
                .collect(Collectors.toList());
        names.add(longName);
        Path file = folder.resolve("large.json");
        Files.writeString(file, roster(names.toArray(String[]::new)));
        RecordingStorage storage = new RecordingStorage(file);
        var loaded = storage.readAddressBook().orElseThrow();
        assertEquals(22, loaded.getPersonList().getFirst().getTags().size());
        assertTrue(loaded.getPersonList().getFirst().getTags().contains(Tag.fromStoredName(longName)));
        assertEquals(loaded, storage.readAddressBook().orElseThrow());
        assertEquals(1, storage.saves);
    }

    @Test
    public void read_explicitPath_migratesThatFileOnly() throws Exception {
        Path configured = folder.resolve("configured.json");
        Path requested = folder.resolve("requested.json");
        Files.writeString(configured, roster("unchanged"));
        Files.writeString(requested, roster("EXAM-PREP"));
        String configuredBefore = Files.readString(configured);
        RecordingStorage storage = new RecordingStorage(configured);
        storage.readAddressBook(requested);
        assertEquals(requested, storage.savedPath);
        assertEquals(configuredBefore, Files.readString(configured));
        assertTrue(Files.readString(requested).contains("exam-prep"));
    }

    @Test
    public void read_saveFails_preservesFileAndReportsMigrationFailure() throws Exception {
        Path file = folder.resolve("roster.json");
        String original = roster("EXAM-PREP");
        Files.writeString(file, original);
        RecordingStorage storage = new RecordingStorage(file);
        storage.fail = true;
        TagMigrationException error = assertThrows(TagMigrationException.class, storage::readAddressBook);
        assertTrue(error.getCause() instanceof IOException);
        assertEquals(original, Files.readString(file));
        assertFalse(storage.wereTagsMigrated());
        storage.fail = false;
        assertTrue(storage.readAddressBook().isPresent());
        assertTrue(storage.wereTagsMigrated());
    }

    @Test
    public void read_legacyRecord_preservesFormatAndMissingOptionalFields() throws Exception {
        Path file = folder.resolve("legacy.json");
        Files.writeString(file, """
                {"persons":[{"name":"Amy","phone":"123","email":"amy@example.com",
                "address":"10 Test Street","tags":["EXAM-PREP"]}]}
                """);
        var storage = new RecordingStorage(file);
        var student = storage.readAddressBook().orElseThrow().getPersonList().getFirst();
        assertFalse(student.isNewFormat());
        assertFalse(student.hasSubjectsField());
        assertFalse(Files.readString(file).contains("subjects"));
        assertEquals(student, storage.readAddressBook().orElseThrow().getPersonList().getFirst());
    }

    @Test
    public void read_missingTags_doesNotRewrite() throws Exception {
        Path file = folder.resolve("no-tags.json");
        String original = """
                {"persons":[{"name":"Amy","phone":"123","subjects":["Math"],"level":"JC 1"}]}
                """;
        Files.writeString(file, original);
        var storage = new RecordingStorage(file);
        assertTrue(storage.readAddressBook().orElseThrow().getPersonList().getFirst().getTags().isEmpty());
        assertEquals(0, storage.saves);
        assertFalse(storage.wereTagsMigrated());
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void read_invalidTag_doesNotPartiallyMigrate() throws Exception {
        Path file = folder.resolve("invalid.json");
        String original = roster("EXAM-PREP", "bad--tag");
        Files.writeString(file, original);
        var storage = new RecordingStorage(file);
        DataLoadingException error = assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(Tag.MESSAGE_CONSTRAINTS, error.getCause().getMessage());
        assertEquals(0, storage.saves);
        assertEquals(original, Files.readString(file));
    }

    private static String roster(String... tags) {
        String entries = java.util.Arrays.stream(tags)
                .map(tag -> "\"" + tag + "\"").collect(Collectors.joining(","));
        return """
                {"persons":[{"name":"Amy","phone":"123","email":"amy@example.com",
                "subjects":["Math","Physics"],"level":"JC 1","notes":"Keep these notes",
                "address":"10 Test Street","tags":[%s]}]}
                """.formatted(entries);
    }

    private static class RecordingStorage extends JsonAddressBookStorage {
        private int saves;
        private boolean fail;
        private Path savedPath;

        RecordingStorage(Path path) {
            super(path);
        }

        @Override
        protected void saveAddressBookAtomically(ReadOnlyAddressBook roster, Path destination) throws IOException {
            saves++;
            savedPath = destination;
            if (fail) {
                throw new IOException("Simulated migration save failure");
            }
            super.saveAddressBookAtomically(roster, destination);
        }
    }
}
