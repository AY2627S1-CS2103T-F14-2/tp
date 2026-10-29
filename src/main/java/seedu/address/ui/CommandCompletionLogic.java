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

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?U)(?<!\\S)([+\\-]?[a-z])/");

    private static final List<CommandSpec> COMMANDS = List.of(
            new CommandSpec("add", "add n/NAME p/PHONE s/SUBJECT l/LEVEL [e/EMAIL] [a/ADDRESS] [r/NOTES] [t/TAG]...",
                    "Add a new student to the roster"),
            new CommandSpec("edit",
                    "edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT]... [l/LEVEL] [r/NOTES] [t/TAG]...",
                    "Edit student details by displayed list index"),
            new CommandSpec("delete", "delete INDEX",
                    "Delete student by displayed list index"),
            new CommandSpec("find", "find [--any] PREFIX/VALUE [PREFIX/VALUE]...",
                    "Search using n/ name, p/ phone, e/ email, a/ address, s/ subject, l/ level, or t/ tag."),
            new CommandSpec("list", "list",
                    "Show the complete student roster"),
            new CommandSpec("show", "show INDEX",
                    "Show complete record of student at displayed index"),
            new CommandSpec("tag", "tag INDEX TAG",
                    "Add a tag to student by displayed list index"),
            new CommandSpec("untag", "untag INDEX TAG or untag --all INDEX",
                    "Remove a tag from student by displayed list index"),
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
            new PrefixSpec("a/", "ADDRESS", "Home address (optional)", false, false),
            new PrefixSpec("r/", "NOTES", "Tutoring notes (optional)", false, false),
            new PrefixSpec("t/", "TAG", "Student tag (optional, repeatable)", false, true)
    );

    private static final List<PrefixSpec> EDIT_PREFIXES = List.of(
            new PrefixSpec("n/", "NAME", "Student full name", false, false),
            new PrefixSpec("p/", "PHONE", "Phone number", false, false),
            new PrefixSpec("e/", "EMAIL", "Email address", false, false),
            new PrefixSpec("a/", "ADDRESS", "Home address", false, false),
            new PrefixSpec("s/", "SUBJECT", "Subject taught (replaces existing)", false, true),
            new PrefixSpec("+s/", "SUBJECT", "Subject taught (append)", false, true),
            new PrefixSpec("-s/", "SUBJECT", "Subject taught (remove)", false, true),
            new PrefixSpec("l/", "LEVEL", "Education level", false, false),
            new PrefixSpec("r/", "NOTES", "Tutoring notes", false, false),
            new PrefixSpec("t/", "TAG", "Student tag (replaces existing)", false, true),
            new PrefixSpec("+t/", "TAG", "Student tag (append)", false, true),
            new PrefixSpec("-t/", "TAG", "Student tag (remove)", false, true)
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

    private record CommandSpec(String commandWord, String formatGuide, String description) {}

    private record PrefixSpec(
            String prefix,
            String valuePlaceholder,
            String description,
            boolean isRequired,
            boolean isRepeatable
    ) {}

    /**
     * Computes suggestions based on the user's current input.
     * When the input is empty or blank, all available commands are returned.
     *
     * @param currentText input currently in the command field.
     * @return an unmodifiable list of suggestions matching the input.
     */
    public List<Suggestion> getSuggestions(String currentText) {
        if (currentText == null) {
            return getAllCommandSuggestions();
        }

        String trimmed = currentText.stripLeading();
        if (trimmed.isEmpty()) {
            return getAllCommandSuggestions();
        }

        // If user is typing the first word (the command name)
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace < 0) {
            return getCommandWordSuggestions(trimmed);
        }

        String commandWord = trimmed.substring(0, firstSpace).toLowerCase(Locale.ROOT);
        String arguments = trimmed.substring(firstSpace + 1);

        return switch (commandWord) {
            case "add" -> getAddCommandSuggestions(trimmed, arguments);
            case "edit" -> getEditCommandSuggestions(trimmed, arguments);
            case "find" -> getFindCommandSuggestions(trimmed, arguments);
            case "show", "delete" -> getIndexCommandSuggestions(commandWord, arguments);
            case "tag" -> getTagCommandSuggestions(trimmed, arguments);
            case "untag" -> getUntagCommandSuggestions(trimmed, arguments);
            default -> Collections.emptyList();
        };

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

        return Optional.of(suggestions.getFirst().completionText());
    }

    private List<Suggestion> getAllCommandSuggestions() {
        List<Suggestion> suggestions = new ArrayList<>();
        for (CommandSpec spec : COMMANDS) {
            suggestions.add(new Suggestion(spec.commandWord + " ", spec.formatGuide, spec.description));
        }
        return Collections.unmodifiableList(suggestions);
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
        String trailingToken = (lastSpace >= 0 && lastSpace < fullInput.length() - 1)
                ? fullInput.substring(lastSpace + 1)
                : "";

        // Check if trailing token is a partially typed prefix (e.g. "n" or "s")
        if (!trailingToken.isEmpty() && !trailingToken.contains("/")) {
            for (PrefixSpec spec : ADD_PREFIXES) {
                if (spec.prefix.startsWith(trailingToken.toLowerCase(Locale.ROOT))) {
                    if (spec.isRepeatable || !usedPrefixes.contains(spec.prefix)) {
                        String completed = fullInput.substring(0, lastSpace + 1) + spec.prefix;
                        suggestions.add(new Suggestion(
                                completed,
                                spec.prefix + spec.valuePlaceholder,
                                spec.description));
                    }
                }
            }
            if (!suggestions.isEmpty()) {
                return Collections.unmodifiableList(suggestions);
            }
        }

        String base = fullInput.endsWith(" ") ? fullInput : fullInput + " ";

        // Suggest remaining required prefixes first
        for (PrefixSpec spec : ADD_PREFIXES) {
            if (spec.isRequired && !usedPrefixes.contains(spec.prefix)) {
                suggestions.add(new Suggestion(
                        base + spec.prefix,
                        spec.prefix + spec.valuePlaceholder,
                        spec.description));
            }
        }

        // Then suggest optional prefixes or repeatable prefixes
        for (PrefixSpec spec : ADD_PREFIXES) {
            if (!spec.isRequired && !usedPrefixes.contains(spec.prefix)) {
                suggestions.add(new Suggestion(
                        base + spec.prefix,
                        spec.prefix + spec.valuePlaceholder,
                        spec.description));
            } else if (spec.isRepeatable && usedPrefixes.contains(spec.prefix)) {
                suggestions.add(new Suggestion(
                        base + spec.prefix,
                        spec.prefix + spec.valuePlaceholder,
                        spec.description + " (additional)"));
            }
        }

        return Collections.unmodifiableList(suggestions);
    }

    /**
     * Returns suggestions for the edit command based on the current input state.
     * When the index is missing, returns a suggestion prompting the user to supply the target index.
     * When a prefix is partially typed (without a slash), returns matching prefix completions for
     * unused or repeatable prefixes.
     * When fields have already been supplied, excludes used single-use prefixes and suggests only the
     * remaining unsupplied prefixes and repeatable prefixes.
     *
     * @param fullInput full user input text currently in the command box.
     * @param arguments argument string following the "edit" command word.
     * @return an unmodifiable list of suggestions for the edit command.
     */
    private List<Suggestion> getEditCommandSuggestions(String fullInput, String arguments) {
        String trimmedArgs = arguments.stripLeading();
        if (trimmedArgs.isEmpty()) {
            return List.of(new Suggestion(
                    "edit 1 ",
                    "edit INDEX",
                    "Positive integer index from displayed list (e.g. 1)"));
        }

        int firstSpace = trimmedArgs.indexOf(' ');
        if (firstSpace < 0) {
            if (!trimmedArgs.contains("/")) {
                return List.of(new Suggestion(
                        "edit " + trimmedArgs + " ",
                        "edit " + trimmedArgs + " [PREFIX/VALUE]...",
                        "Space followed by fields to edit"));
            }
        }

        String prefixArgs = firstSpace >= 0 ? trimmedArgs.substring(firstSpace + 1) : "";
        Set<String> usedPrefixes = extractPrefixes(prefixArgs);
        List<Suggestion> suggestions = new ArrayList<>();

        int lastSpace = fullInput.lastIndexOf(' ');
        String trailingToken = (lastSpace >= 0 && lastSpace < fullInput.length() - 1)
                ? fullInput.substring(lastSpace + 1)
                : "";

        // Check if trailing token is a partially typed prefix (e.g. "n", "+", or "-s")
        if (!trailingToken.isEmpty() && !trailingToken.contains("/")) {
            String lowerToken = trailingToken.toLowerCase(Locale.ROOT);
            for (PrefixSpec spec : EDIT_PREFIXES) {
                if (spec.prefix.startsWith(lowerToken)) {
                    if (spec.isRepeatable || !usedPrefixes.contains(spec.prefix)) {
                        String completed = fullInput.substring(0, lastSpace + 1) + spec.prefix;
                        suggestions.add(new Suggestion(
                                completed,
                                spec.prefix + spec.valuePlaceholder,
                                spec.description));
                    }
                }
            }
            if (!suggestions.isEmpty()) {
                return Collections.unmodifiableList(suggestions);
            }
        }

        String base = fullInput.endsWith(" ") ? fullInput : fullInput + " ";

        // Suggest remaining single-use prefixes and repeatable prefixes
        for (PrefixSpec spec : EDIT_PREFIXES) {
            if (!usedPrefixes.contains(spec.prefix)) {
                suggestions.add(new Suggestion(
                        base + spec.prefix,
                        spec.prefix + spec.valuePlaceholder,
                        spec.description));
            } else if (spec.isRepeatable) {
                suggestions.add(new Suggestion(
                        base + spec.prefix,
                        spec.prefix + spec.valuePlaceholder,
                        spec.description + " (additional)"));
            }
        }

        return Collections.unmodifiableList(suggestions);
    }

    private List<Suggestion> getFindCommandSuggestions(String fullInput, String arguments) {
        String trimmedArgs = arguments.stripLeading();
        if (trimmedArgs.isEmpty() || "--any".startsWith(trimmedArgs.toLowerCase(Locale.ROOT))) {
            String base = fullInput.substring(0, fullInput.indexOf(' ') + 1);
            return List.of(new Suggestion(
                    base + "--any ",
                    "--any",
                    "Match students matching at least one supplied field (OR search)"));
        }
        return Collections.emptyList();
    }

    private List<Suggestion> getIndexCommandSuggestions(String commandWord, String arguments) {
        String trimmedArgs = arguments.strip();
        if (trimmedArgs.isEmpty()) {
            return List.of(new Suggestion(
                    commandWord + " 1",
                    commandWord + " INDEX",
                    "Positive integer index from displayed list (e.g. 1)"));
        }
        return Collections.emptyList();
    }

    private List<Suggestion> getTagCommandSuggestions(String fullInput, String arguments) {
        String trimmedArgs = arguments.strip();
        if (trimmedArgs.isEmpty()) {
            return List.of(new Suggestion(
                    "tag 1 ",
                    "tag INDEX TAG",
                    "Positive integer index followed by tag name (e.g. 1 exam-prep)"));
        }

        String[] parts = trimmedArgs.split("\\s+");
        if (parts.length == 1) {
            if (arguments.endsWith(" ")) {
                return List.of(new Suggestion(
                        fullInput,
                        "TAG",
                        "Alphanumeric tag name (e.g. exam-prep)"));
            } else {
                return List.of(new Suggestion(
                        "tag " + parts[0] + " ",
                        "tag " + parts[0] + " TAG",
                        "Space followed by tag name (e.g. " + parts[0] + " exam-prep)"));
            }
        }

        return Collections.emptyList();
    }

    private List<Suggestion> getUntagCommandSuggestions(String fullInput, String arguments) {
        String trimmedArgs = arguments.strip();
        if (trimmedArgs.isEmpty()) {
            return List.of(
                    new Suggestion(
                            "untag 1 ",
                            "untag INDEX TAG",
                            "Positive integer index followed by tag name (e.g. 1 exam-prep)"),
                    new Suggestion(
                            "untag --all 1",
                            "untag --all INDEX",
                            "Remove all tags from student at displayed index")
            );
        }

        if (trimmedArgs.startsWith("-")) {
            if ("--all".startsWith(trimmedArgs.toLowerCase(Locale.ROOT))) {
                return List.of(new Suggestion(
                        "untag --all 1",
                        "untag --all INDEX",
                        "Remove all tags from student at displayed index"));
            }
        }

        String[] parts = trimmedArgs.split("\\s+");
        if (parts[0].equals("--all")) {
            if (parts.length == 1 && arguments.endsWith(" ")) {
                return List.of(new Suggestion(
                        "untag --all 1",
                        "untag --all INDEX",
                        "Positive integer index from displayed list (e.g. 1)"));
            }
            return Collections.emptyList();
        }

        if (parts.length == 1) {
            if (arguments.endsWith(" ")) {
                return List.of(new Suggestion(
                        fullInput,
                        "TAG",
                        "Tag name to remove from student"));
            } else {
                return List.of(new Suggestion(
                        "untag " + parts[0] + " ",
                        "untag " + parts[0] + " TAG",
                        "Space followed by tag name to remove"));
            }
        }

        return Collections.emptyList();
    }

    private Set<String> extractPrefixes(String arguments) {
        Set<String> prefixes = new HashSet<>();
        Matcher matcher = PREFIX_PATTERN.matcher(arguments);
        while (matcher.find()) {
            prefixes.add(matcher.group(1).toLowerCase(Locale.ROOT) + "/");
        }
        return prefixes;
    }
}
