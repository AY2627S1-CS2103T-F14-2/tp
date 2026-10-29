# TutorRoster User Guide

TutorRoster is a desktop student directory for private freelance tutors. It keeps each student's
contact details, subjects, education level, optional address, tutoring notes, and tags in one local
roster. The graphical interface displays the roster, while short keyboard commands do the work.

TutorRoster is useful when you manage several students and want to update or find records faster
than in a mouse-driven contact application. No Internet connection is required for core features.

<a href="images/Ui.png">
  <img src="images/Ui.png" alt="TutorRoster main window" width="700" style="max-width: 100%; height: auto;">
</a>

*The main window displays student cards, command feedback, the command box, and the local data-file path.*

<page-nav-print />

## Quick start

1. Install Java **25 or later**. On macOS, use a Java distribution that includes JavaFX.
1. Download the runnable JAR from the
   [TutorRoster releases page](https://github.com/AY2627S1-CS2103T-F14-2/tp/releases).
1. Put the JAR in a folder where you can create and update files. TutorRoster needs this
   permission for `data/addressbook.json` and `preferences.json`.
1. Open a terminal in that folder and run:

   ```sh
   java -jar TutorRoster.jar
   ```

   Replace `TutorRoster.jar` if the downloaded file has a different name. Double-clicking the JAR
   may not work on every operating system; use the terminal command when in doubt.
1. The TutorRoster window opens. If no saved roster exists, sample students are displayed. A saved
   roster is loaded on later launches from the same folder.
1. Click the command box, type a command, and press **Enter** or **Run command**. For example:

   ```text
   add n/Ryan Tan p/+6591234567 s/Mathematics l/Secondary 4 r/Prefers worked examples
   ```

   The command box is the primary input method, but buttons and menus remain available for running
   a command or opening help.

## Reading the interface

* **Your students**: the numbered, scrollable roster. Each card shows the name, phone, subjects,
  education level, and any supplied email, notes, and tags. The address is shown in the full record.
* **Result area**: feedback from the most recent command, including validation errors and save errors.
* **Command box**: type commands here. A successful command clears the box; a rejected command
  remains so that you can correct it.
* **Suggestions**: while typing, TutorRoster suggests matching commands and parameter prefixes.
  Press **Tab** to insert the top suggestion or click a suggestion.
* **Student detail modal**: `show INDEX` opens a scrollable complete record above the dimmed roster.
  It shows phone, email, subjects, education level, notes, address, and tags. Missing optional
  values are shown as `Not provided`.
* **Status bar**: shows the path of the roster data file.

The index always belongs to the list currently displayed. A search starts its result numbering at 1.
Commands that change the roster, `list`, `undo`, and `redo` return to the complete roster; `tag` and
`untag` keep the current search filter.

### Keyboard shortcuts

| Action | Shortcut | Behaviour |
| --- | --- | --- |
| Focus command box | `/` | Focuses the command box when another part of the app has focus; the slash is not entered. |
| Autocomplete | `Tab` | Inserts the top matching command or parameter suggestion. |
| Dismiss or move focus | `Esc` | Dismisses suggestions, closes the student modal, or moves focus from the command box to the roster. |
| Previous command | Up arrow | Recalls an earlier command while the command box is focused. |
| Next command | Down arrow | Recalls a later command while the command box is focused. |
| Open or close help | `F1` | Toggles the Help window. |

Command history lasts for the current application session. Consecutive duplicate commands are stored
only once, and an unfinished command is restored when you navigate past the newest history entry.

## Command format

* Command words and parameter prefixes are lowercase, for example `add` and `n/`.
* Replace capitalised placeholders such as `NAME` with your own value.
* Square brackets mark optional input. An ellipsis means that the preceding item may be repeated.
* Parameters may appear in any order unless a command says otherwise.
* Surrounding whitespace is ignored. Names, subjects, and education levels also collapse repeated
  internal whitespace to one space; their entered capitalisation is preserved.
* Values are separated by recognised prefixes. Quoted values are not supported.
* Do not use slash-based abbreviations such as `w/o` inside free-text notes: `/o` can be read as a
  parameter-shaped token. Write `without` instead.
* `INDEX` is a positive integer in the list currently displayed. After `find`, use the index shown in
  the search results, not the student's old index in the full roster.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Open help | `help` | `help` |
| Add student | `add n/NAME p/PHONE s/SUBJECT [s/SUBJECT]... l/LEVEL [e/EMAIL] [a/ADDRESS] [r/NOTES] [t/TAG]...` | `add n/Ryan Tan p/81234567 s/Mathematics l/Secondary 4` |
| Edit student | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]... [+s/SUBJECT]... [-s/SUBJECT]... [l/LEVEL] [r/NOTES] [t/TAG]... [+t/TAG]... [-t/TAG]...` | `edit 1 p/+6591234567 l/Secondary 5` |
| List students | `list` | `list` |
| Find students | `find [--any] [n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...` | `find n/Ryan` |
| Show details | `show INDEX` | `show 1` |
| Delete student | `delete INDEX` | `delete 1` |
| Clear roster | `clear` | `clear` |
| Add tag | `tag INDEX TAG` | `tag 1 exam-prep` |
| Remove tag | `untag INDEX TAG` | `untag 1 exam-prep` |
| Clear one student's tags | `untag --all INDEX` | `untag --all 1` |
| Undo change | `undo` | `undo` |
| Redo change | `redo` | `redo` |
| Exit | `exit` | `exit` |

## Features

### Open help: `help`

**Format:** `help`

Opens a Help window containing the link to this User Guide and a **Copy URL** button. The Help
window can also be toggled with `F1`. Pressing `F1` while the Help window is open closes it.

`help` accepts no arguments. For example, `help details` is rejected with an invalid-command error
and the usage message.

### Add a student: `add`

Adds a student to the end of the roster and shows the complete roster.

**Format:** `add n/NAME p/PHONE s/SUBJECT [s/SUBJECT]... l/LEVEL [e/EMAIL] [a/ADDRESS] [r/NOTES] [t/TAG]...`

| Field | Rules | Example |
| --- | --- | --- |
| `NAME` | 1–100 characters after whitespace normalisation; must contain a letter or number. Letters, numbers, spaces, apostrophes, hyphens, and full stops are allowed. | `Anne-Marie Lee` |
| `PHONE` | 3–15 digits, optionally preceded by `+`. Do not use spaces or hyphens. | `+6591234567` |
| `EMAIL` | Optional. Contains exactly one `@`, a non-empty local part, and a dot-separated non-empty domain. No whitespace. | `ryan@example.com` |
| `ADDRESS` | Optional non-blank text. | `12 Main Street` |
| `SUBJECT` | 1–50 characters after whitespace normalisation; must contain a letter or number. Letters, numbers, spaces, `&`, `+`, `/`, apostrophes, hyphens, and full stops are allowed. | `Physics/Chemistry` |
| `LEVEL` | 1–30 characters after whitespace normalisation; letters, numbers, spaces, and hyphens only. | `Secondary 4` |
| `NOTES` | Optional trimmed free text of 1–500 characters. Control characters are not allowed. | `Prefers worked examples` |
| `TAG` | Optional. Use one `t/` for each tag; each tag is 1–30 English letters or digits with optional single hyphens. | `exam-prep` |

The required fields are `n/`, `p/`, at least one `s/`, and `l/`. The prefixes `s/` and `t/` may be
repeated to supply multiple subjects and tags. The prefixes `n/`, `p/`, `e/`, `a/`, `l/`, and `r/`
can each be supplied only once. Fields can be supplied in any order.

Examples:

```text
add n/Ryan Tan p/+6591234567 e/ryan@example.com s/Mathematics l/Secondary 4
add n/Alicia Lim p/81234567 a/12 Main Street s/Mathematics s/Physics l/JC 1
add n/Sarah Lee p/+821012345678 s/English l/Grade 8 t/exam-prep t/needs-follow-up r/Needs visual explanations
```

Tags are case-insensitive and stored in lowercase. Duplicate equivalent tags are stored once and
produce the warning `Warning: Duplicate tags were ignored.` A student can have at most 20 distinct
new tags.

**Success feedback:** `New student added: NAME`, for example `New student added: Ryan Tan`.

#### Duplicate students and subjects

Two students are duplicates when their normalised names match and either their phone numbers match
(ignoring an initial `+`) or both email addresses are present and match without regard to case.
Different names may share a phone number or email address. Subject and education-level values do not
affect duplicate detection.

The same subject cannot be repeated in one `add` command. Subject comparison ignores case and repeated
whitespace, so `s/English Language s/English  Language` is rejected as a duplicate subject.

#### Common add errors

The result area identifies missing fields, unknown prefixes, repeated single-value prefixes, duplicate
subjects, invalid field values, duplicate students, and save failures. For example, `p/9123-4567` is
rejected because phone numbers cannot contain hyphens.

### Edit a student: `edit`

Edits the student at `INDEX` in the currently displayed list. At least one field must be supplied.
After a successful edit, the complete roster is displayed.

**Format:**

```text
edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]
     [s/SUBJECT]... [+s/SUBJECT]... [-s/SUBJECT]...
     [l/LEVEL] [r/NOTES]
     [t/TAG]... [+t/TAG]... [-t/TAG]...
```

* Supplying `n/`, `p/`, `e/`, `a/`, `l/`, or `r/` replaces that field; omitting it preserves the
  existing value. These prefixes are single-use in one command.
* `e/` with no value clears the email. `a/` with no value clears the address. `r/` with no value
  clears the notes.
* Supplying one or more `s/` values replaces all subjects. `+s/` appends and `-s/` removes subjects.
  At least one subject must remain, and equivalent subjects cannot be repeated.
* Supplying one or more `t/` values replaces all tags. `+t/` appends and `-t/` removes tags. `t/`
  with no value clears all tags.
* The edited record must not become a duplicate of another record. A rejected edit changes nothing.

Examples:

```text
edit 1 p/+6591234567 l/Secondary 5
edit 1 n/Ryan Tan s/Mathematics s/Physics
edit 1 +s/Physics -s/English +t/exam-prep -t/old-tag
edit 1 e/ a/
edit 1 r/Prefers worked examples
```

**Success feedback:** `Edited student: NAME`.

### List students: `list`

**Format:** `list`

Displays every stored student in roster order and clears the current search filter.

**Success feedback:** `Listed all students.` The same message is shown for an empty roster.
`list` accepts no arguments.

### Find students: `find`

**Format:** `find [--any] [n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...`

Supply at least one non-empty field. Searches are case-insensitive and match part of a field:

* `n/` searches names.
* `p/` searches phone numbers, ignoring an optional leading `+` in both the query and stored number.
* `e/` searches email addresses.
* `a/` searches addresses.
* `s/` searches subjects.
* `l/` searches education levels.
* `t/` searches tags.

By default, every supplied search term must match the same student. Put `--any` immediately after
`find` to match a student that satisfies at least one supplied term. Every field can be repeated.

Examples:

```text
find n/Ryan
find n/Ryan s/Mathematics
find --any n/Ryan n/Sarah
find --any t/exam-prep t/group-class
```

The result area reports the number of matches as `N person(s) listed!`. A search with no matches
leaves an empty displayed list; it does not change stored records or undo history.

### Show a student's details: `show`

**Format:** `show INDEX`

Opens the complete record for the student at `INDEX` in the currently displayed list. The modal shows
the name, phone, email, subjects, education level, notes, address, and tags. Optional values that are
not present show as `Not provided`. Close the modal with **Close** or `Esc`.

**Success feedback:** `Showing student details.` `show` does not change the roster or search filter.

### Delete a student: `delete`

**Format:** `delete INDEX`

Deletes the student at `INDEX` in the currently displayed list, then displays the complete remaining
roster. There is no confirmation dialog. Check the name and contact details before submitting an
index, especially after a search.

**Success feedback:** `Student removed: NAME (phone: PHONE)`.

An invalid or out-of-range index leaves the roster and displayed list unchanged. Use `undo` to recover
a successful deletion during the same application session.

### Clear the roster: `clear`

**Format:** `clear`

Removes every student and displays an empty roster.

**Success feedback:** `Address book has been cleared!`

There is no confirmation dialog. Use `undo` during the same session to restore the cleared roster.

### Manage tags: `tag` and `untag`

Tags are short labels for grouping students, such as `exam-prep` or `needs-follow-up`.

| Format | Effect |
| --- | --- |
| `tag INDEX TAG` | Adds one tag without replacing existing tags. |
| `untag INDEX TAG` | Removes one tag. |
| `untag --all INDEX` | Removes every tag from that student. |

Tag names are case-insensitive and stored in lowercase. New names contain 1–30 English letters or
digits, with optional single hyphens between non-empty parts. A student can have at most 20 distinct
new tags. Each command targets one student in the currently displayed list and keeps the current
search filter.

Successful feedback includes the student and tag, for example `Tag added to Ryan Tan: exam-prep`,
`Tag removed from Ryan Tan: exam-prep`, or `All tags removed from Ryan Tan.`

Adding an existing tag, removing an absent tag, or clearing an already empty tag set is a successful
warning-only operation. It shows a `Warning:` message, does not save anything, and does not create an
undo step.

### Undo and redo: `undo` and `redo`

**Formats:** `undo` and `redo` (no arguments)

`undo` reverses the most recent successful roster change. `redo` reapplies the most recently undone
change. In the current version, the changes tracked are `add`, `edit`, `delete`, `clear`, `tag`, and
`untag` when they actually change a record.

* Undo and redo restore complete student records and their original roster order.
* A successful undo or redo displays the complete roster, even if a search was active.
* `find`, `list`, `show`, `help`, rejected commands, and warning-only tag outcomes do not create
  history entries.
* After an undo, a new successful roster change discards the remaining redo branch.
* History lasts only for the current session. Restarting keeps the saved roster but starts with no
  changes available to undo or redo.

| Situation | Feedback |
| --- | --- |
| Successful undo | `Undo successful.` |
| Successful redo | `Redo successful.` |
| Nothing to undo | `There are no changes to undo.` |
| Nothing to redo | `There are no changes to redo.` |

### Exit: `exit`

**Format:** `exit`

Closes TutorRoster. You can also close the main window. Successfully saved records remain available
when TutorRoster is reopened from the same launch folder.

## Saving and restoring records

TutorRoster saves automatically after every successful roster change, including undo and redo. Search,
list, show, help, and failed commands do not write the roster file.

The files are relative to the folder from which TutorRoster is launched:

* `data/addressbook.json` stores the student roster.
* `preferences.json` stores window size and position.

The status bar shows the roster file path. Launch TutorRoster from the same folder each time if you
want to use the same roster.

### Back up or transfer a roster

1. Close TutorRoster.
1. Copy `data/addressbook.json` to a safe location.
1. To restore it, close TutorRoster and place the copy at `data/addressbook.json` in the destination
   launch folder.
1. Start TutorRoster from that folder.

Do not edit the JSON file manually unless you understand the format; invalid data can prevent records
from loading.

### Existing tags from older files

When opening a roster that contains tags with mixed case or duplicates, TutorRoster normalises the tags
to lowercase, merges equivalent tags, and saves the normalised file atomically. It then shows:

```text
Existing tags were updated to lowercase and duplicate tags were merged.
```

Existing tags longer than 30 characters or students with more than 20 existing tags are retained so
that older rosters remain readable. New tag additions still follow the current limits. If the migration
cannot be saved, startup stops and the original roster file is left unchanged.

### Write and load problems

TutorRoster must be placed in a writable folder. A write-protected folder or data file can prevent
changes, preference updates, or tag migration from being saved. Move the JAR to a folder where your
user account has write permission and try again.

If a roster change cannot be saved, the live roster and its undo/redo history are left unchanged. Add
and tag operations show `Unable to save student data. No changes were made.`; other save failures begin
with `Could not save data` and include the reported cause.

If an existing roster cannot be loaded, TutorRoster currently opens with an empty roster without a
dedicated result-area warning. If this happens unexpectedly, close the app, copy the original
`data/addressbook.json` for safekeeping, and restore a readable backup before making more changes.

## FAQ and known issues

**Why does an index refer to a different student after a search?**

Commands that accept an index use the list currently displayed. Search results start at 1, so check the
displayed name and phone before using `show`, `edit`, `delete`, `tag`, or `untag`.

**Why does double-clicking the JAR do nothing?**

Some systems do not launch JavaFX JAR files by double-clicking. Open a terminal in the JAR's folder and
run `java -jar TutorRoster.jar` instead.

**Why does the app open empty?**

If there is no data file, sample students should be displayed. An empty roster can also mean that the
existing data file could not be read. Make a backup of `data/addressbook.json` before entering new
commands.

**Why does the app fail to save?**

Check that the launch folder and its `data` subfolder are writable and that the data file is not
read-only. Then retry the command.

**What happens in macOS fullscreen mode?**

Secondary windows such as Help may behave unexpectedly when the main app is in fullscreen mode. Use
TutorRoster in a normal window if the Help window cannot be focused or dismissed correctly.

**Why did the app reopen off-screen?**

If the app was last used with a second monitor that has since been disconnected, it may reopen outside
the visible area. Close TutorRoster, remove `preferences.json` from the launch folder, and reopen it.
This resets window preferences but does not remove the roster.
