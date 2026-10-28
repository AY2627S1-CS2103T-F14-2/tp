package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * A student record in TutorRoster.
 */
public class Person {

    private final Name name;
    private final Phone phone;
    private final Email email;
    private final List<String> subjects;
    private final String level;
    private final String notes;
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /** Creates a new student after validating all fields. Email may be absent. */
    public Person(String name, String phone, String email, List<String> subjects, String level) {
        this(name, phone, email, subjects, level, "");
    }

    /** Creates a new student after validating all fields. Email and notes may be absent. */
    public Person(String name, String phone, String email, List<String> subjects, String level, String notes) {
        this(name, phone, email, subjects, level, notes, null);
    }

    /** Creates a new student after validating all fields. Optional fields may be absent. */
    public Person(String name, String phone, String email, List<String> subjects, String level, String notes,
            Address address) {
        requireAllNonNull(name, phone, subjects, level);
        String normalizedName = StudentFields.normalizeName(name);
        if (!StudentFields.isValidName(normalizedName)) {
            throw new IllegalArgumentException(StudentFields.INVALID_NAME);
        }
        if (!StudentFields.isValidPhone(phone)) {
            throw new IllegalArgumentException(StudentFields.INVALID_PHONE);
        }
        if (email != null && !StudentFields.isValidEmail(email)) {
            throw new IllegalArgumentException(StudentFields.INVALID_EMAIL);
        }
        if (subjects.isEmpty()) {
            throw new IllegalArgumentException(StudentFields.INVALID_SUBJECT);
        }
        Set<String> subjectKeys = new HashSet<>();
        List<String> normalizedSubjects = new ArrayList<>();
        for (String subject : subjects) {
            if (subject == null) {
                throw new IllegalArgumentException(StudentFields.INVALID_SUBJECT);
            }
            String normalizedSubject = StudentFields.normalizeWhitespace(subject);
            if (!StudentFields.isValidSubject(normalizedSubject)) {
                throw new IllegalArgumentException(StudentFields.INVALID_SUBJECT);
            }
            if (!subjectKeys.add(StudentFields.subjectKey(normalizedSubject))) {
                throw new IllegalArgumentException("Duplicate subject: " + normalizedSubject);
            }
            normalizedSubjects.add(normalizedSubject);
        }
        String normalizedLevel = StudentFields.normalizeWhitespace(level);
        if (!StudentFields.isValidLevel(normalizedLevel)) {
            throw new IllegalArgumentException(StudentFields.INVALID_LEVEL);
        }
        String normalizedNotes = notes == null ? "" : StudentFields.trim(notes);
        if (!normalizedNotes.isEmpty() && !StudentFields.isValidNotes(normalizedNotes)) {
            throw new IllegalArgumentException(StudentFields.INVALID_NOTES);
        }
        this.name = Name.fromStoredValue(normalizedName);
        this.phone = Phone.fromStoredValue(phone);
        this.email = email == null ? null : Email.fromStoredValue(email);
        this.subjects = List.copyOf(normalizedSubjects);
        this.level = normalizedLevel;
        this.notes = normalizedNotes;
        this.address = address;
    }

    private Person(String name, String phone, String email, List<String> subjects, String level, String notes,
            Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, subjects, level, tags);
        this.name = Name.fromStoredValue(name);
        this.phone = Phone.fromStoredValue(phone);
        this.email = email == null ? null : Email.fromStoredValue(email);
        this.subjects = Collections.unmodifiableList(new ArrayList<>(subjects));
        this.level = level;
        String normalizedNotes = notes == null ? "" : StudentFields.trim(notes);
        if (!normalizedNotes.isEmpty() && !StudentFields.isValidNotes(normalizedNotes)) {
            throw new IllegalArgumentException(StudentFields.INVALID_NOTES);
        }
        this.notes = normalizedNotes;
        this.address = address;
        this.tags.addAll(tags);
    }

    /** Restores an already stored record without applying new-add validation to existing data. */
    public static Person fromStoredRecord(String name, String phone, String email, List<String> subjects,
            String level, Address address, Set<Tag> tags) {
        return fromStoredRecord(name, phone, email, subjects, level, "", address, tags);
    }

    /** Restores a stored record with notes. */
    public static Person fromStoredRecord(String name, String phone, String email, List<String> subjects,
            String level, String notes, Address address, Set<Tag> tags) {
        return new Person(name, phone, email, subjects, level, notes, address, tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getLevel() {
        return level;
    }

    /** Returns tutoring-specific notes, or an empty string when no notes were supplied. */
    public String getNotes() {
        return notes;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /** Copies this record with new tags, preserving every other field. */
    public Person withTags(Set<Tag> replacementTags) {
        requireAllNonNull(replacementTags);
        return new Person(name.fullName, phone.value, email == null ? null : email.value,
                subjects, level, notes, address, replacementTags);
    }

    /**
     * Copies a student with replacement student fields and tags.
     * The public student constructor performs the complete validation before the copy is returned.
     */
    public Person withStudentDetails(String replacementName, String replacementPhone, String replacementEmail,
            List<String> replacementSubjects, String replacementLevel, String replacementNotes,
            Set<Tag> replacementTags) {
        return withStudentDetails(replacementName, replacementPhone, replacementEmail, replacementSubjects,
                replacementLevel, replacementNotes, address, replacementTags);
    }

    /** Copies a student with replacement student fields, address, and tags. */
    public Person withStudentDetails(String replacementName, String replacementPhone, String replacementEmail,
            List<String> replacementSubjects, String replacementLevel, String replacementNotes,
            Address replacementAddress, Set<Tag> replacementTags) {
        requireAllNonNull(replacementName, replacementPhone, replacementSubjects, replacementLevel, replacementTags);
        return new Person(replacementName, replacementPhone, replacementEmail, replacementSubjects,
                replacementLevel, replacementNotes, replacementAddress).withTags(replacementTags);
    }

    /**
     * Returns true if both students have the same normalized name and either the same phone or same email.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        if (otherPerson == null) {
            return false;
        }
        boolean sameName = StudentFields.nameKey(name.fullName)
                .equals(StudentFields.nameKey(otherPerson.name.fullName));
        boolean samePhone = StudentFields.phoneKey(phone.value)
                .equals(StudentFields.phoneKey(otherPerson.phone.value));
        boolean sameEmail = email != null && otherPerson.email != null
                && StudentFields.emailKey(email.value).equals(StudentFields.emailKey(otherPerson.email.value));
        return sameName && (samePhone || sameEmail);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && Objects.equals(email, otherPerson.email)
                && subjects.equals(otherPerson.subjects)
                && Objects.equals(level, otherPerson.level)
                && notes.equals(otherPerson.notes)
                && Objects.equals(address, otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, subjects, level, notes, address, tags);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("subjects", subjects)
                .add("level", level)
                .add("address", address);
        if (!notes.isEmpty()) {
            builder.add("notes", notes);
        }
        return builder.add("tags", tags).toString();
    }

}
