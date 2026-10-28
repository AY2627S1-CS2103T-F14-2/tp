package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPEND_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPEND_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMOVE_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMOVE_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "(?U)(?<!\\S)([a-z]/|[+-][a-z]/|/[a-z](?=\\s|$))");
    private static final Set<String> KNOWN_PREFIXES = Set.of(
            PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(), PREFIX_ADDRESS.getPrefix(),
            PREFIX_TAG.getPrefix(), PREFIX_APPEND_TAG.getPrefix(), PREFIX_REMOVE_TAG.getPrefix(),
            PREFIX_SUBJECT.getPrefix(), PREFIX_APPEND_SUBJECT.getPrefix(),
            PREFIX_REMOVE_SUBJECT.getPrefix(), PREFIX_LEVEL.getPrefix(), PREFIX_NOTES.getPrefix());

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        rejectUnknownPrefixes(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_TAG, PREFIX_APPEND_TAG, PREFIX_REMOVE_TAG, PREFIX_SUBJECT,
                        PREFIX_APPEND_SUBJECT, PREFIX_REMOVE_SUBJECT, PREFIX_LEVEL, PREFIX_NOTES);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_LEVEL, PREFIX_NOTES);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String normalizedName = StudentFields.normalizeName(argMultimap.getValue(PREFIX_NAME).get());
            if (!StudentFields.isValidName(normalizedName)) {
                throw new ParseException(StudentFields.INVALID_NAME);
            }
            editPersonDescriptor.setName(Name.fromStoredValue(normalizedName));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            String phone = argMultimap.getValue(PREFIX_PHONE).get();
            if (!StudentFields.isValidPhone(phone)) {
                throw new ParseException(StudentFields.INVALID_PHONE);
            }
            editPersonDescriptor.setPhone(Phone.fromStoredValue(phone));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            String email = argMultimap.getValue(PREFIX_EMAIL).get();
            if (email == null || email.isEmpty()) {
                editPersonDescriptor.clearEmail();
            } else {
                if (!StudentFields.isValidEmail(email)) {
                    throw new ParseException(StudentFields.INVALID_EMAIL);
                }
                editPersonDescriptor.setEmail(Email.fromStoredValue(email));
            }
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            String address = argMultimap.getValue(PREFIX_ADDRESS).get();
            if (address == null || address.isEmpty()) {
                editPersonDescriptor.clearAddress();
            } else {
                editPersonDescriptor.setAddress(ParserUtil.parseAddress(address));
            }
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG), true).ifPresent(editPersonDescriptor::setTags);
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_APPEND_TAG), false)
                .ifPresent(editPersonDescriptor::setTagsToAdd);
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_REMOVE_TAG), false)
                .ifPresent(editPersonDescriptor::setTagsToRemove);
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_SUBJECT)).ifPresent(editPersonDescriptor::setSubjects);
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_APPEND_SUBJECT))
                .ifPresent(editPersonDescriptor::setSubjectsToAdd);
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_REMOVE_SUBJECT))
                .ifPresent(editPersonDescriptor::setSubjectsToRemove);
        if (argMultimap.getValue(PREFIX_LEVEL).isPresent()) {
            editPersonDescriptor.setLevel(ParserUtil.parseLevel(argMultimap.getValue(PREFIX_LEVEL).get()));
        }
        if (argMultimap.getValue(PREFIX_NOTES).isPresent()) {
            String notes = argMultimap.getValue(PREFIX_NOTES).get();
            editPersonDescriptor.setNotes(notes.isEmpty() ? "" : ParserUtil.parseNotes(notes));
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /** Rejects prefix-shaped tokens that are not part of the edit command syntax. */
    private static void rejectUnknownPrefixes(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group(1);
            if (prefix == null || !KNOWN_PREFIXES.contains(prefix)) {
                throw new ParseException("Unknown parameter prefix: " + prefix);
            }
        }
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags, boolean allowEmpty) throws ParseException {
        requireNonNull(tags);

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        if (!allowEmpty && tags.stream().anyMatch(String::isEmpty)) {
            return Optional.of(ParserUtil.parseTags(tags));
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

    /** Parses repeated subjects, rejecting empty and equivalent values. */
    private Optional<List<String>> parseSubjectsForEdit(Collection<String> subjects) throws ParseException {
        requireNonNull(subjects);
        if (subjects.isEmpty()) {
            return Optional.empty();
        }
        List<String> parsedSubjects = new java.util.ArrayList<>();
        Set<String> subjectKeys = new HashSet<>();
        for (String subject : subjects) {
            String parsedSubject = ParserUtil.parseSubject(subject);
            if (!subjectKeys.add(StudentFields.subjectKey(parsedSubject))) {
                throw new ParseException("Duplicate subject: " + parsedSubject);
            }
            parsedSubjects.add(parsedSubject);
        }
        return Optional.of(parsedSubjects);
    }

}
