---
  layout: default.md
  title: ""
---

# TutorRoster

![TutorRoster main window](images/Ui.png)

## A focused student directory for private tutors

TutorRoster keeps student contact details and tutoring context together in one fast, local desktop
workspace. Use short keyboard commands to add, edit, search, tag, inspect, and recover student records;
use the visual roster to confirm what is on screen.

TutorRoster is designed for tutors who manage several students, prefer typing to repetitive mouse
navigation, and want a simple application that works without an Internet connection.

### Start here

* [User Guide](UserGuide.html) — install TutorRoster and learn every implemented command.
* [Developer Guide](DeveloperGuide.html) — understand the architecture, implementation, requirements,
  and manual test path.

### What it does

* **Student profiles** — store names, phone numbers, email addresses, addresses, subjects, education
  levels, tutoring notes, and tags.
* **Fast search** — find by name, phone, email, address, subject, level, or tag using case-insensitive
  partial matching and `ALL`/`ANY` queries.
* **Safe changes** — validate input, detect duplicate student identities, save atomically, and keep
  failed operations from changing the live roster.
* **Session recovery** — undo and redo add, edit, delete, clear, and changed tag operations.
* **Keyboard workflow** — autocomplete suggestions, command history, `/` focus, `Esc` navigation, and
  `F1` help.

TutorRoster intentionally focuses on student-profile management. Lesson scheduling, attendance,
billing, teaching materials, school fields, and separate guardian records are not part of the current
release.

## Acknowledgements

TutorRoster builds on the [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3)
from the [SE-EDU initiative](https://se-education.org/). It uses JavaFX, Jackson, JUnit 5, and MarkBind;
the full attribution is in the [Developer Guide](DeveloperGuide.html#acknowledgements).
