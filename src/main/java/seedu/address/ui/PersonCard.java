package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label initials;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label subjects;
    @FXML
    private Label email;
    @FXML
    private Label level;
    @FXML
    private Label notes;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        CardContent content = CardContent.from(person);
        id.setText(displayedIndex + ". ");
        name.setText(content.name());
        initials.setText(content.initials());
        phone.setText(content.phone());
        setOptionalText(email, content.email());
        setOptionalText(subjects, content.subjects());
        setOptionalText(level, content.level());
        setOptionalText(notes, content.notes());
        person.getTags().stream().map(tag -> tag.tagName).sorted().forEach(tagName -> {
            Label badge = new Label(tagName);
            badge.getStyleClass().add("student-tag");
            badge.setWrapText(true);
            badge.maxWidthProperty().bind(tags.widthProperty());
            tags.getChildren().add(badge);
        });
        tags.setVisible(!person.getTags().isEmpty());
        tags.setManaged(!person.getTags().isEmpty());
    }

    /** Text shown on a student card; absent optional values are hidden when applied to labels. */
    record CardContent(String name, String initials, String phone, String email, String subjects, String level,
                       String notes) {
        static CardContent from(Person person) {
            String fullName = person.getName().fullName;
            String emailText = person.getEmail() == null ? null : "Email: " + person.getEmail().value;
            String subjectsText = person.getSubjects().isEmpty()
                    ? null : "Subjects: " + String.join(", ", person.getSubjects());
            String levelText = person.getLevel() == null ? null : "Level: " + person.getLevel();
            String notesText = person.getNotes().isEmpty() ? null : "Notes: " + person.getNotes();
            return new CardContent(fullName, getInitials(fullName), "Phone: " + person.getPhone().value,
                    emailText, subjectsText, levelText, notesText);
        }
    }

    private static void setOptionalText(Label label, String text) {
        boolean present = text != null;
        label.setVisible(present);
        label.setManaged(present);
        if (present) {
            label.setText(text);
        }
    }

    private static String getInitials(String fullName) {
        String trimmedName = StudentFields.trim(fullName);
        if (trimmedName.isEmpty()) {
            return "?";
        }
        String[] nameParts = trimmedName.split("(?U)\\s+");
        if (nameParts.length == 1) {
            return firstInitial(nameParts[0]).toUpperCase(java.util.Locale.ROOT);
        }
        return (firstInitial(nameParts[0]) + firstInitial(nameParts[nameParts.length - 1]))
                .toUpperCase(java.util.Locale.ROOT);
    }

    /** Returns the initials used by both compact cards and the full detail view. */
    public static String getInitialsForDisplay(String fullName) {
        return getInitials(fullName);
    }

    private static String firstInitial(String part) {
        int codePoint = part.codePoints().filter(Character::isLetterOrDigit)
                .findFirst().orElse(part.codePointAt(0));
        return new String(Character.toChars(codePoint));
    }
}
