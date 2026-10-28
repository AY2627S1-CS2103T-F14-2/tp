# TutorRoster command acceptance test plan

## Interface and comparison rules

TutorRoster inherits AB3's JavaFX GUI. Enter the inputs below into the GUI
command box, pressing Enter after each line. Compare the exact text in the
result area and the numbered names in the roster. Application stdout and
stderr are diagnostic logs, not the user-facing feedback being tested.
This is a GUI adaptation of the test-ui workflow; it is not a console test.

Use exact equality for result-area text, normalizing CRLF to LF and ignoring
one final newline only. Compare roster names and indices in their displayed
order. Error commands must preserve the roster and the current search filter.
Record a transcript for every case, identifying GUI input and displayed output.
Stop on the first mismatch, launch error, unexpected exit, or timeout and mark
later cases as not run.

## Build launch and setup

Build from the repository root using Java 25:

```sh
./gradlew javadoc test checkstyleMain checkstyleTest shadowJar
```

Set `REPO_ROOT` to the repository's absolute path. Create a fresh temporary
directory for this run, with a `data` subdirectory, and copy
`test/fixtures/delete-students.json` to `data/addressbook.json` inside it.
Use this temporary directory as the application working directory. Do not use
or reset the tutor's actual data directory or preferences.

Launch from that temporary directory:

```sh
java -ea --enable-native-access=ALL-UNNAMED -jar "$REPO_ROOT/build/libs/TutorRoster.jar"
```

On Apple Silicon, the inherited dependency configuration may package Intel
JavaFX natives. If startup reports an incompatible architecture, stop that
session without executing cases. Use the matching JavaFX 17.0.7
`mac-aarch64` graphics JAR from the Gradle cache or Maven Central for a fresh
session. This changes the test runtime only. Set `JAVA_FX_ARM_JAR` to its
absolute path and `JAVA_FX_CACHE` to a fresh directory within the isolated
session. Use this alternate launch command:

```sh
java -ea --enable-native-access=ALL-UNNAMED -Djavafx.cachedir="$JAVA_FX_CACHE" \
  -cp "$JAVA_FX_ARM_JAR:$REPO_ROOT/build/libs/TutorRoster.jar" seedu.address.Main
```

For the automated tests on that same machine, extract the JAR's `.dylib`
files into an isolated native-library directory. A temporary Gradle init
script may set the test JVM's `java.library.path` to that directory and add
`--enable-native-access=ALL-UNNAMED`. Record the script and actual build
invocation in the test results. Do not change product dependencies or the
tutor's existing JavaFX cache as part of this test preparation.

Allow 30 seconds for startup and 10 seconds per command. All six cases below
explicitly share one application session, in the recorded order. The starting
roster is `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`. The fixture retains inherited AB3 fields to verify compatibility with existing
records. Find and list feedback below matches the current implementation.

After the final case, enter `exit` and require a normal exit with status 0
within 10 seconds. Terminate the test process if startup, a case, or exit times
out. Never terminate an unrelated application process.

## DEL-UI-01 Delete a filtered student

Aim: Resolve the displayed index before clearing the filter, and return to the
complete remaining roster in order.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `find Ryan` | `1 person(s) listed!` | `1. Ryan Tan` |
| `delete 1` | `Student removed: Ryan Tan (phone: 91234567)` | `1. Alicia Lim`, `2. Sarah Lee` |

## DEL-UI-02 Reject an index outside the filtered list

Aim: Reject an index that is valid in the full roster but invalid in the
displayed search results; preserve the filter and records.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `find Sarah` | `1 person(s) listed!` | `1. Sarah Lee` |
| `delete 2` | `The student index provided is invalid.` | `1. Sarah Lee` |

## DEL-UI-03 Reject malformed deletion commands

Aim: Reject missing, zero, negative, decimal, nonnumeric, extra-argument, and
overflow indices with the specified feedback. The displayed list must remain
`1. Sarah Lee` after every input below.

Exact GUI inputs, in order:

```text
delete
delete 0
delete -1
delete 1.5
delete abc
delete 1 n/Ryan
delete 1 2
delete 2147483648
```

Exact expected result-area output after each input:

```text
Invalid command format. Usage: delete INDEX
```

## DEL-UI-04 Reject deletion from empty search results

Aim: Preserve existing records when the displayed list is empty.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `find Nobody` | `0 person(s) listed!` | None |
| `delete 1` | `The student index provided is invalid.` | None |

## DEL-UI-05 Delete the last displayed index

Aim: Restore the full roster and remove its last student without disturbing
the first student.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `list` | `Listed all students.` | `1. Alicia Lim`, `2. Sarah Lee` |
| `delete 2` | `Student removed: Sarah Lee (phone: 82345678)` | `1. Alicia Lim` |

## DEL-UI-06 Delete the final student and reject another deletion

Aim: Support surrounding whitespace, leave an empty roster after deleting the
final student, and reject a subsequent deletion without changing that roster.

The first input below contains exactly two leading and two trailing spaces.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `  delete 1  ` | `Student removed: Alicia Lim (phone: 81234567)` | None |
| `delete 1` | `The student index provided is invalid.` | None |

## Persistence scope

Automated logic tests verify successful add, delete, undo, and redo saves, and
that failed saves preserve the roster, filter, history, and original data file.
These checks do not substitute for the GUI feedback checks above and below.

## Undo/redo session setup

After completing the Delete session, close that process and create a fresh
isolated session using the same fixture, launch instructions, and timeouts.
The cases below explicitly share this second session, in order. The initial
roster is `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`. Run with Java assertions
enabled (`-ea`). History begins empty and lasts only until the app closes.

## HIST-UI-01 Unavailable commands

Aim: Reject undo and redo before any roster changes without changing the list.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `undo` | `There are no changes to undo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `redo` | `There are no changes to redo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |

## HIST-UI-02 Undo and redo a filtered deletion

Aim: Restore the correct student, complete details, and order; both history
commands return to the full roster. Search commands must not create undo steps.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `find Ryan` | `1 person(s) listed!` | `1. Ryan Tan` |
| `delete 1` | `Student removed: Ryan Tan (phone: 91234567)` | `1. Alicia Lim`, `2. Sarah Lee` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `find Nobody` | `0 person(s) listed!` | None |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Sarah Lee` |

## HIST-UI-03 Invalid commands preserve redo

Aim: Reject arguments and failed commands without consuming redo or changing
an active search filter. `list` must also leave redo available.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `find Ryan` | `1 person(s) listed!` | `1. Ryan Tan` |
| `undo 1` | `Invalid command format. Usage: undo` | `1. Ryan Tan` |
| `redo all` | `Invalid command format. Usage: redo` | `1. Ryan Tan` |
| `delete 2` | `The student index provided is invalid.` | `1. Ryan Tan` |
| `list` | `Listed all students.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Sarah Lee` |

## HIST-UI-04 New branch, add, and repeated undo/redo

Aim: A new successful add after undo clears the old redo branch; repeated
undo/redo reaches the history boundary and restores the new addition.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `add n/Jamie Koh p/81234567 s/Physics l/JC 1` | `New student added: Jamie Koh` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `redo` | `There are no changes to redo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `undo` | `There are no changes to undo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |

Enter `exit` and require status 0 within 10 seconds. Relaunch using this same
isolated session directory for HIST-UI-05.

## HIST-UI-05 Restart keeps roster but clears history

Aim: Saved records remain, while session history does not persist.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `undo` | `There are no changes to undo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `redo` | `There are no changes to redo.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |

Enter `exit` and require status 0 within 10 seconds.


## INT-UI-01 Notes, detail viewing, and undo/redo integration

Aim: Show uses the displayed index without creating an undo step or clearing
redo, and restored student records retain their notes.

Start a fresh isolated session with the same three-student fixture and launch
instructions. This case has its own session and does not continue HIST-UI-05.
After each `show 1`, check the detail modal and close it using its Close button
before entering the next command. For the first and final detail views,
verify these exact displayed field values: name `Jamie Koh`, phone `81234567`, subjects `Physics`, level `JC 1`, and notes
`Needs practice`. Email, address, and tags each show `Not provided`.

| Exact GUI input | Exact expected result-area output | Expected numbered names |
| --- | --- | --- |
| `add n/Jamie Koh p/81234567 s/Physics l/JC 1 r/Needs practice` | `New student added: Jamie Koh` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `find Jamie` | `1 person(s) listed!` | `1. Jamie Koh` |
| `show 1` | `Showing student details.` | `1. Jamie Koh` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `show 1` | `Showing student details.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `redo` | `Redo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `find Jamie` | `1 person(s) listed!` | `1. Jamie Koh` |
| `delete 1` | `Student removed: Jamie Koh (phone: 81234567)` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee` |
| `undo` | `Undo successful.` | `1. Alicia Lim`, `2. Ryan Tan`, `3. Sarah Lee`, `4. Jamie Koh` |
| `find Jamie` | `1 person(s) listed!` | `1. Jamie Koh` |
| `show 1` | `Showing student details.` | `1. Jamie Koh` |

The middle `show 1` targets Alicia and must show her fixture details instead:
name `Alicia Lim`, phone `81234567`, email `alicia@example.com`, address
`Test address 1`; tags, subjects, level, and notes each show `Not provided`.
It must leave Jamie's addition available for redo. Enter `exit` and require
status 0 within 10 seconds after the final modal is closed.
