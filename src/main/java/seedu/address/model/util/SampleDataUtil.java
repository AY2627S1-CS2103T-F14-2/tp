package seedu.address.model.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person("Alex Yeoh", "87438807", "alexyeoh@example.com",
                    List.of("Mathematics"), "Secondary 4"),
            new Person("Bernice Yu", "99272758", "berniceyu@example.com",
                    List.of("English Language", "Literature"), "JC 1"),
            new Person("Charlotte Oliveiro", "93210283", null,
                    List.of("Physics"), "Secondary 3"),
            new Person("David Li", "91031282", "lidavid@example.com",
                    List.of("Chemistry"), "JC 2"),
            new Person("Irfan Ibrahim", "92492021", "irfan@example.com",
                    List.of("Mathematics", "Physics"), "Grade 8"),
            new Person("Roy Balakrishnan", "92624417", null,
                    List.of("English"), "Primary 6")
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
