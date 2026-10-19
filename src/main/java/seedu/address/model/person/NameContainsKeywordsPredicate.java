package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a {@code Person}'s {@code Name} matches the given keywords, ignoring case and allowing partial matches.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    /** The ways that multiple keywords can be combined in a name search. */
    public enum MatchMode {
        ALL,
        ANY
    }

    private final List<String> keywords;
    private final MatchMode matchMode;

    /**
     * Creates a predicate that matches names against all of the given keywords.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, MatchMode.ALL);
    }

    /**
     * Creates a predicate that matches names according to the given {@code matchMode}.
     *
     * @throws NullPointerException if {@code keywords}, any keyword, or {@code matchMode} is null.
     */
    public NameContainsKeywordsPredicate(List<String> keywords, MatchMode matchMode) {
        this.keywords = List.copyOf(requireNonNull(keywords));
        this.matchMode = requireNonNull(matchMode);
    }

    @Override
    public boolean test(Person person) {
        String normalizedName = person.getName().fullName.toLowerCase(Locale.ROOT);

        if (keywords.isEmpty()) {
            return false;
        }

        Predicate<String> nameContainsKeyword = keyword -> !keyword.isBlank()
                && normalizedName.contains(keyword.toLowerCase(Locale.ROOT));
        if (matchMode == MatchMode.ALL) {
            return keywords.stream().allMatch(nameContainsKeyword);
        }
        return keywords.stream().anyMatch(nameContainsKeyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords)
                && matchMode == otherNameContainsKeywordsPredicate.matchMode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", keywords)
                .add("matchMode", matchMode)
                .toString();
    }
}
