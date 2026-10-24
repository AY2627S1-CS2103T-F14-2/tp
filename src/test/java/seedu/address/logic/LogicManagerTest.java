package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.DeleteCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.ShowCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalPersons;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_showFilteredStudent_returnsStudentFromDisplayedOrder() throws Exception {
        model.setAddressBook(TypicalPersons.getTypicalAddressBook());
        logic.execute("find --any n/Daniel p/987");

        CommandResult result = logic.execute("show 2");

        assertEquals(TypicalPersons.DANIEL, result.getPersonToShow().orElseThrow());
        assertEquals(2, logic.getFilteredPersonList().size());
    }

    @Test
    public void execute_showInvalidDisplayedIndex_throwsCommandException() throws Exception {
        model.setAddressBook(TypicalPersons.getTypicalAddressBook());
        logic.execute("find n/Benson");

        CommandException exception = org.junit.jupiter.api.Assertions.assertThrows(CommandException.class, ()
                -> logic.execute("show 2"));
        assertEquals(ShowCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, exception.getMessage());
        assertEquals(1, logic.getFilteredPersonList().size());
    }

    @Test
    public void execute_nullCommand_throwsParseException() {
        assertParseException(null, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    @Test
    public void constructor_nullRequiredDependency_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LogicManager(null, null));
        assertThrows(NullPointerException.class, () -> new LogicManager(model, null));
    }

    @Test
    public void execute_deleteFilteredStudent_savesRemainingRoster() throws Exception {
        model.setAddressBook(TypicalPersons.getTypicalAddressBook());
        logic.execute("find n/Benson");

        CommandResult result = logic.execute("delete 1");

        Model expectedModel = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        expectedModel.deletePerson(TypicalPersons.BENSON);
        assertEquals("Student removed: Benson Meier", result.getFeedbackToUser());
        assertEquals(expectedModel, model);
        assertEquals(expectedModel.getAddressBook(),
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                        .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_addStudent_savesAndShowsFullRoster() throws Exception {
        model.setAddressBook(TypicalPersons.getTypicalAddressBook());
        logic.execute("find n/Benson");

        CommandResult result = logic.execute("add n/Ryan Tan p/+6591234567 "
                + "s/Mathematics  Plus s/Physics l/Secondary  4");

        assertEquals("New student added: Ryan Tan", result.getFeedbackToUser());
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        String stored = Files.readString(temporaryFolder.resolve("addressBook.json"));
        assertTrue(stored.contains("\"subjects\""));
        assertTrue(stored.contains("\"Mathematics Plus\""));
        assertTrue(stored.contains("\"Physics\""));
        assertTrue(stored.contains("\"level\" : \"Secondary 4\""));
    }

    @Test
    public void execute_malformedDelete_preservesDisplayedList() throws Exception {
        model.setAddressBook(TypicalPersons.getTypicalAddressBook());
        logic.execute("find n/Benson");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(TypicalPersons.BENSON));

        assertCommandFailure("delete 1 n/Ryan", ParseException.class,
                DeleteCommand.MESSAGE_INVALID_COMMAND_FORMAT, expectedModel);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() throws Exception {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION);
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() throws Exception {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void execute_undoRedoAddAndFilteredDelete_persistsCompleteRoster() throws Exception {
        String addRyan = "add n/Ryan Tan p/81234567 e/ryan@example.com s/Physics s/Mathematics l/JC 1";
        logic.execute(addRyan);
        Person ryan = model.getAddressBook().getPersonList().getFirst();
        logic.execute("add n/Alicia Lim p/91234567 s/English l/Secondary 4");
        Person alicia = model.getAddressBook().getPersonList().get(1);
        logic.execute("find n/Ryan");
        logic.execute("delete 1");
        assertStoredRoster(List.of(alicia));

        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertStoredRoster(List.of(ryan, alicia));
        logic.execute("find n/Nobody");
        assertEquals(RedoCommand.MESSAGE_SUCCESS, logic.execute("redo").getFeedbackToUser());
        assertStoredRoster(List.of(alicia));
        logic.execute("undo");
        logic.execute("undo");
        assertStoredRoster(List.of(ryan));
        logic.execute("undo");
        assertStoredRoster(List.of());
        assertCommandException("undo", UndoCommand.MESSAGE_FAILURE);
        logic.execute("redo");
        assertStoredRoster(List.of(ryan));
    }

    @Test
    public void execute_clear_removesAllRecordsAndSupportsUndoRedo() throws Exception {
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1");
        logic.execute("add n/Alicia Lim p/91234567 s/English l/Secondary 4");
        logic.execute("find n/Ryan");

        assertEquals(ClearCommand.MESSAGE_SUCCESS, logic.execute("clear").getFeedbackToUser());
        assertStoredRoster(List.of());

        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(2, model.getFilteredPersonList().size());
        assertStoredRoster(List.of(model.getAddressBook().getPersonList().get(0),
                model.getAddressBook().getPersonList().get(1)));

        assertEquals(RedoCommand.MESSAGE_SUCCESS, logic.execute("redo").getFeedbackToUser());
        assertStoredRoster(List.of());
    }

    @Test
    public void execute_clearSaveFailure_preservesRosterAndStoredFile() throws Exception {
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1");
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        String storedBefore = Files.readString(dataPath);
        AddressBook rosterBefore = new AddressBook(model.getAddressBook());

        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(dataPath) {
            @Override
            public void saveAddressBookAtomically(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))));

        assertCommandException("clear",
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
        assertEquals(rosterBefore, model.getAddressBook());
        assertEquals(List.of(rosterBefore.getPersonList().getFirst()), model.getFilteredPersonList());
        assertEquals(storedBefore, Files.readString(dataPath));
    }

    @Test
    public void execute_readOnlyAndRejectedCommands_preserveRedoAndFilter() throws Exception {
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1");
        logic.execute("undo");
        logic.execute("find n/Nobody");
        assertCommandException("undo", UndoCommand.MESSAGE_FAILURE);
        assertParseException("redo 1", RedoCommand.MESSAGE_USAGE);
        assertCommandException("delete 1", MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        assertEquals(List.of(), model.getFilteredPersonList());
        logic.execute("list");
        logic.execute("redo");
        assertEquals("Ryan Tan", model.getFilteredPersonList().getFirst().getName().fullName);
        logic.execute("undo");
        logic.execute("add n/Sarah Lee p/91234567 s/English l/Secondary 4");
        assertCommandException("redo", RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void execute_duplicateAdd_preservesRedo() throws Exception {
        String addRyan = "add n/Ryan Tan p/81234567 s/Physics l/JC 1";
        logic.execute(addRyan);
        logic.execute("add n/Alicia Lim p/91234567 s/English l/Secondary 4");
        logic.execute("undo");
        assertCommandException(addRyan, "This student already exists in TutorRoster.");
        logic.execute("redo");
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_showWithRedoAvailable_preservesHistoryNotesAndSavedRoster() throws Exception {
        boolean[] shouldFailSaving = {false};
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }

            @Override
            public void saveAddressBookAtomically(ReadOnlyAddressBook addressBook) throws IOException {
                if (shouldFailSaving[0]) {
                    throw DUMMY_IO_EXCEPTION;
                }
                super.saveAddressBookAtomically(addressBook);
            }
        };
        logic = new LogicManager(model, storage);
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1 r/Revise algebra");
        Person ryan = model.getAddressBook().getPersonList().getFirst();
        logic.execute("add n/Alicia Lim p/91234567 s/English l/Secondary 4");
        logic.execute("undo");
        logic.execute("find n/Ryan");
        String storedBefore = Files.readString(dataPath);
        shouldFailSaving[0] = true;

        CommandResult result = logic.execute("show 1");

        assertEquals(ShowCommand.MESSAGE_SHOW_STUDENT_SUCCESS, result.getFeedbackToUser());
        assertEquals(ryan, result.getPersonToShow().orElseThrow());
        assertEquals("Revise algebra", result.getPersonToShow().orElseThrow().getNotes());
        assertEquals(List.of(ryan), model.getFilteredPersonList());
        assertTrue(model.canRedoAddressBook());
        assertEquals(storedBefore, Files.readString(dataPath));

        shouldFailSaving[0] = false;
        logic.execute("redo");
        assertEquals(2, model.getAddressBook().getPersonList().size());
        logic.execute("undo");
        logic.execute("undo");
        assertStoredRoster(List.of());
        logic.execute("redo");
        assertStoredRoster(List.of(ryan));
        assertEquals("Revise algebra", model.getFilteredPersonList().getFirst().getNotes());
    }

    @Test
    public void execute_restart_keepsRecordsButStartsFreshHistory() throws Exception {
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1");
        ReadOnlyAddressBook stored = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        model = new ModelManager(stored, new UserPrefs());
        setUp();
        assertCommandException("undo", UndoCommand.MESSAGE_FAILURE);
        assertCommandException("redo", RedoCommand.MESSAGE_FAILURE);
        assertEquals(stored, model.getAddressBook());
    }

    @Test
    public void execute_saveFailures_preserveRosterFilterHistoryAndFile() throws Exception {
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        boolean[] shouldFail = {false};
        JsonAddressBookStorage rosterStorage = new JsonAddressBookStorage(dataPath) {
            @Override
            public void saveAddressBookAtomically(ReadOnlyAddressBook addressBook) throws IOException {
                if (shouldFail[0]) {
                    throw DUMMY_IO_EXCEPTION;
                }
                super.saveAddressBookAtomically(addressBook);
            }
        };
        logic = new LogicManager(model, new StorageManager(rosterStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))));
        logic.execute("add n/Ryan Tan p/81234567 s/Physics l/JC 1");
        logic.execute("add n/Alicia Lim p/91234567 s/English l/Secondary 4");
        logic.execute("find n/Ryan");
        String storedBefore = Files.readString(dataPath);
        AddressBook rosterBefore = new AddressBook(model.getAddressBook());
        shouldFail[0] = true;
        for (String input : List.of("delete 1", "undo")) {
            assertCommandException(input,
                    String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
            assertEquals(rosterBefore, model.getAddressBook());
            assertEquals(List.of(rosterBefore.getPersonList().getFirst()), model.getFilteredPersonList());
            assertEquals(storedBefore, Files.readString(dataPath));
        }
        shouldFail[0] = false;
        logic.execute("undo");
        assertStoredRoster(List.of(rosterBefore.getPersonList().getFirst()));
        storedBefore = Files.readString(dataPath);
        shouldFail[0] = true;
        assertCommandException("redo",
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
        assertCommandException("add n/Sarah Lee p/91234567 s/English l/Secondary 4",
                LogicManager.MESSAGE_ADD_SAVE_FAILURE);
        assertEquals(storedBefore, Files.readString(dataPath));
        shouldFail[0] = false;
        logic.execute("redo");
        assertEquals(rosterBefore, model.getAddressBook());
        assertFalse(model.canRedoAddressBook());
    }

    private void assertStoredRoster(List<Person> expected) throws Exception {
        assertEquals(expected, model.getAddressBook().getPersonList());
        assertEquals(expected, model.getFilteredPersonList());
        assertEquals(model.getAddressBook(),
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                        .readAddressBook().orElseThrow());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.restoreSnapshot(model.createSnapshot());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e) throws IOException {
        Path dataPath = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(dataPath).saveAddressBook(model.getAddressBook());
        String storedBefore = Files.readString(dataPath);

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataPath) {
            @Override
            public void saveAddressBookAtomically(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = "add n/Amy Bee p/85355255 s/Mathematics l/Secondary 4";
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(addCommand, CommandException.class,
                LogicManager.MESSAGE_ADD_SAVE_FAILURE, expectedModel);
        assertEquals(storedBefore, Files.readString(dataPath));
    }
}
