package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Subject;
import seedu.address.model.tag.Tag;

/** JSON representation supporting both legacy contacts and student records. */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedPerson {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> subjects;
    private final String level;

    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("subjects") List<String> subjects,
            @JsonProperty("level") String level) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.subjects = subjects == null ? null : new ArrayList<>(subjects);
        this.level = level;
    }

    /** Legacy contact constructor retained for callers and old JSON fixtures. */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, tags, null, null);
    }

    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().isPresent() ? source.getEmail().value : null;
        address = source.isStudent() ? null : source.getAddress().value;
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
        subjects = source.isStudent()
                ? source.getSubjects().stream().map(subject -> subject.value).collect(Collectors.toList()) : null;
        level = source.isStudent() ? source.getLevel().value : null;
    }

    /** Validates saved records. Partial student fields never silently become legacy contacts. */
    public Person toModelType() throws IllegalValueException {
        requireField(name, Name.class.getSimpleName());
        requireField(phone, Phone.class.getSimpleName());
        try {
            if (subjects != null || level != null) {
                requireField(subjects, Subject.class.getSimpleName());
                requireField(level, Level.class.getSimpleName());
                List<Subject> modelSubjects = new ArrayList<>();
                for (String subject : subjects) {
                    requireField(subject, Subject.class.getSimpleName());
                    modelSubjects.add(new Subject(subject));
                }
                return new Person(new Name(name), new Phone(phone),
                        email == null ? Email.absent() : new Email(email), modelSubjects, new Level(level));
            }
            requireField(email, Email.class.getSimpleName());
            requireField(address, Address.class.getSimpleName());
            List<Tag> modelTags = new ArrayList<>();
            for (JsonAdaptedTag tag : tags) {
                modelTags.add(tag.toModelType());
            }
            return new Person(Name.fromLegacy(name), Phone.fromLegacy(phone), Email.fromLegacy(email),
                    new Address(address), new HashSet<>(modelTags));
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }

    private static void requireField(Object value, String field) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, field));
        }
    }
}
