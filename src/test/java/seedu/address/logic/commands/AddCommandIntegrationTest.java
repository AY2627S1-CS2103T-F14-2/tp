package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_newStudent_success() {
        Person student = new Person("Ryan Tan", "+6591234567", null,
                List.of("Mathematics", "Physics"), "Secondary 4");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(student);

        assertCommandSuccess(new AddCommand(student), model, "New student added: Ryan Tan", expectedModel);
    }

    @Test
    public void execute_sameNormalizedNameAndPhone_duplicate() {
        Person existing = new Person("Ryan  Tan", "+6591234567", null,
                List.of("Mathematics"), "Secondary 4");
        model.addPerson(existing);
        Person duplicate = new Person("ryan tan", "6591234567", null,
                List.of("Physics"), "JC 1");

        assertCommandFailure(new AddCommand(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNormalizedNameAndEmail_duplicate() {
        Person existing = new Person("Ryan Tan", "91234567", "Ryan@Example.com",
                List.of("Mathematics"), "Secondary 4");
        model.addPerson(existing);
        Person duplicate = new Person("RYAN TAN", "81234567", "ryan@example.com",
                List.of("Physics"), "JC 1");

        assertCommandFailure(new AddCommand(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentContact_allowed() {
        Person existing = new Person("Ryan Tan", "91234567", null,
                List.of("Mathematics"), "Secondary 4");
        model.addPerson(existing);
        Person another = new Person("Ryan Tan", "81234567", null,
                List.of("Physics"), "JC 1");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(another);

        assertCommandSuccess(new AddCommand(another), model, "New student added: Ryan Tan", expectedModel);
    }

    @Test
    public void execute_differentNameSharedContact_allowed() {
        Person existing = new Person("Ryan Tan", "91234567", "family@example.com",
                List.of("Mathematics"), "Secondary 4");
        model.addPerson(existing);
        Person sibling = new Person("Alicia Tan", "91234567", "family@example.com",
                List.of("Physics"), "JC 1");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(sibling);

        assertCommandSuccess(new AddCommand(sibling), model, "New student added: Alicia Tan", expectedModel);
    }

    @Test
    public void execute_legacyRecordDoesNotBlockNewStudent() {
        Person legacy = new PersonBuilder().withName("Ryan Tan").withPhone("81234567").build();
        model.addPerson(legacy);
        Person student = new Person("Ryan Tan", "81234567", null,
                List.of("Mathematics"), "Secondary 4");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(student);

        assertCommandSuccess(new AddCommand(student), model, "New student added: Ryan Tan", expectedModel);
    }

}
