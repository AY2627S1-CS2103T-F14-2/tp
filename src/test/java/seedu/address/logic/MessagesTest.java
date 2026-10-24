package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;

public class MessagesTest {

    @Test
    public void publicMethods_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> Messages.getErrorMessageForDuplicatePrefixes((Prefix[]) null));
        assertThrows(NullPointerException.class, ()
                -> Messages.getErrorMessageForDuplicatePrefixes(new Prefix[] {null}));
        assertThrows(NullPointerException.class, () -> Messages.format(null));
    }

    @Test
    public void formatError_standardisesPrefixAndWhitespace() {
        assertEquals("Error: Invalid command format.", Messages.formatError("  Invalid command format.  "));
        assertEquals("Error: Already formatted.", Messages.formatError("Error: Already formatted."));
        assertEquals("Error: Details\nAction: retry.", Messages.formatError("Details\r\nAction: retry."));
        assertEquals("Error: An unexpected error occurred.", Messages.formatError(null));
    }
}
