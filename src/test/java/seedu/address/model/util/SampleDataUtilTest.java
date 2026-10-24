package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void sampleData_containsExpectedStudentsAndTags() {
        assertEquals(6, SampleDataUtil.getSamplePersons().length);
        ReadOnlyAddressBook addressBook = SampleDataUtil.getSampleAddressBook();
        assertEquals(6, addressBook.getPersonList().size());
        assertEquals(Set.of(new Tag("exam-prep"), new Tag("group-class")),
                addressBook.getPersonList().getFirst().getTags());
    }

    @Test
    public void getTagSet_nullValues_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> SampleDataUtil.getTagSet((String[]) null));
        assertThrows(NullPointerException.class, () -> SampleDataUtil.getTagSet("valid", null));
    }
}
