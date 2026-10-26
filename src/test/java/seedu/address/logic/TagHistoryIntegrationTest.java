package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class TagHistoryIntegrationTest {
    @TempDir
    public Path folder;

    private ModelManager model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;
    private Path file;
    private IOException saveFailure;
    private int saves;
    private Person amy;
    private Person bob;

    @BeforeEach
    public void setUp() throws Exception {
        amy = new Person("Amy", "123", null, List.of("Math"), "JC 1", "Amy notes");
        bob = new Person("Bob", "456", "bob@example.com", List.of("Physics"), "JC 2", "Bob notes");
        var roster = new AddressBook();
        roster.addPerson(amy);
        roster.addPerson(bob);
        model = new ModelManager(roster, new UserPrefs());
        file = folder.resolve("roster.json");
        storage = new JsonAddressBookStorage(file) {
            @Override
            public void saveAddressBookAtomically(ReadOnlyAddressBook addressBook) throws IOException {
                saves++;
                if (saveFailure != null) {
                    throw saveFailure;
                }
                super.saveAddressBookAtomically(addressBook);
            }
        };
        storage.saveAddressBook(roster);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
    }

    @Test
    public void execute_filteredTag_undoRedoPreservesDetailsAndSavesEachState() throws Exception {
        var displayed = model.getFilteredPersonList();
        logic.execute("find n/Bob");
        assertEquals("Tag added to Bob: exam-prep", logic.execute("tag 1 exam-prep").getFeedbackToUser());
        Person tagged = bob.withTags(Set.of(new Tag("exam-prep")));
        assertEquals(List.of(tagged), displayed);
        assertEquals(bob.getNotes(), displayed.getFirst().getNotes());
        assertEquals(bob.getEmail(), displayed.getFirst().getEmail());
        assertEquals(bob.getSubjects(), displayed.getFirst().getSubjects());
        assertEquals(bob.getLevel(), displayed.getFirst().getLevel());
        assertEquals(bob.getPhone(), displayed.getFirst().getPhone());
        assertSame(displayed, model.getFilteredPersonList());
        assertRoster(amy, tagged);
        logic.execute("undo");
        assertRoster(amy, bob);
        assertEquals(List.of(amy, bob), displayed);
        assertFalse(model.canUndoAddressBook());
        logic.execute("redo");
        assertRoster(amy, tagged);
        assertFalse(model.canRedoAddressBook());
    }

    @Test
    public void execute_removeAndClear_eachHasOneReversibleStep() throws Exception {
        logic.execute("tag 2 exam-prep");
        logic.execute("tag 2 group-class");
        Person both = bob.withTags(Set.of(new Tag("exam-prep"), new Tag("group-class")));
        Person remaining = bob.withTags(Set.of(new Tag("group-class")));
        logic.execute("find n/Bob");
        assertEquals("Tag removed from Bob: exam-prep", logic.execute("untag 1 exam-prep").getFeedbackToUser());
        assertEquals(List.of(remaining), model.getFilteredPersonList());
        assertRoster(amy, remaining);
        logic.execute("undo");
        assertRoster(amy, both);
        logic.execute("redo");
        assertRoster(amy, remaining);
        logic.execute("undo");
        logic.execute("find n/Bob");
        assertEquals("All tags removed from Bob.", logic.execute("untag --all 1").getFeedbackToUser());
        assertEquals(List.of(bob), model.getFilteredPersonList());
        assertRoster(amy, bob);
        logic.execute("undo");
        assertRoster(amy, both);
        logic.execute("redo");
        assertRoster(amy, bob);
    }

    @Test
    public void execute_warningsAndRejections_doNotSaveOrConsumeHistory() throws Exception {
        logic.execute("tag 2 exam-prep");
        logic.execute("tag 2 group-class");
        logic.execute("undo");
        int savesBefore = saves;
        String storedBefore = Files.readString(file);
        assertTrue(logic.execute("tag 2 EXAM-PREP").getFeedbackToUser().startsWith("Warning:"));
        assertTrue(logic.execute("untag 2 absent").getFeedbackToUser().startsWith("Warning:"));
        assertTrue(logic.execute("untag --all 1").getFeedbackToUser().startsWith("Warning:"));
        assertThrows(CommandException.class, () -> logic.execute("tag 3 exam-prep"));
        assertThrows(ParseException.class, () -> logic.execute("tag 2 bad--tag"));
        assertEquals(savesBefore, saves);
        assertEquals(storedBefore, Files.readString(file));
        assertTrue(model.canRedoAddressBook());
        logic.execute("redo");
        assertRoster(amy, bob.withTags(Set.of(new Tag("exam-prep"), new Tag("group-class"))));
        logic.execute("undo");
        logic.execute("undo");
        assertRoster(amy, bob);
        assertFalse(model.canUndoAddressBook());
    }

    @Test
    public void execute_newTagChangeAfterUndo_discardsRedo() throws Exception {
        logic.execute("tag 2 exam-prep");
        logic.execute("undo");
        logic.execute("tag 2 group-class");
        assertFalse(model.canRedoAddressBook());
        assertThrows(CommandException.class, () -> logic.execute("redo"));
        logic.execute("undo");
        assertRoster(amy, bob);
        assertFalse(model.canUndoAddressBook());
    }

    @Test
    public void execute_saveFailures_preserveFileFilterAndHistory() throws Exception {
        logic.execute("tag 2 exam-prep");
        logic.execute("tag 2 group-class");
        logic.execute("undo");
        logic.execute("find n/Bob");
        Person tagged = bob.withTags(Set.of(new Tag("exam-prep")));
        String original = Files.readString(file);
        for (IOException failure : List.of(new IOException("disk failure"), new AccessDeniedException("roster"))) {
            saveFailure = failure;
            for (String command : List.of("tag 1 follow-up", "untag 1 exam-prep", "untag --all 1", "undo", "redo")) {
                assertThrows(CommandException.class, () -> logic.execute(command));
                assertEquals(List.of(amy, tagged), model.getAddressBook().getPersonList());
                assertEquals(List.of(tagged), model.getFilteredPersonList());
                assertEquals(original, Files.readString(file));
                assertTrue(model.canUndoAddressBook());
                assertTrue(model.canRedoAddressBook());
            }
        }
        saveFailure = null;
        logic.execute("redo");
        assertRoster(amy, bob.withTags(Set.of(new Tag("exam-prep"), new Tag("group-class"))));
        logic.execute("undo");
        logic.execute("undo");
        assertRoster(amy, bob);
    }

    @Test
    public void execute_addWithTags_isOneUndoStepAndInvalidTagsCreateNoStudent() throws Exception {
        assertThrows(ParseException.class, () -> logic.execute("add n/Cam p/789 s/Math l/JC 1 t/bad--tag"));
        assertRoster(amy, bob);
        assertFalse(model.canUndoAddressBook());
        assertEquals(0, saves);
        var result = logic.execute("add n/Cam p/789 s/Math l/JC 1 t/EXAM-PREP t/exam-prep t/group-class");
        assertTrue(result.getFeedbackToUser().contains("Warning: Duplicate tags were ignored."));
        Person cam = new Person("Cam", "789", null, List.of("Math"), "JC 1")
                .withTags(Set.of(new Tag("exam-prep"), new Tag("group-class")));
        assertRoster(amy, bob, cam);
        logic.execute("undo");
        assertRoster(amy, bob);
        assertFalse(model.canUndoAddressBook());
        logic.execute("redo");
        assertRoster(amy, bob, cam);
    }

    private void assertRoster(Person... people) throws Exception {
        assertEquals(List.of(people), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
