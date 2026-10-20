package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores session-only roster snapshots and the position of the current committed state.
 * Snapshots are private copies; changing the working roster never changes an earlier state.
 */
public class VersionedAddressBook extends AddressBook {
    private final List<AddressBook> states = new ArrayList<>();
    private int currentStatePointer;

    /**
     * Creates a history whose initial state is the supplied roster.
     */
    public VersionedAddressBook(ReadOnlyAddressBook initialState) {
        super(initialState);
        states.add(new AddressBook(initialState));
    }

    /**
     * Copies both the working roster and its history, without sharing mutable roster data.
     */
    public VersionedAddressBook(VersionedAddressBook source) {
        super(source);
        states.addAll(source.states);
        currentStatePointer = source.currentStatePointer;
        assert hasValidPointer() : "History must contain the current state";
    }

    /**
     * Records a changed roster and discards any states that could previously be redone.
     * An unchanged roster leaves redo available.
     */
    public void commit() {
        assert hasValidPointer() : "History must contain the current state";
        if (equals(states.get(currentStatePointer))) {
            return;
        }
        states.subList(currentStatePointer + 1, states.size()).clear();
        states.add(new AddressBook(this));
        currentStatePointer++;
        assert currentStatePointer == states.size() - 1 : "A new change must end the redo branch";
    }

    /**
     * Returns whether a previous committed state exists.
     */
    public boolean canUndo() {
        assert hasValidPointer() : "History must contain the current state";
        return currentStatePointer > 0;
    }

    /**
     * Returns whether a later committed state exists.
     */
    public boolean canRedo() {
        assert hasValidPointer() : "History must contain the current state";
        return currentStatePointer < states.size() - 1;
    }

    /**
     * Restores the previous committed roster.
     *
     * @throws IllegalStateException if there is no previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("No roster change to undo");
        }
        assert equals(states.get(currentStatePointer)) : "Commit pending roster changes before undoing";
        resetData(states.get(--currentStatePointer));
    }

    /**
     * Restores the next committed roster.
     *
     * @throws IllegalStateException if there is no later state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("No roster change to redo");
        }
        assert equals(states.get(currentStatePointer)) : "Commit pending roster changes before redoing";
        resetData(states.get(++currentStatePointer));
    }

    /**
     * Replaces the working roster and history while keeping the observable list instance stable.
     */
    public void restoreFrom(VersionedAddressBook source) {
        requireNonNull(source);
        if (source == this) {
            return;
        }
        if (!equals(source)) {
            resetData(source);
        }
        states.clear();
        states.addAll(source.states);
        currentStatePointer = source.currentStatePointer;
        assert hasValidPointer() : "Restoring history must preserve its current state";
    }

    private boolean hasValidPointer() {
        return !states.isEmpty() && currentStatePointer >= 0 && currentStatePointer < states.size();
    }
}
