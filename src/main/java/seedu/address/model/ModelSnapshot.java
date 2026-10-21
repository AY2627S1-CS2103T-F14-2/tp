package seedu.address.model;

import java.util.function.Predicate;

import seedu.address.model.person.Person;

/**
 * An opaque checkpoint of roster data, undo/redo history, and the displayed-list filter.
 * ModelManager uses checkpoints to prepare changes without publishing them before saving.
 */
public final class ModelSnapshot {
    private final VersionedTutorRoster roster;
    private final Predicate<? super Person> filter;

    ModelSnapshot(VersionedTutorRoster roster, Predicate<? super Person> filter) {
        this.roster = new VersionedTutorRoster(roster);
        this.filter = filter;
    }

    VersionedTutorRoster copyRoster() {
        return new VersionedTutorRoster(roster);
    }

    Predicate<? super Person> getFilter() {
        return filter;
    }
}
