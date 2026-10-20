package seedu.address.model;

import java.util.function.Predicate;

import seedu.address.model.person.Person;

/**
 * An opaque checkpoint of roster data, undo/redo history, and the displayed-list filter.
 * ModelManager uses checkpoints to prepare changes without publishing them before saving.
 */
public final class ModelSnapshot {
    private final VersionedAddressBook addressBook;
    private final Predicate<? super Person> filter;

    ModelSnapshot(VersionedAddressBook addressBook, Predicate<? super Person> filter) {
        this.addressBook = new VersionedAddressBook(addressBook);
        this.filter = filter;
    }

    VersionedAddressBook copyAddressBook() {
        return new VersionedAddressBook(addressBook);
    }

    Predicate<? super Person> getFilter() {
        return filter;
    }
}
