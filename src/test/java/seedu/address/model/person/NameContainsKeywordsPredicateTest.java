package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.NameContainsKeywordsPredicate.MatchMode;
import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(null));
        assertThrows(NullPointerException.class, () ->
                new NameContainsKeywordsPredicate(List.of("Alice"), null));
    }

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        NameContainsKeywordsPredicate anyMatchPredicate =
                new NameContainsKeywordsPredicate(firstPredicateKeywordList, MatchMode.ANY);
        assertFalse(firstPredicate.equals(anyMatchPredicate));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_allKeywordsMatch_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameContainsPartialKeywords_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of("aLI")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("LIC")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("ICE")).test(person));
    }

    @Test
    public void test_notAllKeywordsMatch_returnsFalse() {
        Person person = new PersonBuilder().withName("Alice Bob").build();

        assertFalse(new NameContainsKeywordsPredicate(List.of("Carol", "oB")).test(person));
    }

    @Test
    public void test_anyKeywordMatches_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").build();
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(List.of("Carol", "oB"), MatchMode.ANY);

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_blankKeywords_returnsFalse() {
        Person person = new PersonBuilder().withName("Alice Bob").build();

        assertFalse(new NameContainsKeywordsPredicate(List.of("", " ", "\t")).test(person));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + ", matchMode=ALL}";
        assertEquals(expected, predicate.toString());
    }
}
