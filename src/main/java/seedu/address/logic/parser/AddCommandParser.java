package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudentText;
import seedu.address.model.person.Subject;

/** Parses a student addition, reporting structural errors before field errors. */
public class AddCommandParser implements Parser<AddCommand> {
    private static final Pattern PARAMETER = Pattern.compile("(?U)(?<!\\S)([A-Za-z])/");
    private static final Set<String> KNOWN_PREFIXES = Set.of("n/", "p/", "e/", "s/", "l/");

    @Override
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = tokenize(args);
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_LEVEL)) {
            if (arguments.getAllValues(prefix).size() > 1) {
                throw new ParseException("Parameter " + prefix + " must not be specified more than once.");
            }
        }
        if (!arguments.getPreamble().isEmpty()
                || List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL).stream()
                    .anyMatch(prefix -> arguments.getValue(prefix).isEmpty())) {
            throw new ParseException(AddCommand.MESSAGE_INVALID_FORMAT);
        }

        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).orElseThrow());
        Phone phone = ParserUtil.parsePhone(arguments.getValue(PREFIX_PHONE).orElseThrow());
        Email email = arguments.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(arguments.getValue(PREFIX_EMAIL).orElseThrow()) : Email.absent();
        List<Subject> subjects = new ArrayList<>();
        for (String value : arguments.getAllValues(PREFIX_SUBJECT)) {
            if (!Subject.isValidSubject(value)) {
                throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
            }
            subjects.add(new Subject(value));
        }
        String levelValue = arguments.getValue(PREFIX_LEVEL).orElseThrow();
        if (!Level.isValidLevel(levelValue)) {
            throw new ParseException(Level.MESSAGE_CONSTRAINTS);
        }
        Level level = new Level(levelValue);
        for (int i = 0; i < subjects.size(); i++) {
            for (int j = i + 1; j < subjects.size(); j++) {
                if (subjects.get(i).value.equalsIgnoreCase(subjects.get(j).value)) {
                    throw new ParseException("Duplicate subject: " + subjects.get(i));
                }
            }
        }
        return new AddCommand(new Person(name, phone, email, subjects, level));
    }

    /** Single-letter prefixes are tokens only at whitespace boundaries. */
    private ArgumentMultimap tokenize(String args) throws ParseException {
        List<Integer> positions = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        Matcher matcher = PARAMETER.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group(1) + "/";
            if (!KNOWN_PREFIXES.contains(prefix)) {
                throw new ParseException("Unknown parameter prefix: " + prefix);
            }
            positions.add(matcher.start());
            prefixes.add(prefix);
        }
        ArgumentMultimap arguments = new ArgumentMultimap();
        int first = positions.isEmpty() ? args.length() : positions.getFirst();
        arguments.put(new Prefix(""), StudentText.trim(args.substring(0, first)));
        for (int i = 0; i < positions.size(); i++) {
            int end = i + 1 < positions.size() ? positions.get(i + 1) : args.length();
            arguments.put(new Prefix(prefixes.get(i)), StudentText.trim(args.substring(positions.get(i) + 2, end)));
        }
        return arguments;
    }
}
