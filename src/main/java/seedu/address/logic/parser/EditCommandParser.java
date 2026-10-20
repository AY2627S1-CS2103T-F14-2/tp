package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                        PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_NOTES, PREFIX_ADDRESS, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_LEVEL, PREFIX_NOTES, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String name = argMultimap.getValue(PREFIX_NAME).get();
            String normalizedName = StudentFields.normalizeName(name);
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
            if (!StudentFields.isValidEmail(email)) {
                throw new ParseException(StudentFields.INVALID_EMAIL);
            }
            editPersonDescriptor.setEmail(Email.fromStoredValue(email));
        }
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_SUBJECT)).ifPresent(editPersonDescriptor::setSubjects);
        if (argMultimap.getValue(PREFIX_LEVEL).isPresent()) {
            String level = argMultimap.getValue(PREFIX_LEVEL).get();
            String normalizedLevel = StudentFields.normalizeWhitespace(level);
            if (!StudentFields.isValidLevel(normalizedLevel)) {
                throw new ParseException(StudentFields.INVALID_LEVEL);
            }
            editPersonDescriptor.setLevel(normalizedLevel);
        }
        if (argMultimap.getValue(PREFIX_NOTES).isPresent()) {
            String notes = argMultimap.getValue(PREFIX_NOTES).get();
            String trimmedNotes = StudentFields.trim(notes);
            if (!StudentFields.isValidNotes(trimmedNotes)) {
                throw new ParseException(StudentFields.INVALID_NOTES);
            }
            editPersonDescriptor.setNotes(trimmedNotes);
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> subjects} into a {@code List<String>} if {@code subjects} is non-empty.
     */
    private Optional<List<String>> parseSubjectsForEdit(Collection<String> subjects) throws ParseException {
        assert subjects != null;

        if (subjects.isEmpty()) {
            return Optional.empty();
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
        return Optional.of(Collections.unmodifiableList(normalizedSubjects));
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
