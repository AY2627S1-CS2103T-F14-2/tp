package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentMultimapTest {

    @Test
    public void publicMethods_nullArguments_throwNullPointerException() {
        ArgumentMultimap multimap = new ArgumentMultimap();
        Prefix prefix = new Prefix("p/");

        assertThrows(NullPointerException.class, () -> multimap.put(null, "value"));
        assertThrows(NullPointerException.class, () -> multimap.put(prefix, null));
        assertThrows(NullPointerException.class, () -> multimap.getValue(null));
        assertThrows(NullPointerException.class, () -> multimap.getAllValues(null));
        assertThrows(NullPointerException.class, () -> multimap.verifyNoDuplicatePrefixesFor((Prefix[]) null));
        assertThrows(NullPointerException.class, ()
                -> multimap.verifyNoDuplicatePrefixesFor(new Prefix[] {null}));
    }

    @Test
    public void putAndVerifyDuplicatePrefixes_preservesValues() throws ParseException {
        ArgumentMultimap multimap = new ArgumentMultimap();
        Prefix prefix = new Prefix("p/");
        multimap.put(prefix, "first");
        multimap.put(prefix, "second");

        assertEquals("second", multimap.getValue(prefix).orElseThrow());
        assertEquals(java.util.List.of("first", "second"), multimap.getAllValues(prefix));
        assertThrows(ParseException.class, () -> multimap.verifyNoDuplicatePrefixesFor(prefix));
    }
}
