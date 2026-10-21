package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.StudentContainsKeywordsPredicate;
import seedu.address.model.person.StudentContainsKeywordsPredicate.MatchMode;
import seedu.address.model.person.StudentContainsKeywordsPredicate.SearchField;
import seedu.address.model.person.StudentFields;

/** Parses name searches and field-targeted student searches. */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Prefix PREFIX_LEGACY_TAG = new Prefix("/t");

    /** Parses unprefixed name searches and prefixed profile-field searches. */
    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = StudentFields.trim(args);
        if (trimmedArgs.isEmpty()) {
            throw invalidFormat();
        }

        boolean anyKeywordSearch = startsWithAnyOption(trimmedArgs);
        String searchArguments = anyKeywordSearch
                ? StudentFields.trim(trimmedArgs.substring(FindCommand.ANY_KEYWORD_OPTION.length()))
                : trimmedArgs;
        if (searchArguments.isEmpty()) {
            throw invalidFormat();
        }

        if (!containsTargetedPrefix(searchArguments)) {
            String[] arguments = searchArguments.split("(?U)\\s+");
            List<String> nameKeywords = Arrays.stream(arguments).toList();
            return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords,
                    anyKeywordSearch ? NameContainsKeywordsPredicate.MatchMode.ANY
                            : NameContainsKeywordsPredicate.MatchMode.ALL));
        }

        // ArgumentTokenizer recognises prefixes at whitespace boundaries; the leading space also handles
        // a prefix at the beginning of the find argument list.
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(" " + searchArguments,
                PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_NOTES, PREFIX_TAG,
                PREFIX_LEGACY_TAG);
        if (!StudentFields.trim(argumentMultimap.getPreamble()).isEmpty()) {
            throw invalidFormat();
        }

        Map<SearchField, List<String>> criteria = new EnumMap<>(SearchField.class);
        addCriteria(criteria, SearchField.NAME, argumentMultimap.getAllValues(PREFIX_NAME));
        addCriteria(criteria, SearchField.PHONE, argumentMultimap.getAllValues(PREFIX_PHONE));
        addCriteria(criteria, SearchField.EMAIL, argumentMultimap.getAllValues(PREFIX_EMAIL));
        addCriteria(criteria, SearchField.ADDRESS, argumentMultimap.getAllValues(PREFIX_ADDRESS));
        addCriteria(criteria, SearchField.SUBJECT, argumentMultimap.getAllValues(PREFIX_SUBJECT));
        addCriteria(criteria, SearchField.LEVEL, argumentMultimap.getAllValues(PREFIX_LEVEL));
        addCriteria(criteria, SearchField.NOTES, argumentMultimap.getAllValues(PREFIX_NOTES));
        addCriteria(criteria, SearchField.TAG, argumentMultimap.getAllValues(PREFIX_TAG));
        addCriteria(criteria, SearchField.TAG, argumentMultimap.getAllValues(PREFIX_LEGACY_TAG));

        if (criteria.isEmpty()) {
            throw invalidFormat();
        }
        return new FindCommand(new StudentContainsKeywordsPredicate(criteria,
                anyKeywordSearch ? MatchMode.ANY : MatchMode.ALL));
    }

    private static boolean startsWithAnyOption(String args) {
        return args.equals(FindCommand.ANY_KEYWORD_OPTION)
                || args.startsWith(FindCommand.ANY_KEYWORD_OPTION + " ")
                || args.startsWith(FindCommand.ANY_KEYWORD_OPTION + "\t");
    }

    private static boolean containsTargetedPrefix(String args) {
        return args.matches("(?U)(?s).*?(?<!\\S)(?:[a-z]/|/[a-z])(?=\\S|\\s|$).*");
    }

    private static void addCriteria(Map<SearchField, List<String>> criteria, SearchField field,
            List<String> values) throws ParseException {
        if (values.isEmpty()) {
            return;
        }
        List<String> cleanedValues = new ArrayList<>();
        for (String value : values) {
            String cleanedValue = StudentFields.trim(value);
            if (cleanedValue.isEmpty()) {
                throw invalidFormat();
            }
            cleanedValues.add(cleanedValue);
        }
        criteria.computeIfAbsent(field, ignored -> new ArrayList<>()).addAll(cleanedValues);
    }

    private static ParseException invalidFormat() {
        return new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
