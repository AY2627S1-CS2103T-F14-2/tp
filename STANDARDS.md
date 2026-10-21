# Software Engineering Standards

**Consolidated reference for humans and coding agents**  
**Source snapshot:** 20 October 2026  
**Applies to:** Java code, Java error handling and logging, and Git history/branch naming in an SE student project.

This document consolidates the four source pages listed at the end, including the expandable exercises and answer panels in the NUS CS2103/T error-handling chapter. It is a **standards reference**, not a task checklist. It paraphrases the requirements and relevant teaching examples rather than reproducing the source pages verbatim.

## How to interpret this document

- **Must / do not / required** means the referenced standard expresses an explicit requirement or prohibition.
- **Should / recommended / prefer** means the referenced material recommends the practice or describes it as a convention, without making it an unconditional rule.
- **May / acceptable** means a permitted alternative or an option dependent on circumstances.
- **Teaching principle** indicates guidance explained by the adapted textbook, rather than a newly imposed universal coding rule.
- **Examples** illustrate the intent; their particular class names, messages and domain models are not mandatory.

When a source makes a rule conditional, retain that condition. Do not turn a teaching example into a blanket requirement. For Java style topics **not addressed** by the SE Education Java standard, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), as expressly directed by the Java standard. If project-specific requirements override an example here, respect the project requirements while preserving the source conventions where possible.

The Java fragments below focus on individual rules. Some deliberately omit full packages, supporting types, imports, or Javadocs, so they are not all standalone drop-in classes. An agent must complete those elements before committing production code.

> **Language distinction:** This document uses British English. The referenced Java coding standard explicitly requires **American spelling in Java comments**, as well as English for identifiers and comments. Apply that source-specific exception when writing code.

---

## 1. Java naming conventions

### 1.1 Packages

- Package names must use lowercase letters, e.g. `tutorroster.ui` or `tutorroster.storage`.
- In student projects, use the team/group or project name as the root, followed by meaningful package components.
- Do not use an NUS-owned namespace such as `edu.nus.comp.*` for code that is not actually produced by NUS.
- Put every class in a named package; group related classes in an appropriate package.

### 1.2 Types, identifiers and methods

- Class and enum names must be nouns in **PascalCase**, e.g. `StudentRecord`, `AudioSystem`.
- Variable names must be **camelCase**, e.g. `studentCount`, `audioSystem`.
- Constants must use **SCREAMING_SNAKE_CASE**, e.g. `MAX_STUDENTS`, `DEFAULT_TIMEOUT`. The standard defers to the Google Java Style Guide for which fields genuinely count as constants.
- Method names must describe actions (verbs or verbal phrases) in **camelCase**, e.g. `findStudent()`, `computeTotal()`.
- Names must be English words or meaningful English-based identifiers.
- When an acronym appears as part of a longer identifier, write it as a normal word rather than an all-caps segment: `exportHtmlSource()`, `openDvdPlayer()`, not `exportHTMLSource()` or `openDVDPlayer()`.
- Choose identifier length to match scope. Wider-scope values should have descriptive names; short-lived local scratch values can have short names.
- Simple loop indices may be `i`, `j`, `k`, etc. Prefer `j` and `k` for nested loops rather than using them as arbitrary outer-loop indices; one-letter scratch names should be limited to genuinely small scopes.
- Name booleans so they read as predicates, preferably with `is`, `has`, `was`, `can`, `should`, etc., e.g. `isValid`, `hasStudents`, `canDelete()`.
- A boolean setter should use the usual setter naming pattern, e.g. `setFound(boolean isFound)`.
- Collection and array variables should normally have **plural** names, e.g. `students`, `records`, `values`.
- Related constants should share a prefix, e.g. `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

### 1.3 Test-method names

Underscores are permitted in test-method names for the three-part convention:

```text
featureUnderTest_testScenario_expectedBehavior
```

For Java source, the source's own US-English example form is:

```java
sortList_emptyList_exceptionThrown()
getMember_memberNotFound_nullReturned()
```

The scenario and expected-behaviour components may be shortened or omitted when the test's scope makes this sensible. Underscores in ordinary production method names are not the recommended naming style.

---

## 2. Java layout and formatting

### 2.1 Indentation and line length

- Indent Java code using **4 spaces** per block level, not tabs.
- Use **110 characters** as the preferred soft line-length limit.
- Do not exceed **120 characters** per line (the stated hard limit); wrap long lines at readable boundaries.
- Wrapped continuation lines should be indented **8 additional spaces** relative to the parent line.
- When splitting expressions, prefer readability over blindly accepting the IDE's default formatter output.

### 2.2 Line wrapping

- Prefer breaking a line **after a comma**.
- Break **before** an operator, including a continued `+`, a chained `.`, `&` in generic type bounds, and `|` in multi-catch clauses.
- Keep a method or constructor name on the same line as its following `(`. If necessary, break **after** `(` rather than putting the opening parenthesis on a new line by itself.
- Prefer splitting at a higher-level expression boundary instead of breaking the middle of a lower-level parenthesised expression.
- Either a one-line ternary expression or a readable multi-line `?`/`:` layout is acceptable.

```java
String message = "A long message "
        + "continued on the next line";

process(student,
        repository.findByName(name)
                .orElseThrow(),
        isStrict);

String label = isRegistered
        ? "Registered"
        : "Unregistered";
```

### 2.3 Braces and statement layout

- Use **K&R / Egyptian-style braces**: the opening `{` stays on the statement or declaration line.
- Put `} else {`, `} else if (...) {`, `} catch (...) {` and `} finally {` on the expected joined line.
- Put conditional bodies on their own lines; do not write `if (condition) action();`.
- Use braces even when an `if`, `else`, `for`, `while` or other control-flow body contains only one statement.
- Format `for`, `while`, `do`/`while`, `try`/`catch`/`finally` and method declarations using the conventional brace layouts illustrated below.
- Use consistently indented `switch` case bodies. Traditional colon-style switch blocks and modern arrow-style switch blocks are both illustrated as acceptable in the source.
- If a colon-style `switch` case intentionally falls through without `break`, include an explicit `// Fallthrough` comment.

```java
if (isValid) {
    save();
} else if (isRecoverable) {
    retry();
} else {
    reportFailure();
}

for (int i = 0; i < students.size(); i++) {
    process(students.get(i));
}

while (hasWork()) {
    processNext();
}

do {
    readNext();
} while (hasMoreInput());

try {
    loadData();
} catch (IOException exception) {
    handleReadFailure(exception);
} finally {
    closeResources();
}

switch (status) {
    case NEW:
        prepare();
        // Fallthrough
    case READY:
        execute();
        break;
    default:
        reportUnknownStatus();
        break;
}
```

### 2.4 Whitespace

- Place spaces around binary operators, e.g. `a = (b + c) * d;`.
- Put a space after Java control-flow keywords, e.g. `if (condition)`, `while (condition)`.
- Put spaces after commas, e.g. `send(a, b, c)`.
- Put spaces around the colon where it functions as a binary/ternary operator; the special `case VALUE:` switch syntax is not covered by that rule.
- In `for (...)` headers, put a space after each semicolon.
- Separate **logical units** of code within a block using a blank line. Avoid excessive blank lines that fragment a single logical operation.

---

## 3. Java file organisation and declarations

### 3.1 Imports

- Use a consistent import ordering across the team and configure the IDE accordingly.
- The standard shows grouped imports (such as static imports, Java standard-library imports, third-party imports) with separation between groups; the decisive requirement is **consistency**.
- Always import required classes **explicitly**. Do not use wildcard imports such as `import java.util.*;`.
- Keep imports minimal and remove unused imports.

### 3.2 Class and interface member ordering

Organise class/interface contents in this order:

1. Class/interface documentation comment.
2. Class/interface declaration.
3. Class (`static`) fields, grouped by accessibility: `public`, `protected`, package-private, `private`.
4. Instance fields, grouped by the same accessibility order.
5. Constructors.
6. Methods (no mandatory order among methods).

### 3.3 Modifiers and types

- When present, an access modifier (`public`, `protected`, `private`) must come **first** in the method's modifier sequence.
- The source's preferred method-modifier ordering is: `<access> static abstract synchronized <unusual> final native`, where `<unusual>` denotes modifiers such as `volatile`/`transient`. Apply only modifiers that are legal for the declaration; the central requirement is access modifier first.
- Place array brackets on the **type**, e.g. `String[] names`, never `String names[]`.

### 3.4 Variables and encapsulation

- Declare a local variable in the **smallest feasible scope**.
- Initialise it where declared when an appropriate valid value is available.
- If no appropriate initial value exists yet, leave it uninitialised until a genuine value is available; do not invent a bogus default merely to satisfy the previous recommendation.
- Do not expose mutable class fields as `public`, except for fields of genuine data-only classes without behaviour; the rule does not prohibit public constants.
- Prefer non-public fields with appropriate operations to preserve encapsulation.
- Omit unnecessary `this.` qualifiers. Use `this.field` to distinguish a field from a shadowing parameter or local variable, not indiscriminately.

```java
public class Student {
    private final String name;
    private boolean isActive;

    public Student(String name) {
        this.name = name;  // 'name' is shadowed by the parameter.
        isActive = true;   // no 'this.' needed here.
    }
}
```

---

## 4. Java comments and Javadoc

### 4.1 Language and purpose

- All source-code comments must be in **English**, using **American spelling** and avoiding local slang.
- Write descriptive header comments for **all classes** and **public methods**, except where the documented exemptions apply:
  - Simple getters and setters.
  - Overrides whose inherited Javadoc applies exactly as written.
  - Classes and methods used for testing.
- Give **non-trivial private methods** descriptive header comments too.
- Explain **what** a method promises to do, not merely how its implementation happens to work. Source code alone often does not tell callers the intended contract.
- Keep comments indented at the appropriate code indentation level.
- Trailing comments on code lines are allowed if they remain clear.

### 4.2 Javadoc format

- Start a multi-line Javadoc with `/**` on its own line.
- Align the leading `*` characters and include a space after `*` before text.
- Use a short **first sentence** as the summary. For methods, use third-person action wording: **“Returns ...”, “Adds ...”, “Sends ...”**, not “Return ...” or “Returning ...”.
- Separate the description from the tag section with a blank Javadoc line.
- End parameter descriptions with appropriate punctuation.
- Put the Javadoc immediately before the declaration, with **no blank line** between them.
- Include `@param` tags for **all** parameters or for **none**, rather than documenting an arbitrary subset. Omit them all only when the parameter names or main description already explain them sufficiently.
- `@return` may be omitted for `void` methods, or where the return value is already obvious from the description.
- Document relevant `@throws` conditions where doing so clarifies the method contract.
- Use `{@inheritDoc}` when adapting or extending inherited documentation for an override.
- One-line Javadocs are permissible for class fields.

```java
/**
 * Returns the student matching the given identifier.
 * Returns null when no matching student exists.
 *
 * @param studentId Identifier of the requested student.
 * @return Matching student, or null if absent.
 * @throws IllegalArgumentException If studentId is negative.
 */
public Student findStudent(int studentId) {
    // ...
}

/** Number of attempts permitted before giving up. */
private static final int MAX_ATTEMPTS = 3;
```

---

## 5. Error handling: exceptions

The following sections come from the **NUS adapted SE textbook**, including material in its collapsed exercise answer panels. They describe the expected rationale for error-handling decisions. These principles do not imply that every error warrants an exception, assertion, application shutdown or defensive check.

### 5.1 Choose recovery over uncontrolled failure

- **Teaching principle:** Well-written programs should handle plausible failures gracefully where possible, either automatically or through user intervention.
- Where recovery is impossible or unsafe, explicitly handle the outcome, potentially including logging off, safely terminating the relevant operation, or, in extreme cases, shutting down the application.
- Do not treat logging or exception handling as a way to **prevent** external failures; they help report, recover from and diagnose them.

### 5.2 Exceptions represent unusual runtime situations

- Use exceptions for unusual, exceptional runtime conditions, such as a network timeout or unreadable/corrupted file.
- **Do not use exceptions for ordinary control flow.** Use normal returns, conditionals or other normal program logic for expected, non-exceptional results.
- When code encounters an error it cannot appropriately resolve, it can **throw** an exception with information about the failure; a suitable caller may **catch** it and respond.
- Exception propagation follows the call stack until a compatible handler is found. If no handler can handle an uncaught exception, execution can terminate.
- Exceptions enable both separation of mainline logic from error-handling code and propagation of failures to the layer capable of addressing them.
- Catch exceptions where the code can **meaningfully handle** the situation; do not add catches merely to hide a failure.

### 5.3 Exceptions versus assertions

- An exception typically reports an unusual situation caused by a user or the environment, or another recoverable runtime error.
- An assertion failure reports a violated **programmer assumption**, and therefore suggests a programming defect.
- A user-supplied invalid value is **not automatically evidence of a programmer bug**. Handle it as part of the application's input/error contract, rather than assuming an assertion should crash the application.
- Example from a collapsed exercise: a calculator failing an assertion when asked for the square root of a negative number indicates a **bug in the program's handling of that input**. Proper handling may reject the request or use an internally handled exception; the application should not simply terminate for that ordinary user scenario.

---

## 6. Error handling: Java assertions

### 6.1 Intended use

- Use Java `assert` to express programmer assumptions and program-state invariants that should always hold when the relevant code is correct.
- Suitable assumptions include **internal invariants, control-flow invariants, preconditions, postconditions and class invariants**, when such checks are appropriate to the chosen design.
- Assertions are recommended **liberally** where they add safety without material overhead.
- Treat an assertion failure as a possible **code defect**, not as a normal recovery path for user input.

```java
int timeout = Config.getTimeout();
assert timeout > 0 : "timeout should be positive";
```

### 6.2 Runtime behaviour and limitations

- Java assertions can be **disabled without changing the code** and are **disabled by default**.
- Run with `-ea` or `-enableassertions` when assertion checking is wanted; `-da` or `-disableassertions` disables them.
- Enable assertions in the IDE for development/test runs where they should be exercised, and temporarily verify that they are active (for example, with a deliberately failing assertion that is removed afterwards).
- **Never put necessary application work or side effects inside an assertion expression.** If assertions are disabled, the expression may not run at all.

```java
// Bad: the file may not be written if assertions are disabled.
assert writeFile() : "File writing must succeed";

// Better: do the work unconditionally; assert only a property of the result.
boolean isWritten = writeFile();
assert isWritten : "File writing unexpectedly failed";
```

The second example is appropriate **only if failure truly indicates a programmer invariant violation**. If writing the file can legitimately fail (for example, due to filesystem permissions), handle that expected environmental failure using normal error handling rather than relying on `assert`.

### 6.3 Java assertions versus JUnit assertions

- In production/functional Java code, use Java's `assert` for programmer invariants where suitable.
- In test code, use **JUnit assertions**. They are designed for testing, offer richer assertions, and are **not disabled by Java's default assertion setting**.

---

## 7. Logging: purpose, API and levels

### 7.1 What logging is for

- Record selected runtime information for later diagnosis and understanding of application behaviour.
- Logs can be directed to files, databases, remote systems or other destinations; they need not be shown to the user.
- Logging is a diagnostic record, not a mechanism that prevents failures or replaces user-facing error handling.
- Prefer meaningful events and context over indiscriminate logging of every operation.

### 7.2 Recommended implementation

- The **SE Education Java logging conventions recommend `java.util.logging`**.
- Use a `Logger` and meaningful logging levels, rather than treating all messages as equally important.
- Logging configurations can suppress messages beneath the configured level; for example, a `WARNING` threshold excludes `INFO` and `FINE` messages.
- Attach an exception when it provides useful error details, such as `logger.log(Level.WARNING, "...", exception)`.

```java
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoader {
    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

    public void load() {
        logger.log(Level.INFO, "Starting data load");
        try {
            readInput();
            logger.log(Level.INFO, "Data load completed");
        } catch (IOException exception) {
            logger.log(Level.WARNING, "Unable to read input", exception);
        }
    }
}
```

*Illustration only: the class must also supply/import the actual I/O dependencies before this snippet compiles. An application should decide at the right boundary whether to rethrow, recover or notify the user rather than blindly swallowing the exception.*

### 7.3 Logging level meanings

| `java.util.logging.Level` | Meaning in the referenced convention |
| --- | --- |
| `SEVERE` | A critical problem has occurred that may result in application termination. |
| `WARNING` | Execution can continue, but with caution. |
| `INFO` | Noteworthy actions performed by the application. |
| `FINE` | Additional detail usually not noteworthy in normal operation but useful for debugging, such as the contents of a collection rather than only its size. |

Choose the severity based on the event's significance. `FINE` should not replace `WARNING` for an actual warning; `SEVERE` should not be used for routine information.

---

## 8. Defensive programming

### 8.1 General principle and appropriate scope

- **Teaching principle:** Assume that if a public interface leaves an opportunity for things to go wrong, a caller may eventually misuse it. Reduce opportunities for invalid states and misuse when practical.
- Do not expose implementation details or mutable internal state unnecessarily.
- Use **defensive copying** when returning a mutable object that callers are not supposed to modify and when returning a live reference would compromise the owner's state.
- Check relevant validity constraints at object construction and mutation boundaries where the design calls for defensive enforcement.
- Defensive programming is **not an unconditional demand to validate every value at every layer**. Extra checks can increase execution time, code length, complexity and maintenance cost. Select the degree of defensiveness based on system criticality, external consumers, language support and overhead.
- For public or high-risk interfaces, prioritise explicit invariants and meaningful failure behaviour; for tightly controlled internal interfaces, use judgement.

```java
// Risky when Config is mutable: caller receives internal state.
public Config getConfig() {
    return config;
}

// Defensive when callers must not modify the owner's Config.
public Config getConfigCopy() {
    return config.copy();
}
```

### 8.2 Enforce compulsory associations and non-null invariants

- If a relationship has UML multiplicity **`1`**, an instance must be associated with **exactly one** corresponding object while the constraint applies.
- Merely declaring an object reference does **not** enforce the constraint: it can be `null`.
- Initialise a required relationship appropriately in the constructor or an equally controlled creation mechanism, and reject `null` when setting a required link.
- Restrict field visibility so callers cannot bypass validation; normally make the reference `private`.
- Avoid exposing mutation operations that permit illegal disconnection.
- If the relationship is **immutable** after creation, do **not** supply a setter that allows it to change. Use an appropriate immutable field design where practical.

```java
public class Account {
    private final Guarantor guarantor;

    public Account(Guarantor guarantor) {
        if (guarantor == null) {
            throw new IllegalArgumentException("Guarantor is required");
        }
        this.guarantor = guarantor;
    }

    public Guarantor getGuarantor() {
        return guarantor;
    }

    // No setGuarantor(): the relationship is immutable.
}
```

The textbook's collapsed `City`/`Country` exercise explicitly rejects an implementation that simply assigns `this.country = country` from an unrestricted setter when the relationship is compulsory: passing `null` would violate multiplicity.

### 8.3 Enforce cardinality, capacity and uniqueness

- Defensively enforce the multiplicities and other collection constraints established by the design.
- For a relationship with maximum cardinality **8**, a ninth addition must be rejected rather than creating an invalid object graph.
- Where duplicates are not allowed, check membership or use a suitable uniqueness-enforcing collection before adding an item.
- If membership must not fall below a minimum, block removals that would violate that minimum.
- Distinguish rejecting an invalid operation from treating a harmless duplicate as a no-op; follow the intended contract.

The collapsed `Manager#addAccount()` exercise combines both a **maximum of eight accounts** and **no duplicate accounts**. The collapsed `Club` exercise requires validating the member, ensuring membership exists, and preserving a minimum membership of ten before removal. These numbers belong to the *exercise models*, not to every application.

### 8.4 Enforce one-to-one relationships carefully

- A **compulsory one-to-one association in both directions** means both objects require a reciprocal reference.
- Java constructs objects one at a time, so there may be a transient moment during construction when both compulsory ends cannot yet be fully connected.
- Where feasible, encapsulate creation so the partially linked objects are not exposed; make the construction process establish the full invariant as early as practical.
- In the textbook's `MinedCell`/`Mine` example, constructing a `Mine` from the `MinedCell` constructor is *more defensive* than requiring callers to supply a pre-existing unlinked `Mine`.
- Do not interpret the example as a proof that every intermediate construction state can be made valid at every instruction; the textbook explicitly acknowledges this limitation.

### 8.5 Preserve referential integrity of bidirectional links

- For a **bidirectional** relationship, references at the two ends must agree with one another: if `a` points to `b`, `b` must point back to `a` in the manner required by the association.
- Independent setters at the two ends can create inconsistent relationships; avoid APIs that allow such invalid partial updates.
- When assigning a new partner, account for an existing partner at **both ends** and detach or otherwise reconcile old links before completing the new relationship.
- Update **both sides** of the association as one coordinated logical operation.
- Guard against repeated assignment of the same partner to prevent pointless work and accidental recursion.
- Use carefully controlled operations for disconnecting links. Verify that the object requesting disconnection is actually the current partner when the design calls for that check.
- Reject `null` when multiplicity requires a partner; permit it only for genuinely optional links or where the contract explicitly treats `null` as removal.
- Avoid unbounded recursive calls between reciprocal setters: include identity guards or centralise relationship management.
- Where only **one-way** relationships exist, the two independent links do not automatically form a bidirectional association, so bidirectional referential-integrity constraints do not automatically apply.

The textbook demonstrates reciprocal updates with `Man`/`Woman` and `Player`/`Region` examples. Its collapsed `Teacher`/`Student` exercise explains that two independent **unidirectional, optional and mutable** references can each be implemented with a simple setter; a stronger design may separate `resetFavouriteStudent()` from `setFavouriteStudent(Student)` to avoid overloading `null` with a removal meaning. The collapsed `Bank`/`Account` example shows another viable ownership model: the `Bank` manages link creation and removal under explicit lifecycle assumptions. Do not copy its deliberately simplified code as a general-purpose integrity guarantee.

### 8.6 Match lifecycle operations to association constraints

- If an association may not change once formed, do not make it publicly reassignable.
- If an association may be removed, provide a removal operation that checks relevant preconditions and consistently updates dependent references.
- If termination/removal ends the lifecycle of an object, document that assumption instead of silently relying on it.
- For linked objects, choose clearly whether one side owns link management or the relationship is coordinated by dedicated operations; prevent uncontrolled conflicting writes.

A collapsed `Marriage` exercise shows that object construction may need to reject missing participants or participants already in another compulsory exclusive relationship, and that a `divorce()` operation should remove the appropriate links. It is a design illustration, not a prescribed application domain.

---

## 9. Design by Contract (DbC)

- **Design by Contract** specifies component interfaces precisely, including relevant **preconditions, postconditions, exceptions and invariants**.
- Under a **defensive-programming** approach, a method generally checks required preconditions and rejects violations (often with an exception).
- Under **DbC**, the **caller is responsible** for satisfying the method's preconditions. The method guarantees its contract only when those preconditions hold; if they do not, its behaviour is **unspecified by the contract**.
- DbC is **not** an alternative to object-oriented programming; it can be applied to OOP code.
- The textbook identifies Eiffel as a language with native DbC support. Java and C++ do **not** have equivalent built-in DbC facilities in the sense discussed there; programmers can use assertions to check contracts, with the normal limitations of disabled assertions.
- Do not accidentally combine contradictory expectations: document whether an operation **validates external inputs defensively** or **assumes caller-enforced preconditions**. Both approaches are meaningful, but callers must know the contract.

---

## 10. Git commit-message conventions

### 10.1 Subject line

- Every commit must have a meaningful, well-written **subject line**.
- Aim for **at most 50 characters**; **72 characters is the hard limit**.
- Use the **imperative mood**, e.g. `Add student search` rather than `Added student search` or `Adding student search`.
- **Capitalise** the first character of the main subject text.
- Do **not** end the subject with a full stop.
- An optional `<scope>:` or `<category>:` prefix may be used, e.g. `StudentParser: Handle blank names` or `chore: Update release date`.
- Conventional Commits is mentioned as another possible, more elaborate convention, **not a mandatory replacement** for the source's rules.

### 10.2 Body for non-trivial commits

- Non-trivial commits **should** have a body explaining the change.
- Put **one blank line** between subject and body.
- Wrap body text at **72 characters**.
- Separate body paragraphs with blank lines.
- Use paragraphs or bullet points as helpful.
- Explain **what changed and why**; the patch itself should reveal **how** the implementation works.
- Provide enough context for a reader to judge the merits of the change without inspecting the diff first.
- Avoid merely repeating text already present in comments on the modified code.
- A very long and unwieldy commit description may indicate that the change should be split into smaller, more focused commits.

The source recommends this conceptual body sequence:

1. Describe the existing situation in the **present tense**.
2. Explain why it needs to change.
3. Explain what the commit does, using **imperative language** where appropriate.
4. Explain why that approach is chosen.
5. Add other relevant context if necessary.

Avoid unnecessary words such as “currently” and “originally” when simply describing the existing situation. The word “Let's” is an optional device used in the source examples, not a required commit-message prefix.

```text
Make student search case-insensitive

Student lookup compares names using exact case. Users should not
need to remember the original capitalisation of a student's name.

Compare normalised names during lookup so common case variations
return the same matching records.
```

### 10.3 SourceTree editor recommendations

For developers writing messages in SourceTree, the source additionally recommends enabling its fixed-width **72-character column guide** and **commit-message spell checker**. These are tool-specific suggestions; the actual commit-message formatting requirements apply regardless of Git client.

---

## 11. Git branch naming conventions

- Give every branch a meaningful name using relevant keywords.
- Use **kebab-case** for branch names, e.g. `refactor-ui-tests`.
- If a branch relates to a numbered issue, use the format:

```text
<issue-number>-<keywords-from-issue-title>
```

Example: `1234-ui-freeze-error`.

---

## 12. Interpreting the textbook's collapsed exercises

The adapted error-handling page contains expandable questions and answer panels. The following are the **distinct design lessons** from those panels, retained here to prevent an agent from missing constraints that are absent from the visible teaching text:

| Exercise/example | Requirement or conclusion illustrated |
| --- | --- |
| Exceptions: benefits | Exceptions separate ordinary work from error handlers and propagate failures across the call stack; they do **not prevent** environmental faults. |
| Assertions: calculator and negative square root | An assertion failure from normal invalid user input indicates a **programming error in handling that case**; do not terminate the application unnecessarily. |
| Assertions versus exceptions | In the simplified teaching distinction, programmer defects are indicated by assertions; unusual user/environment conditions are indicated by exceptions. |
| Logging and aircraft black box | Logs assist diagnosis **after/during an incident**; they do not prevent incidents. |
| Compulsory City–Country link | An unchecked setter accepting `null` fails to enforce multiplicity `1`. |
| Immutable Account–Guarantor link | Reject a missing guarantor at construction and do not expose a setter for an association declared immutable. |
| Manager–Account link | Enforce both the collection's maximum size (eight in the exercise) and uniqueness. |
| Marriage relationships | Verify compulsory/exclusive participants and coordinate link formation and removal; do not leave inconsistent partners. |
| Teacher–Student links | Two separate unidirectional links do not, by themselves, demand bidirectional consistency; an explicit reset method can be safer than using `null` as a hidden remove instruction. |
| Club membership | Validate arguments and membership and preserve required minimum cardinality during removal (ten in the exercise). |
| Bidirectional Bank–Account | Centralised owner-managed association updates are possible under documented lifecycle assumptions. |
| Bidirectional Player–Region | Setters must coordinate both ends, handle existing links, reject invalid `null` where compulsory, and avoid reciprocal infinite recursion. |
| Referential integrity true/false | A bidirectional reference mismatch is a referential-integrity violation. |
| Defensive-programming trade-offs | More defensiveness can mean **greater safety but also more code, effort, complexity and runtime overhead**. |
| DbC statements | DbC can coexist with OOP; its core rule is that the **caller** is responsible for preconditions. |

These exercise-specific constants and domain assumptions must **not** be promoted into unrelated project-wide rules. Implement the invariants that apply to the actual project model.

---

## 13. Reference and precedence notes

The following four pages are the sources for this document:

1. **NUS CS2103/T adapted textbook: Error Handling**, including the *Defensive programming* section, neighbouring exceptions/assertions/logging and Design-by-Contract sections, and the answers inside expandable exercise panels.  
   <https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/errorHandling.html#defensive-programming>
2. **SE Education: Java logging conventions** (recommended logging API and four severity levels).  
   <https://se-education.org/guides/conventions/java/logging.html>
3. **SE Education: Java coding standard (all rules)** (naming, layout, imports, declarations, comments, Javadoc).  
   <https://se-education.org/guides/conventions/java/index.html>
4. **SE Education: Git conventions** (commit subjects, message bodies, branch names).  
   <https://se-education.org/guides/conventions/git.html>

The NUS site's textbook content is adapted from the SE Education textbook. Its expandable questions and answer panels have been consulted alongside the visible page content. Some original textbook examples are intentionally simplified or contain assumed helper methods; this document states their intended invariants rather than treating every sketch as production-ready, compilable code.

**Additional style fallback expressly specified by source 3:** <https://google.github.io/styleguide/javaguide.html>. That linked guide is a fallback for topics not covered by source 3, not a fifth page exhaustively transcribed here.

**Agent instruction:** Use this file as a readable policy reference. When implementing Java code or making Git commits, apply the matching sections, respect the distinction between mandatory conventions and conditional design guidance, and avoid inventing unrelated restrictions.
