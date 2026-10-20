package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandCompletionLogicTest {

    private final CommandCompletionLogic completionLogic = new CommandCompletionLogic();

    @Test
    public void getSuggestions_commandWordPrefix_returnsMatchingCommands() {
        List<CommandCompletionLogic.Suggestion> sMatches = completionLogic.getSuggestions("s");
        assertEquals(1, sMatches.size());
        assertEquals("show ", sMatches.get(0).completionText());
        assertEquals("show INDEX", sMatches.get(0).formatText());

        List<CommandCompletionLogic.Suggestion> aMatches = completionLogic.getSuggestions("ad");
        assertEquals(1, aMatches.size());
        assertEquals("add ", aMatches.get(0).completionText());

        List<CommandCompletionLogic.Suggestion> unknown = completionLogic.getSuggestions("xyz");
        assertTrue(unknown.isEmpty());
    }

    @Test
    public void autocomplete_commandWordPrefix_returnsCompletedCommand() {
        Optional<String> completed = completionLogic.autocomplete("sh");
        assertTrue(completed.isPresent());
        assertEquals("show ", completed.get());

        Optional<String> listCompleted = completionLogic.autocomplete("li");
        assertTrue(listCompleted.isPresent());
        assertEquals("list ", listCompleted.get());
    }

    @Test
    public void getSuggestions_addCommand_suggestsMissingRequiredPrefixes() {
        List<CommandCompletionLogic.Suggestion> initialAdd = completionLogic.getSuggestions("add ");
        assertFalse(initialAdd.isEmpty());
        // First missing required is n/
        assertEquals("add n/", initialAdd.get(0).completionText());

        // When n/ is already present, next is p/
        List<CommandCompletionLogic.Suggestion> afterName = completionLogic.getSuggestions("add n/Ryan Tan ");
        assertFalse(afterName.isEmpty());
        assertEquals("add n/Ryan Tan p/", afterName.get(0).completionText());
    }

    @Test
    public void getSuggestions_addCommandWithPartialPrefix_completesPrefix() {
        List<CommandCompletionLogic.Suggestion> partialName = completionLogic.getSuggestions("add n");
        assertEquals(1, partialName.size());
        assertEquals("add n/", partialName.get(0).completionText());

        List<CommandCompletionLogic.Suggestion> partialPhone = completionLogic.getSuggestions("add n/Ryan Tan p");
        assertEquals(1, partialPhone.size());
        assertEquals("add n/Ryan Tan p/", partialPhone.get(0).completionText());
    }

    @Test
    public void getSuggestions_addCommandOptionalAndRepeatablePrefixes() {
        // When all required prefixes (n/, p/, s/, l/) are provided
        String allRequired = "add n/Ryan p/81234567 s/Math l/Sec 4 ";
        List<CommandCompletionLogic.Suggestion> suggestions = completionLogic.getSuggestions(allRequired);
        assertFalse(suggestions.isEmpty());

        // s/ is repeatable so it should still be suggested
        boolean hasRepeatableSubject = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("s/"));
        assertTrue(hasRepeatableSubject);

        // e/ and r/ are optional so they should be suggested
        boolean hasEmail = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("e/"));
        assertTrue(hasEmail);

        boolean hasNotes = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("r/"));
        assertTrue(hasNotes);
    }

    @Test
    public void getSuggestions_findCommand_suggestsAnyFlag() {
        List<CommandCompletionLogic.Suggestion> findSuggestions = completionLogic.getSuggestions("find ");
        assertEquals(1, findSuggestions.size());
        assertEquals("find --any ", findSuggestions.get(0).completionText());

        List<CommandCompletionLogic.Suggestion> partialFlag = completionLogic.getSuggestions("find --a");
        assertEquals(1, partialFlag.size());
        assertEquals("find --any ", partialFlag.get(0).completionText());
    }
}
