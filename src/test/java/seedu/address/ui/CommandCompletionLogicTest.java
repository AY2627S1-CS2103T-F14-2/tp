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
        assertEquals("show ", sMatches.getFirst().completionText());
        assertEquals("show INDEX", sMatches.getFirst().formatText());

        List<CommandCompletionLogic.Suggestion> aMatches = completionLogic.getSuggestions("ad");
        assertEquals(1, aMatches.size());
        assertEquals("add ", aMatches.getFirst().completionText());

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
        assertEquals("add n/", initialAdd.getFirst().completionText());

        // When n/ is already present, next is p/
        List<CommandCompletionLogic.Suggestion> afterName = completionLogic.getSuggestions("add n/Ryan Tan ");
        assertFalse(afterName.isEmpty());
        assertEquals("add n/Ryan Tan p/", afterName.getFirst().completionText());
    }

    @Test
    public void getSuggestions_addCommandWithPartialPrefix_completesPrefix() {
        List<CommandCompletionLogic.Suggestion> partialName = completionLogic.getSuggestions("add n");
        assertEquals(1, partialName.size());
        assertEquals("add n/", partialName.getFirst().completionText());

        List<CommandCompletionLogic.Suggestion> partialPhone = completionLogic.getSuggestions("add n/Ryan Tan p");
        assertEquals(1, partialPhone.size());
        assertEquals("add n/Ryan Tan p/", partialPhone.getFirst().completionText());
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
        assertEquals("find --any ", findSuggestions.getFirst().completionText());

        List<CommandCompletionLogic.Suggestion> partialFlag = completionLogic.getSuggestions("find --a");
        assertEquals(1, partialFlag.size());
        assertEquals("find --any ", partialFlag.getFirst().completionText());
    }

    @Test
    public void getSuggestions_editCommandPrefix_returnsMatchingCommands() {
        List<CommandCompletionLogic.Suggestion> edMatches = completionLogic.getSuggestions("ed");
        assertEquals(1, edMatches.size());
        assertEquals("edit ", edMatches.getFirst().completionText());

        List<CommandCompletionLogic.Suggestion> eMatches = completionLogic.getSuggestions("e");
        assertEquals(2, eMatches.size());
        assertTrue(eMatches.stream().anyMatch(s -> s.completionText().equals("edit ")));
        assertTrue(eMatches.stream().anyMatch(s -> s.completionText().equals("exit ")));
    }

    @Test
    public void autocomplete_editCommandPrefix_returnsCompletedCommand() {
        Optional<String> completed = completionLogic.autocomplete("ed");
        assertTrue(completed.isPresent());
        assertEquals("edit ", completed.get());
    }

    @Test
    public void getSuggestions_editCommandArguments_suggestsIndexAndPrefixes() {
        // When typing "edit ", suggests index prompt
        List<CommandCompletionLogic.Suggestion> editEmpty = completionLogic.getSuggestions("edit ");
        assertEquals(1, editEmpty.size());
        assertEquals("edit 1 ", editEmpty.getFirst().completionText());
        assertEquals("edit INDEX", editEmpty.getFirst().formatText());

        // When typing "edit 2", suggests trailing space
        List<CommandCompletionLogic.Suggestion> editPartialIndex = completionLogic.getSuggestions("edit 2");
        assertEquals(1, editPartialIndex.size());
        assertEquals("edit 2 ", editPartialIndex.getFirst().completionText());

        // When index is entered with trailing space "edit 2 ", suggests edit prefixes
        List<CommandCompletionLogic.Suggestion> editWithIndex = completionLogic.getSuggestions("edit 2 ");
        assertFalse(editWithIndex.isEmpty());
        assertTrue(editWithIndex.stream().anyMatch(s -> s.completionText().equals("edit 2 n/")));
        assertTrue(editWithIndex.stream().anyMatch(s -> s.completionText().equals("edit 2 +s/")));
        assertTrue(editWithIndex.stream().anyMatch(s -> s.completionText().equals("edit 2 -s/")));
        assertTrue(editWithIndex.stream().anyMatch(s -> s.completionText().equals("edit 2 +t/")));
        assertTrue(editWithIndex.stream().anyMatch(s -> s.completionText().equals("edit 2 -t/")));

        // Partially typed prefix completes
        List<CommandCompletionLogic.Suggestion> partialName = completionLogic.getSuggestions("edit 2 n");
        assertEquals(1, partialName.size());
        assertEquals("edit 2 n/", partialName.getFirst().completionText());

        List<CommandCompletionLogic.Suggestion> partialAppend = completionLogic.getSuggestions("edit 2 +s");
        assertEquals(1, partialAppend.size());
        assertEquals("edit 2 +s/", partialAppend.getFirst().completionText());

        // Single-use prefix is omitted once used, while repeatable prefix remains
        String withName = "edit 2 n/Ryan ";
        List<CommandCompletionLogic.Suggestion> afterName = completionLogic.getSuggestions(withName);
        boolean hasNameAgain = afterName.stream().anyMatch(s -> s.completionText().endsWith("n/"));
        assertFalse(hasNameAgain);

        String withAppend = "edit 2 +s/Math ";
        List<CommandCompletionLogic.Suggestion> afterAppend = completionLogic.getSuggestions(withAppend);
        boolean hasAppendAgain = afterAppend.stream()
                .anyMatch(s -> s.completionText().endsWith("+s/") && s.description().contains("(additional)"));
        assertTrue(hasAppendAgain);
    }

    @Test
    public void getSuggestions_emptyInput_includesEditCommand() {
        List<CommandCompletionLogic.Suggestion> allSuggestions = completionLogic.getSuggestions("");
        boolean hasEdit = allSuggestions.stream().anyMatch(s -> s.completionText().equals("edit "));
        assertTrue(hasEdit);
    }
}
