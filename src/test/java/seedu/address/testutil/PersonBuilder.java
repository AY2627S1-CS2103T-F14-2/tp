package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_LEVEL = "Secondary 4";
    public static final String DEFAULT_NOTES = "";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private List<String> subjects;
    private String level;
    private String notes;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = Name.fromStoredValue(DEFAULT_NAME);
        phone = Phone.fromStoredValue(DEFAULT_PHONE);
        email = Email.fromStoredValue(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        subjects = new ArrayList<>(List.of("Mathematics"));
        level = DEFAULT_LEVEL;
        notes = DEFAULT_NOTES;
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        subjects = new ArrayList<>(personToCopy.getSubjects());
        level = personToCopy.getLevel();
        notes = personToCopy.getNotes();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = Name.fromStoredValue(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = address == null ? null : new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = Phone.fromStoredValue(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = email == null ? null : Email.fromStoredValue(email);
        return this;
    }

    /**
     * Sets the subjects of the {@code Person} that we are building.
     */
    public PersonBuilder withSubjects(String ... subjects) {
        this.subjects = new ArrayList<>(Arrays.asList(subjects));
        return this;
    }

    /**
     * Sets the education level of the {@code Person} that we are building.
     */
    public PersonBuilder withLevel(String level) {
        this.level = level;
        return this;
    }

    /**
     * Sets the notes of the {@code Person} that we are building.
     */
    public PersonBuilder withNotes(String notes) {
        this.notes = notes == null ? "" : notes;
        return this;
    }

    /**
     * Builds and returns a {@code Person}.
     */
    public Person build() {
        return new Person(name.fullName, phone.value, email == null ? null : email.value,
                subjects, level, notes, address).withTags(tags);
    }

}
