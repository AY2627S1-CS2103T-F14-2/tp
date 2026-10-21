package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/** Matches students against one or more selected profile fields. */
public final class StudentContainsKeywordsPredicate implements Predicate<Person> {
    /** Fields that may be searched by the {@code find} command. */
    public enum SearchField {
        NAME,
        PHONE,
        EMAIL,
        ADDRESS,
        SUBJECT,
        LEVEL,
        NOTES,
        TAG
    }

    /** The ways that multiple search criteria can be combined. */
    public enum MatchMode {
        ALL,
        ANY
    }

    private final Map<SearchField, List<String>> criteria;
    private final MatchMode matchMode;

    /** Creates a predicate that requires every supplied criterion to match. */
    public StudentContainsKeywordsPredicate(Map<SearchField, List<String>> criteria) {
        this(criteria, MatchMode.ALL);
    }

    /** Creates a predicate with explicit all or any criterion matching. */
    public StudentContainsKeywordsPredicate(Map<SearchField, List<String>> criteria, MatchMode matchMode) {
        requireNonNull(criteria);
        requireNonNull(matchMode);
        EnumMap<SearchField, List<String>> copiedCriteria = new EnumMap<>(SearchField.class);
        criteria.forEach((field, keywords) -> {
            requireNonNull(field);
            requireNonNull(keywords);
            List<String> copiedKeywords = new ArrayList<>(keywords);
            copiedKeywords.forEach(keyword -> requireNonNull(keyword));
            copiedCriteria.put(field, List.copyOf(copiedKeywords));
        });
        this.criteria = Collections.unmodifiableMap(copiedCriteria);
        this.matchMode = matchMode;
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        List<Boolean> results = criteria.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(keyword -> matches(entry.getKey(), keyword, person)))
                .toList();
        if (results.isEmpty()) {
            return false;
        }
        return matchMode == MatchMode.ALL ? results.stream().allMatch(Boolean::booleanValue)
                : results.stream().anyMatch(Boolean::booleanValue);
    }

    private static boolean matches(SearchField field, String keyword, Person person) {
        if (keyword.isBlank()) {
            return false;
        }
        String normalisedKeyword = keyword.toLowerCase(Locale.ROOT);
        return valuesFor(field, person).stream()
                .anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(normalisedKeyword));
    }

    private static List<String> valuesFor(SearchField field, Person person) {
        return switch (field) {
            case NAME -> List.of(person.getName().fullName);
            case PHONE -> List.of(person.getPhone().value);
            case EMAIL -> person.getEmail() == null ? List.of() : List.of(person.getEmail().value);
            case ADDRESS -> person.getAddress() == null ? List.of() : List.of(person.getAddress().value);
            case SUBJECT -> person.getSubjects();
            case LEVEL -> person.getLevel() == null ? List.of() : List.of(person.getLevel());
            case NOTES -> person.getNotes().isEmpty() ? List.of() : List.of(person.getNotes());
            case TAG -> person.getTags().stream().map(tag -> tag.tagName).toList();
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof StudentContainsKeywordsPredicate otherPredicate)) {
            return false;
        }
        return criteria.equals(otherPredicate.criteria) && matchMode == otherPredicate.matchMode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("criteria", criteria)
                .add("matchMode", matchMode)
                .toString();
    }
}
