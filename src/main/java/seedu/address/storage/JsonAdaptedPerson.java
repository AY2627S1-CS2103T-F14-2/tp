package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags;
    private final List<String> subjects;
    private final String level;
    private final String notes;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("subjects") List<String> subjects, @JsonProperty("level") String level,
            @JsonProperty("notes") String notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags = tags == null ? null : new ArrayList<>(tags);
        this.subjects = subjects == null ? null : new ArrayList<>(subjects);
        this.level = level;
        this.notes = notes == null ? "" : notes;
    }

    /**
     * Constructs an adapted student without notes.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags,
            List<String> subjects, String level) {
        this(name, phone, email, address, tags, subjects, level, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail() == null ? null : source.getEmail().value;
        subjects = new ArrayList<>(source.getSubjects());
        level = source.getLevel();
        notes = source.getNotes().isEmpty() ? null : source.getNotes();
        address = source.getAddress() == null ? null : source.getAddress().value;
        tags = source.getTags().isEmpty() ? null : source.getTags().stream()
                .sorted(java.util.Comparator.comparing(tag -> tag.tagName))
                .map(JsonAdaptedTag::new).collect(Collectors.toList());
    }

    /** Checks whether loading this record normalizes spelling or merges repeated tags. */
    boolean requiresTagMigration() throws IllegalValueException {
        if (tags == null) {
            return false;
        }
        Set<Tag> uniqueTags = new HashSet<>();
        boolean changed = false;
        for (JsonAdaptedTag storedTag : tags) {
            if (storedTag == null) {
                throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
            }
            Tag normalized = storedTag.toModelType();
            changed |= !normalized.tagName.equals(storedTag.getTagName());
            changed |= !uniqueTags.add(normalized);
        }
        return changed;
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        String normalizedName = StudentFields.normalizeName(name);
        if (!StudentFields.isValidName(normalizedName)) {
            throw new IllegalValueException(StudentFields.INVALID_NAME);
        }

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        String trimmedPhone = StudentFields.trim(phone);
        if (!StudentFields.isValidPhone(trimmedPhone)) {
            throw new IllegalValueException(StudentFields.INVALID_PHONE);
        }

        String trimmedEmail = email == null ? null : StudentFields.trim(email);
        if (trimmedEmail != null) {
            if (!StudentFields.isValidEmail(trimmedEmail)) {
                throw new IllegalValueException(StudentFields.INVALID_EMAIL);
            }
        }

        if (address != null && !Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }

        if (subjects == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "subjects"));
        }
        if (subjects.isEmpty()) {
            throw new IllegalValueException(StudentFields.INVALID_SUBJECT);
        }
        List<String> normalizedSubjects = new ArrayList<>();
        Set<String> subjectKeys = new HashSet<>();
        for (String subject : subjects) {
            if (subject == null) {
                throw new IllegalValueException(StudentFields.INVALID_SUBJECT);
            }
            String normalizedSubject = StudentFields.normalizeWhitespace(subject);
            if (!StudentFields.isValidSubject(normalizedSubject)) {
                throw new IllegalValueException(StudentFields.INVALID_SUBJECT);
            }
            if (!subjectKeys.add(StudentFields.subjectKey(normalizedSubject))) {
                throw new IllegalValueException("Duplicate subject: " + normalizedSubject);
            }
            normalizedSubjects.add(normalizedSubject);
        }

        if (level == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "level"));
        }
        String normalizedLevel = StudentFields.normalizeWhitespace(level);
        if (!StudentFields.isValidLevel(normalizedLevel)) {
            throw new IllegalValueException(StudentFields.INVALID_LEVEL);
        }

        String storedNotes = notes == null ? "" : StudentFields.trim(notes);
        if (!storedNotes.isEmpty() && !StudentFields.isValidNotes(storedNotes)) {
            throw new IllegalValueException(StudentFields.INVALID_NOTES);
        }

        final List<Tag> personTags = new ArrayList<>();
        if (tags != null) {
            for (JsonAdaptedTag tag : tags) {
                if (tag == null) {
                    throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
                }
                personTags.add(tag.toModelType());
            }
        }

        Address storedAddress = address == null ? null : new Address(address);
        return Person.fromStoredRecord(normalizedName, StudentFields.normalizePhone(trimmedPhone), trimmedEmail,
                normalizedSubjects, normalizedLevel, storedNotes, storedAddress, new HashSet<>(personTags));
    }

}
