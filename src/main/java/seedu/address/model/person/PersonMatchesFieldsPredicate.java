package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/** Tests whether a student matches field-specific search terms. */
public class PersonMatchesFieldsPredicate implements Predicate<Person> {
    /** Supported fields for a find query. */
    public enum SearchField {
        NAME, PHONE, SUBJECT, LEVEL, EMAIL, ADDRESS, TAG
    }

    /** The ways that supplied search terms can be combined. */
    public enum MatchMode {
        ALL, ANY
    }

    /** A field and its search value. */
    public record SearchTerm(SearchField field, String value) {
        /** Creates a non-null field-specific search term. */
        public SearchTerm {
            requireNonNull(field);
            requireNonNull(value);
        }
    }

    private final List<SearchTerm> searchTerms;
    private final MatchMode matchMode;

    /** Creates a predicate that combines the given terms according to {@code matchMode}. */
    public PersonMatchesFieldsPredicate(List<SearchTerm> searchTerms, MatchMode matchMode) {
        this.searchTerms = List.copyOf(requireNonNull(searchTerms));
        this.matchMode = requireNonNull(matchMode);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        if (searchTerms.isEmpty()) {
            return false;
        }
        if (matchMode == MatchMode.ALL) {
            return searchTerms.stream().allMatch(term -> matches(person, term));
        }
        return searchTerms.stream().anyMatch(term -> matches(person, term));
    }

    private boolean matches(Person person, SearchTerm searchTerm) {
        return switch (searchTerm.field()) {
            case NAME -> contains(person.getName().fullName, searchTerm.value());
            case PHONE -> containsPhone(person.getPhone() == null ? null : person.getPhone().value,
                    searchTerm.value());
            case SUBJECT -> person.getSubjects().stream().anyMatch(subject -> contains(subject, searchTerm.value()));
            case LEVEL -> contains(person.getLevel(), searchTerm.value());
            case EMAIL -> person.getEmail() != null && contains(person.getEmail().value, searchTerm.value());
            case ADDRESS -> person.getAddress() != null && contains(person.getAddress().value, searchTerm.value());
            case TAG -> person.getTags().stream().anyMatch(tag -> contains(tag.tagName, searchTerm.value()));
        };
    }

    private boolean contains(String fieldValue, String searchValue) {
        return fieldValue != null && searchValue != null && normalize(fieldValue).contains(normalize(searchValue));
    }

    /** Returns whether phone values match after ignoring their optional leading plus signs. */
    private boolean containsPhone(String fieldValue, String searchValue) {
        if (fieldValue == null || searchValue == null) {
            return false;
        }
        String normalizedFieldValue = StudentFields.phoneKey(fieldValue);
        String normalizedSearchValue = StudentFields.phoneKey(searchValue);
        return !normalizedSearchValue.isEmpty() && normalizedFieldValue.contains(normalizedSearchValue);
    }

    private String normalize(String value) {
        return StudentFields.normalizeWhitespace(value).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonMatchesFieldsPredicate otherPredicate)) {
            return false;
        }
        return searchTerms.equals(otherPredicate.searchTerms) && matchMode == otherPredicate.matchMode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("searchTerms", searchTerms)
                .add("matchMode", matchMode)
                .toString();
    }
}
