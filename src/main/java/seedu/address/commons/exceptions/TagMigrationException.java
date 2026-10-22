package seedu.address.commons.exceptions;

import java.io.IOException;

/** A migration could not be saved; startup must not fall back to an empty editable roster. */
public class TagMigrationException extends DataLoadingException {
    public TagMigrationException(IOException cause) {
        super(cause);
    }
}
