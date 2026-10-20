package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_ADD_SAVE_FAILURE = "Unable to save student data. No changes were made.";
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        Model proposedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        proposedModel.restoreSnapshot(model.createSnapshot());
        CommandResult commandResult = command.execute(proposedModel);
        boolean isHistoryCommand = command instanceof UndoCommand || command instanceof RedoCommand;
        boolean hasRosterChanged = !model.getAddressBook().equals(proposedModel.getAddressBook());

        if (hasRosterChanged || isHistoryCommand) {
            if (!isHistoryCommand) {
                proposedModel.commitAddressBook();
            }
            try {
                storage.saveAddressBookAtomically(proposedModel.getAddressBook());
            } catch (AccessDeniedException e) {
                String message = command instanceof AddCommand ? MESSAGE_ADD_SAVE_FAILURE
                        : String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage());
                throw new CommandException(message, e);
            } catch (IOException e) {
                String message = command instanceof AddCommand ? MESSAGE_ADD_SAVE_FAILURE
                        : String.format(FILE_OPS_ERROR_FORMAT, e.getMessage());
                throw new CommandException(message, e);
            }
        }
        // Publish roster, filter, and history together only after a successful save.
        model.restoreSnapshot(proposedModel.createSnapshot());

        return commandResult;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
