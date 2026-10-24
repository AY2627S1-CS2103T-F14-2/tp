[# TutorRoster

[![CI Status](https://github.com/AY2627S1-CS2103T-F14-2/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-F14-2/tp/actions)

![Ui](docs/images/Ui.png)

## About TutorRoster

**TutorRoster** is a desktop student management application designed specifically for **private freelance tutors** who manage between 10 to 20 students.

Tutors often juggle scattered chat messages, spreadsheets, and sticky notes across multiple devices. **TutorRoster** centralizes all student contact details and tutoring information into a single, cohesive, and distraction-free workspace.

Optimized for tutors who type fast and prefer a **Command Line Interface (CLI)** with the visual clarity of a **Graphical User Interface (GUI)**, TutorRoster allows you to retrieve, update, and manage your students much faster than traditional mouse-driven applications.

---

## Key Features

* **Student & Guardian Profiles**: Track crucial tutoring-specific details including subjects taught, education level, address, school, parent/guardian contacts, and customised student notes.
* **Rapid CLI-First Workflow**: Perform common operations (adding, searching, filtering, and updating student profiles) with concise keyboard commands.
* **Targeted Search & Student Tags**: Find students by name, address, subject, education level, notes, contact details, or tags, and organise them with labels such as `exam-prep`. Add labels with `tag INDEX TAG`, remove them with `untag INDEX TAG`, or clear them with `untag --all INDEX`.
* **Data Integrity & Safety**: Built-in duplicate warnings, validation checks, and deletion safeguards to keep your records accurate and secure.

> **Note**: TutorRoster focuses on student contact and profile management. It intentionally does not handle lesson scheduling, attendance tracking, billing/payments, or teaching materials.

## Testing and coverage

Run `./gradlew test` for the automated tests, or `./gradlew coverage` to generate the JaCoCo HTML and XML reports.
The coverage report includes non-FXUI code only: `seedu/address/ui/**`, `Main`, and `MainApp` are excluded. The
`check` task enforces at least 95% line coverage and 90% branch coverage for the included code.

---

## Documentation

* [User Guide](docs/UserGuide.md)
* [Developer Guide](docs/DeveloperGuide.md)
* [Design System](docs/DesignSystem.md)
* [About Us](docs/AboutUs.md)

---

## Acknowledgements
* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
]()
