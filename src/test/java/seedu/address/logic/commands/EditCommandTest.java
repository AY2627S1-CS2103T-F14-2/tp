package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Address;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                editedPerson.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_newStudentFields_preservesAndReplacesNewData() throws Exception {
        Model studentModel = new ModelManager();
        Person student = new Person("Amy Bee", "85355255", "amy@example.com", List.of("Math"), "JC 1",
                "Old notes").withTags(Set.of(new Tag("old-tag")));
        studentModel.addPerson(student);

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setSubjects(List.of("Physics", "Chemistry"));
        descriptor.setLevel("Secondary 4");
        descriptor.setNotes("New notes");
        descriptor.setTags(Set.of(new Tag("new-tag")));

        new EditCommand(Index.fromOneBased(1), descriptor).execute(studentModel);

        Person edited = studentModel.getAddressBook().getPersonList().getFirst();
        assertEquals(List.of("Physics", "Chemistry"), edited.getSubjects());
        assertEquals("Secondary 4", edited.getLevel());
        assertEquals("New notes", edited.getNotes());
        assertEquals(Set.of(new Tag("new-tag")), edited.getTags());
        assertEquals("amy@example.com", edited.getEmail().value);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                editedPerson.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                editedPerson.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                editedPerson.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void execute_studentFieldsSpecifiedUnfilteredList_success() {
        Person student = new Person("Ryan Tan", "+6591234567", "ryan@example.com",
                java.util.List.of("Mathematics", "Physics"), "Secondary 4", "Initial notes");
        model.addPerson(student);
        Index studentIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("+6598765432")
                .withLevel("Secondary 5")
                .build();
        EditCommand editCommand = new EditCommand(studentIndex, descriptor);

        Person expectedStudent = new Person("Ryan Tan", "+6598765432", "ryan@example.com",
                java.util.List.of("Mathematics", "Physics"), "Secondary 5", "Initial notes");
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                expectedStudent.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, expectedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editStudentSubjectsAndNotes_success() {
        Person student = new Person("Alice Wong", "+6581234567", null,
                java.util.List.of("English"), "Secondary 1", "");
        model.addPerson(student);
        Index studentIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withSubjects("English Literature", "History")
                .withNotes("Prefers visual aids")
                .build();
        EditCommand editCommand = new EditCommand(studentIndex, descriptor);

        Person expectedStudent = new Person("Alice Wong", "+6581234567", null,
                java.util.List.of("English Literature", "History"), "Secondary 1", "Prefers visual aids");
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                expectedStudent.getName().fullName);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, expectedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editStudentAddress_success() {
        Person student = new Person("Alice Wong", "+6581234567", null,
                List.of("English"), "Secondary 1", "", new Address("12 Main Street"));
        model.addPerson(student);
        Index studentIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setAddress(new Address("34 River Road"));
        EditCommand editCommand = new EditCommand(studentIndex, descriptor);

        Person expectedStudent = new Person("Alice Wong", "+6581234567", null,
                List.of("English"), "Secondary 1", "", new Address("34 River Road"));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, expectedStudent);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                expectedStudent.getName().fullName);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearOptionalStudentFields_success() {
        Person student = new Person("Alice Wong", "+6581234567", "alice@example.com",
                List.of("English"), "Secondary 1", "", new Address("12 Main Street"));
        model.addPerson(student);
        Index studentIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.clearEmail();
        descriptor.clearAddress();
        EditCommand editCommand = new EditCommand(studentIndex, descriptor);

        Person expectedStudent = new Person("Alice Wong", "+6581234567", null,
                List.of("English"), "Secondary 1", "", null);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, expectedStudent);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                expectedStudent.getName().fullName);
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_appendAndRemoveMultiValueFields_success() {
        Person student = new Person("Alice Wong", "+6581234567", null,
                List.of("English", "Mathematics"), "Secondary 1", "")
                .withTags(Set.of(new Tag("old-tag"), new Tag("keep-tag")));
        model.addPerson(student);
        Index studentIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setSubjectsToAdd(List.of("Physics"));
        descriptor.setSubjectsToRemove(List.of("English"));
        descriptor.setTagsToAdd(Set.of(new Tag("new-tag")));
        descriptor.setTagsToRemove(Set.of(new Tag("old-tag")));
        EditCommand editCommand = new EditCommand(studentIndex, descriptor);

        Person expectedStudent = new Person("Alice Wong", "+6581234567", null,
                List.of("Mathematics", "Physics"), "Secondary 1", "")
                .withTags(Set.of(new Tag("keep-tag"), new Tag("new-tag")));
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                expectedStudent.getName().fullName);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, expectedStudent);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudentPhone_failure() {
        Person student1 = new Person("Ryan Tan", "+6591234567", null,
                java.util.List.of("Mathematics"), "Secondary 4");
        Person student2 = new Person("Ryan Tan", "+6581234567", null,
                java.util.List.of("Mathematics"), "Secondary 4");
        model.addPerson(student1);
        model.addPerson(student2);

        Index student2Index = Index.fromOneBased(model.getFilteredPersonList().size());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("+6591234567").build();
        EditCommand editCommand = new EditCommand(student2Index, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }
}
