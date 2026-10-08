package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a student or a legacy contact in the shared roster.
 * Student records have subjects and level; legacy contacts have address and tags.
 * Field values are immutable. Email.absent() represents an omitted student email.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Subject> subjects;
    private final Level level;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        subjects = List.of();
        level = null;
    }

    /** Creates a student; omitted email is represented by {@link Email#absent()}. */
    public Person(Name name, Phone phone, Email email, List<Subject> subjects, Level level) {
        requireAllNonNull(name, phone, email, subjects, level);
        if (subjects.isEmpty()) {
            throw new IllegalArgumentException("At least one subject is required.");
        }
        for (int i = 0; i < subjects.size(); i++) {
            for (int j = i + 1; j < subjects.size(); j++) {
                if (subjects.get(i).value.equalsIgnoreCase(subjects.get(j).value)) {
                    throw new IllegalArgumentException("Duplicate subject: " + subjects.get(i));
                }
            }
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = null;
        this.subjects = List.copyOf(subjects);
        this.level = level;
    }

    public boolean isStudent() {
        return level != null;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public Level getLevel() {
        return level;
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

    /**
     * Returns true if normalized names match and either phone or present email matches.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && StudentText.normalize(otherPerson.getName().fullName)
                        .equalsIgnoreCase(StudentText.normalize(getName().fullName))
                && (otherPerson.getPhone().value.replaceFirst("^\\+", "")
                        .equals(getPhone().value.replaceFirst("^\\+", ""))
                    || (email.isPresent() && otherPerson.email.isPresent()
                        && email.value.equalsIgnoreCase(otherPerson.email.value)));
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

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && Objects.equals(address, otherPerson.address)
                && tags.equals(otherPerson.tags)
                && subjects.equals(otherPerson.subjects)
                && Objects.equals(level, otherPerson.level);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, subjects, level);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
