---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TutorRoster User Guide

TutorRoster is a desktop application for private tutors to manage students through typed commands and a graphical roster.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/dionhjx/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your TutorRoster.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI should appear in a few seconds. A new data file starts with sample students.<br>
   Student cards show name, phone, optional email, subjects, and education level.

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/Sam Tan p/98765432 s/Mathematics l/Secondary 4` : Adds a student named `Sam Tan` to the roster.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `[e/EMAIL]` may be omitted from an add command.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[s/SUBJECT]...` permits extra subjects after the first required subject.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

The help window shows the add syntax and a link to this guide.

Format: `help`


### Adding a student: `add`

Adds a student to the shared roster.

Format: `add n/NAME p/PHONE [e/EMAIL] s/SUBJECT [s/SUBJECT]... l/LEVEL`

Name, phone, at least one subject, and level are required. Email is optional. Parameters can appear in any order,
with whitespace before each prefix. Only subjects may be repeated. Old `a/` and `t/` parameters are not accepted.

| Field | Accepted values |
|---|---|
| Name | 1–100 Unicode characters; letters, numbers, spaces, apostrophes, hyphens, and full stops; at least one letter or number. |
| Phone | 3–15 ASCII digits, optionally beginning with one `+`. No internal whitespace or hyphens. |
| Email | Nonempty local part, exactly one `@`, no whitespace, and a domain with at least one full stop and no empty segments. |
| Subject | 1–50 Unicode characters; letters, numbers, spaces, `&`, `+`, `/`, apostrophes, hyphens, and full stops; at least one letter or number. |
| Level | 1–30 Unicode characters; letters, numbers, spaces, and hyphens; at least one letter or number. Free text supports different education systems. |

Name, subject, and level whitespace is trimmed and collapsed, including tabs and Unicode whitespace.
Capitalization is preserved for display. Phone and email values are trimmed at their boundaries.
Omit `e/` when there is no email; supplying an empty `e/` is invalid.
Each `s/` supplies one subject. Subjects appear in command order.
Repeated subjects are rejected after comparing normalized whitespace and ignoring case.

Examples:

* `add n/Ryan Tan p/+6591234567 e/ryan@example.com s/Mathematics l/Secondary 4`
* `add n/Alicia Lim p/81234567 s/Mathematics s/Physics l/JC 1`
* `add l/Grade 8 s/English n/Sarah Lee p/+821012345678`
* `add n/Anne-Marie O'Connor p/123 s/C++ s/Physics/Chemistry l/Year 10`

On success, the result area shows `New student added: NAME`.
The card shows name, phone, email if supplied, subjects, and level.
An active search remains active: a student that does not match it is saved but may not appear until `list` is run.

#### Duplicate students

A record is rejected if its normalized name matches an existing record **and** either its phone matches or
both records have matching emails. Name and email comparisons ignore case; phone comparisons ignore one leading
`+`. This rule applies to the entire roster, including legacy contacts and edits to legacy contacts.
Subjects and level do not affect duplicate detection.

Students with different names can share a guardian's phone/email. Students with identical names can coexist
when neither phone nor present email matches. Two omitted emails do not count as matching contact details.
Duplicates produce `This student already exists in TutorRoster.` and leave the existing record untouched.

#### Invalid input

Only one error is shown. Priority is unknown prefix, repeated single-value prefix, missing required parameter,
invalid field (name, phone, email, subjects, then level), repeated subject, then duplicate record.

| Situation | Result |
|---|---|
| Missing required parameter, no parameters, or text before the first prefix | `Invalid command format. Usage: add n/NAME p/PHONE [e/EMAIL] s/SUBJECT [s/SUBJECT]... l/LEVEL` |
| Repeated name (also applies to phone, email, level) | `Parameter n/ must not be specified more than once.` |
| Invalid name | `Invalid name. Names must be 1–100 characters and contain at least one letter or number. Only letters, numbers, spaces, apostrophes, hyphens and full stops are allowed.` |
| Invalid phone | `Invalid phone number. Phone numbers must contain 3–15 digits and may optionally begin with '+'.` |
| Invalid email | `Invalid email. Email must be in the form local-part@domain and must not contain spaces.` |
| Invalid subject | `Invalid subject. Subjects must be 1–50 characters and contain at least one letter or number.` |
| Invalid level | `Invalid education level. Levels must be 1–30 characters and contain only letters, numbers, spaces and hyphens.` |
| Repeated subject | `Duplicate subject: SUBJECT` (using the first occurrence's capitalization) |
| Unknown prefix | `Unknown parameter prefix: x/` |

A standalone single-letter token such as `x/Test` is an unknown prefix. A slash inside a subject such as
`Physics/Chemistry` remains part of the subject. Prefixes are lowercase and require separating whitespace.

If saving fails, the result area shows:
`Unable to save student data. The student remains in this session but may be lost when you restart.`
The in-memory addition and search remain in place. A later successful command will attempt to save the roster again.

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits a legacy contact. Students created by add cannot be edited yet: the result area shows `Student editing is not available yet.`. Legacy edits use the same roster-wide duplicate rule as add.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorRoster automatically saves data after each successful command. Save failures are reported; an unsaved addition stays in memory and may be lost on restart.

### Editing the data file

TutorRoster data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Existing files remain readable. Old contacts keep their address and tags and appear in the same roster;
they do not acquire subjects or level automatically. New student records include a `subjects` array and `level`.

To migrate an old contact manually, close the app and back up the file. In its entry within the existing `persons`
array, supply one or more valid subjects and a level, remove `address` and `tags`, and ensure its name, phone,
and email conform to the new validation rules. Omit the `email` property if there is no email. For example:

```json
{
  "persons": [
    {
      "name": "Ryan Tan",
      "phone": "+6591234567",
      "subjects": ["Mathematics", "Physics"],
      "level": "Secondary 4"
    }
  ]
}
```

Keep the remaining entries when editing the file. A migrated student cannot be edited with `edit` in this release.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, TutorRoster starts with an empty address book at the next run. The invalid file remains on disk until you run a command (TutorRoster saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the TutorRoster to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TutorRoster home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add** | `add n/NAME p/PHONE [e/EMAIL] s/SUBJECT [s/SUBJECT]... l/LEVEL`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Help**   | `help`
