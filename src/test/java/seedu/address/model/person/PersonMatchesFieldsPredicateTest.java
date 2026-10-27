package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.PersonMatchesFieldsPredicate.MatchMode;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchField;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchTerm;

public class PersonMatchesFieldsPredicateTest {

    private static final Person ALEX_ALEXANDER = new Person("Alex Alexander", "91234567", "alex@example.com",
            List.of("Mathematics", "English"), "Secondary 4");

    @Test
    public void test_nameMatchesPartialText_returnsExpectedResult() {
        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.NAME, "Alex A")), MatchMode.ALL);

        assertTrue(predicate.test(ALEX_ALEXANDER));
        assertFalse(predicate.test(new Person("Alex Yeoh", "92345678", "yeoh@example.com",
                List.of("Mathematics"), "Secondary 4")));
        assertTrue(predicate.test(new Person("Salex Alexander", "93456789", "salex@example.com",
                List.of("Mathematics"), "Secondary 4")));
    }

    @Test
    public void test_allFieldsMatch_returnsTrue() {
        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.PHONE, "1234"),
                        new SearchTerm(SearchField.SUBJECT, "Math"),
                        new SearchTerm(SearchField.SUBJECT, "English"),
                        new SearchTerm(SearchField.LEVEL, "secondary 4"),
                        new SearchTerm(SearchField.EMAIL, "EXAMPLE.COM")),
                MatchMode.ALL);

        assertTrue(predicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void test_phoneMatchesWithOrWithoutLeadingPlus_returnsTrue() {
        Person personWithPlus = new Person("Alex Alexander", "+6591234567", "alex@example.com",
                List.of("Mathematics"), "Secondary 4");
        Person personWithoutPlus = new Person("Alex Alexander", "6591234567", "alex@example.com",
                List.of("Mathematics"), "Secondary 4");
        PersonMatchesFieldsPredicate withoutPlusPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.PHONE, "659123")), MatchMode.ALL);
        PersonMatchesFieldsPredicate withPlusPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.PHONE, "+659123")), MatchMode.ALL);

        assertTrue(withoutPlusPredicate.test(personWithPlus));
        assertTrue(withoutPlusPredicate.test(personWithoutPlus));
        assertTrue(withPlusPredicate.test(personWithPlus));
        assertTrue(withPlusPredicate.test(personWithoutPlus));
    }

    @Test
    public void test_anyFieldMatches_returnsTrue() {
        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.NAME, "Beatrice"),
                        new SearchTerm(SearchField.SUBJECT, "English")), MatchMode.ANY);

        assertTrue(predicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void test_repeatedFieldValues_followMatchMode() {
        PersonMatchesFieldsPredicate allPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.NAME, "Alex"),
                        new SearchTerm(SearchField.NAME, "Beatrice")), MatchMode.ALL);
        PersonMatchesFieldsPredicate anyPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.NAME, "Beatrice"),
                        new SearchTerm(SearchField.NAME, "Alex")), MatchMode.ANY);

        assertFalse(allPredicate.test(ALEX_ALEXANDER));
        assertTrue(anyPredicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void test_noTermsOrNoMatches_returnsFalse() {
        PersonMatchesFieldsPredicate noTermsPredicate = new PersonMatchesFieldsPredicate(List.of(), MatchMode.ALL);
        PersonMatchesFieldsPredicate noMatchesPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.NAME, "Beatrice")), MatchMode.ANY);

        assertFalse(noTermsPredicate.test(ALEX_ALEXANDER));
        assertFalse(noMatchesPredicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFieldsPredicate(null, MatchMode.ALL));
        assertThrows(NullPointerException.class, () -> new PersonMatchesFieldsPredicate(List.of(), null));
        assertThrows(NullPointerException.class, () -> new SearchTerm(null, "Alex"));
        assertThrows(NullPointerException.class, () -> new SearchTerm(SearchField.NAME, null));
    }

    @Test
    public void test_optionalFieldMissing_returnsFalse() {
        Person personWithoutEmail = new Person("Alex Alexander", "91234567", null,
                List.of("Mathematics"), "Secondary 4");
        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.EMAIL, "example.com")), MatchMode.ALL);

        assertFalse(predicate.test(personWithoutEmail));
    }

    @Test
    public void test_addressMatchesAndMissingAddress_returnsExpectedResult() {
        Person studentWithAddress = new Person("Alex Alexander", "91234567", null,
                List.of("Mathematics"), "Secondary 4", "", new Address("12 Main Street"));
        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.ADDRESS, "main street")), MatchMode.ALL);

        assertTrue(predicate.test(studentWithAddress));
        assertFalse(predicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void test_missingAndNonMatchingFields_returnsFalse() {
        Person legacyPersonWithoutLevel = new Person(new Name("Alex Alexander"), new Phone("91234567"),
                new Email("alex@example.com"), new Address("1 Main Street"), Set.of());
        PersonMatchesFieldsPredicate missingLevelPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.LEVEL, "Secondary 4")), MatchMode.ALL);
        PersonMatchesFieldsPredicate nonMatchingEmailPredicate = new PersonMatchesFieldsPredicate(
                List.of(new SearchTerm(SearchField.EMAIL, "other@example.com")), MatchMode.ALL);

        assertFalse(missingLevelPredicate.test(legacyPersonWithoutLevel));
        assertFalse(nonMatchingEmailPredicate.test(ALEX_ALEXANDER));
    }

    @Test
    public void equals_returnsExpectedResult() {
        PersonMatchesFieldsPredicate firstPredicate = prepareNamePredicate("Alex", MatchMode.ALL);
        PersonMatchesFieldsPredicate secondPredicate = prepareNamePredicate("Alexander", MatchMode.ALL);
        PersonMatchesFieldsPredicate anyPredicate = prepareNamePredicate("Alex", MatchMode.ANY);

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(prepareNamePredicate("Alex", MatchMode.ALL)));
        assertFalse(firstPredicate.equals(secondPredicate));
        assertFalse(firstPredicate.equals(anyPredicate));
        assertFalse(firstPredicate.equals("predicate"));
        assertFalse(firstPredicate.equals(null));
    }

    private PersonMatchesFieldsPredicate prepareNamePredicate(String value, MatchMode matchMode) {
        return new PersonMatchesFieldsPredicate(List.of(new SearchTerm(SearchField.NAME, value)), matchMode);
    }
}
