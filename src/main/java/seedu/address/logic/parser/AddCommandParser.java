package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

/** Parses the add-student syntax and checks its fields before creating a command. */
public class AddCommandParser implements Parser<AddCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?U)(?<!\\S)([a-z]/|/[a-z](?=\\s|$))");
    private static final List<Map.Entry<String, String>> REQUIRED_PARAMETERS = List.of(
            Map.entry("n/", "n/NAME"), Map.entry("p/", "p/PHONE"),
            Map.entry("s/", "s/SUBJECT"), Map.entry("l/", "l/LEVEL"));

    @Override
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Map<String, List<String>> values = new LinkedHashMap<>();
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        String previousPrefix = null;
        int previousValueStart = 0;
        int firstPrefixStart = -1;

        while (matcher.find()) {
            String prefix = matcher.group(1);
            boolean knownPrefix = isKnownPrefix(prefix);
            if (!knownPrefix && !"r/".equals(previousPrefix)) {
                throw new ParseException("Unknown parameter prefix: " + prefix);
            }
            if (!knownPrefix) {
                continue;
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
        String preamble = firstPrefixStart < 0 ? args : args.substring(0, firstPrefixStart);
        if (!StudentFields.trim(preamble).isEmpty()) {
            throw new ParseException(AddCommand.MESSAGE_USAGE);
        }

        for (String prefix : List.of("n/", "p/", "e/", "a/", "l/", "r/")) {
            if (values.getOrDefault(prefix, List.of()).size() > 1) {
                throw new ParseException("Parameter " + prefix + " must not be specified more than once.");
            }
        }
        for (Map.Entry<String, String> required : REQUIRED_PARAMETERS) {
            if (!values.containsKey(required.getKey())) {
                throw new ParseException(AddCommand.missingRequiredParameter(required.getValue()));
            }
            if (values.get(required.getKey()).getFirst().isEmpty()) {
                throw new ParseException(AddCommand.missingRequiredValue(required.getValue()));
            }
        }

        String name = StudentFields.normalizeName(values.get("n/").getFirst());
        String phone = values.get("p/").getFirst();
        String email = values.containsKey("e/") ? values.get("e/").getFirst() : null;
        String address = values.containsKey("a/") ? values.get("a/").getFirst() : null;
        List<String> subjects = values.get("s/");
        String level = StudentFields.normalizeWhitespace(values.get("l/").getFirst());
        String notes = values.containsKey("r/") ? values.get("r/").getFirst() : "";

        if (!StudentFields.isValidName(name)) {
            throw new ParseException(StudentFields.INVALID_NAME);
        }
        if (!StudentFields.isValidPhone(phone)) {
            throw new ParseException(StudentFields.INVALID_PHONE);
        }
        if (email != null && !StudentFields.isValidEmail(email)) {
            throw new ParseException(StudentFields.INVALID_EMAIL);
        }
        if (address != null && !Address.isValidAddress(address)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        List<String> normalizedSubjects = new ArrayList<>();
        List<String> subjectKeys = new ArrayList<>();
        for (String subject : subjects) {
            String normalized = StudentFields.normalizeWhitespace(subject);
            if (!StudentFields.isValidSubject(normalized)) {
                throw new ParseException(StudentFields.INVALID_SUBJECT);
            }
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
        if (values.containsKey("r/") && !StudentFields.isValidNotes(notes)) {
            throw new ParseException(StudentFields.INVALID_NOTES);
        }
        List<String> tagValues = values.getOrDefault("t/", List.of());
        Set<Tag> tags = parseTags(tagValues);
        if (tags.size() > Tag.MAX_TAGS_PER_STUDENT) {
            throw new ParseException(Tag.MESSAGE_TAG_LIMIT);
        }
        Address parsedAddress = address == null ? null : new Address(address);
        Person student = new Person(name, phone, email, normalizedSubjects, level, notes, parsedAddress)
                .withTags(tags);
        return new AddCommand(student, tags.size() < tagValues.size());
    }

    private static Set<Tag> parseTags(List<String> tagValues) throws ParseException {
        Set<Tag> tags = new HashSet<>();
        for (String tagValue : tagValues) {
            if (!Tag.isValidTagName(tagValue)) {
                throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
            }
            tags.add(new Tag(tagValue));
        }
        return tags;
    }

    private static boolean isKnownPrefix(String prefix) {
        return prefix.equals("n/") || prefix.equals("p/") || prefix.equals("e/")
                || prefix.equals("a/") || prefix.equals("s/") || prefix.equals("l/") || prefix.equals("r/")
                || prefix.equals("t/");
    }
}
