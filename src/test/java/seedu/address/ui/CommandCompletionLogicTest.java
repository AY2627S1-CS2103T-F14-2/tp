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
    public void getSuggestions_rosterCommandPrefixes_suggestsMatchingCommands() {
        for (String command : List.of("undo", "redo", "clear")) {
            String prefix = command.substring(0, command.length() - 1);
            List<CommandCompletionLogic.Suggestion> suggestions = completionLogic.getSuggestions(prefix);
            assertEquals(1, suggestions.size(), prefix);
            assertEquals(command + " ", suggestions.getFirst().completionText(), prefix);
            assertEquals(command, suggestions.getFirst().formatText(), prefix);
            assertEquals(Optional.of(command + " "), completionLogic.autocomplete(prefix), prefix);
        }
    }

    @Test
    public void getSuggestions_emptyInput_includesRosterCommands() {
        List<String> completions = completionLogic.getSuggestions("").stream()
                .map(CommandCompletionLogic.Suggestion::completionText)
                .toList();
        assertTrue(completions.containsAll(List.of("undo ", "redo ", "clear ")));
    }

    @Test
    public void getSuggestions_rosterCommandsWithArguments_returnsNoSuggestions() {
        for (String command : List.of("undo", "redo", "clear")) {
            for (String arguments : List.of("", "1")) {
                String input = command + " " + arguments;
                assertTrue(completionLogic.getSuggestions(input).isEmpty(), input);
                assertTrue(completionLogic.autocomplete(input).isEmpty(), input);
            }
        }
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

        // e/, a/, r/, t/ are optional so they should be suggested
        boolean hasEmail = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("e/"));
        assertTrue(hasEmail);

        boolean hasAddress = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("a/"));
        assertTrue(hasAddress);

        boolean hasNotes = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("r/"));
        assertTrue(hasNotes);

        boolean hasTag = suggestions.stream()
                .anyMatch(s -> s.completionText().endsWith("t/"));
        assertTrue(hasTag);

        // When t/ is already provided, it should still be suggested because it is repeatable
        String withTag = allRequired + "t/exam-prep ";
        List<CommandCompletionLogic.Suggestion> afterTag = completionLogic.getSuggestions(withTag);
        boolean hasAdditionalTag = afterTag.stream()
                .anyMatch(s -> s.completionText().endsWith("t/") && s.description().contains("(additional)"));
        assertTrue(hasAdditionalTag);
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
    public void getSuggestions_editCommandInvalidIndex_returnsNoSuggestions() {
        for (String index : List.of("abc", "0", "-1", "+1", "2147483648", "1/")) {
            for (String input : List.of("edit " + index, "edit " + index + " ", "edit " + index + " n/")) {
                assertTrue(completionLogic.getSuggestions(input).isEmpty(), input);
                assertTrue(completionLogic.autocomplete(input).isEmpty(), input);
            }
        }

        assertEquals("edit 01 ", completionLogic.autocomplete("edit 01").orElseThrow());
    }

    @Test
    public void getSuggestions_editCommandTabAfterIndex_suggestsFields() {
        String input = "edit 1\t n/Alice";
        assertTrue(completionLogic.getSuggestions(input).stream()
                .anyMatch(suggestion -> suggestion.completionText().equals(input + " p/")), input);
    }

    @Test
    public void getSuggestions_emptyInput_includesEditCommand() {
        List<CommandCompletionLogic.Suggestion> allSuggestions = completionLogic.getSuggestions("");
        boolean hasEdit = allSuggestions.stream().anyMatch(s -> s.completionText().equals("edit "));
        assertTrue(hasEdit);
    }

    @Test
    public void getSuggestions_tagCommandPrefix_returnsMatchingCommands() {
        List<CommandCompletionLogic.Suggestion> tMatches = completionLogic.getSuggestions("t");
        assertEquals(1, tMatches.size());
        assertEquals("tag ", tMatches.get(0).completionText());
        assertEquals("tag INDEX TAG", tMatches.get(0).formatText());

        List<CommandCompletionLogic.Suggestion> taMatches = completionLogic.getSuggestions("ta");
        assertEquals(1, taMatches.size());
        assertEquals("tag ", taMatches.get(0).completionText());
    }

    @Test
    public void autocomplete_tagCommandPrefix_returnsCompletedCommand() {
        Optional<String> completed = completionLogic.autocomplete("ta");
        assertTrue(completed.isPresent());
        assertEquals("tag ", completed.get());
    }

    @Test
    public void getSuggestions_tagCommandArguments_suggestsIndexAndTag() {
        // When typing "tag " with no arguments, suggests index with format guide
        List<CommandCompletionLogic.Suggestion> tagEmptyArgs = completionLogic.getSuggestions("tag ");
        assertEquals(1, tagEmptyArgs.size());
        assertEquals("tag 1 ", tagEmptyArgs.get(0).completionText());
        assertEquals("tag INDEX TAG", tagEmptyArgs.get(0).formatText());

        // When typing "tag 2" (partial index without trailing space), suggests completed space
        List<CommandCompletionLogic.Suggestion> tagPartialIndex = completionLogic.getSuggestions("tag 2");
        assertEquals(1, tagPartialIndex.size());
        assertEquals("tag 2 ", tagPartialIndex.get(0).completionText());

        // When index is entered followed by space "tag 2 ", suggests tag format guide
        List<CommandCompletionLogic.Suggestion> tagWithIndex = completionLogic.getSuggestions("tag 2 ");
        assertEquals(1, tagWithIndex.size());
        assertEquals("tag 2 ", tagWithIndex.get(0).completionText());
        assertEquals("TAG", tagWithIndex.get(0).formatText());

        // When tag name is being typed, no suggestions
        List<CommandCompletionLogic.Suggestion> tagComplete = completionLogic.getSuggestions("tag 2 exam-prep");
        assertTrue(tagComplete.isEmpty());
    }

    @Test
    public void getSuggestions_tagCommandInvalidIndex_returnsNoSuggestions() {
        for (String index : List.of("abc", "0", "-1", "+1", "2147483648", "1/")) {
            for (String input : List.of("tag " + index, "tag " + index + " ")) {
                assertTrue(completionLogic.getSuggestions(input).isEmpty(), input);
                assertTrue(completionLogic.autocomplete(input).isEmpty(), input);
            }
        }

        assertEquals("tag 01 ", completionLogic.autocomplete("tag 01").orElseThrow());
    }

    @Test
    public void getSuggestions_tagCommandNonBreakingSpaceBeforeIndex_suggestsTag() {
        String input = "tag \u00a01";
        assertEquals("tag 1 ", completionLogic.autocomplete(input).orElseThrow(), input);
    }

    @Test
    public void getSuggestions_untagCommand_suggestsIndexAndAllFlag() {
        // Prefix matching
        List<CommandCompletionLogic.Suggestion> uMatches = completionLogic.getSuggestions("u");
        assertEquals(2, uMatches.size());
        assertEquals("untag ", uMatches.get(0).completionText());
        assertEquals("undo ", uMatches.get(1).completionText());

        // Autocomplete on "un"
        Optional<String> completed = completionLogic.autocomplete("un");
        assertTrue(completed.isPresent());
        assertEquals("untag ", completed.get());

        // When typing "untag ", suggests both index and --all options
        List<CommandCompletionLogic.Suggestion> untagEmpty = completionLogic.getSuggestions("untag ");
        assertEquals(2, untagEmpty.size());
        assertEquals("untag 1 ", untagEmpty.get(0).completionText());
        assertEquals("untag --all 1", untagEmpty.get(1).completionText());

        // When typing flag prefix "untag --"
        List<CommandCompletionLogic.Suggestion> untagFlag = completionLogic.getSuggestions("untag --");
        assertEquals(1, untagFlag.size());
        assertEquals("untag --all 1", untagFlag.get(0).completionText());

        // When typing "untag --all "
        List<CommandCompletionLogic.Suggestion> untagAllArgs = completionLogic.getSuggestions("untag --all ");
        assertEquals(1, untagAllArgs.size());
        assertEquals("untag --all 1", untagAllArgs.get(0).completionText());

        // When typing "untag 3 "
        List<CommandCompletionLogic.Suggestion> untagWithIndex = completionLogic.getSuggestions("untag 3 ");
        assertEquals(1, untagWithIndex.size());
        assertEquals("TAG", untagWithIndex.get(0).formatText());
    }

    @Test
    public void getSuggestions_untagCommandInvalidIndex_returnsNoSuggestions() {
        for (String index : List.of("abc", "0", "-1", "+1", "2147483648", "1/")) {
            for (String input : List.of("untag " + index, "untag " + index + " ", "untag --all " + index)) {
                assertTrue(completionLogic.getSuggestions(input).isEmpty(), input);
                assertTrue(completionLogic.autocomplete(input).isEmpty(), input);
            }
        }

        assertEquals("untag 01 ", completionLogic.autocomplete("untag 01").orElseThrow());
    }

    @Test
    public void getSuggestions_untagCommandNonBreakingSpaceBeforeIndex_suggestsTag() {
        String input = "untag \u00a01";
        assertEquals("untag 1 ", completionLogic.autocomplete(input).orElseThrow(), input);
    }

    @Test
    public void getSuggestions_showAndDeleteCommands_onlyPromptForMissingIndex() {
        for (String command : List.of("show", "delete")) {
            String missingIndexInput = command + " ";
            assertEquals(command + " 1", completionLogic.autocomplete(missingIndexInput).orElseThrow(),
                    missingIndexInput);
            for (String index : List.of("1", "abc", "0", "-1", "+1", "2147483648")) {
                String input = command + " " + index;
                assertTrue(completionLogic.getSuggestions(input).isEmpty(), input);
                assertTrue(completionLogic.autocomplete(input).isEmpty(), input);
            }
        }
    }

    @Test
    public void getSuggestions_emptyInput_includesTagCommands() {
        List<CommandCompletionLogic.Suggestion> allSuggestions = completionLogic.getSuggestions("");
        boolean hasTag = allSuggestions.stream().anyMatch(s -> s.completionText().equals("tag "));
        boolean hasUntag = allSuggestions.stream().anyMatch(s -> s.completionText().equals("untag "));
        assertTrue(hasTag);
        assertTrue(hasUntag);
    }
}
