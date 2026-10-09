package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;

/** Parses the add-student syntax and checks its fields before creating a command. */
public class AddCommandParser implements Parser<AddCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?U)(?<!\\S)([a-z])/");

    @Override
    public AddCommand parse(String args) throws ParseException {
        Map<String, List<String>> values = new LinkedHashMap<>();
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        String previousPrefix = null;
        int previousValueStart = 0;
        int firstPrefixStart = -1;

        while (matcher.find()) {
            String prefix = matcher.group(1) + "/";
            if (!isKnownPrefix(prefix)) {
                throw new ParseException("Unknown parameter prefix: " + prefix);
            }
            if (firstPrefixStart < 0) {
                firstPrefixStart = matcher.start(1);
            }
            if (previousPrefix != null) {
                values.computeIfAbsent(previousPrefix, ignored -> new ArrayList<>())
                        .add(StudentFields.trim(args.substring(previousValueStart, matcher.start(1))));
            }
            previousPrefix = prefix;
            previousValueStart = matcher.end();
        }

        if (previousPrefix != null) {
            values.computeIfAbsent(previousPrefix, ignored -> new ArrayList<>())
                    .add(StudentFields.trim(args.substring(previousValueStart)));
        }
        if (firstPrefixStart < 0 || !StudentFields.trim(args.substring(0, firstPrefixStart)).isEmpty()) {
            throw new ParseException(AddCommand.MESSAGE_USAGE);
        }

        for (String prefix : List.of("n/", "p/", "e/", "l/")) {
            if (values.getOrDefault(prefix, List.of()).size() > 1) {
                throw new ParseException("Parameter " + prefix + " must not be specified more than once.");
            }
        }
        if (!values.containsKey("n/") || !values.containsKey("p/")
                || !values.containsKey("s/") || !values.containsKey("l/")) {
            throw new ParseException(AddCommand.MESSAGE_USAGE);
        }

        String name = StudentFields.normalizeName(values.get("n/").getFirst());
        String phone = values.get("p/").getFirst();
        String email = values.containsKey("e/") ? values.get("e/").getFirst() : null;
        List<String> subjects = values.get("s/");
        String level = values.get("l/").getFirst();

        if (!StudentFields.isValidName(name)) {
            throw new ParseException(StudentFields.INVALID_NAME);
        }
        if (!StudentFields.isValidPhone(phone)) {
            throw new ParseException(StudentFields.INVALID_PHONE);
        }
        if (email != null && !StudentFields.isValidEmail(email)) {
            throw new ParseException(StudentFields.INVALID_EMAIL);
        }
        List<String> normalizedSubjects = new ArrayList<>();
        List<String> subjectKeys = new ArrayList<>();
        for (String subject : subjects) {
            if (!StudentFields.isValidSubject(subject)) {
                throw new ParseException(StudentFields.INVALID_SUBJECT);
            }
            String normalized = StudentFields.trim(subject);
            String key = StudentFields.subjectKey(normalized);
            if (subjectKeys.contains(key)) {
                throw new ParseException("Duplicate subject: " + normalized);
            }
            normalizedSubjects.add(normalized);
            subjectKeys.add(key);
        }
        if (!StudentFields.isValidLevel(level)) {
            throw new ParseException(StudentFields.INVALID_LEVEL);
        }
        Person student = new Person(name, phone, email, normalizedSubjects, level);
        return new AddCommand(student);
    }

    private static boolean isKnownPrefix(String prefix) {
        return prefix.equals("n/") || prefix.equals("p/") || prefix.equals("e/")
                || prefix.equals("s/") || prefix.equals("l/");
    }
}
