package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
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
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing student in TutorRoster.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the student identified "
            + "by the index number used in the displayed student list. "
            + "Single-value fields are overwritten; use +s/+t to append and -s/-t to remove values.\n"
            + "Use e/ or a/ without a value to clear the optional email or address field.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_SUBJECT + "SUBJECT]... "
            + "[" + PREFIX_APPEND_SUBJECT + "SUBJECT]... "
            + "[" + PREFIX_REMOVE_SUBJECT + "SUBJECT]... "
            + "[" + PREFIX_LEVEL + "LEVEL] "
            + "[" + PREFIX_NOTES + "NOTES] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_APPEND_TAG + "TAG]... "
            + "[" + PREFIX_REMOVE_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This student already exists in TutorRoster.";
    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson;
        try {
            editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new CommandException(exception.getMessage(), exception);
        }

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson.getName().fullName));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(personToEdit);
        requireNonNull(editPersonDescriptor);

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.isEmailCleared()
                ? null
                : editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.isAddressCleared()
                ? null
                : editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = updateTags(personToEdit, editPersonDescriptor);
        List<String> updatedSubjects = updateSubjects(personToEdit, editPersonDescriptor);
        String updatedLevel = editPersonDescriptor.getLevel().orElse(personToEdit.getLevel());
        String updatedNotes = editPersonDescriptor.getNotes().orElse(personToEdit.getNotes());

        return personToEdit.withStudentDetails(updatedName.fullName, updatedPhone.value,
                updatedEmail == null ? null : updatedEmail.value, updatedSubjects, updatedLevel,
                updatedNotes, updatedAddress, updatedTags);
    }

    /** Applies replacement, append, and removal operations to the subject list. */
    private static List<String> updateSubjects(Person personToEdit, EditPersonDescriptor descriptor) {
        requireNonNull(personToEdit);
        requireNonNull(descriptor);
        List<String> subjects = new ArrayList<>(descriptor.getSubjects().orElse(personToEdit.getSubjects()));

        for (String subjectToAdd : descriptor.getSubjectsToAdd().orElse(List.of())) {
            String subjectKey = StudentFields.subjectKey(subjectToAdd);
            boolean alreadyPresent = subjects.stream()
                    .map(StudentFields::subjectKey)
                    .anyMatch(subjectKey::equals);
            if (alreadyPresent) {
                throw new IllegalArgumentException("Duplicate subject: " + subjectToAdd);
            }
            subjects.add(subjectToAdd);
        }

        Set<String> subjectsToRemove = descriptor.getSubjectsToRemove().orElse(List.of()).stream()
                .map(StudentFields::subjectKey)
                .collect(Collectors.toSet());
        subjects.removeIf(subject -> subjectsToRemove.contains(StudentFields.subjectKey(subject)));
        return subjects;
    }

    /** Applies replacement, append, and removal operations to the tag set. */
    private static Set<Tag> updateTags(Person personToEdit, EditPersonDescriptor descriptor) {
        requireNonNull(personToEdit);
        requireNonNull(descriptor);
        Set<Tag> tags = new HashSet<>(descriptor.getTags().orElse(personToEdit.getTags()));
        tags.addAll(descriptor.getTagsToAdd().orElse(Set.of()));
        tags.removeAll(descriptor.getTagsToRemove().orElse(Set.of()));
        if (descriptor.getTagsToAdd().isPresent() && tags.size() > Tag.MAX_TAGS_PER_STUDENT) {
            throw new IllegalArgumentException(Tag.MESSAGE_TAG_LIMIT);
        }
        return tags;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Single-value fields replace the corresponding
     * field value; multi-value fields can also append to or remove from the existing values.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private boolean emailCleared;
        private Address address;
        private boolean addressCleared;
        private Set<Tag> tags;
        private Set<Tag> tagsToAdd;
        private Set<Tag> tagsToRemove;
        private List<String> subjects;
        private List<String> subjectsToAdd;
        private List<String> subjectsToRemove;
        private String level;
        private String notes;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            requireNonNull(toCopy);
            setName(toCopy.name);
            setPhone(toCopy.phone);
            if (toCopy.emailCleared) {
                clearEmail();
            } else {
                setEmail(toCopy.email);
            }
            if (toCopy.addressCleared) {
                clearAddress();
            } else {
                setAddress(toCopy.address);
            }
            setTags(toCopy.tags);
            setTagsToAdd(toCopy.tagsToAdd);
            setTagsToRemove(toCopy.tagsToRemove);
            setSubjects(toCopy.subjects);
            setSubjectsToAdd(toCopy.subjectsToAdd);
            setSubjectsToRemove(toCopy.subjectsToRemove);
            setLevel(toCopy.level);
            setNotes(toCopy.notes);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return emailCleared || addressCleared
                    || CollectionUtil.isAnyNonNull(name, phone, email, address, tags, tagsToAdd, tagsToRemove,
                    subjects, subjectsToAdd, subjectsToRemove, level, notes);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
            emailCleared = false;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /** Marks the optional email field for removal. */
        public void clearEmail() {
            email = null;
            emailCleared = true;
        }

        /** Returns whether the optional email field is marked for removal. */
        public boolean isEmailCleared() {
            return emailCleared;
        }

        public void setAddress(Address address) {
            this.address = address;
            addressCleared = false;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /** Marks the optional address field for removal. */
        public void clearAddress() {
            address = null;
            addressCleared = true;
        }

        /** Returns whether the optional address field is marked for removal. */
        public boolean isAddressCleared() {
            return addressCleared;
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            if (tags == null) {
                this.tags = null;
                return;
            }
            CollectionUtil.requireAllNonNull(tags);
            this.tags = new HashSet<>(tags);
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /** Sets tags to append using a defensive copy. */
        public void setTagsToAdd(Set<Tag> tags) {
            this.tagsToAdd = copyTags(tags);
        }

        /** Returns tags to append, if supplied. */
        public Optional<Set<Tag>> getTagsToAdd() {
            return tagsToAdd == null ? Optional.empty() : Optional.of(Collections.unmodifiableSet(tagsToAdd));
        }

        /** Sets tags to remove using a defensive copy. */
        public void setTagsToRemove(Set<Tag> tags) {
            this.tagsToRemove = copyTags(tags);
        }

        /** Returns tags to remove, if supplied. */
        public Optional<Set<Tag>> getTagsToRemove() {
            return tagsToRemove == null ? Optional.empty() : Optional.of(Collections.unmodifiableSet(tagsToRemove));
        }

        private static Set<Tag> copyTags(Set<Tag> tags) {
            if (tags == null) {
                return null;
            }
            CollectionUtil.requireAllNonNull(tags);
            return new HashSet<>(tags);
        }

        /** Sets the replacement subjects using a defensive copy. */
        public void setSubjects(List<String> subjects) {
            if (subjects == null) {
                this.subjects = null;
                return;
            }
            this.subjects = new ArrayList<>(subjects);
            CollectionUtil.requireAllNonNull(this.subjects);
        }

        /** Returns an unmodifiable replacement subject list, if supplied. */
        public Optional<List<String>> getSubjects() {
            return subjects == null ? Optional.empty() : Optional.of(Collections.unmodifiableList(subjects));
        }

        /** Sets subjects to append using a defensive copy. */
        public void setSubjectsToAdd(List<String> subjects) {
            this.subjectsToAdd = copySubjects(subjects);
        }

        /** Returns subjects to append, if supplied. */
        public Optional<List<String>> getSubjectsToAdd() {
            return subjectsToAdd == null ? Optional.empty() : Optional.of(Collections.unmodifiableList(subjectsToAdd));
        }

        /** Sets subjects to remove using a defensive copy. */
        public void setSubjectsToRemove(List<String> subjects) {
            this.subjectsToRemove = copySubjects(subjects);
        }

        /** Returns subjects to remove, if supplied. */
        public Optional<List<String>> getSubjectsToRemove() {
            return subjectsToRemove == null
                    ? Optional.empty() : Optional.of(Collections.unmodifiableList(subjectsToRemove));
        }

        private static List<String> copySubjects(List<String> subjects) {
            if (subjects == null) {
                return null;
            }
            List<String> copy = new ArrayList<>(subjects);
            CollectionUtil.requireAllNonNull(copy);
            return copy;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public Optional<String> getLevel() {
            return Optional.ofNullable(level);
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Optional<String> getNotes() {
            return Optional.ofNullable(notes);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && emailCleared == otherEditPersonDescriptor.emailCleared
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && addressCleared == otherEditPersonDescriptor.addressCleared
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(tagsToAdd, otherEditPersonDescriptor.tagsToAdd)
                    && Objects.equals(tagsToRemove, otherEditPersonDescriptor.tagsToRemove)
                    && Objects.equals(subjects, otherEditPersonDescriptor.subjects)
                    && Objects.equals(subjectsToAdd, otherEditPersonDescriptor.subjectsToAdd)
                    && Objects.equals(subjectsToRemove, otherEditPersonDescriptor.subjectsToRemove)
                    && Objects.equals(level, otherEditPersonDescriptor.level)
                    && Objects.equals(notes, otherEditPersonDescriptor.notes);
        }

        @Override
        public String toString() {
            ToStringBuilder builder = new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("subjects", subjects)
                    .add("level", level)
                    .add("notes", notes);
            if (tagsToAdd != null) {
                builder.add("tagsToAdd", tagsToAdd);
            }
            if (emailCleared) {
                builder.add("emailCleared", true);
            }
            if (addressCleared) {
                builder.add("addressCleared", true);
            }
            if (tagsToRemove != null) {
                builder.add("tagsToRemove", tagsToRemove);
            }
            if (subjectsToAdd != null) {
                builder.add("subjectsToAdd", subjectsToAdd);
            }
            if (subjectsToRemove != null) {
                builder.add("subjectsToRemove", subjectsToRemove);
            }
            return builder.toString();
        }
    }
}
