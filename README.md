# TutorRoster

TutorRoster is a keyboard-driven desktop application for private tutors to manage students' contact and tutoring
information. It is built on AddressBook Level 3 using Java 25 and JavaFX.

Add a student:

```text
add n/Ryan Tan p/+6591234567 e/ryan@example.com s/Mathematics s/Physics l/Secondary 4
```

Email is optional. Name, phone, at least one subject, and education level are required.
Existing contacts and new students share one roster. Existing files remain readable without automatic migration;
student editing is not available in this release.

* [User Guide](docs/UserGuide.md)
* [Developer Guide](docs/DeveloperGuide.md)
* [Setting up](docs/SettingUp.md)
* [Testing](docs/Testing.md)

Run `gradlew.bat check` on Windows or `./gradlew check` on macOS/Linux.
Build with `gradlew.bat shadowJar`, then run `java -jar build/libs/addressbook.jar`.

Acknowledgement: this project is based on [se-edu/addressbook-level3](https://github.com/se-edu/addressbook-level3).
