package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class StudentWorkflowTest {
    @TempDir
    public Path directory;
    private ModelManager model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(directory.resolve("roster.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
    }

    @Test
    public void add_unicodeWhitespaceThroughoutCommand_success() throws Exception {
        logic.execute("\u00a0add\u00a0n/Ryan\nTan\t p/\u00a0123\u00a0 s/English\nLanguage l/Year\t10\u00a0");
        Person student = model.getAddressBook().getPersonList().getFirst();
        assertEquals("Ryan Tan", student.getName().fullName);
        assertEquals("123", student.getPhone().value);
        assertEquals("English Language", student.getSubjects().getFirst().value);
        assertEquals("Year 10", student.getLevel().value);
    }

    @Test
    public void add_filteredList_preservesSearchAndSavesHiddenStudent() throws Exception {
        logic.execute("add n/Ryan Tan p/111 s/Math l/Year 10");
        logic.execute("find Ryan");
        logic.execute("add n/Alicia Lim p/222 s/Physics l/Year 9");
        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals("Ryan Tan", logic.getFilteredPersonList().getFirst().getName().fullName);
        assertEquals(2, storage.readAddressBook().orElseThrow().getPersonList().size());
        logic.execute("add n/Ryan Lee p/333 s/English l/Year 8");
        assertEquals(2, logic.getFilteredPersonList().size());
        logic.execute("list");
        assertEquals(3, logic.getFilteredPersonList().size());
    }

    @Test
    public void add_duplicatesAgainstWholeRoster_rejectsWithoutChangingData() throws Exception {
        Person legacy = new PersonBuilder().withName("Ryan Tan").withPhone("81234567")
                .withEmail("Ryan@Example.COM").build();
        model.addPerson(legacy);
        logic.execute("find Nobody");
        for (String command : new String[] {
            "add n/ ryan   tan p/+81234567 s/Math l/JC 1",
            "add n/RYAN TAN p/999 e/ryan@example.com s/English l/JC 2"
        }) {
            CommandException error = assertThrows(CommandException.class, () -> logic.execute(command));
            assertEquals(AddCommand.MESSAGE_DUPLICATE_PERSON, error.getMessage());
            assertEquals(1, model.getAddressBook().getPersonList().size());
            assertTrue(logic.getFilteredPersonList().isEmpty());
        }
        logic.execute("add n/Sibling Tan p/81234567 e/Ryan@Example.COM s/Math l/JC 1");
        logic.execute("add n/Ryan Tan p/999 s/Math l/JC 1");
        assertEquals(3, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void edit_studentAfterReload_isDisabled() throws Exception {
        assertEquals("New student added: Ryan Tan",
                logic.execute("add n/Ryan Tan p/111 s/Math l/Year 10").getFeedbackToUser());
        ModelManager reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        EditCommand command = new EditCommandParser().parse(" 1 p/222");
        CommandException error = assertThrows(CommandException.class, () -> command.execute(reloaded));
        assertEquals(EditCommand.MESSAGE_STUDENT_EDIT_UNAVAILABLE, error.getMessage());
        assertEquals("111", reloaded.getAddressBook().getPersonList().getFirst().getPhone().value);
    }

    @Test
    public void edit_legacyContactCannotDuplicateAnotherDespiteMatchingTarget() throws Exception {
        model.addPerson(new PersonBuilder().withName("Ryan").withPhone("111").withEmail("a@example.com").build());
        model.addPerson(new PersonBuilder().withName("Ryan").withPhone("222").withEmail("b@example.com").build());
        EditCommand command = new EditCommandParser().parse(" 1 e/b@example.com");
        CommandException error = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(EditCommand.MESSAGE_DUPLICATE_PERSON, error.getMessage());
        assertEquals("a@example.com", model.getAddressBook().getPersonList().getFirst().getEmail().value);
        assertFalse(model.getAddressBook().getPersonList().getFirst().isStudent());
    }

    @Test
    public void add_invalidCommand_doesNotMutateOrSave() {
        assertThrows(ParseException.class, () -> logic.execute("add n/Ryan p/111 s/Math s/math l/Year 10"));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertFalse(java.nio.file.Files.exists(directory.resolve("roster.json")));
    }
}
