package seedu.address.logic;

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
}
