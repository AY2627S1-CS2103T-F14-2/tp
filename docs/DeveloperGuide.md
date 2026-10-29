---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TutorRoster Developer Guide

TutorRoster is a JavaFX desktop application for private tutors. The current implementation stores
student records locally as JSON and exposes roster operations through a command-driven interface.
This guide describes the v1.6 design and the behaviour that a future developer must preserve.

## Acknowledgements

TutorRoster was started from the [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3),
which is maintained by the [SE-EDU initiative](https://se-education.org/). We retained and adapted the
project's component separation, JavaFX/FXML application shell, command-parser pattern, JSON storage
approach, Gradle layout, and testing conventions. The student domain model, field-specific search,
student tags and migration, multi-value editing, student detail modal, command completion, and atomic
session history are TutorRoster-specific implementations in this repository.

We use the following open-source libraries:

* [OpenJFX](https://openjfx.io/) for the desktop UI.
* [Jackson](https://github.com/FasterXML/jackson) for JSON serialisation.
* [JUnit 5](https://github.com/junit-team/junit5) for automated tests.
* [MarkBind](https://markbind.org/) for the project documentation site.

The product visual design is implemented in the repository's FXML and CSS files. No other student
project is referenced as a source of code or documentation in the current release.

## Setting up and getting started

### Prerequisites

* JDK 25 or later.
* A desktop environment supported by JavaFX.
* Gradle Wrapper, included as `./gradlew`.

### Build and run

From the repository root:

```sh
./gradlew shadowJar
java -jar build/libs/TutorRoster.jar
```

For a development run, use `./gradlew run`. The application reads and writes `data/addressbook.json`
and `preferences.json` relative to its working directory. Start it from a temporary or dedicated
folder when testing storage behaviour so that test data is not mixed with a developer's roster.

### Verification commands

```sh
./gradlew test
./gradlew check
```

`test` runs the JUnit 5 suite. `check` also runs Checkstyle and the configured JaCoCo coverage gate.
The UI package and application-entry classes are excluded from the coverage gate because they require
a desktop session; their behaviour is covered by focused UI tests and manual testing.

For project documentation conventions, see [Documentation](Documentation.md). The legacy setup,
testing, logging, and DevOps pages remain available from [Setting up](SettingUp.md),
[Testing](Testing.md), [Logging](Logging.md), and [DevOps](DevOps.md).

## Design

### Architecture

The high-level component relationships are shown in the
[architecture diagram](diagrams/ArchitectureDiagram.puml):

<puml src="diagrams/ArchitectureDiagram.puml" alt="TutorRoster architecture" />

| Component | Responsibility | Main entry points |
| --- | --- | --- |
| `Main` / `MainApp` | Starts JavaFX, initialises preferences, storage, model, logic, and UI. | `Main.java`, `MainApp.java` |
| UI | Displays the roster and feedback, receives commands, and manages help/detail windows. | `UiManager`, `MainWindow` |
| Logic | Parses commands, invokes model operations, and coordinates save-before-publish execution. | `LogicManager`, command parsers |
| Model | Owns immutable student records, the roster, the filtered list, preferences, and session history. | `ModelManager`, `Person`, `VersionedTutorRoster` |
| Storage | Reads and writes roster and preference files. | `StorageManager`, JSON storage classes |
| Commons | Shared utilities, logging, validation helpers, and small value objects. | `commons` package |

The normal dependency direction is UI → Logic → Model and Storage. Storage depends on model types to
serialise them, but Model does not depend on UI, Logic, or Storage. `MainApp` is the composition root.

### UI component

The [UI class diagram](diagrams/UiClassDiagram.puml) shows the JavaFX composition:

<puml src="diagrams/UiClassDiagram.puml" alt="TutorRoster UI classes" />

`UiManager` creates `MainWindow`. `MainWindow` owns the command box, result display, student list,
status bar, and Help window. The student detail modal is created only when `show INDEX` succeeds and
is displayed as an overlay over the main scene.

* `PersonListPanel` observes the model's filtered observable list and creates a `PersonCard` for each
  displayed student.
* `PersonCard` renders the displayed index, initials, contact details, subjects, level, notes, and
  sorted tag badges. Optional email and notes are hidden when absent.
* `StudentDetailModal` renders every stored field and uses `Not provided` for absent optional values.
* `CommandBox` delegates command execution to `Logic`, keeps invalid input in the text field, and
  shows completion suggestions in a popup.
* `CommandBoxLogic` delegates suggestion generation to `CommandCompletionLogic`. `CommandHistory`
  records commands for the current session and preserves an unfinished draft while navigating.
* `HelpWindow` is a secondary, non-resizable stage. It displays the User Guide URL, copies it to the
  system clipboard, and closes on `F1`.

The UI observes one stable filtered-list instance. Model snapshot publication updates its contents
without replacing the list object that the JavaFX `ListView` is observing.

### Logic component

The [Logic class diagram](diagrams/LogicClassDiagram.puml) shows the parser/command flow:

<puml src="diagrams/LogicClassDiagram.puml" alt="TutorRoster logic classes" />

`LogicManager.execute(commandText)` performs these steps:

1. `AddressBookParser` separates the command word from its arguments and selects the command parser.
2. The selected parser validates syntax and creates a command object.
3. A proposed `ModelManager` is created from a snapshot of the live roster, history, preferences, and
   current filter.
4. The command executes against the proposed model. A parser or command error therefore cannot
   publish a partial state.
5. If the roster changed, the proposed model commits one history state. Undo and redo move the
   existing history pointer instead of appending a new state.
6. A changed roster is saved atomically. Only after saving succeeds is the proposed snapshot
   published to the live model.
7. The resulting `CommandResult` is returned to the UI. It can request a Help window, an exit, or a
   student detail modal in addition to feedback text.

Display-only commands publish a changed filter without committing history or writing the roster file.
The [parser class diagram](diagrams/ParserClasses.puml) lists the command-specific parsers.

### Model component

The [Model class diagram](diagrams/ModelClassDiagram.puml) shows the domain model:

<puml src="diagrams/ModelClassDiagram.puml" alt="TutorRoster model classes" />

`Person` is an immutable student record containing:

* required name, phone, one or more subjects, and education level;
* optional email and address;
* optional notes, stored as an empty string when absent; and
* a defensive, unmodifiable set of `Tag` values.

`StudentFields` centralises normalisation and validation for names, phones, email addresses, subjects,
levels, and notes. `Person.isSamePerson` defines duplicate identity using normalised name plus either
normalised phone or case-insensitive email. `Person.equals` remains a full-record comparison, which is
what history and proposed/live model comparisons use.

`ModelManager` owns:

* a `VersionedTutorRoster` containing the roster and session snapshots;
* the filtered observable list used by the UI; and
* `UserPrefs`, currently GUI window size and position.

`ModelSnapshot` copies the roster, history, and current predicate for isolated command execution. The
filter is deliberately replaced with the full-roster predicate after undo, redo, add, edit, delete,
list, and clear as specified by the User Guide.

### Storage component

The [Storage class diagram](diagrams/StorageClassDiagram.puml) shows the storage classes:

<puml src="diagrams/StorageClassDiagram.puml" alt="TutorRoster storage classes" />

`StorageManager` implements the storage interface and delegates to:

* `JsonAddressBookStorage` for `data/addressbook.json`;
* `JsonUserPrefsStorage` for `preferences.json`; and
* Jackson adapter classes for `Person`, `Tag`, and the root address-book object.

Roster changes use `saveAddressBookAtomically`: a sibling temporary file is written first and then
atomically moved over the destination. A failed write leaves the previous live model, history, and
data file intact. Preferences are saved when the application stops; a missing or unreadable preference
file falls back to default window settings.

When an existing roster contains mixed-case or duplicate tags, `JsonAdaptedPerson` detects the need
for migration. The normalised roster is saved atomically before startup continues. A migration save
failure raises `TagMigrationException`, which stops startup rather than exposing an empty editable
roster.

## Implementation

### Add and edit student records

`AddCommandParser` recognises `n/`, `p/`, `e/`, `a/`, `s/`, `l/`, `r/`, and repeated `t/` values at
parameter boundaries. It rejects unknown prefixes, repeated single-use fields, missing required
fields, empty required values, invalid field values, duplicate subjects, and more than 20 new tags.
Names, subjects, and levels are whitespace-normalised before validation; notes are trimmed and limited
to 500 Unicode characters without control characters.

`EditCommandParser` additionally recognises `+s/`, `-s/`, `+t/`, and `-t/`. The `EditPersonDescriptor`
records replacement, append, removal, and clear operations separately. `EditCommand` creates a new
validated immutable `Person`, rejects duplicate identity, updates the filtered list to the full roster,
and lets `LogicManager` record and save the change as one history step.

### Search and displayed indices

`FindCommandParser` converts each field-prefixed term into a `SearchTerm`. `PersonMatchesFieldsPredicate`
performs case-insensitive partial matching across name, phone, email, address, subject, level, and tag.
Phone matching removes an optional leading `+`. `ALL` mode requires every term to match; `ANY` mode is
selected by `--any`.

Index-based commands read `Model.getFilteredPersonList()` before changing anything. This is important
after a search: the selected displayed person must be resolved before a command resets the filter to
the full roster. `show`, `edit`, `delete`, `tag`, and `untag` all use this displayed-index rule.

### Student tags

`TagCommandParser` supports `tag INDEX TAG`, `untag INDEX TAG`, and `untag --all INDEX`. `TagCommand`
uses `Person.withTags` so every non-tag field is preserved. New tag names are lowercase, 1–30 English
letters or digits with optional single hyphens, and each student can receive at most 20 new tags.

Warning-only operations do not change the model, do not save, and do not create history. Changed tag
operations retain the current search filter. Storage writes tags in sorted order; load-time migration
keeps older tags that exceed the new add limits readable and removable.

### Student detail view and command completion

`ShowCommand` returns a `CommandResult` containing the selected `Person`. `MainWindow` consumes that
result and creates `StudentDetailModal`; the command itself does not alter the roster or filter.

Closing the modal or pressing `Esc` invokes `MainWindow.hideStudentDetails()`. This removes the modal
from the overlay, hides the overlay, and returns keyboard focus to the student list.

`CommandBoxLogic` owns `CommandCompletionLogic` and `CommandHistory`, keeping completion and command
recall independent of JavaFX controls. It executes submitted commands through an injected
`CommandExecutor` supplied by `CommandBox`.

`CommandCompletionLogic` suggests the command words `add`, `edit`, `delete`, `find`, `list`,
`show`, `tag`, `untag`, `help`, and `exit`, and context-sensitive parameter prefixes. The completion
list is UI guidance only: parsers remain the source of truth for validation.

Each `Suggestion` contains completion text, a displayed syntax guide, and a description. `CommandBox`
refreshes the popup when the input changes. Pressing `Tab` applies the first suggestion; clicking a
suggestion applies the selected suggestion and returns focus to the command box.

`CommandHistory` supports Up/Down navigation through entered commands and preserves the unfinished
draft when history browsing starts. This session command recall is separate from the model's
undo/redo history: recalling a command changes the input text without changing the roster.

### Simple commands and lifecycle

`ListCommand` applies the full-roster predicate without changing history. `ClearCommand` replaces the
roster with an empty `AddressBook`; the central `LogicManager` then records and saves it like any other
roster change. `HelpCommand` returns a `CommandResult` that asks `MainWindow` to show `HelpWindow`, and
`ExitCommand` returns a result that asks the main window to save GUI settings and hide the stage. The
window-close handler follows the same preference-saving path.

### Undo and redo

`VersionedTutorRoster` stores private whole-roster snapshots and a pointer to the current state. It
starts with one snapshot of the loaded or sample roster. A successful roster change calls `commit()`;
the method removes any abandoned redo suffix and appends a copy of the changed roster. Undo and redo
restore a previous or later snapshot without appending a new one.

The following diagram traces a successful undo through parsing, working-model restoration, atomic
saving, and publication:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="Successful undo sequence" />

Inside the working model, `VersionedTutorRoster.undo()` first checks availability and restores the
previous snapshot. The pointer is decremented only after restoration succeeds; a failed restoration
can therefore be retried. `ModelManager` then applies the full-roster filter.

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="Undo inside the working model" />

The model-level state and the `ModelSnapshot` serve different purposes. The history states support
stepping backward and forward. The `ModelSnapshot` carries the history and current filter across the
save boundary so that a save failure can discard the entire proposed operation.

| Case | Result |
| --- | --- |
| New change after undo | The redo suffix is discarded and the new change becomes the latest state. |
| Display-only command | The filter may change, but no history state is added. |
| Failed roster save | The live roster, filter, history, and previous file remain unchanged. |
| Application restart | The saved roster is loaded with a fresh one-state session history. |

### Atomic save and publication flow

For `add`, `edit`, `delete`, `clear`, and changed tag operations, execution is intentionally isolated:

```text
live ModelManager
        │ createSnapshot()
        ▼
proposed ModelManager ── command.execute() ── commit if changed
        │
        ├── saveAddressBookAtomically()
        │       ├── failure: discard proposed state
        │       └── success
        ▼
live ModelManager.restoreSnapshot(proposed snapshot)
```

This design also applies to undo and redo, except that those commands move the history pointer rather
than creating a new state. It avoids exposing a change in the UI before persistence has succeeded.

## Testing, logging, and DevOps

The automated suite covers parsing, field validation, duplicate identity, search predicates, tag
limits and migration, filtered index operations, edit semantics, history branching, persistence, and
save-failure rollback. UI classes are intentionally excluded from the JaCoCo gate because JavaFX tests
need a desktop session; the most important UI paths are covered by manual testing below.

Use the project guides for supporting workflows:

* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

## Appendix: Requirements

### Product scope

**Target users** are private freelance tutors who manage multiple students, prefer a desktop app, and
are comfortable typing commands. TutorRoster's value proposition is fast, local student-record
management with a lightweight visual roster and dependable keyboard workflow.

TutorRoster v1.6 covers student contact and tutoring-profile management. It does not implement lesson
scheduling, attendance, billing or payments, teaching materials, school fields, or a separate guardian
entity.

### User stories implemented in the current version

Priorities: High (must have) – `***`; Medium (nice to have) – `**`; Low – `*`.

| Priority | As a … | I want to … | So that I can … |
| --- | --- | --- | --- |
| `***` | new user | open usage instructions | learn the command syntax and recover from mistakes |
| `***` | tutor | add a student with tutoring details | keep a complete record in one place |
| `***` | tutor | edit a student's fields | keep contact and tutoring information current |
| `***` | tutor | find students by field | locate a record without scanning the whole roster |
| `***` | tutor | view a full student profile | inspect optional details before contacting or editing |
| `***` | tutor | delete a student | remove records that are no longer needed |
| `***` | tutor | clear the roster | start again with an empty list |
| `***` | tutor | add and remove tags | group students for follow-up or lesson preparation |
| `***` | tutor | undo or redo roster changes | recover from an accidental change |
| `***` | tutor | save records automatically | retain the roster between sessions |
| `**` | tutor | use autocomplete and command history | enter frequent commands quickly |
| `**` | tutor | use the keyboard for core operations | work efficiently without mouse navigation |

### Requirements yet to be implemented

These are future requirements, not v1.6 behaviour. They must not be documented as available product
features until implemented and tested.

| Priority | As a … | I want to … | So that I can … |
| --- | --- | --- | --- |
| `**` | tutor | sort students by name, level, or tag | find a record in a preferred order |
| `**` | tutor | hide or reveal optional contact fields | reduce accidental exposure on screen |
| `**` | tutor | record a school and structured guardian details | keep more context for each student |
| `*` | tutor | record lesson schedules and attendance | manage the wider tutoring workflow |
| `*` | tutor | record fees and payment status | track administrative work |

### Use cases

The use cases below document non-trivial interaction patterns. The actor is the tutor and the system
is TutorRoster.

#### Use case: Add a student

**MSS**

1. Tutor enters `add` with the required student fields and any optional fields.
2. TutorRoster parses and validates the command.
3. TutorRoster checks the duplicate-student rule.
4. TutorRoster adds the student to a proposed roster and saves it atomically.
5. TutorRoster publishes the new roster and displays a confirmation.

**Extensions**

* 2a. A required field is missing, a prefix is unknown, or a value is invalid. TutorRoster displays
  the relevant error and changes nothing.
* 2b. A subject or tag is repeated. TutorRoster rejects a repeated subject; it stores a repeated
  equivalent tag once and displays a warning.
* 3a. The record is a duplicate. TutorRoster displays `This student already exists in TutorRoster.`
* 4a. Saving fails. TutorRoster displays a save error and does not publish the proposed roster.

#### Use case: Search, inspect, and act on a displayed student

**MSS**

1. Tutor enters a field-specific `find` command.
2. TutorRoster displays matching students in roster order and numbers them from 1.
3. Tutor enters `show`, `edit`, `delete`, `tag`, or `untag` with a displayed index.
4. TutorRoster resolves the index against the filtered list.
5. TutorRoster performs the requested action and displays the appropriate result.

**Extensions**

* 1a. No search field is supplied or a value is empty. TutorRoster shows usage guidance and keeps
  the prior display.
* 2a. No students match. TutorRoster displays an empty list and `0 person(s) listed!`.
* 3a. The index is missing, malformed, or outside the displayed list. TutorRoster shows an error and
  changes nothing.
* 3b. The command is `show`. TutorRoster opens the modal and leaves the roster and filter unchanged.
* 3c. The command is `delete` or `edit`. A successful operation returns to the complete roster.
* 3d. The command is `tag` or `untag`. A successful operation keeps the current filter.

#### Use case: Edit multi-value fields

**MSS**

1. Tutor enters `edit INDEX` with replacement, append, or removal prefixes.
2. TutorRoster builds a new immutable student record from the descriptor.
3. TutorRoster validates the resulting subjects, tags, optional fields, and duplicate identity.
4. TutorRoster saves and publishes the edited roster as one history state.

**Extensions**

* 1a. No field is supplied. TutorRoster reports `At least one field to edit must be provided.`
* 2a. `e/`, `a/`, `r/`, or `t/` is supplied without a value where clearing is supported. The
  corresponding optional value is cleared.
* 3a. Removing all subjects, repeating a subject, exceeding the tag limit, or creating a duplicate
  record is rejected without changing the roster.
* 4a. Saving fails. The prior roster and history remain live.

#### Use case: Undo and redo a change

**MSS**

1. Tutor performs one or more successful roster changes.
2. Tutor enters `undo`.
3. TutorRoster restores the previous whole-roster snapshot in a proposed model.
4. TutorRoster saves the restored roster and publishes it with the full-roster filter.
5. Tutor enters `redo` to restore the next snapshot if required.

**Extensions**

* 2a. No earlier snapshot exists. TutorRoster displays `There are no changes to undo.`
  The roster and filter are unchanged.
* 4a. Saving fails. The history pointer and live model remain unchanged.
* 5a. A new successful roster change is made after undo. TutorRoster discards the abandoned redo
  branch and reports no redo available.

#### Use case: Restore persisted records

**MSS**

1. Tutor starts TutorRoster from a launch folder.
2. TutorRoster reads `data/addressbook.json` and `preferences.json`.
3. TutorRoster displays the saved roster and restores window settings.

**Extensions**

* 2a. The roster file is missing. TutorRoster displays sample students.
* 2b. The roster file cannot be read. TutorRoster starts with an empty roster; the user should make a
  backup before entering new commands.
* 2c. Tags need migration. TutorRoster saves the normalised file before showing the roster; a failed
  migration stops startup and leaves the original file unchanged.

### Non-functional requirements

1. TutorRoster must run on mainstream desktop operating systems with Java 25 or later and JavaFX.
2. TutorRoster should support at least 1,000 student records.
3. With up to 1,000 records, common operations should normally complete within one second.
4. Core roster operations must be executable through keyboard commands without requiring a mouse.
5. Student data must remain local and the core workflow must not depend on an Internet connection,
   database server, or remote service.
6. After a successful data-changing result, the updated roster must be available after restarting
   from the same launch folder.
7. Invalid commands and failed saves must not modify existing records or consume undo/redo history.
8. Roster writes must be atomic so a failed write does not replace the last known-good data file.
9. Error messages must identify the command or field problem and provide enough format guidance for
   correction.
10. The application must preserve optional-field presence and complete student details across save,
    load, undo, redo, and detail viewing.

### Glossary

| Term | Definition |
| --- | --- |
| **TutorRoster** | The desktop application documented here. |
| **Student record** | One immutable stored entry containing a name, phone, subjects, level, optional email and address, optional notes, and tags. |
| **Roster** | The ordered collection of student records. |
| **Displayed list** | The list currently visible in the UI; it may be the full roster or search results. |
| **Displayed index** | A one-based position in the displayed list, used by index-based commands. |
| **Prefix** | The marker before a field value, such as `n/` for name or `s/` for subject. |
| **Field-specific search** | A `find` query that limits matching to named fields such as `n/`, `p/`, `a/`, or `t/`. |
| **Tag** | A lowercase label attached to a student for grouping or follow-up. |
| **Session history** | In-memory whole-roster snapshots used by `undo` and `redo`; it is not persisted. |
| **Proposed model** | An isolated model copy on which a command executes before saving and publication. |
| **Atomic save** | Writing a temporary sibling file and replacing the destination only after the write succeeds. |
| **Migration** | Normalising old stored tags while preserving the rest of the roster. |
| **Value object** | An immutable model type such as `Name`, `Phone`, `Email`, `Address`, or `Tag`. |

## Appendix: Instructions for Manual Testing

These checks complement the User Guide. Use a fresh temporary folder and a copy of the runnable JAR so
that the test roster does not alter a developer's normal data.

### Launch and preferences

1. Put `TutorRoster.jar` in an empty, writable folder.
2. Launch from a terminal with `java -jar TutorRoster.jar`.
   Expected: the main window opens with sample students, a command box, result area, and a status bar
   showing a path under `data/addressbook.json`.
3. Resize and move the window, close it, then launch it again from the same folder.
   Expected: the size and position are restored through `preferences.json`.
4. Repeat the launch from a write-protected folder or with a read-only data file.
   Expected: data-changing commands report a save error and do not change the visible roster.

### Basic roster workflow

1. Enter the following valid command:

   ```text
   add n/Mei Lin p/+6598765432 e/mei@example.com a/12 River Road s/Mathematics s/Physics l/Secondary 3 t/exam-prep r/Prefers diagrams
   ```

   Expected: Mei Lin appears at the end, tags are shown as a badge, and the result says
   `New student added: Mei Lin`.
2. Enter `find n/Mei`, then `show 1`.
   Expected: one match is shown; the modal displays all fields and can be closed with `Esc`.
3. Enter `edit 1 +s/Chemistry +t/needs-follow-up`.
   Expected: subjects and tags are appended and the full roster is shown.
4. Enter `find --any s/Physics t/needs-follow-up`.
   Expected: students matching either condition are displayed and the result area reports the count.
5. Enter `list`.
   Expected: the full roster returns and the result says `Listed all students.`

### Tags and validation

1. Enter `find n/Mei`, then `tag 1 Exam-Prep`.
   Expected: the tag is stored/displayed as `exam-prep` and the filter remains active.
2. Enter `tag 1 exam-prep` again.
   Expected: a `Warning:` says the student already has the tag; no new history step is created.
3. Enter `untag 1 exam-prep`, then `untag --all 1`.
   Expected: the first removes one tag and the second clears all remaining tags.
4. Try `add n/Test p/9123-4567 s/English l/Primary 6`.
   Expected: validation rejects the hyphenated phone and no record is added.
5. Try `add n/Test p/91234567 l/Primary 6`.
   Expected: the missing `s/` field is identified and no record is added.
6. Try `find` and `find --any`.
   Expected: both show usage guidance because a search field is required.

### Index safety and history

1. Enter `find n/Mei`, then `delete 1`.
   Expected: the matching student is removed and the complete roster is displayed.
2. Enter `undo`, then `redo`.
   Expected: the student is restored, then removed again; each command reports success.
3. Enter `undo`, then add a different student, then `redo`.
   Expected: the new add starts a new branch and `redo` reports `There are no changes to redo.`
4. Enter `delete 0`, `show 999`, and `edit 1` with no fields.
   Expected: each is rejected without changing the roster.
5. Enter `clear`, then `undo`.
   Expected: the roster becomes empty and then the previous complete roster is restored.

### Persistence and restart

1. Add or edit a record and wait for the success feedback.
2. Enter `exit`, then launch again from the same folder.
   Expected: the saved roster is present, but `undo` and `redo` report that no session history is available.
3. Copy `data/addressbook.json` while the app is closed, restore the copy in another fresh launch folder,
   and start the app there.
   Expected: the copied roster is loaded.

### UI keyboard paths

1. Focus the roster and press `/`; type `li`, then press `Tab` and `Enter`.
   Expected: `list` is completed and executed.
2. Type part of `add` and inspect the suggestions. Click a suggestion and verify that its prefix is inserted.
3. Use Up and Down arrows in the command box.
   Expected: commands from the current session are recalled, with an unfinished draft restored at the end.
4. Press `F1`, click **Copy URL**, and press `F1` again.
   Expected: the Help window opens, the User Guide URL is copied, and the window closes.
