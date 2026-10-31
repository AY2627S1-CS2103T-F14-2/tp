package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.TagMigrationException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.5.0";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "addressbook.json");
    private static final String TAG_MIGRATION_NOTICE =
            "Existing tags were updated to lowercase and duplicate tags were merged.";

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    private String startupError;
    private final JsonAddressBookStorage addressBookStorage;
    private final JsonUserPrefsStorage userPrefsStorage;

    /** Creates the application with its usual data and preference files. */
    public MainApp() {
        this(new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH), new JsonUserPrefsStorage(USER_PREFS_FILE_PATH));
    }

    /** Creates the application with explicit storage, allowing isolated startup checks. */
    MainApp(JsonAddressBookStorage addressBookStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.addressBookStorage = java.util.Objects.requireNonNull(addressBookStorage);
        this.userPrefsStorage = java.util.Objects.requireNonNull(userPrefsStorage);
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TutorRoster ]===========================");
        super.init();

        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        try {
            model = initModelManager(storage, userPrefs);
        } catch (TagMigrationException e) {
            startupError = Messages.formatError("Unable to save updated tags. Your roster file was not changed.\n"
                    + "Action: Check that the data folder is writable, then restart TutorRoster.");
            logger.severe("Stopping startup because the tag migration could not be saved: " + e.getMessage());
            return;
        }

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath(),
                addressBookStorage.wereTagsMigrated() ? TAG_MIGRATION_NOTICE : "");
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * Sample data is used if {@code storage}'s address book is not found. Startup stops if an existing address book
     * cannot be read, so the user cannot accidentally overwrite it by saving an empty roster.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) throws TagMigrationException {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                    + " populated with sample TutorRoster data.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (TagMigrationException e) {
            throw e;
        } catch (DataLoadingException e) {
            startupError = Messages.formatError("Unable to load the existing roster. Your roster file was not "
                    + "changed.\nAction: Restore a readable backup or repair the data file, then restart TutorRoster.");
            logger.severe("Stopping startup because the roster could not be loaded: " + e.getMessage());
            return null;
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting TutorRoster " + MainApp.VERSION);
        if (startupError != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, startupError);
            alert.setTitle("TutorRoster error");
            alert.setHeaderText("Unable to start TutorRoster");
            alert.showAndWait();
            Platform.exit();
            return;
        }
        assert ui != null : "Successful initialization must create the UI";
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TutorRoster ] =============================");
        if (model == null) {
            return;
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
