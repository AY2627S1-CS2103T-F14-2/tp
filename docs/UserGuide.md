---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TutorRoster User Guide

**TutorRoster** is a desktop student directory for private freelance tutors. It keeps student names,
contact details, subjects, and education levels together in a roster. Type short commands to add,
find, list, and delete students, undo or redo changes, and read results in the graphical interface.

The contact phone number can belong to the student or a parent or guardian. Email is optional.
Student records are saved locally so you can return to them in later sessions.

<page-nav-print />

## Quick start

1. Install Java **25 or later**. On macOS, follow the
   [Java installation guide for Mac users](https://se-education.org/guides/tutorials/javaInstallationMac.html),
   which specifies a Java distribution with JavaFX support.
1. Download the runnable JAR from the
   [TutorRoster releases page](https://github.com/AY2627S1-CS2103T-F14-2/tp/releases).
1. Place the JAR in a dedicated folder where you have permission to create and update files.
   This folder will contain your roster and preferences.
1. Open a terminal in that folder and run the following command. The current build's filename is
   `TutorRoster.jar`; replace it in the command if the downloaded file has a different name.

   ```sh
   java -jar TutorRoster.jar
   ```

1. The TutorRoster window opens. On first use, when no saved roster exists, the current version
   shows sample students. Existing saved records are loaded on subsequent launches.
1. Click the command box, type a command, and press **Enter**. You can then keep typing commands
   without selecting buttons. Press `/` to return focus to the command box from the roster.

   | Command | What it does |
   | --- | --- |
   | `list` | Shows the complete roster. |
   | `add n/Ryan Tan p/+6591234567 s/Mathematics l/Secondary 4` | Adds Ryan's student record. |
   | `find Ryan` | Shows students whose names contain `Ryan`. |
   | `delete 1` | Removes the student numbered 1 in the currently displayed list. |
   | `undo` | Reverses the last successful roster change in this session. |
   | `redo` | Reapplies the last undone change. |
   | `help` | Opens a window containing the User Guide link. |
   | `exit` | Closes TutorRoster. |

Use the [Features](#features) section for command details. Always check the displayed name and
number before deleting a student, particularly after a search.

## Reading the interface

* **Your students:** the numbered roster. Each new student card shows the name, phone number,
  subjects, education level, and email when supplied.
* **Result area:** feedback for the last command, including validation errors.
* **Command box:** where you enter commands and press Enter to execute them. A successful command
  clears the box; an invalid command stays in the box so you can correct it. Use the Up and Down
  arrow keys to recall commands from the current session.
* **Status bar:** the path of the roster data file.

After a search, the roster shows matching students and numbers them from 1. A successful `add`,
`delete`, `list`, `undo`, or `redo` returns to the full roster.

## Features

### Command format

* Command words and prefixes are lowercase: use `add` and `n/`, for example.
* Replace `UPPER_CASE` placeholders with your own values. `n/NAME` can become `n/Ryan Tan`.
* Square brackets mark optional input. Omit `[e/EMAIL]` when you do not want to store an email.
  Do not type the brackets.
* `...` means the preceding item can be repeated. In `s/SUBJECT [s/SUBJECT]...`, the first subject
  is required and additional subjects are optional. Do not type the ellipsis.
* Add parameters may appear in any order. For example, `p/81234567 n/Ryan Tan` is acceptable as
  part of a complete add command.
* Spaces around the complete command and textual values are ignored. In names, subjects, and
  education levels, consecutive internal whitespace is stored as one space. Capitalization is
  preserved for display.
* Only the subject prefix `s/` may be repeated within an add command. Unknown prefixes are rejected.
* `list`, `undo`, and `redo` accept no arguments; `delete` accepts exactly one index. `find` accepts the optional
  `--any` flag before its search keywords. Use `help` and `exit` as shown in their formats below.

If you copy a command from a PDF, check that it remains on one line with spaces between parameters.

### Adding a student: `add`

Adds a student and shows the complete roster, including the new student at the end.

**Format:** `add n/NAME p/PHONE [e/EMAIL] s/SUBJECT [s/SUBJECT]... l/LEVEL`

Required fields are name, phone number, at least one subject, and education level. Email is optional.

| Field | Accepted values | Examples |
| --- | --- | --- |
| `NAME` | 1–100 characters after whitespace normalization. Unicode letters and numbers, spaces, apostrophes, hyphens, and full stops; at least one letter or number. | `Ryan Tan`, `Anne-Marie Lee`, `O'Connor`, `李明` |
| `PHONE` | 3–15 digits, optionally beginning with one `+`. No spaces or hyphens. | `81234567`, `+6591234567` |
| `EMAIL` | Exactly one `@`, a non-empty local part, and a domain containing a full stop with non-empty text between full stops. No whitespace. | `ryan@example.com`, `parent.tan@example.sg` |
| `SUBJECT` | 1–50 characters after whitespace normalization. Letters, numbers, spaces, `&`, `+`, `/`, apostrophes, hyphens, and full stops; at least one letter or number. | `Mathematics`, `C++`, `Physics/Chemistry` |
| `LEVEL` | 1–30 characters after whitespace normalization. Letters, numbers, spaces, and hyphens; at least one letter or number. | `Secondary 4`, `JC 1`, `Grade 8`, `Year-10` |

For example, `s/English  Language` is stored as `English Language`, and `l/Secondary  4` is stored
as `Secondary 4`. An empty `e/` is invalid; omit the prefix entirely when you have no email to supply.

Examples:

* `add n/Ryan Tan p/+6591234567 e/ryan@example.com s/Mathematics l/Secondary 4`
* `add n/Alicia Lim p/81234567 s/Mathematics s/Physics l/JC 1`
* `add l/Grade 8 s/English n/Sarah Lee p/+821012345678`

**Success feedback:** `New student added: NAME`, for example `New student added: Ryan Tan`.

#### Duplicate subjects and students

The same subject cannot appear twice in one add command. Comparison ignores case and differences
in whitespace: `s/English Language s/English  Language` produces `Duplicate subject: English Language`.

A student is considered a duplicate when the normalized name matches an existing student and either:

* the phone number matches, ignoring an initial `+`; or
* both records have an email and the email matches, ignoring case.

Name comparison also ignores case and surrounding or repeated whitespace. Subjects and education
levels do not distinguish duplicate students. The duplicate is rejected with
`This student already exists in TutorRoster.`

Students with different names can share a guardian's phone number or email. Students with the same
name can be added when their contact details differ.

If you are upgrading from AB3, older records are retained. Those older records do not block additions
under the new student duplicate rule; their original address and tags remain in the saved file.

#### Add errors

| Problem | Feedback |
| --- | --- |
| A required prefix is missing | `Missing required parameter: MISSING` |
| An unknown prefix, such as `x/` | `Unknown parameter prefix: x/` |
| A repeated single-use prefix, such as `n/` | `Parameter n/ must not be specified more than once` |
| A repeated subject | `Duplicate subject: SUBJECT` |
| An existing student matches the duplicate rule | `This student already exists in TutorRoster.` |
| Saving fails | `Unable to save student data. No changes were made.` |

Invalid fields produce a message explaining the applicable rule. For example, `p/9123-4567` produces:

```text
Invalid phone number. Phone numbers must contain 3–15 digits and may optionally begin with '+'.
```

Rejected add commands leave the records and currently displayed list unchanged, including when saving fails.

### Listing students: `list`

Shows all stored students in their roster order. Use it to return to the full roster after a search.

**Format:** `list`

**Success feedback:** `Listed all students.` This message is also shown when the roster is empty;
there are no cards in that case. The current version does not include a student count in this message.

Additional arguments, such as `list students`, are rejected with:

```text
Invalid command format!
Usage: list
```

The displayed list remains unchanged when the arguments are invalid.

### Finding students by name: `find`

Shows students whose names contain **all** supplied keywords by default. Use `--any` to show
students whose names contain **at least one** supplied keyword.

**Format:** `find [--any] KEYWORD [MORE_KEYWORDS]`

* Matching is case-insensitive: `find RYAN` and `find ryan` return the same results.
* A keyword may match part of a name: `find ann` can match `Anna Lee` and `Joanne Tan`.
* Only names are searched. Phone numbers, emails, subjects, and levels are not searched.
* Multiple spaces between keywords act as one separator. Keyword order does not matter.
* By default, every keyword must match. `find Ryan Tan` can match `Ryan Wei Tan`, but does not
  match `Ryan Lim` or `Sarah Tan`.
* Place `--any` immediately after `find` for an OR search. `find --any Ryan Sarah` matches names
  containing either `Ryan` or `Sarah`.
* Matching students keep their original roster order and receive new displayed indices starting at 1.

Examples:

* `find Ryan` matches `Ryan Tan` and `Ryan Lim`.
* `find ali` matches `Alicia Lim`.
* `find Ryan Tan` narrows the results to names containing both `Ryan` and `Tan`.
* `find --any Ryan Sarah` matches students whose names contain either `Ryan` or `Sarah`.

The result area currently uses the feedback `N person(s) listed!`. For example, one match shows
`1 person(s) listed!`. No matches shows `0 person(s) listed!` and an empty displayed list; the records
are still stored. Enter `list` to show them again.

A bare `find` or `find --any` is invalid and shows usage instructions because at least one keyword
is required. The current displayed list is preserved.

### Deleting a student: `delete`

Removes a student from the active roster and shows the complete remaining roster.

**Format:** `delete INDEX`

* `INDEX` is a positive integer starting from 1 and refers to the **currently displayed list**.
* After `find`, use the number beside the student in the search results, even if the student had a
  different number in the full roster.
* Successful deletion preserves the order of the remaining students and updates their indices.
* Deleting the last student leaves an empty roster.
* Deletion takes effect immediately without a confirmation dialog. Use `undo` to recover it in the same session.

**Success feedback:** `Student removed: NAME`, for example `Student removed: Ryan Tan`.

Examples:

* `list` followed by `delete 2` removes the second student in the complete roster.
* `find Ryan` followed by `delete 1` removes the first displayed match, then shows the complete
  remaining roster. If several matches appear, check their names and contact details before choosing an index.

| Problem | Feedback |
| --- | --- |
| Missing, zero, negative, decimal, or nonnumeric index; extra arguments | `Invalid command format. Usage: delete INDEX` |
| Positive index outside the displayed list, including an empty list | `The student index provided is invalid.` |

For example, `delete 0`, `delete abc`, and `delete 1 n/Ryan` are invalid. Invalid input leaves the
records and displayed list unchanged. A save failure also leaves the roster unchanged; see [Save errors](#save-errors).

#### Updating student information

Use delete and add to replace an outdated record:

1. Find the student and check the displayed index.
1. Delete that record and check for successful deletion feedback.
1. Add the student again with the corrected details, supplying every required field.

The replacement appears at the end of the full roster. Direct `edit` and bulk `clear` commands are
not available in the current version.

### Undoing and redoing roster changes: `undo` and `redo`

**Formats:** `undo` and `redo` (no arguments)

`undo` reverses the most recent successful roster change. Repeat it to step backward through earlier
changes. `redo` reapplies the most recently undone change; repeat it to step forward again.

* In this version, the available roster changes are `add` and `delete`. Replacing a student using
  delete and add creates two separate undo steps.
* Restoring a roster restores the students' complete saved details and their original order.
  Both commands show the full roster afterward, even if you were viewing search results.
* `find`, `list`, `help`, and rejected commands do not create undo steps or clear redo history.
* After undoing, making a new successful roster change clears the remaining redo history.
* History lasts only for the current application session. Restarting retains the saved roster,
  but starts with no changes to undo or redo. Window settings and command recall are not undone.
* Each successful undo or redo is saved automatically. If saving fails, the roster, displayed
  filter, and undo/redo history remain unchanged, so you can retry after fixing the save problem.

| Command outcome | Feedback |
| --- | --- |
| Successful undo | `Undo successful.` |
| Successful redo | `Redo successful.` |
| No earlier change | `There are no changes to undo.` |
| No undone change | `There are no changes to redo.` |
| Extra arguments to undo, such as `undo 1` | `Invalid command format. Usage: undo` |
| Extra arguments to redo, such as `redo all` | `Invalid command format. Usage: redo` |

An unavailable or invalid command preserves the currently displayed list and records.

Example: after `find Ryan` and `delete 1`, enter `undo` to restore that student and show the full
roster. Enter `redo` to remove the same student again. Check the updated indices before your next
index-based command.

### Viewing help: `help`

**Format:** `help`

Opens a help window containing the User Guide link. Use its copy button to copy the link and open
it in a browser. If the Help window is already open, `help` closes it. The Help menu and **F1** key
also toggle the window; F1 can close it while the Help window has focus.

**Feedback:** `Opened help window.` The current version uses this message for the command even
when it closes an already open Help window.

### Exiting TutorRoster: `exit`

**Format:** `exit`

Closes TutorRoster. You can also close the main window. Successfully saved student records will be
available when you reopen the app from the same folder.

### Saving and restoring student records

TutorRoster saves the roster automatically after successful roster changes, including undo and redo.
You do not need a save command. Commands that only change the displayed list do not write the roster file.

The default data file is `data/addressbook.json`, relative to the folder from which you launch
TutorRoster. Following the Quick start instructions keeps it in the `data` subfolder of the folder containing the JAR. Launch from
that same folder each time to load the same roster. The status bar shows the data file path.

#### Backups and transfer

To make a backup, close TutorRoster and copy `data/addressbook.json` to a separate location.
Keep the original filename when restoring it.

To move your roster to another computer:

1. Install Java and download TutorRoster on the other computer.
1. Close TutorRoster on both computers.
1. Create a `data` folder inside the new TutorRoster launch folder, if it does not already exist.
1. Copy your backed-up `addressbook.json` into that folder. Replacing an existing file replaces that
   computer's roster, so back it up first if you want to keep it.
1. Start TutorRoster from the new launch folder.

Use commands to maintain student records. Editing the JSON file directly can make the roster unreadable.

#### Save errors

If an add cannot be saved, it shows `Unable to save student data. No changes were made.` The student
is not added, and the current displayed list is preserved.

For other commands, a save error currently begins with `Could not save data` and includes details of
the problem. Check that the launch folder and data file can be written to and that storage is available.

If saving an add, delete, undo, or redo fails, no roster change is published. The displayed list,
stored records, and undo/redo history remain unchanged. Resolve the save problem before retrying.

#### Trouble loading an existing roster

If the existing data file cannot be loaded, the current version opens with an empty roster. It does
not yet show a dedicated load-error message in the result area. Subsequent successful roster changes may
overwrite the existing data file with the current roster.

If your saved roster unexpectedly appears empty, close the main window and make a copy of
`data/addressbook.json` before entering more commands. Restore a readable backup before reopening.

## FAQ

**How do I update a student?**

Delete the outdated record and add the corrected record. See
[Updating student information](#updating-student-information).

**Why does `delete 1` remove a different student after a search?**

The index belongs to the list currently on screen. Search results start from 1; use the displayed
student's name and contact details to choose the correct number.

**How do I search for several possible names?**

Use `find --any Ryan Sarah` to match either name. Without `--any`, every keyword must appear in
the same student's name, which is useful for narrowing a search.

**Why are sample students shown on first use?**

The current version starts with sample records when no data file exists. You can delete individual
sample students using their displayed indices.

**How do I transfer or restore my roster?**

Copy the saved `data/addressbook.json` while the app is closed. See [Backups and transfer](#backups-and-transfer).

## Known issues

1. If you move the app to a second screen and later disconnect that screen, it may reopen off-screen.
   Close TutorRoster and remove `preferences.json` from the launch folder, then reopen the app.
   This resets window preferences; it does not remove the roster data file.
1. Protection of unreadable data files is incomplete. Read
   [Trouble loading an existing roster](#trouble-loading-an-existing-roster) before changing a roster
   that unexpectedly opens empty.

## Keyboard shortcuts

| Action | Shortcut | Description |
| --- | --- | --- |
| Focus command input | `/` | When focus is outside the command box, focuses it with the cursor at the end without entering `/`. |
| Return to the roster | `Esc` | When the command box has focus, moves focus to the student list. |
| Previous command | Up arrow | When the command box has focus, recalls an earlier command entered during this session. |
| Next command | Down arrow | Recalls a later command. Moving past the newest command restores the unfinished draft. |
| Toggle Help window | `F1` | Opens the Help window when closed, or closes it when open. |

Command history is kept for the current session. Recalling a command does not execute it; press Enter
when you are ready. Check any displayed index before repeating a delete command because the roster
may have changed since the command was first entered.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add student | `add n/NAME p/PHONE s/SUBJECT l/LEVEL [e/EMAIL] [MORE_SUBJECT]` | `add n/Ryan Tan p/81234567 s/Mathematics l/Secondary 4` |
| List students | `list` | `list` |
| Find by name | `find [--any] KEYWORD [MORE_KEYWORDS]` | `find Ryan Tan` or `find --any Ryan Sarah` |
| Delete student | `delete INDEX` | `delete 2` |
| Undo last change | `undo` | `undo` |
| Redo undone change | `redo` | `redo` |
| Open help | `help` | `help` |
| Exit | `exit` | `exit` |
