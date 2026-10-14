package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    @Test
    public void navigation_restoresDraftAfterVisitingHistory() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("find Ryan");

        assertEquals("find Ryan", commandHistory.previous("draft"));
        assertEquals("list", commandHistory.previous("find Ryan"));
        assertEquals("find Ryan", commandHistory.next());
        assertEquals("draft", commandHistory.next());
    }

    @Test
    public void add_consecutiveDuplicate_keepsOneHistoryEntry() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("list");

        assertEquals("list", commandHistory.previous("draft"));
        assertEquals("draft", commandHistory.next());
    }

    @Test
    public void resetNavigation_discardsPreviousDraft() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.previous("old draft");
        commandHistory.resetNavigation();

        assertEquals("list", commandHistory.previous("new draft"));
        assertEquals("new draft", commandHistory.next());
    }
}
