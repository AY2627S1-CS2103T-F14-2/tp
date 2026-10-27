---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This produces a `Command` object, such as `DeleteCommand`. `LogicManager` executes it against an isolated
   working model containing a checkpoint of the live roster, history, and filter.
1. The command communicates with that working `Model`, for example to delete a student. The Delete sequence
   diagram focuses on these command/model calls; its `m:Model` represents the working model.
1. For a roster change or an undo/redo operation, `LogicManager` saves the proposed roster atomically before
   publishing the working model's checkpoint to the live model. Display-only commands do not save roster data.
1. The result is returned as a `CommandResult` after successful execution and any required save. See
   [Recording and publishing a change](#recording-and-publishing-a-change) for the complete flow.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores student records as `Person` objects in a `UniquePersonList`, together with session history in
  `VersionedTutorRoster`, which extends `AddressBook`.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add student

`AddCommandParser` recognises `n/`, `p/`, `e/`, optional `a/` address, `s/`, `l/`, optional `r/` notes, and `t/` at parameter boundaries.
Each repeated `t/TAG` supplies one tag. Equivalent tags are deduplicated with warning feedback,
and invalid tags or more than 20 distinct tags reject the entire command.
It rejects unknown prefixes and repeated single-use prefixes first. For a missing required prefix or an empty
required value, it reports the affected field and shows separate required and optional parameter lists. Invalid
field values are checked before constructing a student. `StudentFields` defines normalization and field validation.
Names, subjects, and levels lose surrounding Unicode whitespace and collapse each internal whitespace run to one space
before validation.
Subject duplicate comparison uses that normalized value without case; storage retains the normalized value and
entered capitalization. Subjects remain in input order.

Student records have subjects, a level, optional address, and optional notes. Notes are trimmed, limited to 500 Unicode characters,
and cannot contain control characters. `Person.isSamePerson` compares two records by normalized
name and either normalized phone or case-insensitive email.

`AddCommand` changes an isolated working model and resets its filter to the full roster. `LogicManager`
records the changed roster in that model's history, saves it atomically, and then publishes its checkpoint
(roster, history, and filter) to the live model. A save failure leaves the live model and data file unchanged.
This is the shared [recording and publishing flow](#recording-and-publishing-a-change) used for roster changes.

### Student tags

`TagCommandParser` parses `tag INDEX TAG`, `untag INDEX TAG`, and `untag --all INDEX`.
The two command words share `TagCommand`, whose operation is ADD, REMOVE, or CLEAR.
It validates the index against the displayed list and replaces the selected immutable student using
`Person.withTags`. That copy preserves all other fields and optional-field presence.
The model's tag sets are unmodifiable and copied defensively.

`Tag` stores canonical lowercase names. New names contain at most 30 English letters/digits with
single hyphens between nonempty segments. `Tag.fromStoredName` validates the same grammar without
the new length limit, allowing old tags to remain readable and removable. Commands enforce the
20-tag addition limit.

`LogicManager` executes tag commands through the shared snapshot flow, copying the roster, current
filter, and undo/redo history into the working model. Unchanged outcomes return warning feedback
without saving or altering history. Changed outcomes create one undo step and are saved atomically
before the complete snapshot is published. Tag commands retain the current filter; undo/redo show
the full roster. An I/O failure raises a command exception and leaves the live roster, filter, and
history unchanged. The observable list instance remains stable.
Input validation uses exceptions; assertions check internal identity invariants. Logs record operation
and save outcomes without adding tag values to the new log messages.

Storage keeps the existing per-student JSON `tags` array, sorted when written. On load,
`JsonAdaptedPerson` detects spelling changes and duplicate tags. `JsonAddressBookStorage` atomically
saves normalized data only when migration is needed and exposes whether migration occurred.
`MainApp` passes the completed-migration notice to the UI. `TagMigrationException` distinguishes a
failed migration save from other load errors, so it stops startup with an error dialog instead of
falling back to an empty editable roster. Existing tag lengths and counts above the new limits are
retained. Migration is idempotent and preserves unrelated model fields.

Student cards display sorted, wrapping badges and hide the tag area for an empty set. First-launch
sample students include example tags. Tag changes use the undo/redo implementation below;
field-targeted searches can also filter by address, subjects, level, notes, contact details, and tags. Phone searches
normalise an optional leading `+` before applying partial matching.

### Delete student

`DeleteCommandParser` accepts a single positive displayed index through `ParserUtil.parseIndex`.
Malformed arguments produce `Error: Invalid command format.` followed by `Usage: delete INDEX`.

`DeleteCommand.execute` reads the currently filtered list and validates the index against its size.
An out-of-range index produces `The student index provided is invalid.` without changing the model or filter.
For a valid index, it resolves the student before deleting the record, then resets the filter with
`Model.PREDICATE_SHOW_ALL_PERSONS`. Resolving the student before clearing the filter is necessary because a search
result's index can differ from its index in the complete roster.
The command returns `Student removed: NAME (phone: PHONE)` so identical names can be distinguished; it does not ask
for confirmation.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Delete student sequence" />

The diagram shows `DeleteCommand` operating on the isolated working model. `LogicManager` records the deletion
as one undo step and saves the proposed roster atomically before publishing it to the live model. If saving
fails, the live roster, filter, and history remain unchanged; the command can be retried after resolving the
save error. See the shared [recording and publishing flow](#recording-and-publishing-a-change).

### Undo and redo

TutorRoster restores complete roster states so tutors can recover from accidental changes. The enabled
roster-changing commands are currently `add`, `edit`, `delete`, `clear`, `tag`, and `untag`. Tag commands that only
report a warning leave history unchanged. The history mechanism also supports record replacements at the model level.

#### State and responsibilities

| Class | Responsibility |
| --- | --- |
| `VersionedTutorRoster` | Extends `AddressBook` with a private `states` list of roster snapshots and a `currentStatePointer`. Implements `commit()`, `canUndo()`, `canRedo()`, `undo()`, and `redo()`. |
| `ModelManager` | Owns the versioned roster and filtered list. Exposes history operations through `Model`, and shows the full roster after undo/redo. |
| `ModelSnapshot` | Checkpoints the working roster, its entire undo/redo history, and its current filter for isolated execution and later publication. |
| `UndoCommandParser` / `RedoCommandParser` | Reject arguments and create the corresponding command. `AddressBookParser` dispatches to them. |
| `UndoCommand` / `RedoCommand` | Check history availability, restore the previous/next roster through `Model`, and return feedback. |
| `LogicManager` | Coordinates command execution, history commits, atomic persistence, and publication to the live model. |

`VersionedTutorRoster` starts with one snapshot of the loaded or sample roster and a pointer of `0`.
`canUndo()` is true when the pointer is greater than `0`; `canRedo()` is true when the pointer is less than
`states.size() - 1`. Each roster snapshot preserves the complete student records and their order. The private
roster copies can share immutable `Person` values without exposing a mutable historical list.

The history snapshots and `ModelSnapshot` serve different purposes: `states` supports stepping through roster
changes, while `ModelSnapshot` carries both that history and the display filter across the save boundary.
The filter is not restored from an older undo step: a successful undo or redo deliberately shows all students.

#### Recording and publishing a change

`LogicManager.execute` uses the following flow for every parsed command:

1. Construct an isolated `ModelManager` from the live roster and preferences, then call
   `proposedModel.restoreSnapshot(model.createSnapshot())` to copy the current history and filter as well.
1. Execute the command against `proposedModel`. An index-based command therefore resolves the same displayed
   student as it would in the live model. Parser or command errors do not publish any working state.
1. Compare the working and live rosters. If the roster changed and the command is neither undo nor redo, call
   `proposedModel.commitAddressBook()`. `commit()` discards the redo suffix, appends a roster copy, and advances
   the pointer to that new last state. An unchanged roster does not create a history entry.
1. If the roster changed, or the command is undo/redo, call
   `storage.saveAddressBookAtomically(proposedModel.getAddressBook())`. `StorageManager` delegates to
   `JsonAddressBookStorage`, which writes a sibling temporary file and atomically replaces the data file.
1. Only after saving succeeds, publish with `model.restoreSnapshot(proposedModel.createSnapshot())`.
   Restoring the checkpoint transfers the roster, pointer, history, and filter while retaining the observable
   list instance used by the UI. Return the command's `CommandResult`.

Display-only commands such as `find`, `list`, and `show` publish their updated filter without committing history or
writing the roster file. Undo and redo move within the existing history; they do not append a new state.
The same save-before-publication rule applies to them, so a failed save cannot consume an undo or redo step.

#### Successful undo sequence

The following PlantUML sequence diagram follows `undo` through both parsers, the command, the working model,
persistence, and the live model. It assumes an earlier state exists and the save succeeds. Routine data getters
and the internal copying performed by checkpoints are omitted to keep the command flow readable.

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="Undo command parsing, working-model restoration, atomic save, and live-model publication" />

`AddressBookParser.parseCommand("undo")` creates an `UndoCommandParser`, whose `parse("")` returns an
`UndoCommand`. `LogicManager` prepares the working model before calling `UndoCommand.execute(proposedModel)`.
The command calls `canUndoAddressBook()` and then `undoAddressBook()` on that working model. Its success result
is not returned to the UI until the restored roster has been saved and published.

The next diagram expands `undoAddressBook()` inside the working model:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="Working ModelManager restores the previous VersionedTutorRoster state and clears the display filter" />

`VersionedTutorRoster.undo()` checks availability, asserts that there are no uncommitted roster changes,
calls `resetData` with `states.get(currentStatePointer - 1)`, and decrements the pointer only after restoration
succeeds. A restoration exception leaves the pointer unchanged. `ModelManager` then calls
`updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`. These steps affect only the working model until
`LogicManager` completes persistence and publication.

Redo follows the same flow using `RedoCommandParser`, `RedoCommand`, `canRedoAddressBook()`, and
`redoAddressBook()`. `VersionedTutorRoster.redo()` restores `states.get(currentStatePointer + 1)` before
incrementing the pointer, so the same failure protection applies.

#### History example

Assume the session starts with Alicia Lim, Ryan Tan, and Sarah Lee in that order. Let `S0` be that initial
roster, `S1` the roster after deleting Ryan, and `S2` the roster after subsequently adding Jamie Koh.
All commands below succeed unless the feedback says otherwise.

| Command | `states` after publication | Pointer | Result |
| --- | --- | --- | --- |
| Start the session | `[S0]` | `0` | No undo or redo available. |
| `delete 2` | `[S0, S1]` | `1` | Alicia and Sarah remain. |
| `add n/Jamie Koh p/81234567 s/Physics l/JC 1` | `[S0, S1, S2]` | `2` | Jamie is appended. |
| `undo` | `[S0, S1, S2]` | `1` | The complete roster in `S1` is restored. |
| `list` | `[S0, S1, S2]` | `1` | History is unchanged. |
| `redo` | `[S0, S1, S2]` | `2` | Jamie is restored. |
| `undo` | `[S0, S1, S2]` | `1` | Jamie is removed again. |
| `add n/Noah Goh p/91234567 s/English l/Secondary 4` | `[S0, S1, S3]` | `2` | The new roster `S3` replaces the abandoned redo branch `S2`. |
| `redo` | `[S0, S1, S3]` | `2` | `There are no changes to redo.`; history is unchanged. |

The history is session-only. Saving writes the current roster, without `states`, the pointer, or the display
filter. Loading the saved roster into a new `ModelManager` starts a fresh one-state history.

#### Errors and invariants

* Arguments such as `undo 1` and `redo all` produce `ParseException` with
  `Error: Invalid command format.` followed by `Usage: undo` or `Usage: redo` in the UI.
* When history is unavailable, the command throws `CommandException` with `There are no changes to undo.`
  or `There are no changes to redo.`. It does not restore a state or clear the live search filter.
* An `IOException` or `AccessDeniedException` during atomic saving becomes a `CommandException`. The live
  roster, filter, history, and previously saved file remain unchanged. The proposed pointer movement is discarded.
* Java `assert` statements in `VersionedTutorRoster` check that the history contains a valid current pointer,
  new commits end the redo branch, and undo/redo starts from a committed roster. These diagnose programmer errors.
  History availability and command syntax also have runtime checks, so user-input validation works when Java
  assertions are disabled.

#### Design considerations and extension

**Whole-roster snapshots:** A state restores every student field and roster position together. This avoids
maintaining separate inverse operations for add, delete, and future record replacements. The cost is memory
that grows with the number of retained states and roster size; history is not persisted across sessions.

**Save before publication:** Preparing the entire operation on a working model keeps roster data, redo history,
and display state consistent on save failure. This requires copying the working roster and history metadata
for command execution. Updating the live model first would require restoring all three after a failed save.

**Adding a roster-changing command:** Enable its parser and change the supplied working `Model` in `execute`.
`LogicManager` detects the changed roster and records it centrally. Avoid independently committing a second
history step, preserve `Person` immutability when adding fields, and cover undo/redo and save failures in tests.

Existing coverage includes `VersionedTutorRosterTest`, `ModelHistoryTest`, `UndoCommandTest`, `RedoCommandTest`,
`HistoryCommandParserTest`, and `LogicManagerTest`. Together they cover bounds, record replacements, ordering,
redo-branch replacement, filtered deletion, persistence, save failures, failed restorations with retry,
notes preservation, read-only detail viewing, and fresh history after restart.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | new user                                   | see usage instructions       | refer to instructions when I forget how to use the App                 |
| `* * *`  | user                                       | add a new person             |                                                                        |
| `* * *`  | user                                       | delete a person              | remove entries that I no longer need                                   |
| `* * *`  | user                                       | find students by their fields | locate details of students without going through the entire list |
| `* *`    | user                                       | hide private contact details | minimize chance of someone else seeing them by accident                |
| `*`      | user with many persons in the address book | sort persons by name         | locate a person easily                                                 |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1.  User requests to list persons
2.  AddressBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  AddressBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. AddressBook shows an error message.

      Use case resumes at step 2.

### Use cases

(For all use cases below, the **System** is `TutorRoster` and the **Actor** is the `user`.)

**Use case: Add a student**

**MSS**

1. User enters a command to add a student with their name, phone number, subject(s), education level, and optional email address, address, and notes
2. TutorRoster validates the command and student details
3. TutorRoster checks that the student is not a duplicate
4. TutorRoster stores the new student record
5. TutorRoster displays the updated student list and a confirmation message

   Use case ends.

**Extensions**

- 2a. The command or student details are invalid.

  - 2a1. TutorRoster shows an error message.

    Use case ends.

- 3a. The student is already in the roster.

  - 3a1. TutorRoster rejects the duplicate record and shows an error message.

    Use case ends.

- 4a. TutorRoster cannot save the student data.

  - 4a1. TutorRoster shows an error message and makes no changes.

    Use case ends.

---

**Use case: List students**

**MSS**

1. User requests to list students
2. TutorRoster displays all stored students in roster order
3. TutorRoster displays the number of students listed

   Use case ends.

**Extensions**

- 1a. The user provides additional text or parameters.

  - 1a1. TutorRoster shows an error message.

    Use case ends.

- 2a. The roster is empty.

  - 2a1. TutorRoster displays `No students in TutorRoster`.

    Use case ends.

---

**Use case: Find students by field**

**MSS**

1. User enters one or more field-prefixed search values
2. TutorRoster searches the specified fields using case-insensitive partial matching
3. TutorRoster displays the matching students in roster order
4. TutorRoster displays the number of students found

   Use case ends.

**Extensions**

- 1a. The user does not provide a search keyword.

  - 1a1. TutorRoster shows an error message.

    Use case ends.

- 3a. No students match the search keywords.

  - 3a1. TutorRoster displays an empty list and shows `No students found`.

    Use case ends.

---

**Use case: Delete a student**

**MSS**

1. User requests to list students
2. TutorRoster displays a numbered list of students
3. User requests to delete a specific student using its index
4. TutorRoster validates the index and deletes the selected student
5. TutorRoster displays the remaining student list and a confirmation message

   Use case ends.

**Extensions**

- 2a. The displayed list is the result of a previous search.

  - 2a1. TutorRoster interprets the index using the filtered list.

    Use case resumes at step 3.

- 3a. The given index is invalid.

  - 3a1. TutorRoster shows an error message.

    Use case resumes at step 2.

- 4a. TutorRoster cannot save the updated roster.

  - 4a1. TutorRoster shows an error message and makes no changes.

    Use case ends.

---

**Use case: Restore persistent student records**

**MSS**

1. User starts or reopens TutorRoster
2. TutorRoster loads the previously stored student records
3. TutorRoster displays the restored student list

   Use case ends.

**Extensions**

- 2a. No previous student data exists.

  - 2a1. TutorRoster starts with an empty roster.

    Use case ends.

- 2b. TutorRoster cannot load the existing student data.

  - 2b1. TutorRoster shows an error message and starts with an empty roster without overwriting the existing data.

    Use case ends.




### Non-Functional Requirements

1.  TutorRoster should work on any _mainstream OS_ as long as it has Java `25` installed.
2.  TutorRoster should be able to hold at least 1000 student records.
3.  With up to 1000 student records, common operations such as adding, editing, deleting, finding, filtering, and listing students should normally complete within one second.
4.  All core student-management operations should be executable entirely through keyboard commands without requiring a mouse.
5.  A user who is familiar with TutorRoster's commands should be able to perform common student-management operations faster than with a typical mouse-driven contact-management application.
6.  After TutorRoster reports that a data-modifying operation was successful, the updated student information should be saved and available when the application is restarted.
7.  Invalid commands, invalid student information, and failed operations should not modify or remove any existing student records.
8.  If the data file is missing, unreadable, or corrupted, TutorRoster should start safely, inform the user of the problem, and avoid automatically overwriting the affected file.
9.  TutorRoster should store all student data locally in a human-editable text file. All core student-management functions should remain usable without an Internet connection and should not depend on a database management system or remote server.
10. Error messages should identify the invalid field or command and explain the expected input format. Successful data-changing operations should also provide confirmation.

### Glossary

| Term | Definition |
|---|---|
| **TutorRoster** | A keyboard-driven application for private tutors to store, retrieve, and manage students’ contact and tutoring information. |
| **Private tutor** | A person who independently teaches students individually or in small groups. The intended user of TutorRoster. |
| **Student** | A person taught by the tutor whose information is stored in TutorRoster. |
| **Student record** | A stored entry representing one student. It contains a name, contact phone number, optional email address, optional address, one or more subjects, and an education level. |
| **Roster / Student list** | The collection of student records managed by the tutor. |
| **Contact information** | Details used to reach a student or their parent or guardian, such as a phone number or email address. |
| **Main contact number** | The phone number stored in a student record. It may belong to the student or a parent or guardian. |
| **Guardian** | A person responsible for a student who may serve as the tutor’s contact person. |
| **Subject** | An academic subject the tutor teaches a student, such as Mathematics or Physics. A student may have multiple subjects. |
| **Education level** | The academic stage at which a student is taught, such as Primary 6, Secondary 4, or JC 1. |
| **School** | The educational institution a student attends. A planned field beyond the strict MVP. |
| **Tag** | A lowercase label such as `exam-prep`, attached with `tag INDEX TAG` or `t/TAG` during add, and removed with `untag`. |
| **Student notes** | Optional free-text information about a student, such as learning difficulties, goals, preferred explanations, or parent requests. |
| **Preferred contact person** | The student or guardian whom the tutor should contact first. A planned feature beyond the strict MVP. |
| **Command** | A typed instruction that performs an operation, such as `list` or `delete 1`. |
| **CLI-first interface** | An interface where typing commands is the primary way to interact with the application, while a graphical interface displays records and feedback. |
| **Parameter** | A value supplied to a command, such as a student’s name or the index of a record to delete. |
| **Prefix** | A marker identifying a parameter’s type, such as `n/` for name or `s/` for subject. |
| **Student index (`INDEX`)** | A student’s position in the currently displayed numbered list, starting from 1. After a search, it refers to the search results. |
| **Filtered list** | A displayed subset of the roster containing only students who match the current search or filter criteria. |
| **Case-insensitive matching** | Comparing text without distinguishing uppercase from lowercase letters. For example, `Ryan` and `ryan` match. |
| **Whitespace normalization** | Removing surrounding whitespace and treating consecutive internal whitespace as one space in names, subjects, and education levels. |
| **Duplicate student record** | Under the MVP rules, a record with the same normalized name as an existing record and either the same phone number or the same email address. Sharing contact details alone does not make students duplicates. |
| **Input validation** | Checking that command input meets the required format and field rules before accepting it. |
| **Result area** | The part of the interface that displays command feedback, such as success messages or errors. |
| **Data persistence** | Automatically retaining student records between application sessions so they remain available after reopening TutorRoster. |
| **Application session** | One period of using TutorRoster, from opening the application until closing it. |
| **Minimum viable product (MVP)** | The smallest usable version of TutorRoster that supports its essential student-management needs. |

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a student

1. Deleting a student while the full roster is shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first student is removed, all remaining students are displayed, and the result area shows
      `Student removed: NAME (phone: PHONE)`.

   1. Test case: `delete 0`<br>
      Expected: No student is removed, the displayed list is unchanged, and the result area shows
      `Error: Invalid command format.` followed by `Usage: delete INDEX`.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Missing or malformed indices show `Error: Invalid command format.` followed by
      `Usage: delete INDEX`. Positive indices outside the displayed list show
      `Error: The student index provided is invalid.`.
      No student is removed and the displayed list remains unchanged.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
