package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Computes command and parameter completion suggestions based on the user's current input.
 * This class contains only headless logic and does not depend on JavaFX controls.
 */
public class CommandCompletionLogic {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?U)(?<!\\S)([a-z])/");

    private static final List<CommandSpec> COMMANDS = List.of(
            new CommandSpec("add", "add n/NAME p/PHONE s/SUBJECT l/LEVEL [e/EMAIL] [r/NOTES]",
                    "Add a new student to the roster"),
            new CommandSpec("delete", "delete INDEX",
                    "Delete student by displayed list index"),
            new CommandSpec("find", "find [--any] KEYWORD [MORE_KEYWORDS]...",
                    "Find students by name keywords"),
            new CommandSpec("list", "list",
                    "Show the complete student roster"),
            new CommandSpec("show", "show INDEX",
                    "Show complete record of student at displayed index"),
            new CommandSpec("help", "help",
                    "Open the user guide window"),
            new CommandSpec("exit", "exit",
                    "Exit TutorRoster")
    );

    private static final List<PrefixSpec> ADD_PREFIXES = List.of(
            new PrefixSpec("n/", "NAME", "Student full name (required)", true, false),
            new PrefixSpec("p/", "PHONE", "Phone number (required)", true, false),
            new PrefixSpec("s/", "SUBJECT", "Subject taught (required, repeatable)", true, true),
            new PrefixSpec("l/", "LEVEL", "Education level (required)", true, false),
            new PrefixSpec("e/", "EMAIL", "Email address (optional)", false, false),
            new PrefixSpec("r/", "NOTES", "Tutoring notes (optional)", false, false)
    );

    /**
     * A completion suggestion for display in the UI.
     *
     * @param completionText text to replace or append to the command field upon autocompletion.
     * @param formatText syntax format or prefix pattern to display.
     * @param description brief explanation of the command or parameter.
     */
    public record Suggestion(String completionText, String formatText, String description) {
        /**
         * Constructs a {@code Suggestion} after validating non-null parameters.
         */
        public Suggestion {
            requireNonNull(completionText);
            requireNonNull(formatText);
            requireNonNull(description);
        }
    }

    /**
     * Returns matching suggestions for the given input.
     *
     * @param currentText input currently in the command field.
     * @return unmodifiable list of suggestions, empty if no suggestion is appropriate.
     */
    public List<Suggestion> getSuggestions(String currentText) {
        if (currentText == null) {
            return Collections.emptyList();
        }

        String trimmed = currentText.stripLeading();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }

        // If user is typing the first word (the command name)
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace < 0) {
            return getCommandWordSuggestions(trimmed);
        }

        String commandWord = trimmed.substring(0, firstSpace).toLowerCase(Locale.ROOT);
        String arguments = trimmed.substring(firstSpace + 1);

        if (commandWord.equals("add")) {
            return getAddCommandSuggestions(trimmed, arguments);
        } else if (commandWord.equals("find")) {
            return getFindCommandSuggestions(trimmed, arguments);
        } else if (commandWord.equals("show") || commandWord.equals("delete")) {
            return getIndexCommandSuggestions(trimmed, commandWord, arguments);
        }

        return Collections.emptyList();
    }

    /**
     * Computes the autocompleted command text when the user presses Tab.
     *
     * @param currentText input currently in the command field.
     * @return an {@code Optional} containing the autocompleted text, or empty if no completion applies.
     */
    public Optional<String> autocomplete(String currentText) {
        if (currentText == null) {
            return Optional.empty();
        }

        List<Suggestion> suggestions = getSuggestions(currentText);
        if (suggestions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(suggestions.get(0).completionText());
    }

    private List<Suggestion> getCommandWordSuggestions(String prefix) {
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        List<Suggestion> matches = new ArrayList<>();

        for (CommandSpec spec : COMMANDS) {
            if (spec.commandWord.startsWith(lowerPrefix)) {
                String completion = spec.commandWord + " ";
                matches.add(new Suggestion(completion, spec.formatGuide, spec.description));
            }
        }

        return Collections.unmodifiableList(matches);
    }

    private List<Suggestion> getAddCommandSuggestions(String fullInput, String arguments) {
        Set<String> usedPrefixes = extractPrefixes(arguments);
        List<Suggestion> suggestions = new ArrayList<>();

        int lastSpace = fullInput.lastIndexOf(' ');
        String trailingToken = lastSpace >= 0 ? fullInput.substring(lastSpace + 1) : "";
        String baseInput = lastSpace >= 0 ? fullInput.substring(0, lastSpace + 1) : fullInput;

        if (!trailingToken.isEmpty()) {
            String lowerToken = trailingToken.toLowerCase(Locale.ROOT);
            for (PrefixSpec spec : ADD_PREFIXES) {
                boolean matchesPrefix = spec.prefix.startsWith(lowerToken)
                        || spec.prefix.substring(0, 1).equalsIgnoreCase(lowerToken);
                boolean canUse = spec.isRepeatable || !usedPrefixes.contains(spec.prefix);
                if (matchesPrefix && canUse) {
                    String completion = baseInput + spec.prefix;
                    suggestions.add(new Suggestion(completion, spec.prefix + spec.placeholder, spec.description));
                }
            }
            if (!suggestions.isEmpty()) {
                return Collections.unmodifiableList(suggestions);
            }
        }

        // When trailing token is empty (user typed a space), suggest next missing required prefix
        for (PrefixSpec spec : ADD_PREFIXES) {
            if (spec.isRequired && !usedPrefixes.contains(spec.prefix)) {
                String completion = fullInput + (fullInput.endsWith(" ") ? "" : " ") + spec.prefix;
                suggestions.add(new Suggestion(completion, spec.prefix + spec.placeholder, spec.description));
            }
        }

        // If all required prefixes are satisfied, suggest optional prefixes or repeatable subject
        if (suggestions.isEmpty()) {
            for (PrefixSpec spec : ADD_PREFIXES) {
                if (!spec.isRequired && !usedPrefixes.contains(spec.prefix)) {
                    String completion = fullInput + (fullInput.endsWith(" ") ? "" : " ") + spec.prefix;
                    suggestions.add(new Suggestion(completion, spec.prefix + spec.placeholder, spec.description));
                }
            }
            // Repeatable subject
            String repeatSubjectCompletion = fullInput + (fullInput.endsWith(" ") ? "" : " ") + "s/";
            suggestions.add(new Suggestion(repeatSubjectCompletion, "s/SUBJECT",
                    "Additional subject (repeatable)"));
        }

        return Collections.unmodifiableList(suggestions);
    }

    private List<Suggestion> getFindCommandSuggestions(String fullInput, String arguments) {
        String trimmedArgs = arguments.trim();
        List<Suggestion> suggestions = new ArrayList<>();

        if (!trimmedArgs.contains("--any")) {
            int lastSpace = fullInput.lastIndexOf(' ');
            String trailingToken = lastSpace >= 0 ? fullInput.substring(lastSpace + 1) : "";
            String baseInput = lastSpace >= 0 ? fullInput.substring(0, lastSpace + 1) : fullInput;

            if (trailingToken.startsWith("-")) {
                suggestions.add(new Suggestion(baseInput + "--any ", "--any",
                        "Match students with at least one keyword (OR search)"));
            } else if (trimmedArgs.isEmpty()) {
                suggestions.add(new Suggestion(fullInput + (fullInput.endsWith(" ") ? "" : " ") + "--any ",
                        "--any KEYWORD...", "Search by matching any keyword"));
            }
        }

        return Collections.unmodifiableList(suggestions);
    }

    private List<Suggestion> getIndexCommandSuggestions(String fullInput, String commandWord, String arguments) {
        if (arguments.trim().isEmpty()) {
            return List.of(new Suggestion(commandWord + " ", commandWord + " INDEX",
                    "Specify student index from the currently displayed roster"));
        }
        return Collections.emptyList();
    }

    private Set<String> extractPrefixes(String args) {
        Set<String> prefixes = new HashSet<>();
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            prefixes.add(matcher.group(1) + "/");
        }
        return prefixes;
    }

    private record CommandSpec(String commandWord, String formatGuide, String description) {}

    private record PrefixSpec(String prefix, String placeholder, String description,
                              boolean isRequired, boolean isRepeatable) {}
}
