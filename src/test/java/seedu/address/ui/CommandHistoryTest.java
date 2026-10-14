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
        assertEquals("find Ryan", commandHistory.next("list"));
        assertEquals("draft", commandHistory.next("find Ryan"));
    }

    @Test
    public void add_consecutiveDuplicate_keepsOneHistoryEntry() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("list");

        assertEquals("list", commandHistory.previous("draft"));
        assertEquals("draft", commandHistory.next("list"));
    }

    @Test
    public void resetNavigation_discardsPreviousDraft() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.previous("old draft");
        commandHistory.resetNavigation();

        assertEquals("list", commandHistory.previous("new draft"));
        assertEquals("new draft", commandHistory.next("list"));
    }

    @Test
    public void previous_emptyHistory_returnsCurrentText() {
        CommandHistory commandHistory = new CommandHistory();

        assertEquals("draft", commandHistory.previous("draft"));
    }

    @Test
    public void next_emptyHistory_returnsCurrentText() {
        CommandHistory commandHistory = new CommandHistory();

        assertEquals("draft", commandHistory.next("draft"));
    }

    @Test
    public void previous_atOldestCommand_keepsOldestCommand() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("find Ryan");

        commandHistory.previous("draft");
        commandHistory.previous("find Ryan");

        assertEquals("list", commandHistory.previous("list"));
    }

    @Test
    public void next_atNewestCommand_keepsDraft() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.previous("draft");

        assertEquals("draft", commandHistory.next("list"));
        assertEquals("draft", commandHistory.next("draft"));
    }

    @Test
    public void add_afterBrowsing_resetsHistoryPosition() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("find Ryan");
        commandHistory.previous("draft");
        commandHistory.add("delete 1");

        assertEquals("delete 1", commandHistory.previous("new draft"));
        assertEquals("new draft", commandHistory.next("delete 1"));
    }

    @Test
    public void add_nonConsecutiveDuplicate_preservesBothCommands() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.add("find Ryan");
        commandHistory.add("list");

        assertEquals("list", commandHistory.previous("draft"));
        assertEquals("find Ryan", commandHistory.previous("list"));
        assertEquals("list", commandHistory.previous("find Ryan"));
    }

    @Test
    public void previous_emptyDraft_restoresEmptyDraft() {
        CommandHistory commandHistory = new CommandHistory();
        commandHistory.add("list");
        commandHistory.previous("");

        assertEquals("", commandHistory.next("list"));
    }
}
