package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.model.person.PersonMatchesFieldsPredicate.MatchMode;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchField;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchTerm;
import seedu.address.model.person.StudentFields;

/** Parses field-specific search terms into a {@link FindCommand}. */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?U)(?<!\\S)([a-z])/");
    private static final Map<String, SearchField> SEARCH_FIELDS = Map.of(
            PREFIX_NAME.getPrefix(), SearchField.NAME,
            PREFIX_PHONE.getPrefix(), SearchField.PHONE,
            PREFIX_SUBJECT.getPrefix(), SearchField.SUBJECT,
            PREFIX_LEVEL.getPrefix(), SearchField.LEVEL,
            PREFIX_EMAIL.getPrefix(), SearchField.EMAIL);

    @Override
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        boolean isAnySearch = hasAnyOption(trimmedArgs);
        String searchArguments = isAnySearch
                ? StudentFields.trim(trimmedArgs.substring(FindCommand.ANY_KEYWORD_OPTION.length()))
                : trimmedArgs;
        if (searchArguments.isEmpty()) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        Map<String, List<String>> values = parseSearchValues(searchArguments);
        validateSearchValues(values);
        List<SearchTerm> searchTerms = createSearchTerms(values);
        MatchMode matchMode = isAnySearch ? MatchMode.ANY : MatchMode.ALL;
        return new FindCommand(new PersonMatchesFieldsPredicate(searchTerms, matchMode));
    }

    private Map<String, List<String>> parseSearchValues(String searchArguments) throws ParseException {
        Map<String, List<String>> values = new LinkedHashMap<>();
        Matcher matcher = PREFIX_PATTERN.matcher(searchArguments);
        String previousPrefix = null;
        int previousValueStart = 0;
        int firstPrefixStart = -1;

        while (matcher.find()) {
            String prefix = matcher.group(1) + "/";
            if (!SEARCH_FIELDS.containsKey(prefix)) {
                throw new ParseException("Unknown parameter prefix: " + prefix);
            }
            if (firstPrefixStart < 0) {
                firstPrefixStart = matcher.start(1);
            }
            if (previousPrefix != null) {
                addValue(values, previousPrefix, searchArguments.substring(previousValueStart, matcher.start(1)));
            }
            previousPrefix = prefix;
            previousValueStart = matcher.end();
        }

        if (firstPrefixStart != 0 || previousPrefix == null) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        addValue(values, previousPrefix, searchArguments.substring(previousValueStart));
        return values;
    }

    private boolean hasAnyOption(String arguments) {
        int optionLength = FindCommand.ANY_KEYWORD_OPTION.length();
        return arguments.equals(FindCommand.ANY_KEYWORD_OPTION)
                || (arguments.length() > optionLength && arguments.startsWith(FindCommand.ANY_KEYWORD_OPTION)
                && Character.isWhitespace(arguments.charAt(optionLength)));
    }

    private void addValue(Map<String, List<String>> values, String prefix, String value) {
        values.computeIfAbsent(prefix, ignored -> new ArrayList<>()).add(StudentFields.trim(value));
    }

    private void validateSearchValues(Map<String, List<String>> values) throws ParseException {
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            if (entry.getValue().stream().anyMatch(String::isEmpty)) {
                throw new ParseException("Missing value for parameter " + entry.getKey());
            }
        }
    }

    private List<SearchTerm> createSearchTerms(Map<String, List<String>> values) {
        List<SearchTerm> searchTerms = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            SearchField field = SEARCH_FIELDS.get(entry.getKey());
            for (String value : entry.getValue()) {
                searchTerms.add(new SearchTerm(field, value));
            }
        }
        return searchTerms;
    }
}
