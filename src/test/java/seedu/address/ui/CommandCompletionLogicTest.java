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
    public void getSuggestions_nullOrEmptyInput_returnsEmptyList() {
        assertTrue(completionLogic.getSuggestions(null).isEmpty());
        assertTrue(completionLogic.getSuggestions("").isEmpty());
        assertTrue(completionLogic.getSuggestions("   ").isEmpty());
    }

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
    public void getSuggestions_addCommandPartialPrefix_completesPrefix() {
        List<CommandCompletionLogic.Suggestion> partialPhone =
                completionLogic.getSuggestions("add n/Ryan Tan p");
        assertFalse(partialPhone.isEmpty());
        assertEquals("add n/Ryan Tan p/", partialPhone.get(0).completionText());

        List<CommandCompletionLogic.Suggestion> partialNotes =
                completionLogic.getSuggestions("add n/Ryan p/123 s/Math l/JC 1 r");
        assertFalse(partialNotes.isEmpty());
        assertEquals("add n/Ryan p/123 s/Math l/JC 1 r/", partialNotes.get(0).completionText());
    }

    @Test
    public void getSuggestions_addCommandAllRequiredSatisfied_suggestsOptional() {
        List<CommandCompletionLogic.Suggestion> optionalSuggestions =
                completionLogic.getSuggestions("add n/Ryan p/123 s/Math l/JC 1 ");
        assertFalse(optionalSuggestions.isEmpty());
        // Should contain e/ (email) and r/ (notes)
        assertTrue(optionalSuggestions.stream().anyMatch(s -> s.completionText().endsWith("e/")));
        assertTrue(optionalSuggestions.stream().anyMatch(s -> s.completionText().endsWith("r/")));
    }

    @Test
    public void getSuggestions_findCommand_suggestsAnyFlag() {
        List<CommandCompletionLogic.Suggestion> findFlag = completionLogic.getSuggestions("find -");
        assertFalse(findFlag.isEmpty());
        assertEquals("find --any ", findFlag.get(0).completionText());
    }

    @Test
    public void autocomplete_nullOrInvalid_returnsEmpty() {
        assertFalse(completionLogic.autocomplete(null).isPresent());
        assertFalse(completionLogic.autocomplete("").isPresent());
        assertFalse(completionLogic.autocomplete("invalidCommand").isPresent());
    }
}
