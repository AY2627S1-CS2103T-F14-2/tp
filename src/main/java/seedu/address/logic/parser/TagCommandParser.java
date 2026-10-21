package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.TagCommand.Operation;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/** Parses the strict tag/untag syntax; each command targets one displayed student. */
public class TagCommandParser implements Parser<TagCommand> {
    private final boolean removing;

    public TagCommandParser(boolean removing) {
        this.removing = removing;
    }

    @Override
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String usage = removing ? TagCommand.MESSAGE_UNTAG_USAGE : TagCommand.MESSAGE_USAGE;
        String[] parts = args.strip().split("(?U)\\s+");
        if (parts.length != 2) {
            throw new ParseException(usage);
        }
        boolean clear = removing && parts[0].equals("--all");
        Index index;
        try {
            index = ParserUtil.parseIndex(clear ? parts[1] : parts[0]);
        } catch (ParseException e) {
            throw new ParseException(usage, e);
        }
        if (clear) {
            return new TagCommand(index, null, Operation.CLEAR);
        }
        String name = parts[1];
        // Removal also accepts names that were stored before the length limit was introduced.
        if (removing ? !Tag.isValidStoredTagName(name) : !Tag.isValidTagName(name)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new TagCommand(index, removing ? Tag.fromStoredName(name) : new Tag(name),
                removing ? Operation.REMOVE : Operation.ADD);
    }
}
