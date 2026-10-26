---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TutorRoster User Guide

**TutorRoster** is a desktop student directory for private freelance tutors. It keeps student names,
contact details, subjects, education levels, tags, and additional notes together in a roster. Type short commands to
add, find, list, and delete students, undo or redo changes, add tags, and view individual student details in the
graphical interface.

Email and notes are optional.
The contact phone number can belong to the student or their guardian.
Student records are saved locally so you can return to them in later sessions.

<page-nav-print />

<br>

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
   | `add n/Ryan Tan p/+6591234567 s/Mathematics l/Secondary 4 r/Prefers worked examples` | Adds Ryan's student record. |
   | `find Ryan` | Shows students whose names contain `Ryan`. |
   | `show 1` | Opens the complete record for student 1 in the currently displayed list. |
   | `edit 1 p/+6591234567 l/Secondary 5` | Updates student 1's phone and level in the currently displayed list. |
   | `delete 1` | Removes the student numbered 1 in the currently displayed list. |
   | `undo` | Reverses the last successful roster change in this session. |
   | `redo` | Reapplies the last undone change. |
   | `help` | Opens a window containing the User Guide link. |
   | `exit` | Closes TutorRoster. |

Use the [Features](#features) section for command details. Always check the displayed name and
number before deleting a student, particularly after a search.

## Reading the interface

* **Your students:** the numbered roster. Each new student card shows the name, phone number,
  subjects, education level, email, and notes when supplied.
* **Result area:** feedback for the last command, including validation errors.
* **Command box:** where you enter commands and press Enter to execute them. A successful command
  clears the box; an invalid command stays in the box so you can correct it. Use the Up and Down
  arrow keys to recall commands from the current session. As you type, live completion suggestions
  appear below the box showing matching commands and missing parameter fields (such as `n/`,
  `p/`, `s/`, `l/` for `add`). Press **Tab** to autocomplete the top suggestion, or click a suggestion directly.
* **Student detail modal:** the `show` command opens a complete, scrollable student record above a
  dimmed roster. It includes contact details, subjects, education level, notes, address, and tags.
  Press **Esc** or select **Close** to dismiss it.
* **Status bar:** the path of the roster data file.

After a search, the roster shows matching students and numbers them from 1. A successful `add`,
`delete`, `list`, `undo`, or `redo` returns to the full roster.

## Features
<box type="info" seamless>

**Notes about command format:**

* Command words and prefixes are lowercase. For example, `add` and `n/`.
* As you type in the command box, live suggestions show available command words and remaining parameter prefixes. Press **Tab** to autocomplete.
* Words in `UPPER_CASE` are parameters to be replaced with your own values. For example, `n/NAME` becomes `n/Ryan Tan`.
* Square brackets mark optional input. For example, `n/NAME [t/Tag]` becomes `n/Ryan Tan t/online` or `n/Ryan Tan`.
* `...` means that the preceding item may be repeated. For example, `s/SUBJECT [s/SUBJECT]...`
  requires one subject and allows additional subjects.
* Fields may appear in any order. For example, `add p/81234567 n/Ryan Tan` is valid.
* Spaces around the complete command and textual values are ignored. In names, subjects, and
  education levels, consecutive internal whitespace is stored as one space. Capitalization is
  preserved for display.
* Only the subject prefix `s/` may be repeated within an add command. Unknown prefixes are rejected.
* `list`, `undo`, and `redo` accept no arguments; `show` and `delete` accepts exactly one index. `find` accepts the optional
  `--any` flag before its search keywords. Use `help` and `exit` as shown in their formats below.

</box>

If you are using a PDF version of this user guide, be careful when copying commands and ensure that they remain on one line with spaces between parameters.

### Command summary

| Action | Format | Example |
| --- | --- | --- |
| Open help | `help` | `help` |
| Add student | `add n/NAME p/PHONE [e/EMAIL] [a/ADDRESS] s/SUBJECT [s/SUBJECT]... l/LEVEL [t/TAG]... [r/NOTES]` | `add n/Ryan Tan p/81234567 a/12 Main Street s/Mathematics l/Secondary 4 r/Prefers worked examples` |
| List students | `list` | `list` |
| Find students | `find [--any] [n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...` where at least one field is required | `find n/Ryan` |
| Show student details | `show INDEX` | `show 2` |
| Delete student | `delete INDEX` | `delete 2` |
| Clear all students | `clear` | `clear` |
| Add tag | `tag INDEX TAG` | `tag 3 exam-prep` |
| Remove tag | `untag INDEX TAG` | `untag 3 exam-prep` |
| Clear a student's tags | `untag --all INDEX` | `untag --all 3` |
| Undo last change | `undo` | `undo` |
| Redo undone change | `redo` | `redo` |
| Exit | `exit` | `exit` |
---

Tip: While inside the input box, use the Up and Down arrow keys to recall commands from the current session. Press Enter to execute a recalled command.

<br>

### Viewing help: `help`

**Format:** `help`

Opens a help window containing a link to this User Guide.

<br>

### Adding a student: `add`

Adds a student and shows the complete roster, including the new student at the end.

**Format:** `add n/NAME p/PHONE [e/EMAIL] [a/ADDRESS] s/SUBJECT [s/SUBJECT]... l/LEVEL [t/TAG].. [r/NOTES]`

* `t/exam-prep` and `t/group-class` add separate tags; an empty `t/` is invalid.
* Repeated equivalent tags are stored once and produce `Warning: Duplicate tags were ignored.`

| Field | Accepted values | Examples |
| --- | --- | --- |
| `NAME` | 1–100 characters after whitespace normalization. Unicode letters and numbers, spaces, apostrophes, hyphens, and full stops; at least one letter or number. | `Ryan Tan`, `Anne-Marie Lee`, `O'Connor`, `李明` |
| `PHONE` | 3–15 digits, optionally beginning with one `+`. No spaces or hyphens. | `81234567`, `+6591234567` |
| `EMAIL` | Exactly one `@`, a non-empty local part, and a domain containing a full stop with non-empty text between full stops. No whitespace. | `ryan@example.com`, `parent.tan@example.sg` |
| `ADDRESS` | Optional non-blank text. | `12 Main Street`, `45 Orchard Road, Singapore` |
| `SUBJECT` | 1–50 characters after whitespace normalization. Letters, numbers, spaces, `&`, `+`, `/`, apostrophes, hyphens, and full stops; at least one letter or number. | `Mathematics`, `C++`, `Physics/Chemistry` |
| `LEVEL` | 1–30 characters after whitespace normalization. Letters, numbers, spaces, and hyphens; at least one letter or number. | `Secondary 4`, `JC 1`, `Grade 8`, `Year-10` |
| `NOTES` | Optional free text of 1–500 characters after trimming. Control characters are not allowed. | `Prefers worked examples; parent requests weekly updates` |

For example, `s/English  Language` is stored as `English Language`, and `l/Secondary  4` is stored
as `Secondary 4`. An empty `e/` or `r/` is invalid; omit the prefix entirely when you have no email or notes to supply.

Examples:

* `add n/Ryan Tan p/+6591234567 e/ryan@example.com s/Mathematics l/Secondary 4`
* `add n/Ryan Tan p/+6591234567 a/12 Main Street s/Mathematics l/Secondary 4`
* `add n/Alicia Lim p/81234567 s/Mathematics s/Physics l/JC 1`
* `add n/Sarah Lee p/+821012345678 s/English l/Grade 8 r/Needs visual explanations; goal is to improve essay writing`
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
under the new student duplicate rule; their original address remains in the saved file. Tags are
retained, with spelling and duplicates normalized as described under [Tag migration](#tag-migration).

#### Add errors

| Problem | Feedback |
| --- | --- |
| A required prefix is missing | `Missing required parameter: MISSING` |
| An unknown prefix, such as `x/` | `Unknown parameter prefix: x/` |
| A repeated single-use prefix, such as `n/` | `Parameter n/ must not be specified more than once` |
| A repeated subject | `Duplicate subject: SUBJECT` |
| An existing student matches the duplicate rule | `This student already exists in TutorRoster.` |
| Notes are empty, too long, or contain control characters | `Invalid notes. Notes must be 1–500 characters and must not contain control characters.` |
| Saving fails | `Unable to save student data. No changes were made.` |

Invalid fields produce a message explaining the applicable rule. For example, `p/9123-4567` produces:

``
Invalid phone number. Phone numbers must contain 3–15 digits and may optionally begin with '+'.
``

<br>

### Managing student tags: `tag` and `untag`

Categorise students with labels such as `exam-prep`, `group-class`, and `needs-follow-up`.

| Format | Result |
| --- | --- |
| `tag INDEX TAG` | Adds one tag without replacing existing tags. |
| `untag INDEX TAG` | Removes one tag. |
| `untag --all INDEX` | Removes every tag from that student. |

`INDEX` is a positive integer identifying a student in the **currently displayed list**.
For example, after `find n/Ryan`, `tag 1 exam-prep` labels the first displayed Ryan.
These commands keep the current filter. Each command targets one student; `--all` refers to that
student's tags, not all students. Extra arguments and `untag INDEX --all` are invalid.

Tag names contain 1–30 English letters or digits, optionally separated by single hyphens.
`exam-prep`, `group2`, and `2026` are valid. Spaces, underscores, other punctuation, leading or trailing
hyphens, and consecutive hyphens are invalid. Quoted multiword tags are not supported.
Tags are case-insensitive and stored in lowercase: `Exam-Prep` becomes `exam-prep`.
A student can receive up to 20 distinct tags.

Success feedback includes the student and label, such as `Tag added to Ryan Tan: exam-prep`,
`Tag removed from Ryan Tan: exam-prep`, or `All tags removed from Ryan Tan.`
Adding an existing tag, removing an absent tag, and clearing an already empty tag set succeed with
a `Warning:` message. The command box clears, and no records are changed or saved.

Malformed commands show the applicable usage. Invalid tag names show the naming requirements.
An index outside the displayed list shows `The student index provided is invalid.`
Adding a new tag at the limit shows `A student may have at most 20 tags.`
Rejected commands preserve records and the current filter. Save failures show
`Unable to save student data. No changes were made.` and also preserve records and the filter.

Successful tag changes can be undone and redone. Each changed tag command creates one undo step;
warning-only outcomes do not create a step or clear redo history. Undo and redo show the full roster.

<br>

### Listing students: `list`

Shows all stored students in their roster order. Use it to return to the full roster after a search.

**Format:** `list`

**Success feedback:** `Listed all students.` This message is also shown when the roster is empty;
there are no cards in that case. The current version does not include a student count in this message.

Additional arguments, such as `list students`, are rejected with:

```text
Error: Invalid command format.
Usage: list
```

<br>

### Finding students: `find`

Shows students matching the given fields.

**Format:** `find [--any] [n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...`

* You must specify at least one field.
* By default, every given field must match. Use `--any` to find students matching at least one given field.
* Matching is case-insensitive: `find n/RYAN` and `find n/ryan` return the same results.
* A keyword may match part of a field: `find n/Ann` can match `Anna Lee` and `Annie Tan`.
* Addresses are searchable with `a/ADDRESS`, including for student records added with an address and older records.
* Every field may be repeated. For example, `find s/Math s/English` matches students who take both Math and English.
* The order of the fields does not matter.

Examples:
* `find n/Ryan` matches `Ryan Tan` and `Ryan Lim`.
* `find n/Ryan Tan` narrows the results to names containing exactly `Ryan Tan`.
* `find n/Ryan s/Mathematics` matches students whose name contains `Ryan` AND who study Mathematics.
* `find --any n/Ryan s/Mathematics` matches students whose name contains `Ryan` OR who study Mathematics.

<br>

### Viewing a student: `show`

Opens a complete student record that shows the full details.

**Format:** `show INDEX`

* `INDEX` is a positive integer starting from 1 and refers to the **currently displayed list**.
* After `find`, use the number beside the student in the search results, not the number from the full roster.
* The modal displays every available field: name, phone, email, subjects, education level, notes, address, and tags.
* Optional fields that are not present are shown as `Not provided`.
* Press **Esc** or select **Close** to return to the roster.

| Problem | Feedback |
| --- | --- |
| Missing, zero, negative, decimal, or nonnumeric index; extra arguments | `Error: Invalid command format.` followed by `Usage: show INDEX` |
| Positive index outside the displayed list, including an empty list | `The student index provided is invalid. Choose an index from the currently displayed list.` |

<br>

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
* `find n/Ryan` followed by `delete 1` removes the first displayed match, then shows the complete
  remaining roster. If several matches appear, check their names and contact details before choosing an index.

| Problem | Feedback |
| --- | --- |
| Missing, zero, negative, decimal, or nonnumeric index; extra arguments | `Error: Invalid command format.` followed by `Usage: delete INDEX` |
| Positive index outside the displayed list, including an empty list | `The student index provided is invalid.` |

For example, `delete 0`, `delete abc`, and `delete 1 n/Ryan` are invalid. Invalid input leaves the
records and displayed list unchanged. A save failure also leaves the roster unchanged; see [Save errors](#save-errors).

### Clearing all students: `clear`

Removes every student from the roster and saves the empty roster.

**Format:** `clear`

The command shows an empty roster after it succeeds. Use `undo` to restore the removed students during the
same application session.

| Problem | Feedback |
| --- | --- |
| Extra arguments | `Error: Invalid command format.` followed by `Usage: clear` |


### Editing a student: `edit`

Edits the details of an existing student identified by their index in the currently displayed roster.
Existing values are overwritten by the input values, while omitted fields remain unchanged. For multi-value fields,
use `+` to append values and `-` to remove values.

**Format:** `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]... [+s/SUBJECT]... [-s/SUBJECT]... [l/LEVEL] [r/NOTES] [t/TAG]... [+t/TAG]... [-t/TAG]...`

* `INDEX` is a positive integer starting from 1 and refers to the **currently displayed list**.
* At least one field to edit must be provided.
* When editing subjects using `s/`, the existing subjects of the student are replaced by the newly supplied subjects. Use `+s/` to append and `-s/` to remove subjects. At least one subject must remain, and duplicate subjects are not allowed.
* Supplying `t/` replaces the complete tag set; `+t/` appends tags and `-t/` removes tags. `t/` alone clears the tags. Supplying `r/` alone clears notes.
* Supplying `e/` alone clears an existing email, and `a/` alone clears an existing address.
* `a/ADDRESS` overwrites the address for both student and legacy records. Omit it to preserve the current address.
* Single-use prefixes (`n/`, `p/`, `e/`, `l/`, `r/`) cannot be repeated within the same command.
* If the edited student matches another existing student according to TutorRoster duplicate rules, the command is rejected with `This student already exists in TutorRoster.`.
* After a successful edit, the roster resets to show the complete student list.

Examples:

* `edit 3 p/+6591234567 l/Secondary 5`
* `edit 1 n/Ryan Tan s/Mathematics s/Physics`
* `edit 2 +s/Physics -s/English +t/exam-prep -t/old-tag`
* `edit 2 r/Prefers worked examples`
* `edit 2 a/34 River Road`
* `edit 2 e/ a/`

**Success feedback:** `Edited student: NAME`, for example `Edited student: Ryan Tan`.

| Problem | Feedback |
| --- | --- |
| Missing, zero, negative, decimal, or nonnumeric index; extra preamble | `Error: Invalid command format.` followed by the usage guide |
| No fields specified | `At least one field to edit must be provided.` |
| Positive index outside the displayed list, including an empty list | `The student index provided is invalid.` |
| An existing student matches the duplicate rule | `This student already exists in TutorRoster.` |
| Repeated single-use prefix | `Multiple values specified for the following single-valued field(s): ...` |
| Repeated subject | `Duplicate subject: SUBJECT` |
| An unknown prefix, such as `x/` | `Unknown parameter prefix: x/` |
| Invalid field value | Message explaining the applicable rule (e.g. `Invalid phone number. ...`) |

<br>

### Undoing roster changes: `undo`

**Format:** `undo` (no arguments)

`undo` reverses the most recent successful roster change. Repeat it to step backward through earlier changes.

<br>

### Redoing roster changes: `redo`

**Format:** `redo` (no arguments)

`redo` reapplies the most recently undone change. Repeat it to step forward again.

The following behaviour applies to both `undo` and `redo`:

* In this version, the available roster changes are `add`, `edit`, `delete`, `clear`, `tag`, and `untag`.
  Each successful edit creates one undo step.
* Restoring a roster restores the students' complete saved details and their original order.
  Both commands show the full roster afterward, even if you were viewing search results.
* `find`, `list`, `show`, `help`, rejected commands, and tag warnings that leave records unchanged
  do not create undo steps or clear redo history.
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
| Extra arguments to undo, such as `undo 1` | `Error: Invalid command format.` followed by `Usage: undo` |
| Extra arguments to redo, such as `redo all` | `Error: Invalid command format.` followed by `Usage: redo` |

An unavailable or invalid command preserves the currently displayed list and records.

Example: after `find n/Ryan` and `delete 1`, enter `undo` to restore that student and show the full
roster. Enter `redo` to remove the same student again. Check the updated indices before your next
index-based command.

<br>

### Exiting TutorRoster: `exit`

**Format:** `exit`

Closes TutorRoster. You can also close the main window. Successfully saved student records will be
available when you reopen the app from the same folder.

<br>

## Saving and restoring student records

TutorRoster saves the roster automatically after successful roster changes, including undo and redo.
You do not need a save command. Commands that only change the displayed list do not write the roster file.

The default data file is `data/addressbook.json`, relative to the folder from which you launch
TutorRoster. Following the Quick start instructions keeps it in the `data` subfolder of the folder containing the JAR. Launch from
that same folder each time to load the same roster. The status bar shows the data file path.

### Backups and transfer

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

### Tag migration

When opening an existing roster, TutorRoster converts tags to lowercase and merges equivalent tags
on each student. If changes are needed, it saves them atomically before opening the roster and shows:

```text
Existing tags were updated to lowercase and duplicate tags were merged.
```

Existing tag names longer than 30 characters and students with more than 20 tags are retained.
You can remove any existing tag or clear the set. New additions must meet the new limits.
Unrelated student fields and legacy record formats are preserved. Missing tags mean an empty tag set.
Files needing no migration are not rewritten and produce no migration notice.

If the migration cannot be saved, an error dialog appears and startup stops. The original roster
file is left untouched. Resolve the file/folder write problem before restarting.
Older app versions may reject files containing hyphenated tags.

### Save errors

If an add or tag change cannot be saved, it shows `Unable to save student data. No changes were made.`
The roster and current displayed list are preserved.

For other commands, a save error currently begins with `Could not save data` and includes details of
the problem. Check that the launch folder and data file can be written to and that storage is available.

If saving an add, delete, undo, or redo fails, no roster change is published. The displayed list,
stored records, and undo/redo history remain unchanged. Resolve the save problem before retrying.

### Trouble loading an existing roster

Except for a failed tag migration (which stops startup), if the existing data file cannot be loaded,
the current version opens with an empty roster. It does
not yet show a dedicated load-error message in the result area. Subsequent successful roster changes may
overwrite the existing data file with the current roster.

If your saved roster unexpectedly appears empty, close the main window and make a copy of
`data/addressbook.json` before entering more commands. Restore a readable backup before reopening.

<br>

## Undo and redo examples

These examples assume a new session with three saved students in this order:
`1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`. Use a test copy of your roster when trying the
whole sequence. Each row continues from the preceding row; the second example continues from the first.

### Recovering a filtered deletion

You have searched for Ryan and accidentally removed the displayed match. Undo restores Ryan's
complete record in its original position, then shows all students. Redo removes Ryan again;
it does not reinterpret the old displayed index against the full roster.

| Command | Result-area feedback | Displayed roster |
| --- | --- | --- |
| `find n/Ryan` | `1 person(s) listed!` | `1. Ryan Tan` |
| `delete 1` | `Student removed: Ryan Tan` | `1. Alicia Lim`, `2. Sarah Lee` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `find n/Nobody` | `0 person(s) listed!` | Empty search results |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Sarah Lee` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |

The search commands do not become undo steps. After the final row, the deletion can still be redone.

### Making a new change after undo

Continue from the preceding example. Adding Jamie creates a new history branch and discards the
undone deletion. The next `redo` therefore reports that no change can be redone. Undoing the add
removes Jamie; redoing it restores Jamie's record at the end of the roster.

| Command | Result-area feedback | Displayed roster |
| --- | --- | --- |
| `add n/Jamie Koh p/81234567 s/Physics l/JC 1` | `New student added: Jamie Koh` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `redo` | `There are no changes to redo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |

A rejected add, invalid index, or malformed command would leave the previous redo history available.
Closing and reopening TutorRoster retains the last saved roster, but starts a new, empty undo/redo history.

### Reversing a delete-and-add replacement

When you update a student by deleting the old record and adding a corrected one, those are two
separate changes. The first `undo` removes the replacement. A second `undo` restores the original
record, including its old details and roster position. Two `redo` commands reapply the deletion and
then the replacement. If you only want to remove the replacement temporarily, use one `undo`.

<br>

## FAQ

**How do I update a student?**

Use the `edit` command to update the student's details, e.g. `edit 3 p/+6591234567 l/Secondary 5`. See
[Editing a student: edit](#editing-a-student-edit).

**Why does `delete 1` remove a different student after a search?**

The index belongs to the list currently on screen. Search results start from 1; use the displayed
student's name and contact details to choose the correct number.

**How do I search for several possible names?**

This version accepts one name filter per command. To search another name, run a separate
`find n/NAME` command. `--any` is useful for searching across fields, such as
`find --any n/Ryan s/Mathematics`.

**Why are sample students shown on first use?**

The current version starts with sample records when no data file exists. You can delete individual
sample students using their displayed indices.

**How do I transfer or restore my roster?**

Copy the saved `data/addressbook.json` while the app is closed. See [Backups and transfer](#backups-and-transfer).

<br>

## Known issues

1. If you move the app to a second screen and later disconnect that screen, it may reopen off-screen.
   Close TutorRoster and remove `preferences.json` from the launch folder, then reopen the app.
   This resets window preferences; it does not remove the roster data file.
1. Protection of unreadable data files is incomplete. Read
   [Trouble loading an existing roster](#trouble-loading-an-existing-roster) before changing a roster
   that unexpectedly opens empty.

<br>

## Keyboard shortcuts

| Action | Shortcut | Description |
| --- | --- | --- |
| Focus command input | `/` | When focus is outside the command box, focuses it with the cursor at the end without entering `/`. |
| Autocomplete suggestion | `Tab` | Autocompletes the top matching command word or parameter prefix into the command box. |
| Return to the roster | `Esc` | Dismisses completion suggestions if open, closes the student detail modal if open, or moves focus to the student list when the command box has focus. |
| Previous command | Up arrow | When the command box has focus, recalls an earlier command entered during this session. |
| Next command | Down arrow | Recalls a later command. Moving past the newest command restores the unfinished draft. |
| Toggle Help window | `F1` | Opens the Help window when closed, or closes it when open. |

Command history is kept for the current session. Recalling a command does not execute it; press Enter
when you are ready. Check any displayed index before repeating a delete command because the roster
may have changed since the command was first entered.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add student | `add n/NAME p/PHONE [e/EMAIL] [a/ADDRESS] s/SUBJECT [s/SUBJECT]... l/LEVEL [t/TAG]... [r/NOTES]` | `add n/Ryan Tan p/81234567 a/12 Main Street s/Mathematics l/Secondary 4 r/Prefers worked examples` |
| List students | `list` | `list` |
| Find students | `find [--any] [n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...` | `find n/Ryan` or `find --any n/Ryan s/Physics` |
| Show student details | `show INDEX` | `show 2` |
| Edit student | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]... [+s/SUBJECT]... [-s/SUBJECT]... [l/LEVEL] [r/NOTES] [t/TAG]... [+t/TAG]... [-t/TAG]...` | `edit 3 p/+6591234567 l/Secondary 5` |
| Delete student | `delete INDEX` | `delete 2` |
| Undo last change | `undo` | `undo` |
| Redo undone change | `redo` | `redo` |
| Add a tag | `tag INDEX TAG` | `tag 2 exam-prep` |
| Remove a tag | `untag INDEX TAG` | `untag 2 exam-prep` |
| Clear tags | `untag --all INDEX` | `untag --all 2` |
| Open help | `help` | `help` |
| Exit | `exit` | `exit` |
